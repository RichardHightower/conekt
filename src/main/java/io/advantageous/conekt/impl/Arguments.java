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

package io.advantageous.conekt.impl;

/**
 * Helper class to perform extended checks on arguments analogous to
 * {@link java.util.Objects#requireNonNull(Object, String)}.
 */
public class Arguments {

    /**
     * Checks that the specified condition is fulfilled and throws a customized {@link IllegalArgumentException} if it
     * is {@code false}.
     *
     * @param condition condition which must be fulfilled
     * @param message   detail message to be used in the event that a {@code
     *                  IllegalArgumentException} is thrown
     */
    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that the specified number is within the specified minimum and maximum range (inclusively) and throws a
     * customized {@link IllegalArgumentException} if not.
     *
     * @param number  value to check
     * @param min     minimum allowed value
     * @param max     maximum allowed value
     * @param message detail message to be used in the event that a {@code
     *                IllegalArgumentException} is thrown
     */
    public static void requireInRange(int number, int min, int max, String message) {
        if (number < min || number > max) {
            throw new IllegalArgumentException(message);
        }
    }

}
