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

import io.advantageous.conekt.*;
//
import org.junit.Test;

/**
 * please note that this test class does not assert anything about the log output (this would require a kind of log
 * mock), it just runs the different methods to get coverage
 *
 * @author <a href="http://oss.lehmann.cx/">Alexander Lehmann</a>
 */
public class BlockedThreadCheckerTest extends VertxTestBase {

    @Test
    public void testBlockCheckDefault() throws Exception {
        Verticle verticle = new AbstractVerticle() {
            @Override
            public void start() throws InterruptedException {
                Thread.sleep(6000);
                testComplete();
            }
        };
        vertx.deployVerticle(verticle);
        await();
    }

    @Test
    public void testBlockCheckExceptionTimeLimit() throws Exception {
        Verticle verticle = new AbstractVerticle() {
            @Override
            public void start() throws InterruptedException {
                Thread.sleep(2000);
                testComplete();
            }
        };
        // set warning threshold to 1s and the exception threshold as well
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setMaxEventLoopExecuteTime(1000000000);
        vertxOptions.setWarningExceptionTime(1000000000);
        Vertx newVertx = Vertx.vertx(vertxOptions);
        newVertx.deployVerticle(verticle);
        await();
    }

    @Test
    public void testBlockCheckWorker() throws Exception {
        Verticle verticle = new AbstractVerticle() {
            @Override
            public void start() throws InterruptedException {
                Thread.sleep(2000);
                testComplete();
            }
        };
        // set warning threshold to 1s and the exception threshold as well
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setMaxWorkerExecuteTime(1000000000);
        vertxOptions.setWarningExceptionTime(1000000000);
        Vertx newVertx = Vertx.vertx(vertxOptions);
        DeploymentOptions depolymentOptions = new DeploymentOptions();
        depolymentOptions.setWorker(true);
        newVertx.deployVerticle(verticle, depolymentOptions);
        await();
    }
}
