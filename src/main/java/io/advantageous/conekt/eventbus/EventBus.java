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
import io.advantageous.conekt.metrics.Measured;
import io.advantageous.conekt.streams.WriteStream;

/**
 * A Vert.x event-bus is a light-weight distributed messaging system which allows different parts of your application,
 * or different applications and services to communicate with each in a loosely coupled way.
 * <p>
 * An event-bus supports publish-subscribe messaging, point-to-point messaging and request-response messaging.
 * <p>
 * Message delivery is best-effort and messages can be lost if failure of all or part of the event bus occurs.
 * <p>
 * Please refer to the documentation for more information on the event bus.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface EventBus extends Measured {

    /**
     * Sends a message.
     * <p>
     * The message will be delivered to at most one of the handlers registered to the address.
     *
     * @param address the address to send it to
     * @param message the message, may be {@code null}
     * @return a reference to this, so the API can be used fluently
     */
    EventBus send(String address, Object message);

    /**
     * Like {@link #send(String, Object)} but specifying a {@code replyHandler} that will be called if the recipient
     * subsequently replies to the message.
     *
     * @param address      the address to send it to
     * @param message      the message, may be {@code null}
     * @param replyHandler reply handler will be called when any reply from the recipient is received, may be {@code null}
     * @return a reference to this, so the API can be used fluently
     */
    <T> EventBus send(String address, Object message, Handler<AsyncResult<Message<T>>> replyHandler);

    /**
     * Like {@link #send(String, Object)} but specifying {@code options} that can be used to configure the delivery.
     *
     * @param address the address to send it to
     * @param message the message, may be {@code null}
     * @param options delivery options
     * @return a reference to this, so the API can be used fluently
     */
    EventBus send(String address, Object message, DeliveryOptions options);

    /**
     * Like {@link #send(String, Object, DeliveryOptions)} but specifying a {@code replyHandler} that will be called if the recipient
     * subsequently replies to the message.
     *
     * @param address      the address to send it to
     * @param message      the message, may be {@code null}
     * @param options      delivery options
     * @param replyHandler reply handler will be called when any reply from the recipient is received, may be {@code null}
     * @return a reference to this, so the API can be used fluently
     */
    <T> EventBus send(String address, Object message, DeliveryOptions options, Handler<AsyncResult<Message<T>>> replyHandler);

    /**
     * Publish a message.<p>
     * The message will be delivered to all handlers registered to the address.
     *
     * @param address the address to publish it to
     * @param message the message, may be {@code null}
     * @return a reference to this, so the API can be used fluently
     */
    EventBus publish(String address, Object message);

    /**
     * Like {@link #publish(String, Object)} but specifying {@code options} that can be used to configure the delivery.
     *
     * @param address the address to publish it to
     * @param message the message, may be {@code null}
     * @param options the delivery options
     * @return a reference to this, so the API can be used fluently
     */
    EventBus publish(String address, Object message, DeliveryOptions options);

    /**
     * Create a message consumer against the specified address.
     * <p>
     * The returned consumer is not yet registered
     * at the address, registration will be effective when {@link MessageConsumer#handler(Handler)}
     * is called.
     *
     * @param address the address that it will register it at
     * @return the event bus message consumer
     */
    <T> MessageConsumer<T> consumer(String address);

    /**
     * Create a consumer and register it against the specified address.
     *
     * @param address the address that will register it at
     * @param handler the handler that will process the received messages
     * @return the event bus message consumer
     */
    <T> MessageConsumer<T> consumer(String address, Handler<Message<T>> handler);

    /**
     * Like {@link #consumer(String)} but the address won't be propagated across the cluster.
     *
     * @param address the address to register it at
     * @return the event bus message consumer
     */
    <T> MessageConsumer<T> localConsumer(String address);

    /**
     * Like {@link #consumer(String, Handler)} but the address won't be propagated across the cluster.
     *
     * @param address the address that will register it at
     * @param handler the handler that will process the received messages
     * @return the event bus message consumer
     */
    <T> MessageConsumer<T> localConsumer(String address, Handler<Message<T>> handler);

    /**
     * Create a message sender against the specified address.
     * <p>
     * The returned sender will invoke the {@link #send(String, Object)}
     * method when the stream {@link WriteStream#write(Object)} method is called with the sender
     * address and the provided data.
     *
     * @param address the address to send it to
     * @return The sender
     */
    <T> MessageProducer<T> sender(String address);

    /**
     * Like {@link #sender(String)} but specifying delivery options that will be used for configuring the delivery of
     * the message.
     *
     * @param address the address to send it to
     * @param options the delivery options
     * @return The sender
     */
    <T> MessageProducer<T> sender(String address, DeliveryOptions options);

    /**
     * Create a message publisher against the specified address.
     * <p>
     * The returned publisher will invoke the {@link #publish(String, Object)}
     * method when the stream {@link WriteStream#write(Object)} method is called with the publisher
     * address and the provided data.
     *
     * @param address The address to publish it to
     * @return The publisher
     */
    <T> MessageProducer<T> publisher(String address);

    /**
     * Like {@link #publisher(String)} but specifying delivery options that will be used for configuring the delivery of
     * the message.
     *
     * @param address the address to publish it to
     * @param options the delivery options
     * @return The publisher
     */
    <T> MessageProducer<T> publisher(String address, DeliveryOptions options);

    /**
     * Register a message codec.
     * <p>
     * You can register a message codec if you want to send any non standard message across the event bus.
     * E.g. you might want to send POJOs directly across the event bus.
     * <p>
     * To use a message codec for a send, you should specify it in the delivery options.
     *
     * @param codec the message codec to register
     * @return a reference to this, so the API can be used fluently
     */
    EventBus registerCodec(MessageCodec codec);

    /**
     * Unregister a message codec.
     * <p>
     *
     * @param name the name of the codec
     * @return a reference to this, so the API can be used fluently
     */
    EventBus unregisterCodec(String name);

    /**
     * Register a default message codec.
     * <p>
     * You can register a message codec if you want to send any non standard message across the event bus.
     * E.g. you might want to send POJOs directly across the event bus.
     * <p>
     * Default message codecs will be used to serialise any messages of the specified type on the event bus without
     * the codec having to be specified in the delivery options.
     *
     * @param clazz the class for which to use this codec
     * @param codec the message codec to register
     * @return a reference to this, so the API can be used fluently
     */
    <T> EventBus registerDefaultCodec(Class<T> clazz, MessageCodec<T, ?> codec);

    /**
     * Unregister a default message codec.
     * <p>
     *
     * @param clazz the class for which the codec was registered
     * @return a reference to this, so the API can be used fluently
     */
    EventBus unregisterDefaultCodec(Class clazz);

    /**
     * Start the event bus. This would not normally be called in user code
     *
     * @param completionHandler handler will be called when event bus is started
     */
    void start(Handler<AsyncResult<Void>> completionHandler);

    /**
     * Close the event bus and release any resources held. This would not normally be called in user code
     *
     * @param completionHandler may be {@code null}
     */
    void close(Handler<AsyncResult<Void>> completionHandler);

    /**
     * Add an interceptor that will be called whenever a message is sent from Vert.x
     *
     * @param interceptor the interceptor
     * @return a reference to this, so the API can be used fluently
     */
    EventBus addInterceptor(Handler<SendContext> interceptor);

    /**
     * Remove an interceptor
     *
     * @param interceptor the interceptor
     * @return a reference to this, so the API can be used fluently
     */
    EventBus removeInterceptor(Handler<SendContext> interceptor);

}

