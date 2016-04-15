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

package io.advantageous.conekt.eventbus;

import io.advantageous.conekt.AsyncResult;
import io.advantageous.conekt.Handler;
import io.advantageous.conekt.streams.WriteStream;

/**
 * Represents a stream of message that can be written to.
 * <p>
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public interface MessageProducer<T> extends WriteStream<T> {

    int DEFAULT_WRITE_QUEUE_MAX_SIZE = 1000;

    /**
     * Synonym for {@link #write(Object)}.
     *
     * @param message the message to send
     * @return reference to this for fluency
     */
    MessageProducer<T> send(T message);

    <R> MessageProducer<T> send(T message, Handler<AsyncResult<Message<R>>> replyHandler);

    @Override
    MessageProducer<T> exceptionHandler(Handler<Throwable> handler);

    @Override
    MessageProducer<T> write(T data);

    @Override
    MessageProducer<T> setWriteQueueMaxSize(int maxSize);

    @Override
    MessageProducer<T> drainHandler(Handler<Void> handler);

    /**
     * Update the delivery options of this producer.
     *
     * @param options the new options
     * @return this producer object
     */
    MessageProducer<T> deliveryOptions(DeliveryOptions options);

    /**
     * @return The address to which the producer produces messages.
     */
    String address();

    /**
     * Closes the producer, calls {@link #close()}
     */
    @Override
    void end();

    /**
     * Closes the producer, this method should be called when the message producer is not used anymore.
     */
    void close();
}
