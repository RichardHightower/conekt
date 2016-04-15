/*
 * Copyright (c) 2011-2013 The original author or authors
 *  ------------------------------------------------------
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *      The Eclipse Public License is available at
 *      http://www.eclipse.org/legal/epl-v10.html
 *
 *      The Apache License v2.0 is available at
 *      http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.smallvertx.test.core;

import io.smallvertx.core.Vertx;
import io.smallvertx.core.VertxOptions;
import io.smallvertx.core.metrics.MetricsOptions;
import io.smallvertx.core.metrics.impl.DummyVertxMetrics;
import io.smallvertx.core.spi.VertxMetricsFactory;
import io.smallvertx.core.spi.metrics.VertxMetrics;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ConfigurableMetricsFactory implements VertxMetricsFactory {

    public static VertxMetricsFactory delegate;

    @Override
    public VertxMetrics metrics(Vertx vertx, VertxOptions options) {
        return delegate != null ? delegate.metrics(vertx, options) : new DummyVertxMetrics();
    }

    @Override
    public MetricsOptions newOptions() {
        return delegate != null ? delegate.newOptions() : new MetricsOptions();
    }
}
