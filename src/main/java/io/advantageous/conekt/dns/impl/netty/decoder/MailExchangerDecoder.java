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
import io.advantageous.conekt.dns.impl.netty.decoder.record.MailExchangerRecord;
import io.netty.buffer.ByteBuf;

/**
 * Decodes MX (mail exchanger) resource records.
 */
public class MailExchangerDecoder implements RecordDecoder<MailExchangerRecord> {

    /**
     * Returns a decoded MX (mail exchanger) resource record, stored as an
     * instance of {@link MailExchangerRecord}.
     *
     * @param response the {@link DnsResponse} received that contained the resource
     *                 record being decoded
     * @param resource the {@link DnsResource} being decoded
     */
    @Override
    public MailExchangerRecord decode(DnsResponse response, DnsResource resource) {
        ByteBuf packet = response.content().readerIndex(resource.contentIndex());
        int priority = packet.readShort();
        String name = DnsResponseDecoder.readName(packet);
        return new MailExchangerRecord(priority, name);
    }

}
