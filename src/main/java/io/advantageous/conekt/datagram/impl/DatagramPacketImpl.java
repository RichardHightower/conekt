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

import io.advantageous.conekt.buffer.Buffer;
import io.advantageous.conekt.datagram.DatagramPacket;
import io.advantageous.conekt.net.SocketAddress;
import io.advantageous.conekt.net.impl.SocketAddressImpl;

import java.net.InetSocketAddress;


/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
final class DatagramPacketImpl implements DatagramPacket {
    private final InetSocketAddress sender;
    private final Buffer buffer;
    private SocketAddress senderAddress;

    DatagramPacketImpl(InetSocketAddress sender, Buffer buffer) {
        this.sender = sender;
        this.buffer = buffer;
    }

    @Override
    public SocketAddress sender() {
        if (senderAddress == null) {
            senderAddress = new SocketAddressImpl(sender.getPort(), sender.getAddress().getHostAddress());
        }
        return senderAddress;
    }

    @Override
    public Buffer data() {
        return buffer;
    }
}
