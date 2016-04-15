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

package io.advantageous.conekt.test.fakemetrics;

import io.advantageous.conekt.metrics.Measured;
import io.advantageous.conekt.net.SocketAddress;
import io.advantageous.conekt.spi.metrics.DatagramSocketMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FakeDatagramSocketMetrics extends FakeMetricsBase implements DatagramSocketMetrics {

    private final List<PacketMetric> reads = Collections.synchronizedList(new ArrayList<>());
    private final List<PacketMetric> writes = Collections.synchronizedList(new ArrayList<>());
    private volatile SocketAddress localAddress;

    public FakeDatagramSocketMetrics(Measured measured) {
        super(measured);
    }

    public SocketAddress getLocalAddress() {
        return localAddress;
    }

    public List<PacketMetric> getReads() {
        return reads;
    }

    public List<PacketMetric> getWrites() {
        return writes;
    }

    @Override
    public void listening(SocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    @Override
    public void bytesRead(Void socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        reads.add(new PacketMetric(remoteAddress, numberOfBytes));
    }

    @Override
    public void bytesWritten(Void socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        writes.add(new PacketMetric(remoteAddress, numberOfBytes));
    }

    @Override
    public void exceptionOccurred(Void socketMetric, SocketAddress remoteAddress, Throwable t) {

    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void close() {
    }
}
