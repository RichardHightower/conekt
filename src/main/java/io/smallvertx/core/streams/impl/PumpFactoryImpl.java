/*
 * Copyright 2014 Red Hat, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.smallvertx.core.streams.impl;

import io.smallvertx.core.spi.PumpFactory;
import io.smallvertx.core.streams.Pump;
import io.smallvertx.core.streams.ReadStream;
import io.smallvertx.core.streams.WriteStream;

import java.util.Objects;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class PumpFactoryImpl implements PumpFactory {
    @Override
    public <T> Pump pump(ReadStream<T> rs, WriteStream<T> ws) {
        Objects.requireNonNull(rs);
        Objects.requireNonNull(ws);
        return new PumpImpl<>(rs, ws);
    }

    @Override
    public <T> Pump pump(ReadStream<T> rs, WriteStream<T> ws, int writeQueueMaxSize) {
        Objects.requireNonNull(rs);
        Objects.requireNonNull(ws);
        return new PumpImpl<>(rs, ws, writeQueueMaxSize);
    }
}
