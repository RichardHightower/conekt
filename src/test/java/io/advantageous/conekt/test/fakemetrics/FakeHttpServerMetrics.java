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

import io.advantageous.conekt.http.*;
import io.advantageous.conekt.net.SocketAddress;
import io.advantageous.conekt.spi.metrics.HttpServerMetrics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FakeHttpServerMetrics extends FakeMetricsBase implements HttpServerMetrics<HttpServerMetric, WebSocketMetric, SocketMetric> {

    public final HttpServer server;
    private final ConcurrentMap<WebSocketBase, WebSocketMetric> webSockets = new ConcurrentHashMap<>();
    private final ConcurrentMap<HttpServerRequest, HttpServerMetric> requests = new ConcurrentHashMap<>();

    public FakeHttpServerMetrics(HttpServer server) {
        super(server);
        this.server = server;
    }

    public WebSocketMetric getMetric(ServerWebSocket ws) {
        return webSockets.get(ws);
    }

    public HttpServerMetric getMetric(HttpServerRequest requests) {
        return this.requests.get(requests);
    }

    @Override
    public HttpServerMetric requestBegin(SocketMetric socketMetric, HttpServerRequest request) {
        HttpServerMetric metric = new HttpServerMetric(request, socketMetric);
        requests.put(request, metric);
        return metric;
    }

    @Override
    public void responseEnd(HttpServerMetric requestMetric, HttpServerResponse response) {
        requests.remove(requestMetric.request);
    }

    @Override
    public WebSocketMetric upgrade(HttpServerMetric requestMetric, ServerWebSocket serverWebSocket) {
        requests.remove(requestMetric.request);
        WebSocketMetric metric = new WebSocketMetric(requestMetric.socket, serverWebSocket);
        webSockets.put(serverWebSocket, metric);
        return metric;
    }

    @Override
    public WebSocketMetric connected(SocketMetric socketMetric, ServerWebSocket serverWebSocket) {
        WebSocketMetric metric = new WebSocketMetric(socketMetric, serverWebSocket);
        webSockets.put(serverWebSocket, metric);
        return metric;
    }

    @Override
    public void disconnected(WebSocketMetric serverWebSocketMetric) {
        webSockets.remove(serverWebSocketMetric.ws);
    }

    @Override
    public SocketMetric connected(SocketAddress remoteAddress, String remoteName) {
        return new SocketMetric(remoteAddress, remoteName);
    }

    @Override
    public void disconnected(SocketMetric socketMetric, SocketAddress remoteAddress) {
        socketMetric.connected.set(false);
    }

    @Override
    public void bytesRead(SocketMetric socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        socketMetric.bytesRead.addAndGet(numberOfBytes);
    }

    @Override
    public void bytesWritten(SocketMetric socketMetric, SocketAddress remoteAddress, long numberOfBytes) {
        socketMetric.bytesWritten.addAndGet(numberOfBytes);
    }

    @Override
    public void exceptionOccurred(SocketMetric socketMetric, SocketAddress remoteAddress, Throwable t) {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void close() {
    }
}
