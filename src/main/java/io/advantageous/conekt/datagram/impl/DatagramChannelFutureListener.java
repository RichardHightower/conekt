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
package io.advantageous.conekt.datagram.impl;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.advantageous.conekt.AsyncResult;
import io.advantageous.conekt.Future;
import io.advantageous.conekt.Handler;
import io.advantageous.conekt.impl.ContextImpl;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
final class DatagramChannelFutureListener<T> implements ChannelFutureListener {
    private final Handler<AsyncResult<T>> handler;
    private final T result;
    private final ContextImpl context;

    DatagramChannelFutureListener(T result, Handler<AsyncResult<T>> handler, ContextImpl context) {
        this.handler = handler;
        this.result = result;
        this.context = context;
    }

    @Override
    public void operationComplete(final ChannelFuture future) throws Exception {
        context.executeFromIO(() -> notifyHandler(future));
    }

    private void notifyHandler(ChannelFuture future) {
        if (future.isSuccess()) {
            handler.handle(Future.succeededFuture(result));
        } else {
            handler.handle(Future.failedFuture(future.cause()));
        }
    }
}
