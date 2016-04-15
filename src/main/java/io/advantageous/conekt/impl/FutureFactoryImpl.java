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

import io.advantageous.conekt.Future;
import io.advantageous.conekt.spi.FutureFactory;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class FutureFactoryImpl implements FutureFactory {

    @Override
    public <T> Future<T> future() {
        return new FutureImpl<>();
    }

    // TODO - for completed futures with null values we could maybe reuse a static instance to save allocation
    @Override
    public <T> Future<T> completedFuture() {
        return new FutureImpl<>((T) null);
    }

    @Override
    public <T> Future<T> completedFuture(T result) {
        return new FutureImpl<>(result);
    }

    @Override
    public <T> Future<T> completedFuture(Throwable t) {
        return new FutureImpl<>(t);
    }

    @Override
    public <T> Future<T> completedFuture(String failureMessage, boolean failed) {
        return new FutureImpl<>(failureMessage, true);
    }
}
