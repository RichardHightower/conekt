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

package io.advantageous.conekt.spi;

import io.advantageous.conekt.Vertx;
import io.advantageous.conekt.VertxOptions;
import io.advantageous.conekt.metrics.MetricsOptions;
import io.advantageous.conekt.spi.metrics.VertxMetrics;

/**
 * A factory for the plugable metrics SPI.
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public interface VertxMetricsFactory {

    /**
     * Create a new {@link VertxMetrics} object.<p/>
     * <p>
     * No specific thread and context can be expected when this method is called.
     *
     * @param vertx   the vertx instance
     * @param options the metrics configuration option
     * @return the metrics implementation
     */
    VertxMetrics metrics(Vertx vertx, VertxOptions options);

    /**
     * Create an empty metrics options. Providers can override this method to provide a custom metrics options subclass
     * that exposes custom configuration. It is used by the {@link io.vertx.core.Starter} class when
     * creating new options when building a CLI vert.x
     *
     * @return new metrics options
     */
    default MetricsOptions newOptions() {
        return new MetricsOptions();
    }

}
