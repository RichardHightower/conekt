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

package io.advantageous.conekt.file;

import io.advantageous.conekt.AsyncResult;
import io.advantageous.conekt.streams.Pump;
import io.advantageous.conekt.streams.ReadStream;
import io.advantageous.conekt.streams.WriteStream;
import io.advantageous.conekt.Handler;
import io.advantageous.conekt.buffer.Buffer;
import io.advantageous.conekt.http.HttpClientRequest;

/**
 * Represents a file on the file-system which can be read from, or written to asynchronously.
 * <p>
 * This class also implements {@link ReadStream} and
 * {@link WriteStream}. This allows the data to be pumped to and from
 * other streams, e.g. an {@link HttpClientRequest} instance,
 * using the {@link Pump} class
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface AsyncFile extends ReadStream<Buffer>, WriteStream<Buffer> {

    @Override
    AsyncFile handler(Handler<Buffer> handler);

    @Override
    AsyncFile pause();

    @Override
    AsyncFile resume();

    @Override
    AsyncFile endHandler(Handler<Void> endHandler);

    @Override
    AsyncFile write(Buffer data);

    @Override
    AsyncFile setWriteQueueMaxSize(int maxSize);

    @Override
    AsyncFile drainHandler(Handler<Void> handler);

    @Override
    AsyncFile exceptionHandler(Handler<Throwable> handler);

    /**
     * Close the file, see {@link #close()}.
     */
    @Override
    void end();

    /**
     * Close the file. The actual close happens asynchronously.
     */
    void close();

    /**
     * Close the file. The actual close happens asynchronously.
     * The handler will be called when the close is complete, or an error occurs.
     *
     * @param handler the handler
     */
    void close(Handler<AsyncResult<Void>> handler);

    /**
     * Write a {@link Buffer} to the file at position {@code position} in the file, asynchronously.
     * <p>
     * If {@code position} lies outside of the current size
     * of the file, the file will be enlarged to encompass it.
     * <p>
     * When multiple writes are invoked on the same file
     * there are no guarantees as to order in which those writes actually occur
     * <p>
     * The handler will be called when the write is complete, or if an error occurs.
     *
     * @param buffer   the buffer to write
     * @param position the position in the file to write it at
     * @param handler  the handler to call when the write is complete
     * @return a reference to this, so the API can be used fluently
     */
    AsyncFile write(Buffer buffer, long position, Handler<AsyncResult<Void>> handler);

    /**
     * Reads {@code length} bytes of data from the file at position {@code position} in the file, asynchronously.
     * <p>
     * The read data will be written into the specified {@code Buffer buffer} at position {@code offset}.
     * <p>
     * If data is read past the end of the file then zero bytes will be read.<p>
     * When multiple reads are invoked on the same file there are no guarantees as to order in which those reads actually occur.
     * <p>
     * The handler will be called when the close is complete, or if an error occurs.
     *
     * @param buffer   the buffer to read into
     * @param offset   the offset into the buffer where the data will be read
     * @param position the position in the file where to start reading
     * @param length   the number of bytes to read
     * @param handler  the handler to call when the write is complete
     * @return a reference to this, so the API can be used fluently
     */
    AsyncFile read(Buffer buffer, int offset, long position, int length, Handler<AsyncResult<Buffer>> handler);

    /**
     * Flush any writes made to this file to underlying persistent storage.
     * <p>
     * If the file was opened with {@code flush} set to {@code true} then calling this method will have no effect.
     * <p>
     * The actual flush will happen asynchronously.
     *
     * @return a reference to this, so the API can be used fluently
     */
    AsyncFile flush();

    /**
     * Same as {@link #flush} but the handler will be called when the flush is complete or if an error occurs
     */
    AsyncFile flush(Handler<AsyncResult<Void>> handler);

    /**
     * Sets the position from which data will be read from when using the file as a {@link ReadStream}.
     *
     * @param readPos the position in the file
     * @return a reference to this, so the API can be used fluently
     */
    AsyncFile setReadPos(long readPos);

    /**
     * Sets the position from which data will be written when using the file as a {@link WriteStream}.
     *
     * @param writePos the position in the file
     * @return a reference to this, so the API can be used fluently
     */
    AsyncFile setWritePos(long writePos);

    /**
     * Sets the buffer size that will be used to read the data from the file. Changing this value will impact how much
     * the data will be read at a time from the file system.
     *
     * @param readBufferSize the buffer size
     * @return a reference to this, so the API can be used fluently
     */
    AsyncFile setReadBufferSize(int readBufferSize);
}
