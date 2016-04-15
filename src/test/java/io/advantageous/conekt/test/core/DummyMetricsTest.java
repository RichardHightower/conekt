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

package io.advantageous.conekt.test.core;

import io.advantageous.conekt.ConektOptions;
import io.advantageous.conekt.datagram.DatagramSocket;
import io.advantageous.conekt.datagram.DatagramSocketOptions;
import io.advantageous.conekt.http.HttpClient;
import io.advantageous.conekt.http.HttpClientOptions;
import io.advantageous.conekt.http.HttpServer;
import io.advantageous.conekt.http.HttpServerOptions;
import io.advantageous.conekt.metrics.MetricsOptions;
import io.advantageous.conekt.net.NetClient;
import io.advantageous.conekt.net.NetClientOptions;
import io.advantageous.conekt.net.NetServer;
import io.advantageous.conekt.net.NetServerOptions;
import org.junit.Test;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class DummyMetricsTest extends VertxTestBase {

    @Override
    protected ConektOptions getOptions() {
        return new ConektOptions().setMetricsOptions(new MetricsOptions().setEnabled(false)); // Just to be explicit
    }

    @Test
    public void testDummyVertxMetrics() {
        assertFalse(conekt.isMetricsEnabled());
    }

    @Test
    public void testDummyNetServerMetrics() {
        NetServer server = conekt.createNetServer(new NetServerOptions());
        assertFalse(server.isMetricsEnabled());
    }

    @Test
    public void testDummyNetClientMetrics() {
        NetClient client = conekt.createNetClient(new NetClientOptions());
        assertFalse(client.isMetricsEnabled());
    }

    @Test
    public void testDummyHttpServerMetrics() {
        HttpServer server = conekt.createHttpServer(new HttpServerOptions());
        assertFalse(server.isMetricsEnabled());
    }

    @Test
    public void testDummyHttpClientMetrics() {
        HttpClient client = conekt.createHttpClient(new HttpClientOptions());
        assertFalse(client.isMetricsEnabled());
    }

    @Test
    public void testDummyEventBusMetrics() {
        assertFalse(conekt.eventBus().isMetricsEnabled());
    }

    @Test
    public void testDummyDatagramSocketMetrics() {
        DatagramSocket sock = conekt.createDatagramSocket(new DatagramSocketOptions());
        assertFalse(sock.isMetricsEnabled());
    }
}
