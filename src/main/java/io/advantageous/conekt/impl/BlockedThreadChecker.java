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

import io.advantageous.conekt.ConektException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class BlockedThreadChecker {


    private static final Logger log = LoggerFactory.getLogger(BlockedThreadChecker.class);

    private static final Object O = new Object();
    private final Map<ConektThread, Object> threads = new WeakHashMap<>();
    private final Timer timer; // Need to use our own timer - can't use event loop for this

    BlockedThreadChecker(long interval, long maxEventLoopExecTime, long maxWorkerExecTime, long warningExceptionTime) {
        timer = new Timer("conekt-blocked-thread-checker", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (BlockedThreadChecker.this) {
                    long now = System.nanoTime();
                    for (ConektThread thread : threads.keySet()) {
                        long execStart = thread.startTime();
                        long dur = now - execStart;
                        final long timeLimit = thread.isWorker() ? maxWorkerExecTime : maxEventLoopExecTime;
                        if (execStart != 0 && dur > timeLimit) {
                            final String message = "Thread " + thread + " has been blocked for " + (dur / 1000000) + " ms, time limit is " + (timeLimit / 1000000);
                            if (dur <= warningExceptionTime) {
                                log.warn(message);
                            } else {
                                ConektException stackTrace = new ConektException("Thread blocked");
                                stackTrace.setStackTrace(thread.getStackTrace());
                                log.warn(message, stackTrace);
                            }
                        }
                    }
                }
            }
        }, interval, interval);
    }

    public synchronized void registerThread(ConektThread thread) {
        threads.put(thread, O);
    }

    public void close() {
        timer.cancel();
    }
}
