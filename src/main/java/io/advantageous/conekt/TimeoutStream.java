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

package io.advantageous.conekt;

import io.advantageous.conekt.streams.ReadStream;

/**
 * A timeout stream is triggered by a timer, the {@link Handler} will be call when the timer is fired,
 * it can be once or several times depending on the nature of the timer related to this stream. The
 * {@link ReadStream#endHandler(Handler)} will be called after the timer handler has been called.
 * <p>
 * Pausing the timer inhibits the timer shots until the stream is resumed. Setting a null handler callback cancels
 * the timer.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public interface TimeoutStream extends ReadStream<Long> {

    @Override
    TimeoutStream exceptionHandler(Handler<Throwable> handler);

    @Override
    TimeoutStream handler(Handler<Long> handler);

    @Override
    TimeoutStream pause();

    @Override
    TimeoutStream resume();

    @Override
    TimeoutStream endHandler(Handler<Void> endHandler);

    /**
     * Cancels the timeout. Note this has the same effect as calling {@link #handler(Handler)} with a null
     * argument.
     */
    void cancel();

}
