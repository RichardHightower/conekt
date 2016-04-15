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
import io.advantageous.conekt.Conekt;
import io.advantageous.conekt.Handler;
import io.advantageous.conekt.TimeoutStream;
import io.advantageous.conekt.streams.ReadStream;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class TimerTest extends VertxTestBase {

    @Test
    public void testTimer() throws Exception {
        timer(1);
    }

    @Test
    public void testPeriodic() throws Exception {
        periodic(10);
    }

    @Test
    /**
     * Test the timers fire with approximately the correct delay
     */
    public void testTimings() throws Exception {
        final long start = System.currentTimeMillis();
        final long delay = 2000;
        conekt.setTimer(delay, timerID -> {
            long dur = System.currentTimeMillis() - start;
            assertTrue(dur >= delay);
            long maxDelay = delay * 2;
            assertTrue("Timer accuracy: " + dur + " vs " + maxDelay, dur < maxDelay); // 100% margin of error (needed for CI)
            conekt.cancelTimer(timerID);
            testComplete();
        });
        await();
    }

    @Test
    public void testInVerticle() throws Exception {
        class MyIoActor extends AbstractIoActor {
            AtomicInteger cnt = new AtomicInteger();

            @Override
            public void start() {
                Thread thr = Thread.currentThread();
                conekt.setTimer(1, id -> {
                    assertSame(thr, Thread.currentThread());
                    if (cnt.incrementAndGet() == 5) {
                        testComplete();
                    }
                });
                conekt.setPeriodic(2, id -> {
                    assertSame(thr, Thread.currentThread());
                    if (cnt.incrementAndGet() == 5) {
                        testComplete();
                    }
                });
            }
        }
        MyIoActor verticle = new MyIoActor();
        conekt.deployVerticle(verticle);
        await();
    }

    private void periodic(long delay) throws Exception {
        final int numFires = 10;
        final AtomicLong id = new AtomicLong(-1);
        id.set(conekt.setPeriodic(delay, new Handler<Long>() {
            int count;

            public void handle(Long timerID) {
                assertEquals(id.get(), timerID.longValue());
                count++;
                if (count == numFires) {
                    conekt.cancelTimer(timerID);
                    setEndTimer();
                }
                if (count > numFires) {
                    fail("Fired too many times");
                }
            }
        }));
        await();
    }

    private void timer(long delay) throws Exception {
        final AtomicLong id = new AtomicLong(-1);
        id.set(conekt.setTimer(delay, new Handler<Long>() {
            int count;
            boolean fired;

            public void handle(Long timerID) {
                assertFalse(fired);
                fired = true;
                assertEquals(id.get(), timerID.longValue());
                assertEquals(0, count);
                count++;
                setEndTimer();
            }
        }));
        await();
    }

    private void setEndTimer() {
        // Set another timer to trigger test complete - this is so if the first timer is called more than once we will
        // catch it
        conekt.setTimer(10, id -> testComplete());
    }

    @Test
    public void testTimerStreamSetHandlerSchedulesTheTimer() throws Exception {
        conekt.runOnContext(v -> {
            ReadStream<Long> timer = conekt.timerStream(200);
            AtomicBoolean handled = new AtomicBoolean();
            timer.handler(l -> {
                assertFalse(handled.get());
                handled.set(true);
            });
            timer.endHandler(v2 -> {
                assertTrue(handled.get());
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testTimerStreamExceptionDuringHandle() throws Exception {
        conekt.runOnContext(v -> {
            ReadStream<Long> timer = conekt.timerStream(200);
            AtomicBoolean handled = new AtomicBoolean();
            timer.handler(l -> {
                assertFalse(handled.get());
                handled.set(true);
                throw new RuntimeException();
            });
            timer.endHandler(v2 -> {
                assertTrue(handled.get());
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testTimerStreamCallingWithNullHandlerCancelsTheTimer() throws Exception {
        conekt.runOnContext(v -> {
            ReadStream<Long> timer = conekt.timerStream(200);
            AtomicInteger count = new AtomicInteger();
            timer.handler(l -> {
                if (count.incrementAndGet() == 1) {
                    timer.handler(null);
                    conekt.setTimer(200, id -> {
                        assertEquals(1, count.get());
                        testComplete();
                    });
                } else {
                    fail();
                }
            });
        });
        await();
    }

    @Test
    public void testTimerStreamCancellation() throws Exception {
        conekt.runOnContext(v -> {
            TimeoutStream timer = conekt.timerStream(200);
            AtomicBoolean called = new AtomicBoolean();
            timer.handler(l -> {
                called.set(true);
            });
            timer.cancel();
            conekt.setTimer(500, id -> {
                assertFalse(called.get());
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testTimerSetHandlerTwice() throws Exception {
        conekt.runOnContext(v -> {
            ReadStream<Long> timer = conekt.timerStream(200);
            timer.handler(l -> testComplete());
            try {
                timer.handler(l -> fail());
                fail();
            } catch (IllegalStateException ignore) {
            }
        });
        await();
    }

    @Test
    public void testTimerPauseResume() throws Exception {
        ReadStream<Long> timer = conekt.timerStream(10);
        timer.handler(l -> testComplete());
        timer.pause();
        timer.resume();
        await();
    }

    @Test
    public void testTimerPause() throws Exception {
        conekt.runOnContext(v -> {
            ReadStream<Long> timer = conekt.timerStream(10);
            timer.handler(l -> fail());
            timer.endHandler(l -> testComplete());
            timer.pause();
        });
        await();
    }

    @Test
    public void testPeriodicStreamHandler() throws Exception {
        TimeoutStream timer = conekt.periodicStream(10);
        AtomicInteger count = new AtomicInteger();
        timer.handler(l -> {
            int value = count.incrementAndGet();
            switch (value) {
                case 0:
                    break;
                case 1:
                    throw new RuntimeException();
                case 2:
                    timer.cancel();
                    testComplete();
                    break;
                default:
                    fail();
            }
        });
        timer.endHandler(v -> {
            fail();
        });
        await();
    }

    @Test
    public void testPeriodicSetHandlerTwice() throws Exception {
        conekt.runOnContext(v -> {
            ReadStream<Long> timer = conekt.periodicStream(200);
            timer.handler(l -> testComplete());
            try {
                timer.handler(l -> fail());
                fail();
            } catch (IllegalStateException ignore) {
            }
        });
        await();
    }

    @Test
    public void testPeriodicPauseResume() throws Exception {
        ReadStream<Long> timer = conekt.periodicStream(200);
        AtomicInteger count = new AtomicInteger();
        timer.handler(id -> {
            int cnt = count.incrementAndGet();
            if (cnt == 2) {
                timer.pause();
                conekt.setTimer(500, id2 -> {
                    assertEquals(2, count.get());
                    timer.resume();
                });
            } else if (cnt == 3) {
                testComplete();
            }
        });
        await();
    }

    @Test
    public void testTimeoutStreamEndCallbackAsynchronously() {
        TimeoutStream stream = conekt.timerStream(200);
        ThreadLocal<Object> stack = new ThreadLocal<>();
        stack.set(true);
        stream.endHandler(v2 -> {
            assertTrue(Conekt.currentContext().isEventLoopContext());
            assertNull(stack.get());
            testComplete();
        });
        stream.handler(id -> {
        });
        await();
    }
}
