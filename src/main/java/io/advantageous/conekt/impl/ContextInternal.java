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

import io.netty.channel.EventLoop;
import io.advantageous.conekt.Context;
import io.advantageous.conekt.Conekt;

/**
 * This interface provides an api for vert.x core internal use only
 * It is not part of the public API and should not be used by
 * developers creating vert.x applications
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public interface ContextInternal extends Context {

    /**
     * Return the Netty EventLoop used by this Context. This can be used to integrate
     * a Netty Server with a Vert.x runtime, specially the Context part.
     *
     * @return the EventLoop
     */
    EventLoop nettyEventLoop();

    /**
     * Execute the context task and switch on this context if necessary, this also associates the
     * current thread with the current context so {@link Conekt#currentContext()} returns this context.<p/>
     * <p>
     * The caller thread should be the the event loop thread of this context.
     *
     * @param task the task to execute
     */
    void executeFromIO(ContextTask task);
}
