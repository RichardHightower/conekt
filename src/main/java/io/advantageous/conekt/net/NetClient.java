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

package io.advantageous.conekt.net;

import io.advantageous.conekt.Handler;
import io.advantageous.conekt.metrics.Measured;
import io.advantageous.conekt.AsyncResult;

/**
 * A TCP client.
 * <p>
 * Multiple connections to different servers can be made using the same instance.
 * <p>
 * This client supports a configurable number of connection attempts and a configurable
 * delay between attempts.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface NetClient extends Measured {

    /**
     * Open a connection to a server at the specific {@code port} and {@code host}.
     * <p>
     * {@code host} can be a valid host name or IP address. The connect is done asynchronously and on success, a
     * {@link NetSocket} instance is supplied via the {@code connectHandler} instance
     *
     * @param port the port
     * @param host the host
     * @return a reference to this, so the API can be used fluently
     */
    NetClient connect(int port, String host, Handler<AsyncResult<NetSocket>> connectHandler);

    /**
     * Close the client.
     * <p>
     * Any sockets which have not been closed manually will be closed here. The close is asynchronous and may not
     * complete until some time after the method has returned.
     */
    void close();

}
