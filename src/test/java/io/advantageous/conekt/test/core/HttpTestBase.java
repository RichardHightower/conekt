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

package io.advantageous.conekt.test.core;

import io.advantageous.conekt.Handler;
import io.advantageous.conekt.http.HttpClient;
import io.advantageous.conekt.http.HttpServer;
import io.advantageous.conekt.http.HttpServerOptions;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class HttpTestBase extends VertxTestBase {

    public static final String DEFAULT_HTTP_HOST = "localhost";
    public static final int DEFAULT_HTTP_PORT = 8080;
    public static final String DEFAULT_TEST_URI = "some-uri";
    private static final Handler noOp = e -> {
    };
    protected HttpServer server;
    protected HttpClient client;

    public void setUp() throws Exception {
        super.setUp();
        server = conekt.createHttpServer(new HttpServerOptions().setPort(DEFAULT_HTTP_PORT).setHost(DEFAULT_HTTP_HOST));
    }

    protected void tearDown() throws Exception {
        if (client != null) {
            client.close();
        }
        if (server != null) {
            CountDownLatch latch = new CountDownLatch(1);
            server.close((asyncResult) -> {
                assertTrue(asyncResult.succeeded());
                latch.countDown();
            });
            awaitLatch(latch);
        }
        super.tearDown();
    }

    @SuppressWarnings("unchecked")
    protected <E> Handler<E> noOpHandler() {
        return noOp;
    }
}
