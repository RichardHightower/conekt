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
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes TXT (text) resource records.
 */
public class TextDecoder implements RecordDecoder<List<String>> {

    /**
     * Returns a decoded TXT (text) resource record, stored as an
     * {@link java.util.ArrayList} of {@code String}s.
     *
     * @param response the DNS response that contains the resource record being
     *                 decoded
     * @param resource the resource record being decoded
     */
    @Override
    public List<String> decode(DnsResponse response, DnsResource resource) {
        List<String> list = new ArrayList<>();
        ByteBuf data = resource.content().readerIndex(response.originalIndex());
        int index = data.readerIndex();
        while (index < data.writerIndex()) {
            int len = data.getUnsignedByte(index++);
            list.add(data.toString(index, len, CharsetUtil.UTF_8));
            index += len;
        }
        return list;
    }

}
