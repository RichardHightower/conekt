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

import io.advantageous.conekt.dns.impl.netty.DnsResponse;
import io.netty.handler.codec.DecoderException;
import io.advantageous.conekt.dns.impl.netty.DnsResource;

/**
 * Used for decoding resource records.
 *
 * @param <T> the type of data to return after decoding a resource record (for
 *            example, an {@link AddressDecoder} will return a {@link io.netty.buffer.ByteBuf})
 */
public interface RecordDecoder<T> {

    /**
     * Returns a generic type {@code T} defined in a class implementing
     * {@link RecordDecoder} after decoding a resource record when given a DNS
     * response packet.
     *
     * @param response the DNS response that contains the resource record being
     *                 decoded
     * @param resource the resource record being decoded
     */
    T decode(DnsResponse response, DnsResource resource) throws DecoderException;

}
