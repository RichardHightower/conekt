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

package io.smallvertx.core.metrics;


/**
 * Vert.x metrics base configuration, this class can be extended by provider implementations to configure
 * those specific implementations.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class MetricsOptions {

    /**
     * The default value of metrics enabled false
     */
    public static final boolean DEFAULT_METRICS_ENABLED = false;

    private boolean enabled;

    /**
     * Default constructor
     */
    public MetricsOptions() {
        enabled = DEFAULT_METRICS_ENABLED;
    }

    /**
     * Copy constructor
     *
     * @param other The other {@link MetricsOptions} to copy when creating this
     */
    public MetricsOptions(MetricsOptions other) {
        enabled = other.isEnabled();
    }


    /**
     * Will metrics be enabled on the Vert.x instance?
     *
     * @return true if enabled, false if not.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set whether metrics will be enabled on the Vert.x instance.
     *
     * @param enable true if metrics enabled, or false if not.
     * @return a reference to this, so the API can be used fluently
     */
    public MetricsOptions setEnabled(boolean enable) {
        this.enabled = enable;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricsOptions that = (MetricsOptions) o;

        if (enabled != that.enabled) return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result = (enabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MetricsOptions{" +
                "enabled=" + enabled +
                '}';
    }
}
