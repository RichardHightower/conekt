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
package io.advantageous.conekt;


/*
 * Vert.x hates Java checked exceptions and doesn't want to pollute it's API with them.
 * <p>
 * This is a general purpose exception class that is often thrown from Vert.x APIs if things go wrong.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 *
 */
public class VertxException extends RuntimeException {

    /**
     * Create an instance given a message
     *
     * @param message the message
     */
    public VertxException(String message) {
        super(message);
    }

    /**
     * Create an instance given a message and a cause
     *
     * @param message the message
     * @param cause   the cause
     */
    public VertxException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create an instance given a cause
     *
     * @param cause the cause
     */
    public VertxException(Throwable cause) {
        super(cause);
    }
}
