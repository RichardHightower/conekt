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
package io.advantageous.conekt.http.impl;

import io.advantageous.conekt.Conekt;
import io.advantageous.conekt.http.HttpServerFileUpload;
import io.advantageous.conekt.streams.Pump;
import io.advantageous.conekt.Handler;
import io.advantageous.conekt.buffer.Buffer;
import io.advantageous.conekt.file.AsyncFile;
import io.advantageous.conekt.file.OpenOptions;

import java.nio.charset.Charset;

/**
 * This class is optimised for performance when used on the same event loop that is was passed to the handler with.
 * However it can be used safely from other threads.
 * <p>
 * The internal state is protected using the synchronized keyword. If always used on the same event loop, then
 * we benefit from biased locking which makes the overhead of synchronized near zero.
 *
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
class HttpServerFileUploadImpl implements HttpServerFileUpload {

    private final HttpServerRequestImpl req;
    private final Conekt conekt;
    private final String name;
    private final String filename;
    private final String contentType;
    private final String contentTransferEncoding;
    private final Charset charset;

    private Handler<Buffer> dataHandler;
    private Handler<Void> endHandler;
    private AsyncFile file;
    private Handler<Throwable> exceptionHandler;

    private long size;
    private boolean paused;
    private Buffer pauseBuff;
    private boolean complete;
    private boolean lazyCalculateSize;

    HttpServerFileUploadImpl(Conekt conekt, HttpServerRequestImpl req, String name, String filename, String contentType,
                             String contentTransferEncoding,
                             Charset charset, long size) {
        this.conekt = conekt;
        this.req = req;
        this.name = name;
        this.filename = filename;
        this.contentType = contentType;
        this.contentTransferEncoding = contentTransferEncoding;
        this.charset = charset;
        this.size = size;
        if (size == 0) {
            lazyCalculateSize = true;
        }
    }

    @Override
    public String filename() {
        return filename;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String contentType() {
        return contentType;
    }

    @Override
    public String contentTransferEncoding() {
        return contentTransferEncoding;
    }

    @Override
    public String charset() {
        return charset.toString();
    }

    @Override
    public synchronized long size() {
        return size;
    }

    @Override
    public synchronized HttpServerFileUpload handler(Handler<Buffer> handler) {
        this.dataHandler = handler;
        return this;
    }

    @Override
    public synchronized HttpServerFileUpload pause() {
        req.pause();
        paused = true;
        return this;
    }

    @Override
    public synchronized HttpServerFileUpload resume() {
        if (paused) {
            req.resume();
            paused = false;
            if (pauseBuff != null) {
                doReceiveData(pauseBuff);
                pauseBuff = null;
            }
            if (complete) {
                handleComplete();
            }
        }
        return this;
    }

    @Override
    public synchronized HttpServerFileUpload exceptionHandler(Handler<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    @Override
    public synchronized HttpServerFileUpload endHandler(Handler<Void> handler) {
        this.endHandler = handler;
        return this;
    }

    @Override
    public HttpServerFileUpload streamToFileSystem(String filename) {
        pause();
        conekt.fileSystem().open(filename, new OpenOptions(), ar -> {
            if (ar.succeeded()) {
                file = ar.result();
                Pump p = Pump.pump(HttpServerFileUploadImpl.this, ar.result());
                p.start();
                resume();
            } else {
                notifyExceptionHandler(ar.cause());
            }
        });
        return this;
    }

    @Override
    public synchronized boolean isSizeAvailable() {
        return !lazyCalculateSize;
    }

    synchronized void receiveData(Buffer data) {
        if (data.length() != 0) {
            // Can sometimes receive zero length packets from Netty!
            if (lazyCalculateSize) {
                size += data.length();
            }
            doReceiveData(data);
        }
    }

    synchronized void doReceiveData(Buffer data) {
        if (!paused) {
            if (dataHandler != null) {
                dataHandler.handle(data);
            }
        } else {
            if (pauseBuff == null) {
                pauseBuff = Buffer.buffer();
            }
            pauseBuff.appendBuffer(data);
        }
    }

    synchronized void complete() {
        if (paused) {
            complete = true;
        } else {
            handleComplete();
        }
    }

    private void handleComplete() {
        lazyCalculateSize = false;
        if (file == null) {
            notifyEndHandler();
        } else {
            file.close(ar -> {
                if (ar.failed()) {
                    notifyExceptionHandler(ar.cause());
                }
                notifyEndHandler();
            });
        }
    }

    private void notifyEndHandler() {
        if (endHandler != null) {
            endHandler.handle(null);
        }
    }

    private void notifyExceptionHandler(Throwable cause) {
        if (exceptionHandler != null) {
            exceptionHandler.handle(cause);
        }
    }
}