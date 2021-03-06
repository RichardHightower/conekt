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

package io.advantageous.conekt.test.core;

import io.advantageous.conekt.ConektOptions;
import io.advantageous.conekt.metrics.MetricsOptions;
import org.junit.Test;

import java.util.Random;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class ConektOptionsTest extends VertxTestBase {

    @Test
    public void testOptions() {
        ConektOptions options = new ConektOptions();
        assertEquals(2 * Runtime.getRuntime().availableProcessors(), options.getEventLoopPoolSize());
        int rand = TestUtils.randomPositiveInt();
        assertEquals(options, options.setEventLoopPoolSize(rand));
        assertEquals(rand, options.getEventLoopPoolSize());
        try {
            options.setEventLoopPoolSize(0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        assertEquals(20, options.getWorkerPoolSize());
        rand = TestUtils.randomPositiveInt();
        assertEquals(options, options.setWorkerPoolSize(rand));
        assertEquals(rand, options.getWorkerPoolSize());
        try {
            options.setWorkerPoolSize(0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        assertEquals(20, options.getInternalBlockingPoolSize());
        rand = TestUtils.randomPositiveInt();
        assertEquals(options, options.setInternalBlockingPoolSize(rand));
        assertEquals(rand, options.getInternalBlockingPoolSize());
        try {
            options.setInternalBlockingPoolSize(0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        assertFalse(options.isClustered());
        assertEquals(options, options.setClustered(true));
        assertTrue(options.isClustered());
        assertEquals(0, options.getClusterPort());
        assertEquals(options, options.setClusterPort(1234));
        assertEquals(1234, options.getClusterPort());
        try {
            options.setClusterPort(-1);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        try {
            options.setClusterPort(65536);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        assertEquals(-1, options.getClusterPublicPort());
        assertEquals(options, options.setClusterPublicPort(1234));
        assertEquals(1234, options.getClusterPublicPort());
        try {
            options.setClusterPublicPort(-1);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        try {
            options.setClusterPublicPort(65536);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        assertEquals("localhost", options.getClusterHost());
        String randString = TestUtils.randomUnicodeString(100);
        assertEquals(options, options.setClusterHost(randString));
        assertEquals(randString, options.getClusterHost());
        assertEquals(null, options.getClusterPublicHost());
        randString = TestUtils.randomUnicodeString(100);
        assertEquals(options, options.setClusterPublicHost(randString));
        assertEquals(randString, options.getClusterPublicHost());
        assertEquals(20000, options.getClusterPingInterval());
        long randomLong = TestUtils.randomPositiveLong();
        assertEquals(options, options.setClusterPingInterval(randomLong));
        assertEquals(randomLong, options.getClusterPingInterval());
        try {
            options.setClusterPingInterval(-1);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals(randomLong, options.getClusterPingInterval());
        }
        assertEquals(20000, options.getClusterPingReplyInterval());
        randomLong = TestUtils.randomPositiveLong();
        assertEquals(options, options.setClusterPingReplyInterval(randomLong));
        assertEquals(randomLong, options.getClusterPingReplyInterval());
        try {
            options.setClusterPingReplyInterval(-1);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertEquals(randomLong, options.getClusterPingReplyInterval());
        }
        assertEquals(1000, options.getBlockedThreadCheckInterval());
        rand = TestUtils.randomPositiveInt();
        assertEquals(options, options.setBlockedThreadCheckInterval(rand));
        assertEquals(rand, options.getBlockedThreadCheckInterval());
        try {
            options.setBlockedThreadCheckInterval(0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        assertEquals(2000l * 1000000, options.getMaxEventLoopExecuteTime()); // 2 seconds in nano seconds
        rand = TestUtils.randomPositiveInt();
        assertEquals(options, options.setMaxEventLoopExecuteTime(rand));
        assertEquals(rand, options.getMaxEventLoopExecuteTime());
        try {
            options.setMaxEventLoopExecuteTime(0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        assertEquals(1l * 60 * 1000 * 1000000, options.getMaxWorkerExecuteTime()); // 1 minute in nano seconds
        rand = TestUtils.randomPositiveInt();
        assertEquals(options, options.setMaxWorkerExecuteTime(rand));
        assertEquals(rand, options.getMaxWorkerExecuteTime());
        try {
            options.setMaxWorkerExecuteTime(0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        rand = TestUtils.randomPositiveInt();
        assertEquals(1, options.getQuorumSize());
        assertEquals(options, options.setQuorumSize(rand));
        assertEquals(rand, options.getQuorumSize());
        try {
            options.setQuorumSize(0);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        try {
            options.setQuorumSize(-1);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        assertEquals(ConektOptions.DEFAULT_HA_GROUP, options.getHAGroup());
        randString = TestUtils.randomUnicodeString(100);
        assertEquals(options, options.setHAGroup(randString));
        assertEquals(randString, options.getHAGroup());

        try {
            options.setHAGroup(null);
            fail("Should throw exception");
        } catch (NullPointerException e) {
            // OK
        }
        assertNotNull(options.getMetricsOptions());

        try {
            options.setWarningExceptionTime(-1);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
        assertEquals(options, options.setWarningExceptionTime(1000000000l));
        assertEquals(1000000000l, options.getWarningExceptionTime());
    }

    @Test
    public void testCopyOptions() {
        ConektOptions options = new ConektOptions();

        int clusterPort = TestUtils.randomPortInt();
        int clusterPublicPort = TestUtils.randomPortInt();
        int eventLoopPoolSize = TestUtils.randomPositiveInt();
        int internalBlockingPoolSize = TestUtils.randomPositiveInt();
        int workerPoolSize = TestUtils.randomPositiveInt();
        int blockedThreadCheckInterval = TestUtils.randomPositiveInt();
        String clusterHost = TestUtils.randomAlphaString(100);
        String clusterPublicHost = TestUtils.randomAlphaString(100);
        long clusterPingInterval = TestUtils.randomPositiveLong();
        long clusterPingReplyInterval = TestUtils.randomPositiveLong();
        int maxEventLoopExecuteTime = TestUtils.randomPositiveInt();
        int maxWorkerExecuteTime = TestUtils.randomPositiveInt();
        Random rand = new Random();
        boolean haEnabled = rand.nextBoolean();
        boolean metricsEnabled = rand.nextBoolean();
        int quorumSize = 51214;
        String haGroup = TestUtils.randomAlphaString(100);
        long warningExceptionTime = TestUtils.randomPositiveLong();
        options.setClusterPort(clusterPort);
        options.setClusterPublicPort(clusterPublicPort);
        options.setEventLoopPoolSize(eventLoopPoolSize);
        options.setInternalBlockingPoolSize(internalBlockingPoolSize);
        options.setWorkerPoolSize(workerPoolSize);
        options.setBlockedThreadCheckInterval(blockedThreadCheckInterval);
        options.setClusterHost(clusterHost);
        options.setClusterPublicHost(clusterPublicHost);
        options.setClusterPingInterval(clusterPingInterval);
        options.setClusterPingReplyInterval(clusterPingReplyInterval);
        options.setMaxEventLoopExecuteTime(maxEventLoopExecuteTime);
        options.setMaxWorkerExecuteTime(maxWorkerExecuteTime);
        options.setQuorumSize(quorumSize);
        options.setHAGroup(haGroup);
        options.setMetricsOptions(
                new MetricsOptions().
                        setEnabled(metricsEnabled));
        options.setWarningExceptionTime(warningExceptionTime);
        options = new ConektOptions(options);
        assertEquals(clusterPort, options.getClusterPort());
        assertEquals(clusterPublicPort, options.getClusterPublicPort());
        assertEquals(clusterPingInterval, options.getClusterPingInterval());
        assertEquals(clusterPingReplyInterval, options.getClusterPingReplyInterval());
        assertEquals(eventLoopPoolSize, options.getEventLoopPoolSize());
        assertEquals(internalBlockingPoolSize, options.getInternalBlockingPoolSize());
        assertEquals(workerPoolSize, options.getWorkerPoolSize());
        assertEquals(blockedThreadCheckInterval, options.getBlockedThreadCheckInterval());
        assertEquals(clusterHost, options.getClusterHost());
        assertEquals(clusterPublicHost, options.getClusterPublicHost());
        assertEquals(maxEventLoopExecuteTime, options.getMaxEventLoopExecuteTime());
        assertEquals(maxWorkerExecuteTime, options.getMaxWorkerExecuteTime());
        assertEquals(quorumSize, options.getQuorumSize());
        assertEquals(haGroup, options.getHAGroup());
        MetricsOptions metricsOptions = options.getMetricsOptions();
        assertNotNull(metricsOptions);
        assertEquals(metricsEnabled, metricsOptions.isEnabled());
        assertEquals(warningExceptionTime, options.getWarningExceptionTime());
    }

}
