/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.smallvertx.core;


import java.util.ArrayList;
import java.util.List;

/**
 * Options for configuring a verticle deployment.
 * <p>
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class DeploymentOptions {

    public static final boolean DEFAULT_WORKER = false;
    public static final boolean DEFAULT_MULTI_THREADED = false;
    public static final String DEFAULT_ISOLATION_GROUP = null;
    public static final int DEFAULT_INSTANCES = 1;

    private boolean worker;
    private boolean multiThreaded;
    private String isolationGroup;
    private List<String> extraClasspath;
    private int instances;
    private List<String> isolatedClasses;

    /**
     * Default constructor
     */
    public DeploymentOptions() {
        this.worker = DEFAULT_WORKER;
        this.multiThreaded = DEFAULT_MULTI_THREADED;
        this.isolationGroup = DEFAULT_ISOLATION_GROUP;
        this.instances = DEFAULT_INSTANCES;
    }

    /**
     * Copy constructor
     *
     * @param other the instance to copy
     */
    public DeploymentOptions(DeploymentOptions other) {
        this.worker = other.isWorker();
        this.multiThreaded = other.isMultiThreaded();
        this.isolationGroup = other.getIsolationGroup();
        this.extraClasspath = other.getExtraClasspath() == null ? null : new ArrayList<>(other.getExtraClasspath());
        this.instances = other.instances;
        this.isolatedClasses = other.getIsolatedClasses() == null ? null : new ArrayList<>(other.getIsolatedClasses());
    }


    /**
     * Should the verticle(s) be deployed as a worker verticle?
     *
     * @return true if will be deployed as worker, false otherwise
     */
    public boolean isWorker() {
        return worker;
    }

    /**
     * Set whether the verticle(s) should be deployed as a worker verticle
     *
     * @param worker true for worker, false otherwise
     * @return a reference to this, so the API can be used fluently
     */
    public DeploymentOptions setWorker(boolean worker) {
        this.worker = worker;
        return this;
    }

    /**
     * Should the verticle(s) be deployed as a multi-threaded worker verticle?
     * <p>
     * Ignored if {@link #isWorker} is not true.
     *
     * @return true if will be deployed as multi-threaded worker, false otherwise
     */
    public boolean isMultiThreaded() {
        return multiThreaded;
    }

    /**
     * Set whether the verticle(s) should be deployed as a multi-threaded worker verticle
     *
     * @param multiThreaded true for multi-threaded worker, false otherwise
     * @return a reference to this, so the API can be used fluently
     */
    public DeploymentOptions setMultiThreaded(boolean multiThreaded) {
        this.multiThreaded = multiThreaded;
        return this;
    }

    /**
     * Get the isolation group that will be used when deploying the verticle(s)
     *
     * @return the isolation group
     */
    public String getIsolationGroup() {
        return isolationGroup;
    }

    /**
     * Set the isolation group that will be used when deploying the verticle(s)
     *
     * @param isolationGroup - the isolation group
     * @return a reference to this, so the API can be used fluently
     */
    public DeploymentOptions setIsolationGroup(String isolationGroup) {
        this.isolationGroup = isolationGroup;
        return this;
    }


    /**
     * Get any extra classpath to be used when deploying the verticle.
     * <p>
     * Ignored if no isolation group is set.
     *
     * @return any extra classpath
     */
    public List<String> getExtraClasspath() {
        return extraClasspath;
    }

    /**
     * Set any extra classpath to be used when deploying the verticle.
     * <p>
     * Ignored if no isolation group is set.
     *
     * @return a reference to this, so the API can be used fluently
     */
    public DeploymentOptions setExtraClasspath(List<String> extraClasspath) {
        this.extraClasspath = extraClasspath;
        return this;
    }

    /**
     * Get the number of instances that should be deployed.
     *
     * @return the number of instances
     */
    public int getInstances() {
        return instances;
    }

    /**
     * Set the number of instances that should be deployed.
     *
     * @param instances the number of instances
     * @return a reference to this, so the API can be used fluently
     */
    public DeploymentOptions setInstances(int instances) {
        this.instances = instances;
        return this;
    }

    /**
     * Get the list of isolated class names, the names can be a Java class fully qualified name such as
     * 'com.mycompany.myproject.engine.MyClass' or a wildcard matching such as `com.mycompany.myproject.*`.
     *
     * @return the list of isolated classes
     */
    public List<String> getIsolatedClasses() {
        return isolatedClasses;
    }

    /**
     * Set the isolated class names.
     *
     * @param isolatedClasses the list of isolated class names
     * @return a reference to this, so the API can be used fluently
     */
    public DeploymentOptions setIsolatedClasses(List<String> isolatedClasses) {
        this.isolatedClasses = isolatedClasses;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeploymentOptions that = (DeploymentOptions) o;

        if (worker != that.worker) return false;
        if (multiThreaded != that.multiThreaded) return false;
        if (instances != that.instances) return false;
        if (isolationGroup != null ? !isolationGroup.equals(that.isolationGroup) : that.isolationGroup != null)
            return false;
        if (extraClasspath != null ? !extraClasspath.equals(that.extraClasspath) : that.extraClasspath != null)
            return false;
        return !(isolatedClasses != null ? !isolatedClasses.equals(that.isolatedClasses) : that.isolatedClasses != null);

    }

    @Override
    public int hashCode() {
        int result = 13;
        result = 31 * result + (worker ? 1 : 0);
        result = 31 * result + (multiThreaded ? 1 : 0);
        result = 31 * result + (isolationGroup != null ? isolationGroup.hashCode() : 0);
        result = 31 * result + (extraClasspath != null ? extraClasspath.hashCode() : 0);
        result = 31 * result + instances;
        result = 31 * result + (isolatedClasses != null ? isolatedClasses.hashCode() : 0);
        return result;
    }
}
