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


import io.advantageous.conekt.Conekt;
import io.advantageous.conekt.Handler;
import io.advantageous.conekt.http.impl.HttpServerImpl;
import io.advantageous.conekt.net.impl.NetServerImpl;
import io.advantageous.conekt.net.impl.ServerID;
import io.advantageous.conekt.spi.metrics.ConektMetrics;
import io.netty.channel.EventLoopGroup;
import io.advantageous.conekt.AsyncResult;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * This interface provides services for vert.x core internal use only
 * It is not part of the public API and should not be used by
 * developers creating vert.x applications
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface ConektInternal extends Conekt {

    @Override
    ContextImpl getOrCreateContext();

    EventLoopGroup getEventLoopGroup();

    EventLoopGroup getAcceptorEventLoopGroup();

    ExecutorService getWorkerPool();

    Map<ServerID, HttpServerImpl> sharedHttpServers();

    Map<ServerID, NetServerImpl> sharedNetServers();

    ConektMetrics metricsSPI();

    /**
     * Get the current context
     *
     * @return the context
     */
    ContextImpl getContext();

    /**
     * @return event loop context
     */
    EventLoopContext createEventLoopContext(String deploymentID, ClassLoader tccl);

    /**
     * @return worker loop context
     */
    ContextImpl createWorkerContext(boolean multiThreaded, String deploymentID, ClassLoader tccl);


    Deployment getDeployment(String deploymentID);

    File resolveFile(String fileName);

    <T> void executeBlockingInternal(Action<T> action, Handler<AsyncResult<T>> resultHandler);


}
