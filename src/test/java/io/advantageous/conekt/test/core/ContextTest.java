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

import io.advantageous.conekt.AbstractIoActor;
import io.advantageous.conekt.Context;
import io.advantageous.conekt.DeploymentOptions;
import io.advantageous.conekt.Conekt;
import io.advantageous.conekt.impl.ContextInternal;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class ContextTest extends VertxTestBase {

    @Test
    public void testRunOnContext() throws Exception {
        conekt.runOnContext(v -> {
            Thread th = Thread.currentThread();
            Context ctx = Conekt.currentContext();
            ctx.runOnContext(v2 -> {
                assertEquals(th, Thread.currentThread());
                // Execute it a few times to make sure it returns same context
                for (int i = 0; i < 10; i++) {
                    Context c = Conekt.currentContext();
                    assertEquals(ctx, c);
                }
                // And simulate a third party thread - e.g. a 3rd party async library wishing to return a result on the
                // correct context
                new Thread() {
                    public void run() {
                        ctx.runOnContext(v3 -> {
                            assertEquals(th, Thread.currentThread());
                            assertEquals(ctx, Conekt.currentContext());
                            testComplete();
                        });
                    }
                }.start();
            });
        });
        await();
    }

    @Test
    public void testNoContext() throws Exception {
        assertNull(Conekt.currentContext());
    }

    @Test
    public void testPutGetRemoveData() throws Exception {
        SomeObject obj = new SomeObject();
        conekt.runOnContext(v -> {
            Context ctx = Conekt.currentContext();
            ctx.put("foo", obj);
            ctx.runOnContext(v2 -> {
                assertEquals(obj, ctx.get("foo"));
                assertTrue(ctx.remove("foo"));
                ctx.runOnContext(v3 -> {
                    assertNull(ctx.get("foo"));
                    testComplete();
                });
            });
        });
        await();
    }

    @Test
    public void testGettingContextContextUnderContextAnotherInstanceShouldReturnDifferentContext() throws Exception {
        Conekt other = Conekt.vertx();
        Context context = conekt.getOrCreateContext();
        context.runOnContext(v -> {
            Context otherContext = other.getOrCreateContext();
            assertNotSame(otherContext, context);
            testComplete();
        });
        await();
    }

    @Test
    public void testExecuteOrderedBlocking() throws Exception {
        Context context = conekt.getOrCreateContext();
        context.executeBlocking(f -> {
            assertTrue(Context.isOnWorkerThread());
            f.complete(1 + 2);
        }, r -> {
            assertTrue(Context.isOnEventLoopThread());
            assertEquals(r.result(), 3);
            testComplete();
        });
        await();
    }

    @Test
    public void testExecuteUnorderedBlocking() throws Exception {
        Context context = conekt.getOrCreateContext();
        context.executeBlocking(f -> {
            assertTrue(Context.isOnWorkerThread());
            f.complete(1 + 2);
        }, false, r -> {
            assertTrue(Context.isOnEventLoopThread());
            assertEquals(r.result(), 3);
            testComplete();
        });
        await();
    }

    @Test
    public void testEventLoopExecuteFromIo() throws Exception {
        ContextInternal eventLoopContext = (ContextInternal) conekt.getOrCreateContext();

        // Check from other thread
        try {
            eventLoopContext.executeFromIO(this::fail);
            fail();
        } catch (IllegalStateException expected) {
        }

        // Check from event loop thread
        eventLoopContext.nettyEventLoop().execute(() -> {
            // Should not be set yet
            assertNull(Conekt.currentContext());
            Thread vertxThread = Thread.currentThread();
            AtomicBoolean nested = new AtomicBoolean(true);
            eventLoopContext.executeFromIO(() -> {
                assertTrue(nested.get());
                assertSame(eventLoopContext, Conekt.currentContext());
                assertSame(vertxThread, Thread.currentThread());
            });
            nested.set(false);
            testComplete();
        });
        await();
    }

    @Test
    public void testWorkerExecuteFromIo() throws Exception {
        AtomicReference<ContextInternal> workerContext = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        conekt.deployVerticle(new AbstractIoActor() {
            @Override
            public void start() throws Exception {
                workerContext.set((ContextInternal) context);
                latch.countDown();
            }
        }, new DeploymentOptions().setWorker(true));
        awaitLatch(latch);
        workerContext.get().nettyEventLoop().execute(() -> {
            assertNull(Conekt.currentContext());
            workerContext.get().executeFromIO(() -> {
                assertSame(workerContext.get(), Conekt.currentContext());
                assertTrue(Context.isOnWorkerThread());
                testComplete();
            });
        });
        await();
    }

    class SomeObject {
    }
}
