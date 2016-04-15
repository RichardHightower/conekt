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

package io.advantageous.conekt.eventbus.impl;

import io.advantageous.conekt.Context;
import io.advantageous.conekt.spi.metrics.EventBusMetrics;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class HandlerHolder<T> {

    private final EventBusMetrics metrics;
    private final Context context;
    private final HandlerRegistration<T> handler;
    private final boolean replyHandler;
    private final boolean localOnly;
    private boolean removed;

    public HandlerHolder(EventBusMetrics metrics, HandlerRegistration<T> handler, boolean replyHandler, boolean localOnly,
                         Context context) {
        this.metrics = metrics;
        this.context = context;
        this.handler = handler;
        this.replyHandler = replyHandler;
        this.localOnly = localOnly;
    }

    // We use a synchronized block to protect removed as it can be unregistered from a different thread
    public void setRemoved() {
        boolean unregisterMetric = false;
        synchronized (this) {
            if (!removed) {
                removed = true;
                unregisterMetric = true;
            }
        }
        if (unregisterMetric) {
            metrics.handlerUnregistered(handler.getMetric());
        }
    }

    // Because of biased locks the overhead of the synchronized lock should be very low as it's almost always
    // called by the same event loop
    public synchronized boolean isRemoved() {
        return removed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandlerHolder that = (HandlerHolder) o;
        if (handler != null ? !handler.equals(that.handler) : that.handler != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return handler != null ? handler.hashCode() : 0;
    }

    public Context getContext() {
        return context;
    }

    public HandlerRegistration<T> getHandler() {
        return handler;
    }

    public boolean isReplyHandler() {
        return replyHandler;
    }

    public boolean isLocalOnly() {
        return localOnly;
    }

    ;
}
