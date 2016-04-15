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

package io.advantageous.conekt.streams.impl;

import io.advantageous.conekt.file.AsyncFile;
import io.advantageous.conekt.http.HttpServerRequest;
import io.advantageous.conekt.net.NetSocket;
import io.advantageous.conekt.streams.Pump;
import io.advantageous.conekt.streams.ReadStream;
import io.advantageous.conekt.streams.WriteStream;
import io.advantageous.conekt.Handler;
import io.advantageous.conekt.http.WebSocket;

/**
 * Pumps data from a {@link ReadStream} to a {@link WriteStream} and performs flow control where necessary to
 * prevent the write stream buffer from getting overfull.<p>
 * Instances of this class read bytes from a {@link ReadStream} and write them to a {@link WriteStream}. If data
 * can be read faster than it can be written this could result in the write queue of the {@link WriteStream} growing
 * without bound, eventually causing it to exhaust all available RAM.<p>
 * To prevent this, after each write, instances of this class check whether the write queue of the {@link
 * WriteStream} is full, and if so, the {@link ReadStream} is paused, and a {@code drainHandler} is set on the
 * {@link WriteStream}. When the {@link WriteStream} has processed half of its backlog, the {@code drainHandler} will be
 * called, which results in the pump resuming the {@link ReadStream}.<p>
 * This class can be used to pump from any {@link ReadStream} to any {@link WriteStream},
 * e.g. from an {@link HttpServerRequest} to an {@link AsyncFile},
 * or from {@link NetSocket} to a {@link WebSocket}.<p>
 * <p>
 * Instances of this class are not thread-safe.<p>
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class PumpImpl<T> implements Pump {

    private final ReadStream<T> readStream;
    private final WriteStream<T> writeStream;
    private final Handler<T> dataHandler;
    private final Handler<Void> drainHandler;
    private int pumped;

    /**
     * Create a new {@code Pump} with the given {@code ReadStream} and {@code WriteStream}. Set the write queue max size
     * of the write stream to {@code maxWriteQueueSize}
     */
    PumpImpl(ReadStream<T> rs, WriteStream<T> ws, int maxWriteQueueSize) {
        this(rs, ws);
        this.writeStream.setWriteQueueMaxSize(maxWriteQueueSize);
    }

    PumpImpl(ReadStream<T> rs, WriteStream<T> ws) {
        this.readStream = rs;
        this.writeStream = ws;
        drainHandler = v -> readStream.resume();
        dataHandler = data -> {
            writeStream.write(data);
            incPumped();
            if (writeStream.writeQueueFull()) {
                readStream.pause();
                writeStream.drainHandler(drainHandler);
            }
        };
    }

    /**
     * Set the write queue max size to {@code maxSize}
     */
    @Override
    public PumpImpl setWriteQueueMaxSize(int maxSize) {
        writeStream.setWriteQueueMaxSize(maxSize);
        return this;
    }

    /**
     * Start the Pump. The Pump can be started and stopped multiple times.
     */
    @Override
    public PumpImpl start() {
        readStream.handler(dataHandler);
        return this;
    }

    /**
     * Stop the Pump. The Pump can be started and stopped multiple times.
     */
    @Override
    public PumpImpl stop() {
        writeStream.drainHandler(null);
        readStream.handler(null);
        return this;
    }

    /**
     * Return the total number of elements pumped by this pump.
     */
    @Override
    public synchronized int numberPumped() {
        return pumped;
    }

    // Note we synchronize as numberPumped can be called from a different thread however incPumped will always
    // be called from the same thread so we benefit from bias locked optimisation which should give a very low
    // overhead
    private synchronized void incPumped() {
        pumped++;
    }


}
