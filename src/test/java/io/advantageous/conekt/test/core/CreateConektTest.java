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

import io.advantageous.conekt.Conekt;
import io.advantageous.conekt.ConektOptions;
import org.junit.Test;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class CreateConektTest extends AsyncTestBase {

    @Test
    public void testCreateSimpleVertx() {
        Conekt conekt = Conekt.vertx();
        assertNotNull(conekt);
    }

    @Test
    public void testCreateVertxWithOptions() {
        ConektOptions options = new ConektOptions();
        Conekt conekt = Conekt.vertx(options);
        assertNotNull(conekt);
    }

    @Test
    public void testFailCreateClusteredVertxSynchronously() {
        ConektOptions options = new ConektOptions();
        options.setClustered(true);
        try {
            Conekt.vertx(options);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testCreateClusteredVertxAsync() {
        ConektOptions options = new ConektOptions();
        options.setClustered(true);
        Conekt.clusteredVertx(options, ar -> {
            assertTrue(ar.succeeded());
            assertNotNull(ar.result());
            Conekt v = ar.result();
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
        ConektOptions options = new ConektOptions();
        Conekt.clusteredVertx(options, ar -> {
            assertTrue(ar.succeeded());
            assertNotNull(ar.result());
            assertTrue(options.isClustered());
            Conekt v = ar.result();
            v.close(ar2 -> {
                assertTrue(ar2.succeeded());
                testComplete();
            });
        });
        await();
    }

}
