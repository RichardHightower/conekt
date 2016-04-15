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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.Executor;

/**
 * A factory for producing executors that run all tasks in order, which delegate to a single common executor instance.
 *
 * @author <a href="david.lloyd@jboss.com">David Lloyd</a>
 * @author <a href="mailto:tim.fox@jboss.com">Tim Fox</a>
 * @version <tt>$Revision$</tt>
 */
public class OrderedExecutorFactory {

    private static final Logger log = LoggerFactory.getLogger(OrderedExecutorFactory.class);

    private final Executor parent;

    /**
     * Construct a new instance delegating to the given parent executor.
     *
     * @param parent the parent executor
     */
    public OrderedExecutorFactory(Executor parent) {
        this.parent = parent;
    }

    /**
     * Get an executor that always executes tasks in order.
     *
     * @return an ordered executor
     */
    public Executor getExecutor() {
        return new OrderedExecutor(parent);
    }

    /**
     * An executor that always runs all tasks in order, using a delegate executor to run the tasks.
     * <p>
     * More specifically, any call B to the {@link #execute(Runnable)} method that happens-after another call A to the
     * same method, will result in B's task running after A's.
     */
    private static final class OrderedExecutor implements Executor {
        // @protectedby tasks
        private final LinkedList<Runnable> tasks = new LinkedList<>();
        private final Executor parent;
        private final Runnable runner;
        // @protectedby tasks
        private boolean running;

        /**
         * Construct a new instance.
         *
         * @param parent the parent executor
         */
        public OrderedExecutor(Executor parent) {
            this.parent = parent;
            runner = () -> {
                for (; ; ) {
                    final Runnable task;
                    synchronized (tasks) {
                        task = tasks.poll();
                        if (task == null) {
                            running = false;
                            return;
                        }
                    }
                    try {
                        task.run();
                    } catch (Throwable t) {
                        log.error("Caught unexpected Throwable", t);
                    }
                }
            };
        }

        /**
         * Run a task.
         *
         * @param command the task to run.
         */
        public void execute(Runnable command) {
            synchronized (tasks) {
                tasks.add(command);
                if (!running) {
                    running = true;
                    parent.execute(runner);
                }
            }
        }
    }
}
