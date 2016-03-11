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

package io.smallvertx.core.datagram;

import io.smallvertx.core.Handler;
import io.smallvertx.core.buffer.Buffer;
import io.smallvertx.core.net.SocketAddress;
import io.smallvertx.core.streams.WriteStream;
import io.vertx.codegen.annotations.VertxGen;

/**
 * A {@link WriteStream} for sending packets to a {@link SocketAddress}.
 * The stream {@link WriteStream#exceptionHandler} is called when the write fails.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@VertxGen
public interface PacketWritestream extends WriteStream<Buffer> {

    @Override
    PacketWritestream exceptionHandler(Handler<Throwable> handler);

    @Override
    PacketWritestream write(Buffer data);

    @Override
    PacketWritestream setWriteQueueMaxSize(int maxSize);

    @Override
    PacketWritestream drainHandler(Handler<Void> handler);
}
