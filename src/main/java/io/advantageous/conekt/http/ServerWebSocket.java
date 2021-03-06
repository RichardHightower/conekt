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

package io.advantageous.conekt.http;

import io.advantageous.conekt.Handler;
import io.advantageous.conekt.MultiMap;
import io.advantageous.conekt.buffer.Buffer;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

/**
 * Represents a server side WebSocket.
 * <p>
 * Instances of this class are passed into a {@link HttpServer#websocketHandler} or provided
 * when a WebSocket handshake is manually {@link HttpServerRequest#upgrade}ed.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface ServerWebSocket extends WebSocketBase {

    @Override
    ServerWebSocket exceptionHandler(Handler<Throwable> handler);

    @Override
    ServerWebSocket handler(Handler<Buffer> handler);

    @Override
    ServerWebSocket pause();

    @Override
    ServerWebSocket resume();

    @Override
    ServerWebSocket endHandler(Handler<Void> endHandler);

    @Override
    ServerWebSocket write(Buffer data);

    @Override
    ServerWebSocket setWriteQueueMaxSize(int maxSize);

    @Override
    ServerWebSocket drainHandler(Handler<Void> handler);

    @Override
    ServerWebSocket writeFrame(WebSocketFrame frame);

    @Override
    ServerWebSocket writeFinalTextFrame(String text);

    @Override
    ServerWebSocket writeFinalBinaryFrame(Buffer data);

    @Override
    ServerWebSocket writeBinaryMessage(Buffer data);

    @Override
    ServerWebSocket closeHandler(Handler<Void> handler);

    @Override
    ServerWebSocket frameHandler(Handler<WebSocketFrame> handler);

    /*
     * @return the WebSocket handshake URI. This is a relative URI.
     */
    String uri();

    /**
     * @return the WebSocket handshake path.
     */
    String path();

    /**
     * @return the WebSocket handshake query string.
     */
    String query();

    /**
     * @return the headers in the WebSocket handshake
     */
    MultiMap headers();

    /**
     * Reject the WebSocket.
     * <p>
     * Calling this method from the websocket handler when it is first passed to you gives you the opportunity to reject
     * the websocket, which will cause the websocket handshake to fail by returning
     * a 404 response code.
     * <p>
     * You might use this method, if for example you only want to accept WebSockets with a particular path.
     */
    void reject();

    /**
     * @return an array of the peer certificates. Returns null if connection is
     * not SSL.
     * @throws javax.net.ssl.SSLPeerUnverifiedException SSL peer's identity has not been verified.
     */
    X509Certificate[] peerCertificateChain() throws SSLPeerUnverifiedException;
}
