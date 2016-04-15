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
package io.advantageous.conekt.dns.impl;

import io.advantageous.conekt.dns.SrvRecord;
import io.advantageous.conekt.dns.impl.netty.decoder.record.ServiceRecord;


/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
final class SrcRecordImpl implements SrvRecord, Comparable<SrvRecord> {
    private final ServiceRecord record;

    SrcRecordImpl(ServiceRecord record) {
        this.record = record;
    }

    @Override
    public int priority() {
        return record.priority();
    }

    @Override
    public int weight() {
        return record.weight();
    }

    @Override
    public int port() {
        return record.port();
    }

    @Override
    public String name() {
        return record.name();
    }

    @Override
    public String protocol() {
        return record.protocol();
    }

    @Override
    public String service() {
        return record.service();
    }

    @Override
    public String target() {
        return record.target();
    }

    @Override
    public int compareTo(SrvRecord o) {
        return Integer.valueOf(priority()).compareTo(o.priority());
    }
}
