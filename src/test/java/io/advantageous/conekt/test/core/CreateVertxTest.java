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

import io.advantageous.conekt.Vertx;
import io.advantageous.conekt.VertxOptions;
import org.junit.Test;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class CreateVertxTest extends AsyncTestBase {

    @Test
    public void testCreateSimpleVertx() {
        Vertx vertx = Vertx.vertx();
        assertNotNull(vertx);
    }

    @Test
    public void testCreateVertxWithOptions() {
        VertxOptions options = new VertxOptions();
        Vertx vertx = Vertx.vertx(options);
        assertNotNull(vertx);
    }

    @Test
    public void testFailCreateClusteredVertxSynchronously() {
        VertxOptions options = new VertxOptions();
        options.setClustered(true);
        try {
            Vertx.vertx(options);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testCreateClusteredVertxAsync() {
        VertxOptions options = new VertxOptions();
        options.setClustered(true);
        Vertx.clusteredVertx(options, ar -> {
            assertTrue(ar.succeeded());
            assertNotNull(ar.result());
            Vertx v = ar.result();
            v.close(ar2 -> {
                assertTrue(ar2.succeeded());
                testComplete();
            });
        });
        await();
    }

    /*
    If the user doesn't explicitly set clustered to true, it should still create a clustered Vert.x
     */
    @Test
    public void testCreateClusteredVertxAsyncDontSetClustered() {
        VertxOptions options = new VertxOptions();
        Vertx.clusteredVertx(options, ar -> {
            assertTrue(ar.succeeded());
            assertNotNull(ar.result());
            assertTrue(options.isClustered());
            Vertx v = ar.result();
            v.close(ar2 -> {
                assertTrue(ar2.succeeded());
                testComplete();
            });
        });
        await();
    }

}
