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
package io.advantageous.conekt.impl;

import io.advantageous.conekt.*;
import io.advantageous.conekt.spi.VertxFactory;

/**
 * @author pidster
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class VertxFactoryImpl implements VertxFactory {

    @Override
    public Vertx vertx() {
        return new VertxImpl();
    }

    @Override
    public Vertx vertx(VertxOptions options) {
        if (options.isClustered()) {
            throw new IllegalArgumentException("Please use Vertx.clusteredVertx() to create a clustered Vert.x instance");
        }
        return new VertxImpl(options);
    }

    @Override
    public void clusteredVertx(VertxOptions options, final Handler<AsyncResult<Vertx>> resultHandler) {
        // We don't require the user to set clustered to true if they use this method
        options.setClustered(true);
        new VertxImpl(options, resultHandler);
    }

    @Override
    public Context context() {
        return VertxImpl.context();
    }
}
