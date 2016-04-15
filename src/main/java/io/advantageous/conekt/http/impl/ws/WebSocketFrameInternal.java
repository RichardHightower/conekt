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

package io.advantageous.conekt.http.impl.ws;

import io.advantageous.conekt.http.WebSocketFrame;
import io.advantageous.conekt.http.impl.FrameType;
import io.netty.buffer.ByteBuf;

/**
 * A Web Socket frame that represents either text or binary data.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 */
public interface WebSocketFrameInternal extends WebSocketFrame {
    /**
     * Returns the content of this frame as-is, with no UTF-8 decoding.
     */
    ByteBuf getBinaryData();

    /**
     * Sets the type and the content of this frame.
     *
     * @param binaryData the content of the frame.  If <tt>(type &amp; 0x80 == 0)</tt>,
     *                   it must be encoded in UTF-8.
     * @throws IllegalArgumentException if If <tt>(type &amp; 0x80 == 0)</tt> and the data is not encoded
     *                                  in UTF-8
     */
    void setBinaryData(ByteBuf binaryData);

    /**
     * Set the type of the content of this frame and populate it with the given content
     *
     * @param textData the content of the frame. Must be valid UTF-8
     */
    void setTextData(String textData);

    FrameType type();
}
