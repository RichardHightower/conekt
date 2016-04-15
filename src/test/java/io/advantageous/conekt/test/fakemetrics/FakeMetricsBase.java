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

package io.advantageous.conekt.test.fakemetrics;

import io.advantageous.conekt.metrics.Measured;
import io.advantageous.conekt.spi.metrics.Metrics;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FakeMetricsBase implements Metrics {

    private static final ConcurrentHashMap<Object, FakeMetricsBase> metricsMap = new ConcurrentHashMap<>();
    final Measured key;

    public FakeMetricsBase(Measured measured) {
        metricsMap.put(measured, this);
        key = measured;
    }

    public static <M extends FakeMetricsBase> M getMetrics(Measured measured) {
        // Free cast :-)
        return (M) metricsMap.get(measured);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void close() {
        metricsMap.remove(key);
    }
}
