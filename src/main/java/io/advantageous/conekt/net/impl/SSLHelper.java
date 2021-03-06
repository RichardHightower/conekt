/*
 *
 *  * Copyright (c) 2011-2016 The original author or authors
 *  * This project contains modified work from the Vert.x Project.
 *  * The Vert.x project Copyright is owned by Red Hat and/or the
 *  * original authors of the Vert.x project including Tim Fox, Julien Vet,
 *  * Norman Maurer, and many others.
 *  * We have left the original author tags on this MODIFIED COPY/FORK.
 *  *
 *  * Modified work is Copyright (c) 2015-2016 Rick Hightower and Geoff Chandler.
 *  * ------------------------------------------------------
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available under the terms of the Eclipse Public License v1.0
 *  * and Apache License v2.0 which accompanies this distribution.
 *  *
 *  *     The Eclipse Public License is available at
 *  *     http://www.eclipse.org/legal/epl-v10.html
 *  *
 *  *     The Apache License v2.0 is available at
 *  *     http://www.opensource.org/licenses/apache2.0.php
 *  *
 *  * You may elect to redistribute this code under either of these licenses.
 *
 */

package io.advantageous.conekt.net.impl;

import io.advantageous.conekt.ConektException;
import io.advantageous.conekt.http.HttpClientOptions;
import io.advantageous.conekt.impl.ConektInternal;
import io.advantageous.conekt.net.NetServerOptions;
import io.netty.handler.ssl.SslHandler;
import io.advantageous.conekt.buffer.Buffer;
import io.advantageous.conekt.http.ClientAuth;
import io.advantageous.conekt.http.HttpServerOptions;
import io.advantageous.conekt.net.NetClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.security.SecureRandom;
import java.security.cert.CRL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is a pretty sucky class - could do with a refactoring
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class SSLHelper {


    private static final Logger log = LoggerFactory.getLogger(SSLHelper.class);

    // Make sure SSLv3 is NOT enabled due to POODLE vulnerability http://en.wikipedia.org/wiki/POODLE
    private static final String[] ENABLED_PROTOCOLS = {"SSLv2Hello", "TLSv1", "TLSv1.1", "TLSv1.2"};

    private boolean ssl;
    private KeyStoreHelper keyStoreHelper;
    private KeyStoreHelper trustStoreHelper;
    private boolean trustAll;
    private ArrayList<String> crlPaths;
    private ArrayList<Buffer> crlValues;
    private ClientAuth clientAuth = ClientAuth.NONE;
    private Set<String> enabledCipherSuites;
    private boolean verifyHost;

    private SSLContext sslContext;

    public SSLHelper(HttpClientOptions options, KeyStoreHelper keyStoreHelper, KeyStoreHelper trustStoreHelper) {
        this.ssl = options.isSsl();
        this.keyStoreHelper = keyStoreHelper;
        this.trustStoreHelper = trustStoreHelper;
        this.trustAll = options.isTrustAll();
        this.crlPaths = new ArrayList<>(options.getCrlPaths());
        this.crlValues = new ArrayList<>(options.getCrlValues());
        this.enabledCipherSuites = options.getEnabledCipherSuites();
        this.verifyHost = options.isVerifyHost();
    }

    public SSLHelper(HttpServerOptions options, KeyStoreHelper keyStoreHelper, KeyStoreHelper trustStoreHelper) {
        this.ssl = options.isSsl();
        this.keyStoreHelper = keyStoreHelper;
        this.trustStoreHelper = trustStoreHelper;
        this.clientAuth = options.getClientAuth();
        this.crlPaths = options.getCrlPaths() != null ? new ArrayList<>(options.getCrlPaths()) : null;
        this.crlValues = options.getCrlValues() != null ? new ArrayList<>(options.getCrlValues()) : null;
        this.enabledCipherSuites = options.getEnabledCipherSuites();
    }

    public SSLHelper(NetClientOptions options, KeyStoreHelper keyStoreHelper, KeyStoreHelper trustStoreHelper) {
        this.ssl = options.isSsl();
        this.keyStoreHelper = keyStoreHelper;
        this.trustStoreHelper = trustStoreHelper;
        this.trustAll = options.isTrustAll();
        this.crlPaths = new ArrayList<>(options.getCrlPaths());
        this.crlValues = new ArrayList<>(options.getCrlValues());
        this.enabledCipherSuites = options.getEnabledCipherSuites();
    }

    public SSLHelper(NetServerOptions options, KeyStoreHelper keyStoreHelper, KeyStoreHelper trustStoreHelper) {
        this.ssl = options.isSsl();
        this.keyStoreHelper = keyStoreHelper;
        this.trustStoreHelper = trustStoreHelper;
        this.clientAuth = options.getClientAuth();
        this.crlPaths = options.getCrlPaths() != null ? new ArrayList<>(options.getCrlPaths()) : null;
        this.crlValues = options.getCrlValues() != null ? new ArrayList<>(options.getCrlValues()) : null;
        this.enabledCipherSuites = options.getEnabledCipherSuites();
    }

    /*
    Proxy the specified trust managers with an implementation checking first the provided certificates
    against the the Certificate Revocation List (crl) before delegating to the original trust managers.
     */
    private static TrustManager[] createUntrustRevokedCertTrustManager(TrustManager[] trustMgrs, ArrayList<CRL> crls) {
        trustMgrs = trustMgrs.clone();
        for (int i = 0; i < trustMgrs.length; i++) {
            TrustManager trustMgr = trustMgrs[i];
            if (trustMgr instanceof X509TrustManager) {
                X509TrustManager x509TrustManager = (X509TrustManager) trustMgr;
                trustMgrs[i] = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        checkRevocaked(x509Certificates);
                        x509TrustManager.checkClientTrusted(x509Certificates, s);
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        checkRevocaked(x509Certificates);
                        x509TrustManager.checkServerTrusted(x509Certificates, s);
                    }

                    private void checkRevocaked(X509Certificate[] x509Certificates) throws CertificateException {
                        for (X509Certificate cert : x509Certificates) {
                            for (CRL crl : crls) {
                                if (crl.isRevoked(cert)) {
                                    throw new CertificateException("Certificate revoked");
                                }
                            }
                        }
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return x509TrustManager.getAcceptedIssuers();
                    }
                };
            }
        }
        return trustMgrs;
    }

    // Create a TrustManager which trusts everything
    private static TrustManager createTrustAllTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public boolean isSSL() {
        return ssl;
    }

    public ClientAuth getClientAuth() {
        return clientAuth;
    }

    /*
    If you don't specify a trust store, and you haven't set system properties, the system will try to use either a file
    called jsssecacerts or cacerts in the JDK/JRE security directory.
    You can override this by specifying the javax.echo.ssl.trustStore system property

    If you don't specify a key store, and don't specify a system property no key store will be used
    You can override this by specifying the javax.echo.ssl.keyStore system property
     */
    private SSLContext createContext(ConektInternal vertx) {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            KeyManager[] keyMgrs = keyStoreHelper == null ? null : keyStoreHelper.getKeyMgrs(vertx);
            TrustManager[] trustMgrs;
            if (trustAll) {
                trustMgrs = new TrustManager[]{createTrustAllTrustManager()};
            } else {
                trustMgrs = trustStoreHelper == null ? null : trustStoreHelper.getTrustMgrs(vertx);
            }
            if (trustMgrs != null && crlPaths != null && crlValues != null && (crlPaths.size() > 0 || crlValues.size() > 0)) {
                Stream<Buffer> tmp = crlPaths.
                        stream().
                        map(path -> vertx.resolveFile(path).getAbsolutePath()).
                        map(vertx.fileSystem()::readFileBlocking);
                tmp = Stream.concat(tmp, crlValues.stream());
                CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
                ArrayList<CRL> crls = new ArrayList<>();
                for (Buffer crlValue : tmp.collect(Collectors.toList())) {
                    crls.addAll(certificatefactory.generateCRLs(new ByteArrayInputStream(crlValue.getBytes())));
                }
                trustMgrs = createUntrustRevokedCertTrustManager(trustMgrs, crls);
            }
            context.init(keyMgrs, trustMgrs, new SecureRandom());
            return context;
        } catch (Exception e) {
            throw new ConektException(e);
        }
    }

    private SslHandler createHandler(SSLEngine engine, boolean client) {
        if (enabledCipherSuites != null && !enabledCipherSuites.isEmpty()) {
            String[] toUse = enabledCipherSuites.toArray(new String[enabledCipherSuites.size()]);
            engine.setEnabledCipherSuites(toUse);
        }
        engine.setUseClientMode(client);
        Set<String> enabledProtocols = new HashSet<>(Arrays.asList(ENABLED_PROTOCOLS));
        enabledProtocols.retainAll(Arrays.asList(engine.getEnabledProtocols()));
        engine.setEnabledProtocols(enabledProtocols.toArray(new String[0]));
        if (!client) {
            switch (getClientAuth()) {
                case REQUEST: {
                    engine.setWantClientAuth(true);
                    break;
                }
                case REQUIRED: {
                    engine.setNeedClientAuth(true);
                    break;
                }
                case NONE: {
                    engine.setNeedClientAuth(false);
                    break;
                }
            }
        } else if (verifyHost) {
            SSLParameters sslParameters = engine.getSSLParameters();
            sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
            engine.setSSLParameters(sslParameters);
        }
        return new SslHandler(engine);
    }

    private SSLContext getContext(ConektInternal vertx) {
        if (sslContext == null) {
            sslContext = createContext(vertx);
        }
        return sslContext;
    }

    // This is called to validate some of the SSL params as that only happens when the context is created
    public synchronized void validate(ConektInternal vertx) {
        if (ssl) {
            getContext(vertx);
        }
    }

    public SslHandler createSslHandler(ConektInternal vertx, boolean client, String host, int port) {
        SSLEngine engine = getContext(vertx).createSSLEngine(host, port);
        return createHandler(engine, client);
    }

    public SslHandler createSslHandler(ConektInternal vertx, boolean client) {
        SSLEngine engine = getContext(vertx).createSSLEngine();
        return createHandler(engine, client);
    }

}
