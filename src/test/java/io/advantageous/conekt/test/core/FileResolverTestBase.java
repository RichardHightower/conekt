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

package io.advantageous.conekt.test.core;

import io.advantageous.conekt.Conekt;
import io.advantageous.conekt.impl.ConektInternal;
import io.advantageous.conekt.impl.FileResolver;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public abstract class FileResolverTestBase extends VertxTestBase {

    protected FileResolver resolver;

    protected String webRoot;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        resolver = new FileResolver(conekt);
    }

    @Override
    protected void tearDown() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        resolver.close(onSuccess(res -> {
            latch.countDown();
        }));
        awaitLatch(latch);
        super.tearDown();
    }

    @Test
    public void testResolveNotExistFile() {
        File file = resolver.resolveFile("doesnotexist.txt");
        assertFalse(file.exists());
        assertEquals("doesnotexist.txt", file.getPath());
    }

    @Test
    public void testResolveNotExistDirectory() {
        File file = resolver.resolveFile("somedir");
        assertFalse(file.exists());
        assertEquals("somedir", file.getPath());
    }

    @Test
    public void testResolveNotExistFileInDirectory() {
        File file = resolver.resolveFile("somedir/doesnotexist.txt");
        assertFalse(file.exists());
        assertEquals("somedir" + File.separator + "doesnotexist.txt", file.getPath());
    }





    @Test
    public void testResolveSubDirectoryFromClasspath() throws Exception {
        for (int i = 0; i < 2; i++) {
            File file = resolver.resolveFile(webRoot + "/subdir");
            assertTrue(file.exists());
            assertTrue(file.getPath().startsWith(".conekt" + File.separator + "file-cache-"));
            assertTrue(file.isDirectory());
        }
    }




    @Test
    public void testDeleteCacheDir() throws Exception {
        Conekt conekt2 = Conekt.vertx();
        FileResolver resolver2 = new FileResolver(conekt2);
        File file = resolver2.resolveFile(webRoot + "/somefile.html");
        assertTrue(file.exists());
        File cacheDir = file.getParentFile().getParentFile();
        assertTrue(cacheDir.exists());
        resolver2.close(onSuccess(res -> {
            assertFalse(cacheDir.exists());
            conekt2.close(res2 -> {
                testComplete();
            });
        }));
        await();
    }

    @Test
    public void testCacheDirDeletedOnVertxClose() {
        ConektInternal vertx2 = (ConektInternal) Conekt.vertx();
        File file = vertx2.resolveFile(webRoot + "/somefile.html");
        assertTrue(file.exists());
        File cacheDir = file.getParentFile().getParentFile();
        assertTrue(cacheDir.exists());
        vertx2.close(onSuccess(v -> {
            assertFalse(cacheDir.exists());
            testComplete();
        }));
        await();
    }


    @Test
    public void testFileSystemReadDirectory() {
        assertTrue(conekt.fileSystem().existsBlocking("webroot"));
        assertTrue(conekt.fileSystem().propsBlocking("webroot").isDirectory());
    }


    private String readFile(File file) {
        return conekt.fileSystem().readFileBlocking(file.getAbsolutePath()).toString();
    }

}
