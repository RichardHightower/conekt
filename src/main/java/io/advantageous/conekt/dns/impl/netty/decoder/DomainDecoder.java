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
package io.advantageous.conekt.dns.impl.netty.decoder;

import io.advantageous.conekt.dns.impl.netty.DnsResource;
import io.advantageous.conekt.dns.impl.netty.DnsResponse;
import io.advantageous.conekt.dns.impl.netty.DnsResponseDecoder;

/**
 * Decodes any record that simply returns a domain name, such as NS (name
 * server) and CNAME (canonical name) resource records.
 */
public class DomainDecoder implements RecordDecoder<String> {

    /**
     * Returns the decoded domain name for a resource record.
     *
     * @param response the {@link DnsResponse} received that contained the resource
     *                 record being decoded
     * @param resource the {@link DnsResource} being decoded
     */
    @Override
    public String decode(DnsResponse response, DnsResource resource) {
        return DnsResponseDecoder.getName(response.content(), resource.contentIndex());
    }

}
