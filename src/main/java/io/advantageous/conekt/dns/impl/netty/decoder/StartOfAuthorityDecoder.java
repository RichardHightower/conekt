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
import io.advantageous.conekt.dns.impl.netty.decoder.record.StartOfAuthorityRecord;
import io.netty.buffer.ByteBuf;

/**
 * Decodes SOA (start of authority) resource records.
 */
public class StartOfAuthorityDecoder implements RecordDecoder<StartOfAuthorityRecord> {

    /**
     * Returns a decoded SOA (start of authority) resource record, stored as an
     * instance of {@link StartOfAuthorityRecord}.
     *
     * @param response the DNS response that contains the resource record being
     *                 decoded
     * @param resource the resource record being decoded
     */
    @Override
    public StartOfAuthorityRecord decode(DnsResponse response, DnsResource resource) {
        ByteBuf packet = response.content().readerIndex(resource.contentIndex());
        String mName = DnsResponseDecoder.readName(packet);
        String rName = DnsResponseDecoder.readName(packet);
        long serial = packet.readUnsignedInt();
        int refresh = packet.readInt();
        int retry = packet.readInt();
        int expire = packet.readInt();
        long minimum = packet.readUnsignedInt();
        return new StartOfAuthorityRecord(mName, rName, serial, refresh, retry, expire, minimum);
    }

}
