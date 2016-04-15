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

import io.advantageous.conekt.http.HttpClientOptions;
import io.advantageous.conekt.http.HttpMethod;
import io.advantageous.conekt.http.HttpServerOptions;
import org.junit.Test;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public class HttpCompressionTest extends HttpTestBase {

    public void setUp() throws Exception {
        super.setUp();
        client = conekt.createHttpClient(new HttpClientOptions().setTryUseCompression(true));
        server = conekt.createHttpServer(new HttpServerOptions().setPort(DEFAULT_HTTP_PORT).setCompressionSupported(true));
    }

    @Test
    public void testDefaultRequestHeaders() {
        server.requestHandler(req -> {
            assertEquals(2, req.headers().size());
            assertEquals("localhost:" + DEFAULT_HTTP_PORT, req.headers().get("host"));
            assertNotNull(req.headers().get("Accept-Encoding"));
            req.response().end();
        });

        server.listen(onSuccess(server -> {
            client.request(HttpMethod.GET, DEFAULT_HTTP_PORT, DEFAULT_HTTP_HOST, "some-uri", resp -> testComplete()).end();
        }));

        await();
    }
}
