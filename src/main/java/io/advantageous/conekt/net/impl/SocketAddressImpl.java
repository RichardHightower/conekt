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

package io.advantageous.conekt.net.impl;

import io.advantageous.conekt.impl.Arguments;
import io.advantageous.conekt.net.SocketAddress;

import java.util.Objects;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class SocketAddressImpl implements SocketAddress {

    private final String hostAddress;
    private final int port;

    public SocketAddressImpl(int port, String host) {
        Objects.requireNonNull(host, "no null host accepted");
        Arguments.require(!host.isEmpty(), "no empty host accepted");
        Arguments.requireInRange(port, 0, 65535, "port p must be in range 0 <= p <= 65535");
        this.port = port;
        this.hostAddress = host;
    }

    public String host() {
        return hostAddress;
    }

    public int port() {
        return port;
    }

    public String toString() {
        return hostAddress + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SocketAddressImpl that = (SocketAddressImpl) o;

        if (port != that.port) return false;
        if (hostAddress != null ? !hostAddress.equals(that.hostAddress) : that.hostAddress != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hostAddress != null ? hostAddress.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }
}
