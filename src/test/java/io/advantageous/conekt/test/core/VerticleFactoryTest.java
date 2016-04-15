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

import io.advantageous.conekt.*;
//
import io.advantageous.conekt.impl.ConektInternal;
import io.advantageous.conekt.impl.Deployment;
import io.advantageous.conekt.spi.IoActorFactory;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class VerticleFactoryTest extends VertxTestBase {

    public void setUp() throws Exception {
        super.setUp();
        // Unregister the factory that's loaded from the classpath
        IoActorFactory factory = conekt.verticleFactories().iterator().next();
        conekt.unregisterVerticleFactory(factory);
    }

    @Test
    public void testRegister() {
        assertTrue(conekt.verticleFactories().isEmpty());
        IoActorFactory fact1 = new TestVerticleFactory("foo");
        conekt.registerVerticleFactory(fact1);
        assertEquals(1, conekt.verticleFactories().size());
        assertTrue(conekt.verticleFactories().contains(fact1));
    }

    @Test
    public void testUnregister() {
        IoActorFactory fact1 = new TestVerticleFactory("foo");
        conekt.registerVerticleFactory(fact1);
        assertEquals(1, conekt.verticleFactories().size());
        assertTrue(conekt.verticleFactories().contains(fact1));
        conekt.unregisterVerticleFactory(fact1);
        assertFalse(conekt.verticleFactories().contains(fact1));
        assertTrue(conekt.verticleFactories().isEmpty());
    }

    @Test
    public void testRegisterTwice() {
        IoActorFactory fact1 = new TestVerticleFactory("foo");
        conekt.registerVerticleFactory(fact1);
        try {
            conekt.registerVerticleFactory(fact1);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testUnregisterTwice() {
        IoActorFactory fact1 = new TestVerticleFactory("foo");
        conekt.registerVerticleFactory(fact1);
        conekt.unregisterVerticleFactory(fact1);
        try {
            conekt.unregisterVerticleFactory(fact1);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testUnregisterNoFact() {
        IoActorFactory fact1 = new TestVerticleFactory("foo");
        try {
            conekt.unregisterVerticleFactory(fact1);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testRegisterUnregisterTwo() {
        IoActorFactory fact1 = new TestVerticleFactory("foo");
        IoActorFactory fact2 = new TestVerticleFactory("bar");
        conekt.registerVerticleFactory(fact1);
        assertEquals(1, conekt.verticleFactories().size());
        conekt.registerVerticleFactory(fact2);
        assertEquals(2, conekt.verticleFactories().size());
        assertTrue(conekt.verticleFactories().contains(fact1));
        assertTrue(conekt.verticleFactories().contains(fact2));
        conekt.unregisterVerticleFactory(fact1);
        assertFalse(conekt.verticleFactories().contains(fact1));
        assertEquals(1, conekt.verticleFactories().size());
        assertTrue(conekt.verticleFactories().contains(fact2));
        conekt.unregisterVerticleFactory(fact2);
        assertTrue(conekt.verticleFactories().isEmpty());
        assertFalse(conekt.verticleFactories().contains(fact1));
        assertFalse(conekt.verticleFactories().contains(fact2));
    }

    @Test
    public void testMatchWithPrefix() {
        TestIoActor verticle1 = new TestIoActor();
        TestIoActor verticle2 = new TestIoActor();
        TestIoActor verticle3 = new TestIoActor();
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", verticle1);
        TestVerticleFactory fact2 = new TestVerticleFactory("bb", verticle2);
        TestVerticleFactory fact3 = new TestVerticleFactory("cc", verticle3);
        conekt.registerVerticleFactory(fact1);
        conekt.registerVerticleFactory(fact2);
        conekt.registerVerticleFactory(fact3);
        String name1 = "aa:myverticle1";
        String name2 = "bb:myverticle2";
        String name3 = "cc:myverticle3";
        conekt.deployVerticle(name1, new DeploymentOptions(), ar -> {
            assertTrue(ar.succeeded());
            assertEquals(name1, fact1.identifier);
            assertTrue(verticle1.startCalled);
            assertFalse(verticle2.startCalled);
            assertFalse(verticle3.startCalled);
            assertNull(fact2.identifier);
            assertNull(fact3.identifier);
            conekt.deployVerticle(name2, new DeploymentOptions(), ar2 -> {
                assertTrue(ar2.succeeded());
                assertEquals(name2, fact2.identifier);
                assertTrue(verticle2.startCalled);
                assertFalse(verticle3.startCalled);
                assertNull(fact3.identifier);
                conekt.deployVerticle(name3, new DeploymentOptions(), ar3 -> {
                    assertTrue(ar3.succeeded());
                    assertEquals(name3, fact3.identifier);
                    assertTrue(verticle3.startCalled);
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testMatchWithSuffix() {
        TestIoActor verticle1 = new TestIoActor();
        TestIoActor verticle2 = new TestIoActor();
        TestIoActor verticle3 = new TestIoActor();
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", verticle1);
        TestVerticleFactory fact2 = new TestVerticleFactory("bb", verticle2);
        TestVerticleFactory fact3 = new TestVerticleFactory("cc", verticle3);
        conekt.registerVerticleFactory(fact1);
        conekt.registerVerticleFactory(fact2);
        conekt.registerVerticleFactory(fact3);
        String name1 = "myverticle1.aa";
        String name2 = "myverticle2.bb";
        String name3 = "myverticle3.cc";
        conekt.deployVerticle(name1, new DeploymentOptions(), ar -> {
            assertTrue(ar.succeeded());
            assertEquals(name1, fact1.identifier);
            assertTrue(verticle1.startCalled);
            assertFalse(verticle2.startCalled);
            assertFalse(verticle3.startCalled);
            assertNull(fact2.identifier);
            assertNull(fact3.identifier);
            conekt.deployVerticle(name2, new DeploymentOptions(), ar2 -> {
                assertTrue(ar2.succeeded());
                assertEquals(name2, fact2.identifier);
                assertTrue(verticle2.startCalled);
                assertFalse(verticle3.startCalled);
                assertNull(fact3.identifier);
                conekt.deployVerticle(name3, new DeploymentOptions(), ar3 -> {
                    assertTrue(ar3.succeeded());
                    assertEquals(name3, fact3.identifier);
                    assertTrue(verticle3.startCalled);
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testNoMatch() {
        TestIoActor verticle1 = new TestIoActor();
        TestIoActor verticle2 = new TestIoActor();
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", verticle1);
        TestVerticleFactory fact2 = new TestVerticleFactory("bb", verticle2);
        conekt.registerVerticleFactory(fact1);
        conekt.registerVerticleFactory(fact2);
        String name1 = "cc:myverticle1";
        // If no match it will default to the simple Java ioActor factory and then fail with ClassNotFoundException
        conekt.deployVerticle(name1, new DeploymentOptions(), ar -> {
            assertFalse(ar.succeeded());
            assertFalse(verticle1.startCalled);
            assertFalse(verticle2.startCalled);
            assertTrue(ar.cause() instanceof ClassNotFoundException);
            testComplete();
        });
        await();
    }

    @Test
    public void testResolve() {
        TestIoActor verticle = new TestIoActor();
        TestVerticleFactory fact = new TestVerticleFactory("actual", verticle);
        conekt.registerVerticleFactory(fact);
        TestVerticleFactory factResolve = new TestVerticleFactory("resolve", "actual:myverticle");
        conekt.registerVerticleFactory(factResolve);
        DeploymentOptions original = new DeploymentOptions().setWorker(false).setIsolationGroup("somegroup");
        DeploymentOptions options = new DeploymentOptions(original);
        conekt.deployVerticle("resolve:someid", options, res -> {
            assertTrue(res.succeeded());
            assertEquals("resolve:someid", factResolve.identifierToResolve);
            assertEquals(options, factResolve.deploymentOptionsToResolve);
            assertEquals("actual:myverticle", fact.identifier);
            assertTrue(verticle.startCalled);
            assertTrue(verticle.startCalled);
            assertEquals(1, conekt.deploymentIDs().size());
            Deployment dep = ((ConektInternal) conekt).getDeployment(res.result());
            assertNotNull(dep);
            assertFalse(original.equals(dep.deploymentOptions()));
            assertTrue(dep.deploymentOptions().isWorker());
            assertEquals("othergroup", dep.deploymentOptions().getIsolationGroup());
            testComplete();
        });
        await();
    }

    @Test
    public void testOrdering() {
        TestIoActor verticle = new TestIoActor();
        TestVerticleFactory fact2 = new TestVerticleFactory("aa", verticle, 2);
        conekt.registerVerticleFactory(fact2);
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", verticle, 1);
        conekt.registerVerticleFactory(fact1);
        TestVerticleFactory fact3 = new TestVerticleFactory("aa", verticle, 3);
        conekt.registerVerticleFactory(fact3);
        conekt.deployVerticle("aa:someverticle", res -> {
            assertTrue(res.succeeded());
            assertEquals("aa:someverticle", fact1.identifier);
            assertNull(fact2.identifier);
            assertNull(fact3.identifier);
            testComplete();
        });
        await();
    }

    @Test
    public void testOrderingFailedInCreate() {
        TestIoActor verticle = new TestIoActor();
        TestVerticleFactory fact2 = new TestVerticleFactory("aa", verticle, 2);
        conekt.registerVerticleFactory(fact2);
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", verticle, 1, true);
        conekt.registerVerticleFactory(fact1);
        TestVerticleFactory fact3 = new TestVerticleFactory("aa", verticle, 3);
        conekt.registerVerticleFactory(fact3);
        conekt.deployVerticle("aa:someverticle", res -> {
            assertTrue(res.succeeded());
            assertEquals("aa:someverticle", fact2.identifier);
            assertNull(fact1.identifier);
            assertNull(fact3.identifier);
            testComplete();
        });
        await();
    }

    @Test
    public void testOrderingFailedInCreate2() {
        TestIoActor verticle = new TestIoActor();
        TestVerticleFactory fact2 = new TestVerticleFactory("aa", verticle, 2, true);
        conekt.registerVerticleFactory(fact2);
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", verticle, 1, true);
        conekt.registerVerticleFactory(fact1);
        TestVerticleFactory fact3 = new TestVerticleFactory("aa", verticle, 3);
        conekt.registerVerticleFactory(fact3);
        conekt.deployVerticle("aa:someverticle", res -> {
            assertTrue(res.succeeded());
            assertEquals("aa:someverticle", fact3.identifier);
            assertNull(fact1.identifier);
            assertNull(fact2.identifier);
            testComplete();
        });
        await();
    }

    @Test
    public void testOrderingFailedInCreateAll() {
        TestIoActor verticle = new TestIoActor();
        TestVerticleFactory fact2 = new TestVerticleFactory("aa", verticle, 2, true);
        conekt.registerVerticleFactory(fact2);
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", verticle, 1, true);
        conekt.registerVerticleFactory(fact1);
        TestVerticleFactory fact3 = new TestVerticleFactory("aa", verticle, 3, true);
        conekt.registerVerticleFactory(fact3);
        conekt.deployVerticle("aa:someverticle", res -> {
            assertFalse(res.succeeded());
            assertTrue(res.cause() instanceof ClassNotFoundException);
            assertNull(fact1.identifier);
            assertNull(fact2.identifier);
            assertNull(fact3.identifier);
            testComplete();
        });
        await();
    }

    @Test
    public void testOrderingFailedInResolve() {
        TestIoActor verticle = new TestIoActor();

        TestVerticleFactory factActual = new TestVerticleFactory("actual", verticle);
        conekt.registerVerticleFactory(factActual);

        TestVerticleFactory fact2 = new TestVerticleFactory("aa", "actual:someverticle", 2);
        conekt.registerVerticleFactory(fact2);
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", "actual:someverticle", 1, true);
        conekt.registerVerticleFactory(fact1);
        TestVerticleFactory fact3 = new TestVerticleFactory("aa", "actual:someverticle", 3);
        conekt.registerVerticleFactory(fact3);
        conekt.deployVerticle("aa:blah", res -> {
            assertTrue(res.succeeded());
            assertNull(fact2.identifier);
            assertNull(fact1.identifier);
            assertNull(fact3.identifier);
            assertEquals("aa:blah", fact2.identifierToResolve);
            assertNull(fact1.identifierToResolve);
            assertNull(fact3.identifierToResolve);
            assertEquals("actual:someverticle", factActual.identifier);
            testComplete();
        });
        await();
    }

    @Test
    public void testOrderingFailedInResolve2() {
        TestIoActor verticle = new TestIoActor();

        TestVerticleFactory factActual = new TestVerticleFactory("actual", verticle);
        conekt.registerVerticleFactory(factActual);

        TestVerticleFactory fact2 = new TestVerticleFactory("aa", "actual:someverticle", 2, true);
        conekt.registerVerticleFactory(fact2);
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", "actual:someverticle", 1, true);
        conekt.registerVerticleFactory(fact1);
        TestVerticleFactory fact3 = new TestVerticleFactory("aa", "actual:someverticle", 3);
        conekt.registerVerticleFactory(fact3);
        conekt.deployVerticle("aa:blah", res -> {
            assertTrue(res.succeeded());
            assertNull(fact2.identifier);
            assertNull(fact1.identifier);
            assertNull(fact3.identifier);
            assertEquals("aa:blah", fact3.identifierToResolve);
            assertNull(fact1.identifierToResolve);
            assertNull(fact2.identifierToResolve);
            assertEquals("actual:someverticle", factActual.identifier);
            testComplete();
        });
        await();
    }

    @Test
    public void testOrderingAllFailedInResolve() {
        TestIoActor verticle = new TestIoActor();

        TestVerticleFactory factActual = new TestVerticleFactory("actual", verticle);
        conekt.registerVerticleFactory(factActual);

        TestVerticleFactory fact2 = new TestVerticleFactory("aa", "actual:someverticle", 2, true);
        conekt.registerVerticleFactory(fact2);
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", "actual:someverticle", 1, true);
        conekt.registerVerticleFactory(fact1);
        TestVerticleFactory fact3 = new TestVerticleFactory("aa", "actual:someverticle", 3, true);
        conekt.registerVerticleFactory(fact3);
        conekt.deployVerticle("aa:blah", res -> {
            assertTrue(res.failed());
            assertTrue(res.cause() instanceof IOException);
            assertNull(fact2.identifier);
            assertNull(fact1.identifier);
            assertNull(fact3.identifier);
            assertNull(fact3.identifierToResolve);
            assertNull(fact1.identifierToResolve);
            assertNull(fact2.identifierToResolve);
            assertNull(factActual.identifier);
            testComplete();
        });
        await();
    }

    @Test
    public void testNotBlockingCreate() {
        TestIoActor verticle1 = new TestIoActor();
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", verticle1);
        conekt.registerVerticleFactory(fact1);
        String name1 = "aa:myverticle1";
        conekt.deployVerticle(name1, new DeploymentOptions(), ar -> {
            assertTrue(ar.succeeded());
            assertEquals(name1, fact1.identifier);
            assertFalse(fact1.blockingCreate);
            assertFalse(fact1.createWorkerThread);
            testComplete();
        });
        await();
    }

    @Test
    public void testBlockingCreate() {
        TestIoActor verticle1 = new TestIoActor();
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", verticle1);
        fact1.blockingCreate = true;
        conekt.registerVerticleFactory(fact1);
        String name1 = "aa:myverticle1";
        conekt.deployVerticle(name1, new DeploymentOptions(), ar -> {
            assertTrue(ar.succeeded());
            assertEquals(name1, fact1.identifier);
            assertTrue(fact1.blockingCreate);
            assertTrue(fact1.createWorkerThread);
            assertTrue(fact1.createContext.isEventLoopContext());
            testComplete();
        });
        await();
    }

    @Test
    public void testBlockingCreateFailureInCreate() {
        TestIoActor verticle1 = new TestIoActor();
        TestVerticleFactory fact1 = new TestVerticleFactory("aa", verticle1);
        fact1.blockingCreate = true;
        fact1.failInCreate = true;
        conekt.registerVerticleFactory(fact1);
        String name1 = "aa:myverticle1";
        conekt.deployVerticle(name1, new DeploymentOptions(), ar -> {
            assertFalse(ar.succeeded());
            testComplete();
        });
        await();
    }

    class TestVerticleFactory implements IoActorFactory {

        String prefix;
        IoActor ioActor;
        String identifier;

        String resolvedIdentifier;

        String identifierToResolve;
        DeploymentOptions deploymentOptionsToResolve;
        int order;
        boolean failInCreate;
        boolean failInResolve;
        Context createContext;
        boolean createWorkerThread;
        boolean blockingCreate;

        TestVerticleFactory(String prefix) {
            this.prefix = prefix;
        }

        TestVerticleFactory(String prefix, IoActor ioActor) {
            this.prefix = prefix;
            this.ioActor = ioActor;
        }

        TestVerticleFactory(String prefix, String resolvedIdentifier) {
            this.prefix = prefix;
            this.resolvedIdentifier = resolvedIdentifier;
        }

        TestVerticleFactory(String prefix, IoActor ioActor, int order) {
            this.prefix = prefix;
            this.ioActor = ioActor;
            this.order = order;
        }

        TestVerticleFactory(String prefix, IoActor ioActor, int order, boolean failInCreate) {
            this.prefix = prefix;
            this.ioActor = ioActor;
            this.order = order;
            this.failInCreate = failInCreate;
        }

        TestVerticleFactory(String prefix, String resolvedIdentifier, int order) {
            this.prefix = prefix;
            this.resolvedIdentifier = resolvedIdentifier;
            this.order = order;
        }

        TestVerticleFactory(String prefix, String resolvedIdentifier, int order, boolean failInResolve) {
            this.prefix = prefix;
            this.resolvedIdentifier = resolvedIdentifier;
            this.order = order;
            this.failInResolve = failInResolve;
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public boolean requiresResolve() {
            return resolvedIdentifier != null;
        }

        @Override
        public void resolve(String identifier, DeploymentOptions deploymentOptions, ClassLoader classLoader, Future<String> resolution) {
            if (failInResolve) {
                resolution.fail(new IOException("whatever"));
            } else {
                identifierToResolve = identifier;
                deploymentOptionsToResolve = deploymentOptions;
                deploymentOptions.setWorker(true);
                deploymentOptions.setIsolationGroup("othergroup");
                resolution.complete(resolvedIdentifier);
            }
        }

        @Override
        public void init(Conekt conekt) {
        }

        @Override
        public String prefix() {
            return prefix;
        }


        @Override
        public IoActor createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
            if (failInCreate) {
                throw new ClassNotFoundException("whatever");
            }
            this.identifier = verticleName;
            this.createContext = Conekt.currentContext();
            this.createWorkerThread = Context.isOnWorkerThread();
            return ioActor;
        }

        @Override
        public void close() {

        }

        @Override
        public boolean blockingCreate() {
            return blockingCreate;
        }
    }

    class TestIoActor extends AbstractIoActor {

        boolean startCalled;

        @Override
        public void start() throws Exception {
            startCalled = true;
        }

        @Override
        public void stop() throws Exception {

        }
    }
}
