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
import io.advantageous.conekt.eventbus.*;
import io.advantageous.conekt.impl.*;
//
import io.advantageous.conekt.eventbus.impl.HandlerRegistration;
import io.advantageous.conekt.http.CaseInsensitiveHeaders;
import io.advantageous.conekt.streams.Pump;
import io.advantageous.conekt.streams.ReadStream;
import io.advantageous.conekt.streams.WriteStream;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class LocalEventBusTest extends EventBusTestBase {

    private Conekt conekt;
    private EventBus eb;
    private boolean running;

    public void setUp() throws Exception {
        super.setUp();
        conekt = Conekt.vertx();
        eb = conekt.eventBus();
        running = true;
    }

    protected void tearDown() throws Exception {
        closeVertx();
        super.tearDown();
    }

    private void closeVertx() throws Exception {
        if (running) {
            CountDownLatch latch = new CountDownLatch(1);
            conekt.close(ar -> {
                assertTrue(ar.succeeded());
                latch.countDown();
            });
            assertTrue(latch.await(30, TimeUnit.SECONDS));
            running = false;
        }
    }

    @Test
    public void testDeliveryOptions() {
        DeliveryOptions options = new DeliveryOptions();

        TestUtils.assertIllegalArgumentException(() -> options.setSendTimeout(0));
        TestUtils.assertIllegalArgumentException(() -> options.setSendTimeout(-1));
        TestUtils.assertNullPointerException(() -> options.addHeader(null, ""));
        TestUtils.assertNullPointerException(() -> options.addHeader("", null));
    }

    @Test
    public void testArgumentValidation() throws Exception {
        TestUtils.assertNullPointerException(() -> eb.send(null, ""));
        TestUtils.assertNullPointerException(() -> eb.send(null, "", handler -> {
        }));
        TestUtils.assertNullPointerException(() -> eb.send(null, "", new DeliveryOptions()));
        TestUtils.assertNullPointerException(() -> eb.send("", "", (DeliveryOptions) null));
        TestUtils.assertNullPointerException(() -> eb.send(null, "", new DeliveryOptions(), handler -> {
        }));
        TestUtils.assertNullPointerException(() -> eb.send("", "", null, handler -> {
        }));
        TestUtils.assertNullPointerException(() -> eb.publish(null, ""));
        TestUtils.assertNullPointerException(() -> eb.publish(null, "", new DeliveryOptions()));
        TestUtils.assertNullPointerException(() -> eb.publish("", "", null));
        TestUtils.assertNullPointerException(() -> eb.consumer(null));
        TestUtils.assertNullPointerException(() -> eb.consumer(null, msg -> {
        }));
        TestUtils.assertNullPointerException(() -> eb.consumer(ADDRESS1, null));
        TestUtils.assertNullPointerException(() -> eb.localConsumer(null));
        TestUtils.assertNullPointerException(() -> eb.localConsumer(null, msg -> {
        }));
        TestUtils.assertNullPointerException(() -> eb.localConsumer(ADDRESS1, null));
        TestUtils.assertNullPointerException(() -> eb.sender(null));
        TestUtils.assertNullPointerException(() -> eb.sender(null, new DeliveryOptions()));
        TestUtils.assertNullPointerException(() -> eb.publisher("", null));
        TestUtils.assertNullPointerException(() -> eb.publisher(null, new DeliveryOptions()));
        TestUtils.assertNullPointerException(() -> eb.registerCodec(null));
        TestUtils.assertNullPointerException(() -> eb.unregisterCodec(null));
        TestUtils.assertNullPointerException(() -> eb.registerDefaultCodec(null, new MyPOJOEncoder1()));
        TestUtils.assertNullPointerException(() -> eb.registerDefaultCodec(Object.class, null));
        TestUtils.assertNullPointerException(() -> eb.unregisterDefaultCodec(null));
    }

    @Test
    public void testRegisterUnregister() {
        String str = TestUtils.randomUnicodeString(100);
        Handler<Message<String>> handler = msg -> fail("Should not receive message");
        MessageConsumer reg = eb.<String>consumer(ADDRESS1).handler(handler);
        assertEquals(ADDRESS1, reg.address());
        reg.unregister();
        eb.send(ADDRESS1, str);
        conekt.setTimer(1000, id -> testComplete());
        await();
    }

    @Test
    public void testUnregisterTwice() {
        Handler<Message<String>> handler = msg -> {
        };
        MessageConsumer reg = eb.<String>consumer(ADDRESS1).handler(handler);
        reg.unregister();
        reg.unregister(); // Ok to unregister twice
        testComplete();
    }

    @Test
    public void testRegisterLocal1() {
        String str = TestUtils.randomUnicodeString(100);
        eb.<String>localConsumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            testComplete();
        }).completionHandler(ar -> {
            assertTrue(ar.succeeded());
            eb.send(ADDRESS1, str);
        });
        await();
    }

    @Test
    public void testRegisterLocal2() {
        String str = TestUtils.randomUnicodeString(100);
        eb.localConsumer(ADDRESS1, (Message<String> msg) -> {
            assertEquals(str, msg.body());
            testComplete();
        }).completionHandler(ar -> {
            assertTrue(ar.succeeded());
            eb.send(ADDRESS1, str);
        });
        await();
    }

    @Test
    public void testRegisterWithCompletionHandler() {
        String str = TestUtils.randomUnicodeString(100);
        MessageConsumer<String> reg = eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            testComplete();
        });
        reg.completionHandler(ar -> {
            assertTrue(ar.succeeded());
            eb.send(ADDRESS1, str);
        });
        await();
    }

    @Test
    public void testSendRoundRobin() {
        String str = TestUtils.randomUnicodeString(100);
        int numHandlers = 10;
        int numMessages = 100;
        Handler<Message<String>>[] handlers = new Handler[numHandlers];
        Map<Handler, Integer> countMap = new ConcurrentHashMap<>();
        AtomicInteger totalCount = new AtomicInteger();
        for (int i = 0; i < numHandlers; i++) {
            int index = i;
            handlers[i] = (Message<String> msg) -> {
                assertEquals(str, msg.body());
                Integer cnt = countMap.get(handlers[index]);
                int icnt = cnt == null ? 0 : cnt;
                icnt++;
                countMap.put(handlers[index], icnt);
                if (totalCount.incrementAndGet() == numMessages) {
                    assertEquals(numHandlers, countMap.size());
                    for (Integer ind : countMap.values()) {
                        assertEquals(numMessages / numHandlers, ind.intValue());
                    }
                    testComplete();
                }
            };
            eb.<String>consumer(ADDRESS1).handler(handlers[i]);
        }

        for (int i = 0; i < numMessages; i++) {
            eb.send(ADDRESS1, str);
        }

        await();
    }

    @Test
    public void testSendRegisterSomeUnregisterOne() {
        String str = TestUtils.randomUnicodeString(100);
        AtomicInteger totalCount = new AtomicInteger();
        Handler<Message<String>> handler1 = msg -> fail("Should not receive message");
        Handler<Message<String>> handler2 = msg -> {
            assertEquals(str, msg.body());
            if (totalCount.incrementAndGet() == 2) {
                testComplete();
            }
        };
        Handler<Message<String>> handler3 = msg -> {
            assertEquals(str, msg.body());
            if (totalCount.incrementAndGet() == 2) {
                testComplete();
            }
        };

        MessageConsumer reg = eb.<String>consumer(ADDRESS1).handler(handler1);
        eb.<String>consumer(ADDRESS1).handler(handler2);
        eb.<String>consumer(ADDRESS1).handler(handler3);
        reg.unregister();
        eb.send(ADDRESS1, str);
        eb.send(ADDRESS1, str);

        await();
    }

    @Test
    public void testSendRegisterSameHandlerMultipleTimes() {
        String str = TestUtils.randomUnicodeString(100);
        AtomicInteger totalCount = new AtomicInteger();
        Handler<Message<String>> handler = (Message<String> msg) -> {
            assertEquals(str, msg.body());
            if (totalCount.incrementAndGet() == 3) {
                testComplete();
            }
        };
        eb.<String>consumer(ADDRESS1).handler(handler);
        eb.<String>consumer(ADDRESS1).handler(handler);
        eb.<String>consumer(ADDRESS1).handler(handler);

        eb.send(ADDRESS1, str);
        eb.send(ADDRESS1, str);
        eb.send(ADDRESS1, str);
        await();
    }

    @Test
    public void testSendWithNoHandler() {
        eb.send(ADDRESS1, TestUtils.randomUnicodeString(100));
        conekt.setTimer(1000, id -> testComplete());
        await();
    }

    @Test
    public void testSendMultipleAddresses() {
        String str = TestUtils.randomUnicodeString(100);
        AtomicInteger cnt = new AtomicInteger();
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            fail("Should not receive message");
        });
        eb.<String>consumer(ADDRESS2).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            if (cnt.incrementAndGet() == 2) {
                testComplete();
            }
        });
        eb.send(ADDRESS2, str);
        eb.send(ADDRESS2, str);
        await();
    }

    @Test
    public void testSendWithTimeoutNoTimeoutNoReply() {
        String str = TestUtils.randomUnicodeString(1000);
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            testComplete();
        });
        long timeout = 1000;
        eb.send(ADDRESS1, str, new DeliveryOptions().setSendTimeout(timeout), ar -> {
        });
        await();
    }

    @Test
    public void testSendWithReply() {
        String str = TestUtils.randomUnicodeString(1000);
        String reply = TestUtils.randomUnicodeString(1000);
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            msg.reply(reply);
        });
        eb.send(ADDRESS1, str, onSuccess((Message<String> msg) -> {
            assertEquals(reply, msg.body());
            testComplete();
        }));
        await();
    }

    @Test
    public void testReplyToReply() {
        String str = TestUtils.randomUnicodeString(1000);
        String reply = TestUtils.randomUnicodeString(1000);
        String replyReply = TestUtils.randomUnicodeString(1000);
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            msg.reply(reply, onSuccess((Message<String> rep) -> {
                assertEquals(replyReply, rep.body());
                testComplete();
            }));
        });
        eb.send(ADDRESS1, str, onSuccess((Message<String> msg) -> {
            assertEquals(reply, msg.body());
            msg.reply(replyReply);
        }));
        await();
    }

    @Test
    public void testSendReplyWithTimeout() {
        String str = TestUtils.randomUnicodeString(1000);
        String reply = TestUtils.randomUnicodeString(1000);
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            long start = System.currentTimeMillis();
            long timeout = 1000;
            msg.reply(reply, new DeliveryOptions().setSendTimeout(timeout), ar -> {
                long now = System.currentTimeMillis();
                assertFalse(ar.succeeded());
                Throwable cause = ar.cause();
                assertTrue(cause instanceof ReplyException);
                ReplyException re = (ReplyException) cause;
                assertEquals(-1, re.failureCode());
                assertEquals(ReplyFailure.TIMEOUT, re.failureType());
                assertTrue(now - start >= timeout);
                testComplete();
            });
        });
        eb.send(ADDRESS1, str, onSuccess((Message<String> msg) -> {
            assertEquals(reply, msg.body());
            // Now don't reply
        }));
        await();
    }

    @Test
    public void testSendReplyWithTimeoutNoTimeout() {
        String str = TestUtils.randomUnicodeString(1000);
        String reply = TestUtils.randomUnicodeString(1000);
        String replyReply = TestUtils.randomUnicodeString(1000);
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            long timeout = 1000;
            msg.reply(reply, new DeliveryOptions().setSendTimeout(timeout), ar -> {
                assertTrue(ar.succeeded());
                assertEquals(replyReply, ar.result().body());
                testComplete();
            });
        });
        eb.send(ADDRESS1, str, onSuccess((Message<String> msg) -> {
            assertEquals(reply, msg.body());
            msg.reply(replyReply);
        }));
        await();
    }

    @Test
    public void testSendWithTimeoutNoTimeoutReply() {
        String str = TestUtils.randomUnicodeString(1000);
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            msg.reply(23);
        });
        long timeout = 1000;
        eb.send(ADDRESS1, str, new DeliveryOptions().setSendTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
            assertTrue(ar.succeeded());
            assertEquals(23, (int) ar.result().body());
            testComplete();
        });
        await();
    }

    @Test
    public void testSendWithTimeoutNoReply() {
        String str = TestUtils.randomUnicodeString(1000);
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
        });
        long timeout = 1000;
        long start = System.currentTimeMillis();
        eb.send(ADDRESS1, str, new DeliveryOptions().setSendTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
            long now = System.currentTimeMillis();
            assertFalse(ar.succeeded());
            Throwable cause = ar.cause();
            assertTrue(cause instanceof ReplyException);
            ReplyException re = (ReplyException) cause;
            assertEquals(-1, re.failureCode());
            assertEquals(ReplyFailure.TIMEOUT, re.failureType());
            assertTrue(now - start >= timeout);
            testComplete();
        });
        await();
    }

    @Test
    public void testSendWithTimeoutNoHandlers() {
        String str = TestUtils.randomUnicodeString(1000);
        long timeout = 1000;
        eb.send(ADDRESS1, str, new DeliveryOptions().setSendTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
            assertFalse(ar.succeeded());
            Throwable cause = ar.cause();
            assertTrue(cause instanceof ReplyException);
            ReplyException re = (ReplyException) cause;
            assertEquals(-1, re.failureCode());
            assertEquals(ReplyFailure.NO_HANDLERS, re.failureType());
            assertEquals("No handlers for address " + ADDRESS1, re.getMessage());
            testComplete();
        });
        await();
    }

    @Test
    public void testSendWithTimeoutRecipientFailure() {
        String str = TestUtils.randomUnicodeString(1000);
        String failureMsg = TestUtils.randomUnicodeString(1000);
        int failureCode = 123;
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            msg.fail(failureCode, failureMsg);
        });
        long timeout = 1000;
        eb.send(ADDRESS1, str, new DeliveryOptions().setSendTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
            assertFalse(ar.succeeded());
            Throwable cause = ar.cause();
            assertTrue(cause instanceof ReplyException);
            ReplyException re = (ReplyException) cause;
            assertEquals(failureCode, re.failureCode());
            assertEquals(failureMsg, re.getMessage());
            assertEquals(ReplyFailure.RECIPIENT_FAILURE, re.failureType());
            testComplete();
        });
        await();
    }

    @Test
    public void testSendWithTimeoutReplyAfterTimeout() {
        String str = TestUtils.randomUnicodeString(1000);
        long timeout = 1000;
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            conekt.setTimer((int) (timeout * 1.5), id -> {
                msg.reply("too late!");
            });
        });
        eb.send(ADDRESS1, str, new DeliveryOptions().setSendTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
            assertFalse(ar.succeeded());
            Throwable cause = ar.cause();
            assertTrue(cause instanceof ReplyException);
            ReplyException re = (ReplyException) cause;
            assertEquals(-1, re.failureCode());
            assertEquals(ReplyFailure.TIMEOUT, re.failureType());
            testComplete();
        });
        await();
    }

    @Test
    public void testSendWithTimeoutNoTimeoutAfterReply() {
        String str = TestUtils.randomUnicodeString(1000);
        long timeout = 1000;
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            msg.reply("a reply");
        });
        AtomicBoolean received = new AtomicBoolean();
        eb.send(ADDRESS1, str, new DeliveryOptions().setSendTimeout(timeout), (AsyncResult<Message<Integer>> ar) -> {
            assertFalse(received.get());
            assertTrue(ar.succeeded());
            received.set(true);
            // Now wait longer than timeout and make sure we don't receive any other reply
            conekt.setTimer(timeout * 2, tid -> {
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testReplyToSendWithNoReplyHandler() {
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            msg.reply("a reply");
            testComplete();
        });
        eb.send(ADDRESS1, "whatever");
        await();
    }

    @Test
    public void testReplyToPublish() {
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            msg.reply("a reply");
            testComplete();
        });
        eb.publish(ADDRESS1, "whatever");
        await();
    }

    @Test
    public void testFailAfterSend() {
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            msg.fail(0, "a failure");
            testComplete();
        });
        eb.publish(ADDRESS1, "whatever");
        await();
    }

    @Test
    public void testFailAfterPublish() {
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            msg.fail(0, "a failure");
            testComplete();
        });
        eb.publish(ADDRESS1, "whatever");
        await();
    }

    // Sends with different types

    @Test
    public void testPublish() {
        String str = TestUtils.randomUnicodeString(100);
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            testComplete();
        });
        eb.publish(ADDRESS1, str);
        await();
    }

    @Test
    public void testPublishMultipleHandlers() {
        String str = TestUtils.randomUnicodeString(100);
        AtomicInteger count = new AtomicInteger();
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            if (count.incrementAndGet() == 2) {
                testComplete();
            }
        });
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            if (count.incrementAndGet() == 2) {
                testComplete();
            }
        });
        eb.publish(ADDRESS1, str);
        await();
    }

    @Test
    public void testPublishSameHandlerRegisteredTwice() {
        String str = TestUtils.randomUnicodeString(1000);
        AtomicInteger count = new AtomicInteger();
        Handler<Message<String>> handler = (Message<String> msg) -> {
            assertEquals(str, msg.body());
            if (count.incrementAndGet() == 2) {
                testComplete();
            }
        };
        eb.<String>consumer(ADDRESS1).handler(handler);
        eb.<String>consumer(ADDRESS1).handler(handler);
        eb.publish(ADDRESS1, str);
        await();
    }

    @Test
    public void testPublishMultipleHandlersUnregisterOne() {
        String str = TestUtils.randomUnicodeString(1000);
        Handler<Message<String>> handler1 = (Message<String> msg) -> {
            assertEquals(str, msg.body());
            testComplete();
        };
        Handler<Message<String>> handler2 = (Message<String> msg) -> {
            fail("Should not be called");
        };

        eb.<String>consumer(ADDRESS1).handler(handler1);
        MessageConsumer reg = eb.<String>consumer(ADDRESS1).handler(handler2);
        reg.unregister();
        eb.publish(ADDRESS1, str);

        await();
    }

    @Test
    public void testPublishMultipleHandlersDifferentAddresses() {
        String str = TestUtils.randomUnicodeString(1000);
        eb.<String>consumer(ADDRESS1).handler((Message<String> msg) -> {
            assertEquals(str, msg.body());
            testComplete();
        });
        eb.<String>consumer(ADDRESS2).handler((Message<String> msg) -> {
            fail("Should not receive message");
        });
        eb.publish(ADDRESS1, str);
        await();
    }

    @Test
    public void testNonRegisteredCodecType() {
        class Boom {
        }
        eb.consumer("foo").handler(msg -> {
            fail("Should not have gotten here");
        });

        try {
            eb.send("foo", new Boom());
        } catch (IllegalArgumentException e) {
            testComplete();
        }

        await();
    }


    @Test
    public void testCloseEventBus() {
        eb.close(ar -> {
            assertTrue(ar.succeeded());
            testComplete();
        });
        await();
    }

    @Test
    public void testInVerticle() throws Exception {
        testInVerticle(false, false);
    }

    @Test
    public void testInWorkerVerticle() throws Exception {
        testInVerticle(true, false);
    }

    @Test
    public void testInMultithreadedWorkerVerticle() throws Exception {
        testInVerticle(true, true);
    }

    private void testInVerticle(boolean worker, boolean multiThreaded) throws Exception {
        class MyIoActor extends AbstractIoActor {
            Context ctx;

            @Override
            public void start() {
                ctx = context;
                if (worker) {
                    if (multiThreaded) {
                        assertTrue(ctx instanceof MultiThreadedWorkerContext);
                    } else {
                        assertTrue(ctx instanceof WorkerContext && !(ctx instanceof MultiThreadedWorkerContext));
                    }
                } else {
                    assertTrue(ctx instanceof EventLoopContext);
                }
                Thread thr = Thread.currentThread();
                MessageConsumer<?> reg = conekt.eventBus().consumer(ADDRESS1).handler(msg -> {
                    assertSame(ctx, context);
                    if (!worker) {
                        assertSame(thr, Thread.currentThread());
                    }
                    msg.reply("bar");
                });
                reg.completionHandler(ar -> {
                    assertTrue(ar.succeeded());
                    assertSame(ctx, context);
                    if (!worker) {
                        assertSame(thr, Thread.currentThread());
                    }
                    conekt.eventBus().send(ADDRESS1, "foo", onSuccess((Message<Object> reply) -> {
                        assertSame(ctx, context);
                        if (!worker) {
                            assertSame(thr, Thread.currentThread());
                        }
                        assertEquals("bar", reply.body());
                        testComplete();
                    }));
                });
            }
        }
        MyIoActor verticle = new MyIoActor();
        conekt.deployVerticle(verticle, new DeploymentOptions().setWorker(worker).setMultiThreaded(multiThreaded));
        await();
    }

    @Test
    public void testContextsSend() throws Exception {
        Set<ContextImpl> contexts = new ConcurrentHashSet<>();
        CountDownLatch latch = new CountDownLatch(2);
        conekt.eventBus().consumer(ADDRESS1).handler(msg -> {
            msg.reply("bar");
            contexts.add(((ConektInternal) conekt).getContext());
            latch.countDown();
        });
        conekt.eventBus().send(ADDRESS1, "foo", onSuccess((Message<Object> reply) -> {
            assertEquals("bar", reply.body());
            contexts.add(((ConektInternal) conekt).getContext());
            latch.countDown();
        }));
        awaitLatch(latch);
        assertEquals(2, contexts.size());
    }

    @Test
    public void testContextsPublish() throws Exception {
        Set<ContextImpl> contexts = new ConcurrentHashSet<>();
        AtomicInteger cnt = new AtomicInteger();
        int numHandlers = 10;
        for (int i = 0; i < numHandlers; i++) {
            conekt.eventBus().consumer(ADDRESS1).handler(msg -> {
                contexts.add(((ConektInternal) conekt).getContext());
                if (cnt.incrementAndGet() == numHandlers) {
                    assertEquals(numHandlers, contexts.size());
                    testComplete();
                }
            });
        }
        conekt.eventBus().publish(ADDRESS1, "foo");
        await();
    }

    @Test
    public void testHeadersCopiedAfterSend() throws Exception {
        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add("foo", "bar");
        conekt.eventBus().consumer(ADDRESS1).handler(msg -> {
            assertNotSame(headers, msg.headers());
            assertEquals("bar", msg.headers().get("foo"));
            testComplete();
        });
        conekt.eventBus().send(ADDRESS1, "foo", new DeliveryOptions().setHeaders(headers));
        headers.remove("foo");
        await();
    }

    @Test
    public void testDecoderSendAsymmetric() throws Exception {
        MessageCodec codec = new MyPOJOEncoder1();
        conekt.eventBus().registerCodec(codec);
        String str = TestUtils.randomAlphaString(100);
        testSend(new MyPOJO(str), str, null, new DeliveryOptions().setCodecName(codec.name()));
    }

    @Test
    public void testDecoderReplyAsymmetric() throws Exception {
        MessageCodec codec = new MyPOJOEncoder1();
        conekt.eventBus().registerCodec(codec);
        String str = TestUtils.randomAlphaString(100);
        testReply(new MyPOJO(str), str, null, new DeliveryOptions().setCodecName(codec.name()));
    }

    @Test
    public void testDecoderSendSymmetric() throws Exception {
        MessageCodec codec = new MyPOJOEncoder2();
        conekt.eventBus().registerCodec(codec);
        String str = TestUtils.randomAlphaString(100);
        MyPOJO pojo = new MyPOJO(str);
        testSend(pojo, pojo, null, new DeliveryOptions().setCodecName(codec.name()));
    }

    @Test
    public void testDecoderReplySymmetric() throws Exception {
        MessageCodec codec = new MyPOJOEncoder2();
        conekt.eventBus().registerCodec(codec);
        String str = TestUtils.randomAlphaString(100);
        MyPOJO pojo = new MyPOJO(str);
        testReply(pojo, pojo, null, new DeliveryOptions().setCodecName(codec.name()));
    }

    @Test
    public void testNoRegisteredDecoder() throws Exception {
        try {
            conekt.eventBus().send(ADDRESS1, "foo", new DeliveryOptions().setCodecName("iqwjdoqiwd"));
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testRegisterSystemDecoder() throws Exception {
        try {
            conekt.eventBus().registerCodec(new MySystemDecoder());
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testUnregisterDecoder() throws Exception {
        MessageCodec codec = new MyPOJOEncoder1();
        conekt.eventBus().registerCodec(codec);
        conekt.eventBus().unregisterCodec(codec.name());
        try {
            conekt.eventBus().send(ADDRESS1, new MyPOJO("foo"), new DeliveryOptions().setCodecName(codec.name()));
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }

    @Test
    public void testRegisterTwice() throws Exception {
        MessageCodec codec = new MyPOJOEncoder1();
        conekt.eventBus().registerCodec(codec);
        try {
            conekt.eventBus().registerCodec(codec);
            fail("Should throw exception");
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void testCodecNullName() throws Exception {
        try {
            conekt.eventBus().registerCodec(new NullNameCodec());
            fail("Should throw exception");
        } catch (NullPointerException e) {
            // OK
        }
    }

    @Test
    public void testDefaultDecoderSendAsymmetric() throws Exception {
        MessageCodec codec = new MyPOJOEncoder1();
        conekt.eventBus().registerDefaultCodec(MyPOJO.class, codec);
        String str = TestUtils.randomAlphaString(100);
        testSend(new MyPOJO(str), str, null, null);
    }

    @Test
    public void testDefaultDecoderReplyAsymmetric() throws Exception {
        MessageCodec codec = new MyPOJOEncoder1();
        conekt.eventBus().registerDefaultCodec(MyPOJO.class, codec);
        String str = TestUtils.randomAlphaString(100);
        testReply(new MyPOJO(str), str, null, null);
    }

    @Test
    public void testDefaultDecoderSendSymetric() throws Exception {
        MessageCodec codec = new MyPOJOEncoder2();
        conekt.eventBus().registerDefaultCodec(MyPOJO.class, codec);
        String str = TestUtils.randomAlphaString(100);
        MyPOJO pojo = new MyPOJO(str);
        testSend(pojo, pojo, null, null);
    }

    @Test
    public void testDefaultDecoderReplySymetric() throws Exception {
        MessageCodec codec = new MyPOJOEncoder2();
        conekt.eventBus().registerDefaultCodec(MyPOJO.class, codec);
        String str = TestUtils.randomAlphaString(100);
        MyPOJO pojo = new MyPOJO(str);
        testReply(pojo, pojo, null, null);
    }

    @Test
    public void testNoRegisteredDefaultDecoder() throws Exception {
        TestUtils.assertIllegalArgumentException(() -> conekt.eventBus().send(ADDRESS1, new MyPOJO("foo")));
    }

    @Test
    public void testRegisterDefaultSystemDecoder() throws Exception {
        TestUtils.assertIllegalArgumentException(() -> conekt.eventBus().registerDefaultCodec(MyPOJO.class, new MySystemDecoder()));
    }

    @Test
    public void testUnregisterDefaultDecoder() throws Exception {
        MessageCodec codec = new MyPOJOEncoder1();
        conekt.eventBus().registerDefaultCodec(MyPOJO.class, codec);
        conekt.eventBus().unregisterDefaultCodec(MyPOJO.class);
        TestUtils.assertIllegalArgumentException(() -> conekt.eventBus().send(ADDRESS1, new MyPOJO("foo")));
    }

    @Test
    public void testRegisterDefaultTwice() throws Exception {
        MessageCodec codec = new MyPOJOEncoder1();
        conekt.eventBus().registerDefaultCodec(MyPOJO.class, codec);
        TestUtils.assertIllegalStateException(() -> conekt.eventBus().registerDefaultCodec(MyPOJO.class, codec));
    }

    @Test
    public void testDefaultCodecNullName() throws Exception {
        TestUtils.assertNullPointerException(() -> conekt.eventBus().registerDefaultCodec(String.class, new NullNameCodec()));
    }


    @Override
    protected <T, R> void testSend(T val, R received, Consumer<T> consumer, DeliveryOptions options) {
        eb.<T>consumer(ADDRESS1).handler((Message<T> msg) -> {
            if (consumer == null) {
                assertEquals(received, msg.body());
                if (options != null && options.getHeaders() != null) {
                    assertNotNull(msg.headers());
                    assertEquals(options.getHeaders().size(), msg.headers().size());
                    for (Map.Entry<String, String> entry : options.getHeaders().entries()) {
                        assertEquals(msg.headers().get(entry.getKey()), entry.getValue());
                    }
                }
            } else {
                consumer.accept(msg.body());
            }
            testComplete();
        });
        if (options != null) {
            eb.send(ADDRESS1, val, options);
        } else {
            eb.send(ADDRESS1, val);
        }
        await();
    }

    @Override
    protected <T> void testSend(T val, Consumer<T> consumer) {
        testSend(val, val, consumer, null);
    }

    @Override
    protected <T> void testReply(T val, Consumer<T> consumer) {
        testReply(val, val, consumer, null);
    }

    @Override
    protected <T, R> void testReply(T val, R received, Consumer<R> consumer, DeliveryOptions options) {

        String str = TestUtils.randomUnicodeString(1000);
        eb.consumer(ADDRESS1).handler(msg -> {
            assertEquals(str, msg.body());
            if (options != null) {
                msg.reply(val, options);
            } else {
                msg.reply(val);
            }
        });
        eb.send(ADDRESS1, str, onSuccess((Message<R> reply) -> {
            if (consumer == null) {
                assertEquals(received, reply.body());
                if (options != null && options.getHeaders() != null) {
                    assertNotNull(reply.headers());
                    assertEquals(options.getHeaders().size(), reply.headers().size());
                    for (Map.Entry<String, String> entry : options.getHeaders().entries()) {
                        assertEquals(reply.headers().get(entry.getKey()), entry.getValue());
                    }
                }
            } else {
                consumer.accept(reply.body());
            }
            testComplete();
        }));
        await();
    }

    @Override
    protected <T> void testPublish(T val, Consumer<T> consumer) {
        AtomicInteger count = new AtomicInteger();
        class MyHandler implements Handler<Message<T>> {
            @Override
            public void handle(Message<T> msg) {
                if (consumer == null) {
                    assertEquals(val, msg.body());
                } else {
                    consumer.accept(msg.body());
                }
                if (count.incrementAndGet() == 2) {
                    testComplete();
                }
            }
        }
        eb.<T>consumer(ADDRESS1).handler(new MyHandler());
        eb.<T>consumer(ADDRESS1).handler(new MyHandler());
        eb.publish(ADDRESS1, val);
        await();
    }

    @Test
    public void testPauseResumeMessageStream() {
        testPauseResume((consumer, handler) -> consumer.handler(message -> handler.handle(message.body())));
    }

    @Test
    public void testPauseResumeBodyStream() {
        testPauseResume((consumer, handler) -> consumer.bodyStream().handler(handler));
    }

    private void testPauseResume(BiFunction<MessageConsumer<String>, Handler<String>, ReadStream<?>> register) {
        String[] data = new String[11];
        for (int i = 0; i < data.length; i++) {
            data[i] = TestUtils.randomAlphaString(10);
        }
        Set<String> expected = new HashSet<>();
        Handler<String> handler = body -> {
            assertTrue("Was expecting " + expected + " to contain " + body, expected.remove(body));
            if (expected.isEmpty()) {
                testComplete();
            }
        };
        MessageConsumer<String> reg = eb.<String>consumer(ADDRESS1).setMaxBufferedMessages(10);
        ReadStream<?> controller = register.apply(reg, handler);
        ((HandlerRegistration<String>) reg).discardHandler(msg -> {
            assertEquals(data[10], msg.body());
            expected.addAll(Arrays.asList(data).subList(0, 10));
            controller.resume();
        });
        controller.pause();
        for (String msg : data) {
            eb.publish(ADDRESS1, msg);
        }
        await();
    }

    @Test
    public void testExceptionWhenDeliveringBufferedMessageWithMessageStream() {
        testExceptionWhenDeliveringBufferedMessage((consumer, handler) -> consumer.handler(message -> handler.handle(message.body())));
    }

    @Test
    public void testExceptionWhenDeliveringBufferedMessageWithBodyStream() {
        testExceptionWhenDeliveringBufferedMessage((consumer, handler) -> consumer.bodyStream().handler(handler));
    }

    private void testExceptionWhenDeliveringBufferedMessage(BiFunction<MessageConsumer<String>, Handler<String>, ReadStream<?>> register) {
        String[] data = new String[11];
        for (int i = 0; i < data.length; i++) {
            data[i] = TestUtils.randomAlphaString(10);
        }
        Set<String> expected = new HashSet<>();
        Handler<String> handler = body -> {
            assertTrue("Was expecting " + expected + " to contain " + body, expected.remove(body));
            if (expected.isEmpty()) {
                testComplete();
            } else {
                throw new RuntimeException();
            }
        };
        MessageConsumer<String> reg = eb.<String>consumer(ADDRESS1).setMaxBufferedMessages(10);
        ReadStream<?> controller = register.apply(reg, handler);
        ((HandlerRegistration<String>) reg).discardHandler(msg -> {
            assertEquals(data[10], msg.body());
            expected.addAll(Arrays.asList(data).subList(0, 10));
            controller.resume();
        });
        controller.pause();
        for (String msg : data) {
            eb.publish(ADDRESS1, msg);
        }
        await();
    }

    @Test
    public void testUnregisterationOfRegisteredConsumerCallsEndHandlerWithMessageStream() {
        MessageConsumer<String> consumer = eb.consumer(ADDRESS1);
        testUnregisterationOfRegisteredConsumerCallsEndHandler(consumer, consumer);
    }

    @Test
    public void testUnregisterationOfRegisteredConsumerCallsEndHandlerWithBodyStream() {
        MessageConsumer<String> consumer = eb.consumer(ADDRESS1);
        testUnregisterationOfRegisteredConsumerCallsEndHandler(consumer, consumer.bodyStream());
    }

    private void testUnregisterationOfRegisteredConsumerCallsEndHandler(MessageConsumer<String> consumer, ReadStream<?> readStream) {
        consumer.handler(msg -> {
        });
        consumer.endHandler(v -> {
            fail();
        });
        consumer.unregister();
        conekt.runOnContext(d -> {
            testComplete();
        });
        await();
    }

    @Test
    public void testUnregisterThenUnsetEndHandler() {
        MessageConsumer<String> consumer = eb.consumer(ADDRESS1);
        consumer.endHandler(v -> {
        });
        consumer.unregister(res -> {
            testComplete();
        });
        consumer.endHandler(null);
        await();
    }

    @Test
    public void testUnregistrationWhenSettingNullHandlerWithConsumer() {
        MessageConsumer<String> consumer = eb.consumer(ADDRESS1);
        testUnregistrationWhenSettingNullHandler(consumer, consumer);
    }

    @Test
    public void testUnregistrationWhenSettingNullHandlerWithBodyStream() {
        MessageConsumer<String> consumer = eb.consumer(ADDRESS1);
        testUnregistrationWhenSettingNullHandler(consumer, consumer.bodyStream());
    }

    private void testUnregistrationWhenSettingNullHandler(MessageConsumer<String> consumer, ReadStream<?> readStream) {
        readStream.handler(msg -> {
        });
        assertTrue(consumer.isRegistered());
        readStream.handler(null);
        assertFalse(consumer.isRegistered());
    }

    @Test
    public void testSender() {
        String str = TestUtils.randomUnicodeString(100);
        WriteStream<String> sender = eb.sender(ADDRESS1);
        eb.consumer(ADDRESS1).handler(message -> {
            if (message.body().equals(str)) {
                testComplete();
            }
        });
        sender.write(str);
        await();
    }

    @Test
    public void testSenderWithOptions() {
        String str = TestUtils.randomUnicodeString(100);
        WriteStream<String> sender = eb.sender(ADDRESS1, new DeliveryOptions().addHeader("foo", "foo_value"));
        eb.consumer(ADDRESS1).handler(message -> {
            if (message.body().equals(str) && "foo_value".equals(message.headers().get("foo"))) {
                testComplete();
            }
        });
        sender.write(str);
        await();
    }

    @Test
    public void testPublisher() {
        String str = TestUtils.randomUnicodeString(100);
        MessageProducer<String> publisher = eb.publisher(ADDRESS1);
        assertEquals(ADDRESS1, publisher.address());
        AtomicInteger count = new AtomicInteger();
        int n = 2;
        for (int i = 0; i < n; i++) {
            eb.consumer(ADDRESS1).handler(message -> {
                if (message.body().equals(str) && count.incrementAndGet() == n) {
                    testComplete();
                }
            });
        }
        publisher.write(str);
        await();
    }

    @Test
    public void testPublisherWithOptions() {
        String str = TestUtils.randomUnicodeString(100);
        MessageProducer<String> publisher = eb.publisher(ADDRESS1, new DeliveryOptions().addHeader("foo", "foo_value"));
        assertEquals(ADDRESS1, publisher.address());
        AtomicInteger count = new AtomicInteger();
        int n = 2;
        for (int i = 0; i < n; i++) {
            eb.consumer(ADDRESS1).handler(message -> {
                if (message.body().equals(str) && "foo_value".equals(message.headers().get("foo")) && count.incrementAndGet() == n) {
                    testComplete();
                }
            });
        }
        publisher.write(str);
        await();
    }

    @Test
    public void testPump() {
        String str = TestUtils.randomUnicodeString(100);
        ReadStream<String> consumer = eb.<String>consumer(ADDRESS1).bodyStream();
        consumer.handler(message -> {
            if (message.equals(str)) {
                testComplete();
            }
        });
        MessageProducer<String> producer = eb.sender(ADDRESS2);
        Pump.pump(consumer, producer);
        producer.write(str);
    }

    @Test
    public void testConsumerHandlesCompletionAsynchronously() {
        MessageConsumer<Object> consumer = eb.consumer(ADDRESS1);
        ThreadLocal<Object> stack = new ThreadLocal<>();
        stack.set(true);
        consumer.completionHandler(v -> {
            assertNull(stack.get());
            assertTrue(Conekt.currentContext().isEventLoopContext());
            testComplete();
        });
        consumer.handler(msg -> {
        });
        await();
    }

    @Test
    public void testConsumerHandlesCompletionAsynchronously2() {
        MessageConsumer<Object> consumer = eb.consumer(ADDRESS1);
        consumer.handler(msg -> {
        });
        ThreadLocal<Object> stack = new ThreadLocal<>();
        stack.set(true);
        consumer.completionHandler(v -> {
            assertNull(stack.get());
            assertTrue(Conekt.currentContext().isEventLoopContext());
            testComplete();
        });
        await();
    }

    @Test
    public void testUpdateDeliveryOptionsOnProducer() {
        MessageProducer<String> producer = eb.sender(ADDRESS1);
        MessageConsumer<String> consumer = eb.<String>consumer(ADDRESS1);
        consumer.completionHandler(v -> {
            assertTrue(v.succeeded());
            producer.write("no-header");
        });
        consumer.handler(msg -> {
            String body = msg.body();
            assertNotNull(body);
            switch (body) {
                case "no-header":
                    assertNull(msg.headers().get("header-name"));
                    producer.deliveryOptions(new DeliveryOptions().addHeader("header-name", "header-value"));
                    producer.write("with-header");
                    break;
                case "with-header":
                    assertEquals("header-value", msg.headers().get("header-name"));
                    testComplete();
                    break;
                default:
                    fail();
            }
        });
        await();
    }

    @Test
    public void testCloseCallsEndHandlerWithRegistrationContext() throws Exception {
        Context ctx = conekt.getOrCreateContext();
        CountDownLatch registered = new CountDownLatch(1);
        ctx.runOnContext(v1 -> {
            MessageConsumer<String> consumer = eb.consumer(ADDRESS1);
            consumer.endHandler(v2 -> {
                assertSame(Conekt.currentContext(), ctx);
                testComplete();
            });
            consumer.handler(msg -> {
            });
            consumer.completionHandler(ar -> {
                assertTrue(ar.succeeded());
                registered.countDown();
            });
        });
        awaitLatch(registered);
        closeVertx();
        await();
    }

    @Test
    public void testConsumerUnregisterDoesNotCancelTimer0() throws Exception {
        Context ctx = conekt.getOrCreateContext();
        ctx.runOnContext(v -> {
            // The delay does not matter so much, it will always be executed after this task anyway
            conekt.setTimer(50, id -> {
                assertEquals(0, (long) id);
                testComplete();
            });
            eb.consumer(ADDRESS1).unregister();
        });
        await();
    }
}

