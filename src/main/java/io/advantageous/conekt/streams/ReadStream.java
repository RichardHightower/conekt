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

package io.advantageous.conekt.streams;

import io.advantageous.conekt.Handler;

/**
 * Represents a stream of items that can be read from.
 * <p>
 * Any class that implements this interface can be used by a {@link Pump} to pump data from it
 * to a {@link WriteStream}.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface ReadStream<T> extends StreamBase {

    /**
     * Set an exception handler on the read stream.
     *
     * @param handler the exception handler
     * @return a reference to this, so the API can be used fluently
     */
    ReadStream<T> exceptionHandler(Handler<Throwable> handler);

    /**
     * Set a data handler. As data is read, the handler will be called with the data.
     *
     * @return a reference to this, so the API can be used fluently
     */
    ReadStream<T> handler(Handler<T> handler);

    /**
     * Pause the {@code ReadSupport}. While it's paused, no data will be sent to the {@code dataHandler}
     *
     * @return a reference to this, so the API can be used fluently
     */
    ReadStream<T> pause();

    /**
     * Resume reading. If the {@code ReadSupport} has been paused, reading will recommence on it.
     *
     * @return a reference to this, so the API can be used fluently
     */
    ReadStream<T> resume();

    /**
     * Set an end handler. Once the stream has ended, and there is no more data to be read, this handler will be called.
     *
     * @return a reference to this, so the API can be used fluently
     */
    ReadStream<T> endHandler(Handler<Void> endHandler);

}
