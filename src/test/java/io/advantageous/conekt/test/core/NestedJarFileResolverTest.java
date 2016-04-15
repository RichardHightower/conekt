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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class NestedJarFileResolverTest extends FileResolverTestBase {

    private ClassLoader prevCL;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // This is inside the jar webroot3.jar
        webRoot = "webroot4";

        prevCL = Thread.currentThread().getContextClassLoader();
        URL webroot3URL = prevCL.getResource("webroot4.jar");
        ClassLoader loader = new ClassLoader(prevCL = Thread.currentThread().getContextClassLoader()) {
            @Override
            public URL getResource(String name) {
                try {
                    if (name.startsWith("lib/")) {
                        return new URL("jar:" + webroot3URL + "!/" + name);
                    } else if (name.startsWith("webroot4")) {
                        return new URL("jar:" + webroot3URL + "!/lib/nested.jar!/" + name.substring(7));
                    }
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return super.getResource(name);
            }
        };
        Thread.currentThread().setContextClassLoader(loader);
    }

    @Override
    public void after() throws Exception {
        if (prevCL != null) {
            Thread.currentThread().setContextClassLoader(prevCL);
        }
        super.after();
    }
}
