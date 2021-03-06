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

package io.advantageous.conekt.net.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.advantageous.conekt.buffer.Buffer;
import io.advantageous.conekt.impl.ContextImpl;

import java.util.Map;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class ConektNetHandler extends ConektHandler<NetSocketImpl> {

    private final Map<Channel, NetSocketImpl> connectionMap;

    public ConektNetHandler(Map<Channel, NetSocketImpl> connectionMap) {
        this.connectionMap = connectionMap;
    }

    @Override
    protected NetSocketImpl getConnection(Channel channel) {
        return connectionMap.get(channel);
    }

    @Override
    protected NetSocketImpl removeConnection(Channel channel) {
        return connectionMap.remove(channel);
    }


    @Override
    protected void channelRead(NetSocketImpl sock, ContextImpl context, ChannelHandlerContext chctx, Object msg) throws Exception {
        if (sock != null) {
            ByteBuf buf = (ByteBuf) msg;
            context.executeFromIO(() -> sock.handleDataReceived(Buffer.buffer(buf)));
        } else {
            // just discard
        }
    }

    @Override
    protected Object safeObject(Object msg, ByteBufAllocator allocator) throws Exception {
        if (msg instanceof ByteBuf) {
            return safeBuffer((ByteBuf) msg, allocator);
        }
        return msg;
    }
}
