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
package io.advantageous.conekt.dns.impl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

/**
 * A DNS response packet which is sent to a client after a server receives a
 * query.
 */
public class DnsResponse extends DnsMessage<DnsResponseHeader> implements ByteBufHolder {

    private final ByteBuf rawPacket;
    private final int originalIndex;

    public DnsResponse(ByteBuf rawPacket) {
        this.rawPacket = rawPacket;
        this.originalIndex = rawPacket.readerIndex();
    }

    /**
     * Returns the non-decoded DNS response packet.
     */
    @Override
    public ByteBuf content() {
        return rawPacket;
    }

    @Override
    public int refCnt() {
        return rawPacket.refCnt();
    }

    @Override
    public boolean release() {
        return rawPacket.release();
    }

    /**
     * Returns a deep copy of this DNS response.
     */
    @Override
    public DnsResponse copy() {
        return DnsResponseDecoder.decodeResponse(rawPacket.copy(), rawPacket.alloc());
    }

    /**
     * Returns a duplicate of this DNS response.
     */
    @Override
    public ByteBufHolder duplicate() {
        return DnsResponseDecoder.decodeResponse(rawPacket.duplicate(), rawPacket.alloc());
    }

    @Override
    public DnsResponse retain() {
        rawPacket.retain();
        return this;
    }

    @Override
    public DnsResponse retain(int increment) {
        rawPacket.retain(increment);
        return this;
    }

    @Override
    public boolean release(int decrement) {
        return rawPacket.release(decrement);
    }

    /**
     * Returns the original index at which the DNS response packet starts for the {@link io.netty.buffer.ByteBuf}
     * stored in this {@link io.netty.buffer.ByteBufHolder}.
     */
    public int originalIndex() {
        return originalIndex;
    }

}
