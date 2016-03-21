/*
 * Copyright (c) 2011-2013 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.smallvertx.core.http;

import io.smallvertx.core.Handler;
import io.smallvertx.core.MultiMap;
import io.smallvertx.core.buffer.Buffer;
import io.smallvertx.core.net.NetSocket;
import io.smallvertx.core.streams.Pump;
import io.smallvertx.core.streams.ReadStream;

import java.util.List;

/**
 * Represents a client-side HTTP response.
 * <p>
 * Vert.x provides you with one of these via the handler that was provided when creating the {@link HttpClientRequest}
 * or that was set on the {@link HttpClientRequest} instance.
 * <p>
 * It implements {@link ReadStream} so it can be used with
 * {@link Pump} to pump data with flow control.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface HttpClientResponse extends ReadStream<Buffer> {

    @Override
    HttpClientResponse resume();

    @Override
    HttpClientResponse exceptionHandler(Handler<Throwable> handler);

    @Override
    HttpClientResponse handler(Handler<Buffer> handler);

    @Override
    HttpClientResponse pause();

    @Override
    HttpClientResponse endHandler(Handler<Void> endHandler);

    /**
     * @return the status code of the response
     */
    int statusCode();

    /**
     * @return the status message of the response
     */
    String statusMessage();

    /**
     * @return the headers
     */
         MultiMap headers();

    /**
     * Return the first header value with the specified name
     *
     * @param headerName the header name
     * @return the header value
     */
     String getHeader(String headerName);

    /**
     * Return the first header value with the specified name
     *
     * @param headerName the header name
     * @return the header value
     */
    String getHeader(CharSequence headerName);

    /**
     * Return the first trailer value with the specified name
     *
     * @param trailerName the trailer name
     * @return the trailer value
     */
     String getTrailer(String trailerName);

    /**
     * @return the trailers
     */
         MultiMap trailers();

    /**
     * @return the Set-Cookie headers (including trailers)
     */
         List<String> cookies();

    /**
     * Convenience method for receiving the entire request body in one piece.
     * <p>
     * This saves you having to manually set a dataHandler and an endHandler and append the chunks of the body until
     * the whole body received. Don't use this if your request body is large - you could potentially run out of RAM.
     *
     * @param bodyHandler This handler will be called after all the body has been received
     */
    HttpClientResponse bodyHandler(Handler<Buffer> bodyHandler);

    /**
     * Get a net socket for the underlying connection of this request.
     * <p>
     * USE THIS WITH CAUTION! Writing to the socket directly if you don't know what you're doing can easily break the HTTP protocol
     * <p>
     * One valid use-case for calling this is to receive the {@link NetSocket} after a HTTP CONNECT was issued to the
     * remote peer and it responded with a status code of 200.
     *
     * @return the net socket
     */
         NetSocket netSocket();

}
