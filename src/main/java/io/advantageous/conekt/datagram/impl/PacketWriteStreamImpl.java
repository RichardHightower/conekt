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

package io.advantageous.conekt.datagram.impl;

import io.advantageous.conekt.AsyncResult;
import io.advantageous.conekt.Handler;
import io.advantageous.conekt.buffer.Buffer;
import io.advantageous.conekt.datagram.DatagramSocket;
import io.advantageous.conekt.datagram.PacketWritestream;

/**
 * A write stream for packets.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
class PacketWriteStreamImpl implements PacketWritestream, Handler<AsyncResult<DatagramSocket>> {

    private final int port;
    private final String host;
    private DatagramSocketImpl datagramSocket;
    private Handler<Throwable> exceptionHandler;

    PacketWriteStreamImpl(DatagramSocketImpl datagramSocket, int port, String host) {
        this.datagramSocket = datagramSocket;
        this.port = port;
        this.host = host;
    }

    @Override
    public void handle(AsyncResult<DatagramSocket> event) {
        if (event.failed() && exceptionHandler != null) {
            exceptionHandler.handle(event.cause());
        }
    }

    @Override
    public PacketWritestream exceptionHandler(Handler<Throwable> handler) {
        exceptionHandler = handler;
        return this;
    }

    @Override
    public PacketWritestream write(Buffer data) {
        datagramSocket.send(data, port, host, this);
        return this;
    }

    @Override
    public PacketWritestream setWriteQueueMaxSize(int maxSize) {
        return this;
    }

    @Override
    public boolean writeQueueFull() {
        return false;
    }

    @Override
    public PacketWritestream drainHandler(Handler<Void> handler) {
        return this;
    }

    @Override
    public void end() {
    }
}
