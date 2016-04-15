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

import io.netty.util.concurrent.FastThreadLocalThread;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
final class VertxThread extends FastThreadLocalThread {

    private final boolean worker;
    private long execStart;
    private ContextImpl context;

    public VertxThread(Runnable target, String name, boolean worker) {
        super(target, name);
        this.worker = worker;
    }

    ContextImpl getContext() {
        return context;
    }

    void setContext(ContextImpl context) {
        this.context = context;
    }

    public final void executeStart() {
        execStart = System.nanoTime();
    }

    public final void executeEnd() {
        execStart = 0;
    }

    public long startTime() {
        return execStart;
    }

    public boolean isWorker() {
        return worker;
    }

}
