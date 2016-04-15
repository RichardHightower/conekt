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

import io.advantageous.conekt.Verticle;
import io.advantageous.conekt.Vertx;
import io.advantageous.conekt.datagram.DatagramSocket;
import io.advantageous.conekt.datagram.DatagramSocketOptions;
import io.advantageous.conekt.eventbus.EventBus;
import io.advantageous.conekt.http.HttpClient;
import io.advantageous.conekt.http.HttpClientOptions;
import io.advantageous.conekt.http.HttpServer;
import io.advantageous.conekt.http.HttpServerOptions;
import io.advantageous.conekt.net.*;
import io.advantageous.conekt.spi.metrics.*;

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
