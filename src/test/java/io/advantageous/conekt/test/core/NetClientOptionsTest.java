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

import io.advantageous.conekt.net.NetClientOptions;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * test that the default object of NetClientOptions equals to when creating
 * a NetClientOptions object from an empty Json object. Previously the json constructor
 * used null for the enabledCipherSuite property which breaks the addEnabledCipherSuite
 * operation.
 */

/**
 * @author <a href="http://oss.lehmann.cx/">Alexander Lehmann</a>
 */
public class NetClientOptionsTest {

    @Test
    public final void testEquals() {
        NetClientOptions options1 = new NetClientOptions();
        NetClientOptions options2 = new NetClientOptions();
        assertEquals(options1, options2);
    }

    @Test
    public final void testAdd() {
        NetClientOptions options = new NetClientOptions();
        options.addEnabledCipherSuite("XXX");
    }

}
