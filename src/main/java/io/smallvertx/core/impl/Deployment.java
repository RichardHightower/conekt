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

package io.smallvertx.core.impl;

import io.smallvertx.core.AsyncResult;
import io.smallvertx.core.DeploymentOptions;
import io.smallvertx.core.Handler;
import io.smallvertx.core.Verticle;

import java.util.Set;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public interface Deployment {

    void addChild(Deployment deployment);

    void removeChild(Deployment deployment);

    void undeploy(Handler<AsyncResult<Void>> completionHandler);

    void doUndeploy(ContextImpl undeployingContext, Handler<AsyncResult<Void>> completionHandler);

    String deploymentID();

    String verticleIdentifier();

    DeploymentOptions deploymentOptions();

    Set<Verticle> getVerticles();

    boolean isChild();
}
