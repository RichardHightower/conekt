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

import io.advantageous.conekt.spi.PumpFactory;
import io.advantageous.conekt.ServiceHelper;
import io.advantageous.conekt.file.AsyncFile;
import io.advantageous.conekt.http.HttpServerRequest;
import io.advantageous.conekt.http.WebSocket;
import io.advantageous.conekt.net.NetSocket;

/**
 * Pumps data from a {@link ReadStream} to a {@link WriteStream} and performs flow control where necessary to
 * prevent the write stream buffer from getting overfull.
 * <p>
 * Instances of this class read items from a {@link ReadStream} and write them to a {@link WriteStream}. If data
 * can be read faster than it can be written this could result in the write queue of the {@link WriteStream} growing
 * without bound, eventually causing it to exhaust all available RAM.
 * <p>
 * To prevent this, after each write, instances of this class check whether the write queue of the {@link
 * WriteStream} is full, and if so, the {@link ReadStream} is paused, and a {@code drainHandler} is set on the
 * {@link WriteStream}.
 * <p>
 * When the {@link WriteStream} has processed half of its backlog, the {@code drainHandler} will be
 * called, which results in the pump resuming the {@link ReadStream}.
 * <p>
 * This class can be used to pump from any {@link ReadStream} to any {@link WriteStream},
 * e.g. from an {@link HttpServerRequest} to an {@link AsyncFile},
 * or from {@link NetSocket} to a {@link WebSocket}.
 * <p>
 * Please see the documentation for more information.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface Pump {

    PumpFactory factory = ServiceHelper.loadFactory(PumpFactory.class);

    /**
     * Create a new {@code Pump} with the given {@code ReadStream} and {@code WriteStream}
     *
     * @param rs the read stream
     * @param ws the write stream
     * @return the pump
     */
    static <T> Pump pump(ReadStream<T> rs, WriteStream<T> ws) {
        return factory.pump(rs, ws);
    }

    /**
     * Create a new {@code Pump} with the given {@code ReadStream} and {@code WriteStream} and
     * {@code writeQueueMaxSize}
     *
     * @param rs                the read stream
     * @param ws                the write stream
     * @param writeQueueMaxSize the max size of the write queue
     * @return the pump
     */
    static <T> Pump pump(ReadStream<T> rs, WriteStream<T> ws, int writeQueueMaxSize) {
        return factory.pump(rs, ws, writeQueueMaxSize);
    }

    /**
     * Set the write queue max size to {@code maxSize}
     *
     * @param maxSize the max size
     * @return a reference to this, so the API can be used fluently
     */
    Pump setWriteQueueMaxSize(int maxSize);

    /**
     * Start the Pump. The Pump can be started and stopped multiple times.
     *
     * @return a reference to this, so the API can be used fluently
     */
    Pump start();

    /**
     * Stop the Pump. The Pump can be started and stopped multiple times.
     *
     * @return a reference to this, so the API can be used fluently
     */
    Pump stop();

    /**
     * Return the total number of items pumped by this pump.
     */
    int numberPumped();


}
