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

import io.advantageous.conekt.Handler;

import java.util.concurrent.Executor;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class MultiThreadedWorkerContext extends WorkerContext {

    public MultiThreadedWorkerContext(VertxInternal vertx, Executor orderedInternalExec, Executor workerExec,
                                      String deploymentID, ClassLoader tccl) {
        super(vertx, orderedInternalExec, workerExec, deploymentID, tccl);
    }

    @Override
    public void executeAsync(Handler<Void> task) {
        workerExec.execute(wrapTask(null, task, false));
    }

    @Override
    public boolean isMultiThreadedWorkerContext() {
        return true;
    }
}
