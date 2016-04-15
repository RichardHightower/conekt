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

import io.advantageous.conekt.streams.ReadStream;
import io.advantageous.conekt.Handler;

/**
 * A stream for {@link HttpClient} WebSocket connection.
 * <p>
 * When the connection attempt is successful, the stream handler is called back with the {@link WebSocket}
 * argument, immediately followed by a call to the end handler. When the connection attempt fails, the exception handler is invoked.
 * <p>
 * The connection occurs when the {@link #handler} method is called with a non null handler, the other handlers should be
 * set before setting the handler.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public interface WebSocketStream extends ReadStream<WebSocket> {

    @Override
    WebSocketStream exceptionHandler(Handler<Throwable> handler);

    @Override
    WebSocketStream handler(Handler<WebSocket> handler);

    @Override
    WebSocketStream pause();

    @Override
    WebSocketStream resume();

    @Override
    WebSocketStream endHandler(Handler<Void> endHandler);

}
