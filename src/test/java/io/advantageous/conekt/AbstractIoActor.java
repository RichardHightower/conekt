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


/**
 * An abstract base class that you can extend to write your own IoActor classes.
 * <p>
 * Instead of implementing {@link IoActor} directly it it often simpler to just extend this class.
 * <p>
 * In the simplest case, just override the {@link #start} method. If you have verticle clean-up to do you can
 * optionally override the {@link #stop} method too.
 * <p>If you're verticle does extra start-up or clean-up which takes some time (e.g. it deploys other verticles) then
 * you should override the asynchronous {@link #start(Future) start} and {@link #stop(Future) stop} methods.
 * <p>
 * This class also provides maintains references to the {@link Conekt} and {@link Context}
 * instances of the verticle for easy access.<p>
 * and {@link #deploymentID deployment ID}.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public abstract class AbstractIoActor implements IoActor {


    /**
     * Reference to the Vert.x instance that deployed this verticle
     */
    protected Conekt conekt;

    /**
     * Reference to the context of the verticle
     */
    protected Context context;

    /**
     * Get the Vert.x instance
     *
     * @return the Vert.x instance
     */
    public Conekt getConekt() {
        return conekt;
    }

    /**
     * Initialise the verticle.<p>
     * This is called by Vert.x when the verticle instance is deployed. Don't call it yourself.
     *
     * @param conekt   the deploying Vert.x instance
     * @param context the context of the verticle
     */
    @Override
    public void init(Conekt conekt, Context context) {
        this.conekt = conekt;
        this.context = context;
    }

    /**
     * Get the deployment ID of the verticle deployment
     *
     * @return the deployment ID
     */
    public String deploymentID() {
        return context.deploymentID();
    }


    /**
     * Start the verticle.<p>
     * This is called by Vert.x when the verticle instance is deployed. Don't call it yourself.<p>
     * If your verticle does things in it's startup which take some time then you can override this method
     * and call the startFuture some time later when start up is complete.
     *
     * @param startFuture a future which should be called when verticle start-up is complete.
     * @throws Exception
     */
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        start();
        startFuture.complete();
    }

    /**
     * Stop the verticle.<p>
     * This is called by Vert.x when the verticle instance is un-deployed. Don't call it yourself.<p>
     * If your verticle does things in it's shut-down which take some time then you can override this method
     * and call the stopFuture some time later when clean-up is complete.
     *
     * @param stopFuture a future which should be called when verticle clean-up is complete.
     * @throws Exception
     */
    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        stop();
        stopFuture.complete();
    }

    /**
     * If your verticle does a simple, synchronous start-up then override this method and put your start-up
     * code in there.
     *
     * @throws Exception
     */
    public void start() throws Exception {
    }

    /**
     * If your verticle has simple synchronous clean-up tasks to complete then override this method and put your clean-up
     * code in there.
     *
     * @throws Exception
     */
    public void stop() throws Exception {
    }

}
