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

import io.advantageous.conekt.file.FileProps;

import java.nio.file.attribute.BasicFileAttributes;


public class FilePropsImpl implements FileProps {

    private final long creationTime;
    private final long lastAccessTime;
    private final long lastModifiedTime;
    private final boolean isDirectory;
    private final boolean isOther;
    private final boolean isRegularFile;
    private final boolean isSymbolicLink;
    private final long size;

    public FilePropsImpl(BasicFileAttributes attrs) {
        creationTime = attrs.creationTime().toMillis();
        lastModifiedTime = attrs.lastModifiedTime().toMillis();
        lastAccessTime = attrs.lastAccessTime().toMillis();
        isDirectory = attrs.isDirectory();
        isOther = attrs.isOther();
        isRegularFile = attrs.isRegularFile();
        isSymbolicLink = attrs.isSymbolicLink();
        size = attrs.size();
    }

    @Override
    public long creationTime() {
        return creationTime;
    }

    @Override
    public long lastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public long lastModifiedTime() {
        return lastModifiedTime;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public boolean isOther() {
        return isOther;
    }

    @Override
    public boolean isRegularFile() {
        return isRegularFile;
    }

    @Override
    public boolean isSymbolicLink() {
        return isSymbolicLink;
    }

    @Override
    public long size() {
        return size;
    }
}
