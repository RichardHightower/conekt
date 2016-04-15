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

package io.advantageous.conekt.file.impl;

import io.advantageous.conekt.impl.ContextImpl;
import io.advantageous.conekt.AsyncResult;
import io.advantageous.conekt.Handler;
import io.advantageous.conekt.file.AsyncFile;
import io.advantageous.conekt.file.OpenOptions;
import io.advantageous.conekt.impl.VertxInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author Juergen Donnerstag
 */
public class WindowsFileSystem extends FileSystemImpl {

    private static final Logger log = LoggerFactory.getLogger(WindowsFileSystem.class);

    public WindowsFileSystem(final VertxInternal vertx) {
        super(vertx);
    }

    private static void logInternal(final String perms) {
        if (perms != null && log.isDebugEnabled()) {
            log.debug("You are running on Windows and POSIX style file permissions are not supported");
        }
    }

    @Override
    protected BlockingAction<Void> chmodInternal(String path, String perms, String dirPerms,
                                                 Handler<AsyncResult<Void>> handler) {
        Objects.requireNonNull(path);
        Objects.requireNonNull(perms);
        logInternal(perms);
        logInternal(dirPerms);
        if (log.isDebugEnabled()) {
            log.debug("You are running on Windows and POSIX style file permissions are not supported!");
        }
        return new BlockingAction<Void>(handler) {
            @Override
            public Void perform() {
                return null;
            }
        };
    }

    @Override
    protected BlockingAction<Void> mkdirInternal(String path, final String perms, final boolean createParents,
                                                 Handler<AsyncResult<Void>> handler) {
        logInternal(perms);
        return super.mkdirInternal(path, null, createParents, handler);
    }

    @Override
    protected AsyncFile doOpen(String path, OpenOptions options,
                               ContextImpl context) {
        logInternal(options.getPerms());
        return new AsyncFileImpl(vertx, path, options, context);
    }

    @Override
    protected BlockingAction<Void> createFileInternal(String p, final String perms, Handler<AsyncResult<Void>> handler) {
        logInternal(perms);
        return super.createFileInternal(p, null, handler);
    }

    @Override
    protected BlockingAction<Void> chownInternal(String path, String user, String group, Handler<AsyncResult<Void>> handler) {
        if (group != null && log.isDebugEnabled()) {
            log.debug("You are running on Windows and POSIX style file ownership is not supported");
        }
        return super.chownInternal(path, user, group, handler);
    }
}
