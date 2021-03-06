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

/**
 * == Writing TCP servers and clients
 * <p>
 * Vert.x allows you to easily write non blocking TCP clients and servers.
 * <p>
 * === Creating a TCP server
 * <p>
 * The simplest way to create a TCP server, using all default options is as follows:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example1}
 * ----
 * <p>
 * === Configuring a TCP server
 * <p>
 * If you don't want the default, a server can be configured by passing in a {@link io.advantageous.conekt.net.NetServerOptions}
 * instance when creating it:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example2}
 * ----
 * <p>
 * === Start the Server Listening
 * <p>
 * To tell the server to listen for incoming requests you use one of the {@link io.advantageous.conekt.net.NetServer#listen}
 * alternatives.
 * <p>
 * To tell the server to listen at the host and port as specified in the options:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example3}
 * ----
 * <p>
 * Or to specify the host and port in the call to listen, ignoring what is configured in the options:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example4}
 * ----
 * <p>
 * The default host is `0.0.0.0` which means 'listen on all available addresses' and the default port is `0`, which is a
 * special value that instructs the server to find a random unused local port and use that.
 * <p>
 * The actual bind is asynchronous so the server might not actually be listening until some time *after* the call to
 * listen has returned.
 * <p>
 * If you want to be notified when the server is actually listening you can provide a handler to the `listen` call.
 * For example:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example5}
 * ----
 * <p>
 * === Listening on a random port
 * <p>
 * If `0` is used as the listening port, the server will find an unused random port to listen on.
 * <p>
 * To find out the real port the server is listening on you can call {@link io.advantageous.conekt.net.NetServer#actualPort()}.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example5_1}
 * ----
 * <p>
 * === Getting notified of incoming connections
 * <p>
 * To be notified when a connection is made you need to set a {@link io.advantageous.conekt.net.NetServer#connectHandler(Handler)}:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example6}
 * ----
 * <p>
 * When a connection is made the handler will be called with an instance of {@link io.advantageous.conekt.net.NetSocket}.
 * <p>
 * This is a socket-like interface to the actual connection, and allows you to read and write data as well as do various
 * other things like close the socket.
 * <p>
 * === Reading data from the socket
 * <p>
 * To read data from the socket you set the {@link io.advantageous.conekt.net.NetSocket#handler(Handler)} on the
 * socket.
 * <p>
 * This handler will be called with an instance of {@link io.advantageous.conekt.buffer.Buffer} every time data is received on
 * the socket.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example7}
 * ----
 * <p>
 * === Writing data to a socket
 * <p>
 * You write to a socket using one of {@link io.advantageous.conekt.net.NetSocket#write}.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example8}
 * ----
 * <p>
 * Write operations are asynchronous and may not occur until some time after the call to write has returned.
 * <p>
 * === Closed handler
 * <p>
 * If you want to be notified when a socket is closed, you can set a {@link io.advantageous.conekt.net.NetSocket#closeHandler(Handler)}
 * on it:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example9_1}
 * ----
 * <p>
 * === Handling exceptions
 * <p>
 * You can set an {@link io.advantageous.conekt.net.NetSocket#exceptionHandler(Handler)} to receive any
 * exceptions that happen on the socket.
 * <p>
 * === Event bus write handler
 * <p>
 * Every socket automatically registers a handler on the event bus, and when any buffers are received in this handler,
 * it writes them to itself.
 * <p>
 * This enables you to write data to a socket which is potentially in a completely different verticle or even in a
 * different Vert.x instance by sending the buffer to the address of that handler.
 * <p>
 * The address of the handler is given by {@link io.advantageous.conekt.net.NetSocket#writeHandlerID()}
 * <p>
 * === Local and remote addresses
 * <p>
 * The local address of a {@link io.advantageous.conekt.net.NetSocket} can be retrieved using {@link io.advantageous.conekt.net.NetSocket#localAddress()}.
 * <p>
 * The remote address, (i.e. the address of the other end of the connection) of a {@link io.advantageous.conekt.net.NetSocket}
 * can be retrieved using {@link io.advantageous.conekt.net.NetSocket#remoteAddress()}.
 * <p>
 * === Sending files or resources from the classpath
 * <p>
 * Files and classpath resources can be written to the socket directly using {@link io.advantageous.conekt.net.NetSocket#sendFile}. This can be a very
 * efficient way to send files, as it can be handled by the OS kernel directly where supported by the operating system.
 * <p>
 * Please see the chapter about <<classpath, serving files from the classpath>> for restrictions of the
 * classpath resolution or disabling it.
 * <p>
 * [source,$lang]
 * ----
 * ----
 * <p>
 * === Streaming sockets
 * <p>
 * Instances of {@link io.advantageous.conekt.net.NetSocket} are also {@link io.advantageous.conekt.streams.ReadStream} and
 * {@link io.advantageous.conekt.streams.WriteStream} instances so they can be used to pump data to or from other
 * read and write streams.
 * <p>
 * See the chapter on <<streams, streams and pumps>> for more information.
 * <p>
 * === Upgrading connections to SSL/TLS
 * <p>
 * A non SSL/TLS connection can be upgraded to SSL/TLS using {@link io.advantageous.conekt.net.NetSocket#upgradeToSsl(Handler)}.
 * <p>
 * The server or client must be configured for SSL/TLS for this to work correctly. Please see the <<ssl, chapter on SSL/TLS>>
 * for more information.
 * <p>
 * === Closing a TCP Server
 * <p>
 * Call {@link io.advantageous.conekt.net.NetServer#close()} to close the server. Closing the server closes any open connections
 * and releases all server resources.
 * <p>
 * The close is actually asynchronous and might not complete until some time after the call has returned.
 * If you want to be notified when the actual close has completed then you can pass in a handler.
 * <p>
 * This handler will then be called when the close has fully completed.
 * <p>
 * [source,$lang]
 * ----
 * ----
 * <p>
 * === Automatic clean-up in verticles
 * <p>
 * If you're creating TCP servers and clients from inside verticles, those servers and clients will be automatically closed
 * when the verticle is undeployed.
 * <p>
 * === Scaling - sharing TCP servers
 * <p>
 * The handlers of any TCP server are always executed on the same event loop thread.
 * <p>
 * This means that if you are running on a server with a lot of cores, and you only have this one instance
 * deployed then you will have at most one core utilised on your server.
 * <p>
 * In order to utilise more cores of your server you will need to deploy more instances of the server.
 * <p>
 * You can instantiate more instances programmatically in your code:
 * <p>
 * [source,$lang]
 * ----
 * ----
 * <p>
 * or, if you are using verticles you can simply deploy more instances of your server verticle by using the `-instances` option
 * on the command line:
 * <p>
 * vertx run com.mycompany.MyVerticle -instances 10
 * <p>
 * or when programmatically deploying your verticle
 * <p>
 * [source,$lang]
 * ----
 * ----
 * <p>
 * Once you do this you will find the echo server works functionally identically to before, but all your cores on your
 * server can be utilised and more work can be handled.
 * <p>
 * At this point you might be asking yourself *'How can you have more than one server listening on the
 * same host and port? Surely you will get port conflicts as soon as you try and deploy more than one instance?'*
 * <p>
 * _Vert.x does a little magic here.*_
 * <p>
 * When you deploy another server on the same host and port as an existing server it doesn't actually try and create a
 * new server listening on the same host/port.
 * <p>
 * Instead it internally maintains just a single server, and, as incoming connections arrive it distributes
 * them in a round-robin fashion to any of the connect handlers.
 * <p>
 * Consequently Vert.x TCP servers can scale over available cores while each instance remains single threaded.
 * <p>
 * === Creating a TCP client
 * <p>
 * The simplest way to create a TCP client, using all default options is as follows:
 * <p>
 * [source,$lang]
 * ----
 * ----
 * <p>
 * === Configuring a TCP client
 * <p>
 * If you don't want the default, a client can be configured by passing in a {@link io.advantageous.conekt.net.NetClientOptions}
 * instance when creating it:
 * <p>
 * [source,$lang]
 * ----
 * ----
 * <p>
 * === Making connections
 * <p>
 * To make a connection to a server you use {@link io.advantageous.conekt.net.NetClient#connect(int, java.lang.String, Handler)},
 * specifying the port and host of the server and a handler that will be called with a result containing the
 * {@link io.advantageous.conekt.net.NetSocket} when connection is successful or with a failure if connection failed.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example15}
 * ----
 * <p>
 * === Configuring connection attempts
 * <p>
 * A client can be configured to automatically retry connecting to the server in the event that it cannot connect.
 * This is configured with {@link io.advantageous.conekt.net.NetClientOptions#setReconnectInterval(long)} and
 * {@link io.advantageous.conekt.net.NetClientOptions#setReconnectAttempts(int)}.
 * <p>
 * NOTE: Currently Vert.x will not attempt to reconnect if a connection fails, reconnect attempts and interval
 * only apply to creating initial connections.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example16}
 * ----
 * <p>
 * By default, multiple connection attempts are disabled.
 * <p>
 * [[ssl]]
 * === Configuring servers and clients to work with SSL/TLS
 * <p>
 * TCP clients and servers can be configured to use http://en.wikipedia.org/wiki/Transport_Layer_Security[Transport Layer Security]
 * - earlier versions of TLS were known as SSL.
 * <p>
 * The APIs of the servers and clients are identical whether or not SSL/TLS is used, and it's enabled by configuring
 * the {@link io.advantageous.conekt.net.NetClientOptions} or {@link io.advantageous.conekt.net.NetServerOptions} instances used
 * to create the servers or clients.
 * <p>
 * ==== Enabling SSL/TLS on the server
 * <p>
 * SSL/TLS is enabled with  {@link io.advantageous.conekt.net.NetServerOptions#setSsl(boolean) ssl}.
 * <p>
 * By default it is disabled.
 * <p>
 * ==== Specifying key/certificate for the server
 * <p>
 * SSL/TLS servers usually provide certificates to clients in order verify their identity to clients.
 * <p>
 * Certificates/keys can be configured for servers in several ways:
 * <p>
 * The first method is by specifying the location of a Java key-store which contains the certificate and private key.
 * <p>
 * Java key stores can be managed with the http://docs.oracle.com/javase/6/docs/technotes/tools/solaris/keytool.html[keytool]
 * utility which ships with the JDK.
 * <p>
 * The password for the key store should also be provided:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example17}
 * ----
 * <p>
 * Alternatively you can read the key store yourself as a buffer and provide that directly:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example18}
 * ----
 * <p>
 * Key/certificate in PKCS#12 format (http://en.wikipedia.org/wiki/PKCS_12), usually with the `.pfx`  or the `.p12`
 * extension can also be loaded in a similar fashion than JKS key stores:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example19}
 * ----
 * <p>
 * Buffer configuration is also supported:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example20}
 * ----
 * <p>
 * Another way of providing server private key and certificate separately using `.pem` files.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example21}
 * ----
 * <p>
 * Buffer configuration is also supported:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example22}
 * ----
 * <p>
 * Keep in mind that pem configuration, the private key is not crypted.
 * <p>
 * ==== Specifying trust for the server
 * <p>
 * SSL/TLS servers can use a certificate authority in order to verify the identity of the clients.
 * <p>
 * Certificate authorities can be configured for servers in several ways:
 * <p>
 * Java trust stores can be managed with the http://docs.oracle.com/javase/6/docs/technotes/tools/solaris/keytool.html[keytool]
 * utility which ships with the JDK.
 * <p>
 * The password for the trust store should also be provided:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example23}
 * ----
 * <p>
 * Alternatively you can read the trust store yourself as a buffer and provide that directly:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example24}
 * ----
 * <p>
 * Certificate authority in PKCS#12 format (http://en.wikipedia.org/wiki/PKCS_12), usually with the `.pfx`  or the `.p12`
 * extension can also be loaded in a similar fashion than JKS trust stores:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example25}
 * ----
 * <p>
 * Buffer configuration is also supported:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example26}
 * ----
 * <p>
 * Another way of providing server certificate authority using a list `.pem` files.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example27}
 * ----
 * <p>
 * Buffer configuration is also supported:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example28}
 * ----
 * <p>
 * ==== Enabling SSL/TLS on the client
 * <p>
 * Net Clients can also be easily configured to use SSL. They have the exact same API when using SSL as when using standard sockets.
 * <p>
 * To enable SSL on a NetClient the function setSSL(true) is called.
 * <p>
 * ==== Client trust configuration
 * <p>
 * If the {@link io.advantageous.conekt.net.ClientOptionsBase#setTrustAll trustALl} is set to true on the client, then the client will
 * trust all server certificates. The connection will still be encrypted but this mode is vulnerable to 'man in the middle' attacks. I.e. you can't
 * be sure who you are connecting to. Use this with caution. Default value is false.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example29}
 * ----
 * <p>
 * If {@link io.advantageous.conekt.net.ClientOptionsBase#setTrustAll trustAll} is not set then a client trust store must be
 * configured and should contain the certificates of the servers that the client trusts.
 * <p>
 * Likewise server configuration, the client trust can be configured in several ways:
 * <p>
 * The first method is by specifying the location of a Java trust-store which contains the certificate authority.
 * <p>
 * It is just a standard Java key store, the same as the key stores on the server side. The client
 * trust store location is set by using the function {@link io.advantageous.conekt.net.JksOptions#setPath path} on the
 * {@link io.advantageous.conekt.net.JksOptions jks options}. If a server presents a certificate during connection which is not
 * in the client trust store, the connection attempt will not succeed.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example30}
 * ----
 * <p>
 * Buffer configuration is also supported:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example31}
 * ----
 * <p>
 * Certificate authority in PKCS#12 format (http://en.wikipedia.org/wiki/PKCS_12), usually with the `.pfx`  or the `.p12`
 * extension can also be loaded in a similar fashion than JKS trust stores:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example32}
 * ----
 * <p>
 * Buffer configuration is also supported:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example33}
 * ----
 * <p>
 * Another way of providing server certificate authority using a list `.pem` files.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example34}
 * ----
 * <p>
 * Buffer configuration is also supported:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example35}
 * ----
 * <p>
 * ==== Specifying key/certificate for the client
 * <p>
 * If the server requires client authentication then the client must present its own certificate to the server when
 * connecting. The client can be configured in several ways:
 * <p>
 * The first method is by specifying the location of a Java key-store which contains the key and certificate.
 * Again it's just a regular Java key store. The client keystore location is set by using the function
 * {@link io.advantageous.conekt.net.JksOptions#setPath(java.lang.String) path} on the
 * {@link io.advantageous.conekt.net.JksOptions jks options}.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example36}
 * ----
 * <p>
 * Buffer configuration is also supported:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example37}
 * ----
 * <p>
 * Key/certificate in PKCS#12 format (http://en.wikipedia.org/wiki/PKCS_12), usually with the `.pfx`  or the `.p12`
 * extension can also be loaded in a similar fashion than JKS key stores:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example38}
 * ----
 * <p>
 * Buffer configuration is also supported:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example39}
 * ----
 * <p>
 * Another way of providing server private key and certificate separately using `.pem` files.
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example40}
 * ----
 * <p>
 * Buffer configuration is also supported:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example41}
 * ----
 * <p>
 * Keep in mind that pem configuration, the private key is not crypted.
 * <p>
 * ==== Revoking certificate authorities
 * <p>
 * Trust can be configured to use a certificate revocation list (CRL) for revoked certificates that should no
 * longer be trusted. The {@link io.advantageous.conekt.net.NetClientOptions#addCrlPath(java.lang.String) crlPath} configures
 * the crl list to use:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example42}
 * ----
 * <p>
 * Buffer configuration is also supported:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example43}
 * ----
 * <p>
 * ==== Configuring the Cipher suite
 * <p>
 * By default, the TLS configuration will uses the Cipher suite of the JVM running Vert.x. This Cipher suite can be
 * configured with a suite of enabled ciphers:
 * <p>
 * [source,$lang]
 * ----
 * {@link examples.NetExamples#example44}
 * ----
 * <p>
 * Cipher suite can be specified on the {@link io.advantageous.conekt.net.NetServerOptions} or {@link io.advantageous.conekt.net.NetClientOptions} configuration.
 */
package io.advantageous.conekt.net;

import io.advantageous.conekt.Handler;

