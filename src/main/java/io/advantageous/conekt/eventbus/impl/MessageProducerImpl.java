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

import io.advantageous.conekt.AsyncResult;
import io.advantageous.conekt.Conekt;
import io.advantageous.conekt.eventbus.*;
import io.advantageous.conekt.Handler;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class MessageProducerImpl<T> implements MessageProducer<T> {

    public static final String CREDIT_ADDRESS_HEADER_NAME = "__vertx.credit";

    private final Conekt conekt;
    private final EventBus bus;
    private final boolean send;
    private final String address;
    private final Queue<T> pending = new ArrayDeque<>();
    private final MessageConsumer<Integer> creditConsumer;
    private DeliveryOptions options;
    private int credits = DEFAULT_WRITE_QUEUE_MAX_SIZE;
    private Handler<Void> drainHandler;

    public MessageProducerImpl(Conekt conekt, String address, boolean send, DeliveryOptions options) {
        this.conekt = conekt;
        this.bus = conekt.eventBus();
        this.address = address;
        this.send = send;
        this.options = options;
        if (send) {
            String creditAddress = UUID.randomUUID().toString() + "-credit";
            creditConsumer = bus.consumer(creditAddress, msg -> {
                doReceiveCredit(msg.body());
            });
            options.addHeader(CREDIT_ADDRESS_HEADER_NAME, creditAddress);
        } else {
            creditConsumer = null;
        }
    }

    @Override
    public synchronized MessageProducer<T> deliveryOptions(DeliveryOptions options) {
        this.options = options;
        return this;
    }

    @Override
    public MessageProducer<T> send(T message) {
        doSend(message, null);
        return this;
    }

    @Override
    public <R> MessageProducer<T> send(T message, Handler<AsyncResult<Message<R>>> replyHandler) {
        doSend(message, replyHandler);
        return this;
    }

    @Override
    public MessageProducer<T> exceptionHandler(Handler<Throwable> handler) {
        return this;
    }

    @Override
    public synchronized MessageProducer<T> setWriteQueueMaxSize(int maxSize) {
        this.credits = maxSize;
        return this;
    }

    @Override
    public synchronized MessageProducer<T> write(T data) {
        if (send) {
            doSend(data, null);
        } else {
            bus.publish(address, data, options);
        }
        return this;
    }

    @Override
    public boolean writeQueueFull() {
        return pending.size() >= 0;
    }

    @Override
    public synchronized MessageProducer<T> drainHandler(Handler<Void> handler) {
        this.drainHandler = handler;
        return this;
    }

    @Override
    public String address() {
        return address;
    }

    @Override
    public void end() {
        close();
    }

    @Override
    public void close() {
        if (creditConsumer != null) {
            creditConsumer.unregister();
        }
    }

    // Just in case user forget to call close()
    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    private synchronized <R> void doSend(T data, Handler<AsyncResult<Message<R>>> replyHandler) {
        if (credits > 0) {
            credits--;
            if (replyHandler == null) {
                bus.send(address, data, options);
            } else {
                bus.send(address, data, options, replyHandler);
            }
        } else {
            pending.add(data);
        }
    }

    private synchronized void doReceiveCredit(int credit) {
        credits += credit;
        while (credits > 0) {
            T data = pending.poll();
            if (data == null) {
                break;
            } else {
                credits--;
                bus.send(address, data, options);
            }
        }
        final Handler<Void> theDrainHandler = drainHandler;
        if (theDrainHandler != null && pending.isEmpty()) {
            this.drainHandler = null;
            conekt.runOnContext(v -> theDrainHandler.handle(null));
        }
    }

}
