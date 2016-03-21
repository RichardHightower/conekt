/*
 * Copyright (c) 2011-2013 The original author or authors
 *  ------------------------------------------------------
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *      The Eclipse Public License is available at
 *      http://www.eclipse.org/legal/epl-v10.html
 *
 *      The Apache License v2.0 is available at
 *      http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.smallvertx.test.fakemetrics;

import io.smallvertx.core.Verticle;
import io.smallvertx.core.Vertx;
import io.smallvertx.core.datagram.DatagramSocket;
import io.smallvertx.core.datagram.DatagramSocketOptions;
import io.smallvertx.core.eventbus.EventBus;
import io.smallvertx.core.http.HttpClient;
import io.smallvertx.core.http.HttpClientOptions;
import io.smallvertx.core.http.HttpServer;
import io.smallvertx.core.http.HttpServerOptions;
import io.smallvertx.core.net.*;
import io.smallvertx.core.spi.metrics.*;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FakeVertxMetrics extends FakeMetricsBase implements VertxMetrics {

    public static AtomicReference<EventBus> eventBus = new AtomicReference<>();

    public FakeVertxMetrics(Vertx vertx) {
        super(vertx);
    }

    @Override
    public boolean isMetricsEnabled() {
        return true;
    }

    public void verticleDeployed(Verticle verticle) {
    }

    public void verticleUndeployed(Verticle verticle) {
    }

    public void timerCreated(long id) {
    }

    public void timerEnded(long id, boolean cancelled) {
    }

    public EventBusMetrics createMetrics(EventBus eventBus) {
        return new FakeEventBusMetrics(eventBus);
    }

    public HttpServerMetrics<?, ?, ?> createMetrics(HttpServer server, SocketAddress localAddress, HttpServerOptions options) {
        return new FakeHttpServerMetrics(server);
    }

    public HttpClientMetrics<?, ?, ?> createMetrics(HttpClient client, HttpClientOptions options) {
        return new FakeHttpClientMetrics(client);
    }

    public TCPMetrics<?> createMetrics(NetServer server, SocketAddress localAddress, NetServerOptions options) {
        return new TCPMetrics<Object>() {

            public Object connected(SocketAddress remoteAddress, String remoteName) {
                return null;
            }

            public void disconnected(Object socketMetric, SocketAddress remoteAddress) {
            }

            public void bytesRead(Object socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
            }

            public void bytesWritten(Object socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
            }

            public void exceptionOccurred(Object socketMetric, SocketAddress remoteAddress, Throwable t) {
            }

            public boolean isEnabled() {
                return false;
            }

            public void close() {
            }
        };
    }

    public TCPMetrics<?> createMetrics(NetClient client, NetClientOptions options) {
        return new TCPMetrics<Object>() {

            public Object connected(SocketAddress remoteAddress, String remoteName) {
                return null;
            }

            public void disconnected(Object socketMetric, SocketAddress remoteAddress) {
            }

            public void bytesRead(Object socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
            }

            public void bytesWritten(Object socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
            }

            public void exceptionOccurred(Object socketMetric, SocketAddress remoteAddress, Throwable t) {
            }

            public boolean isEnabled() {
                return false;
            }

            public void close() {
            }
        };
    }

    public DatagramSocketMetrics createMetrics(DatagramSocket socket, DatagramSocketOptions options) {
        return new FakeDatagramSocketMetrics(socket);
    }

    public boolean isEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void eventBusInitialized(EventBus bus) {
        this.eventBus.set(bus);
    }
}
