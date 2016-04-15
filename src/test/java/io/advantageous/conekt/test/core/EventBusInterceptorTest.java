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

import io.advantageous.conekt.Handler;
import io.advantageous.conekt.eventbus.EventBus;
import io.advantageous.conekt.eventbus.SendContext;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class EventBusInterceptorTest extends VertxTestBase {

    protected EventBus eb;

    @Test
    public void testInterceptorSend() {
        eb.addInterceptor(sc -> {
            assertEquals("armadillo", sc.message().body());
            assertTrue(sc.send());
            sc.next();
        });
        eb.consumer("some-address", msg -> {
            assertEquals("armadillo", msg.body());
            testComplete();
        });
        eb.send("some-address", "armadillo");
        await();
    }

    @Test
    public void testInterceptorPublish() {
        eb.addInterceptor(sc -> {
            assertEquals("armadillo", sc.message().body());
            assertFalse(sc.send());
            sc.next();
        });
        eb.consumer("some-address", msg -> {
            assertEquals("armadillo", msg.body());
            testComplete();
        });
        eb.publish("some-address", "armadillo");
        await();
    }

    @Test
    public void testInterceptorNoNext() {
        eb.addInterceptor(sc -> {
            assertEquals("armadillo", sc.message().body());
        });
        eb.consumer("some-address", msg -> {
            fail("Should not receive message");
        });
        eb.send("some-address", "armadillo");
        conekt.setTimer(200, tid -> testComplete());
        await();
    }

    @Test
    public void testMultipleInterceptors() {
        AtomicInteger cnt = new AtomicInteger();
        int interceptorNum = 10;
        for (int i = 0; i < interceptorNum; i++) {
            final int expectedCount = i;
            eb.addInterceptor(sc -> {
                assertEquals("armadillo", sc.message().body());
                int count = cnt.getAndIncrement();
                assertEquals(expectedCount, count);
                sc.next();
            });
        }
        eb.consumer("some-address", msg -> {
            assertEquals("armadillo", msg.body());
            assertEquals(interceptorNum, cnt.get());
            testComplete();
        });
        eb.send("some-address", "armadillo");
        await();
    }

    @Test
    public void testRemoveInterceptor() {

        AtomicInteger cnt1 = new AtomicInteger();
        AtomicInteger cnt2 = new AtomicInteger();

        Handler<SendContext> eb1 = sc -> {
            cnt1.incrementAndGet();
            sc.next();
        };

        Handler<SendContext> eb2 = sc -> {
            cnt2.incrementAndGet();
            sc.next();
        };

        eb.addInterceptor(eb1).addInterceptor(eb2);

        eb.consumer("some-address", msg -> {
            if (msg.body().equals("armadillo")) {
                assertEquals(1, cnt1.get());
                assertEquals(1, cnt2.get());
                eb.removeInterceptor(eb2);
                eb.send("some-address", "aardvark");
            } else if (msg.body().equals("aardvark")) {
                assertEquals(2, cnt1.get());
                assertEquals(1, cnt2.get());
                testComplete();
            } else {
                fail("wrong body");
            }
        });
        eb.send("some-address", "armadillo");
        await();
    }

    @Test
    public void testInterceptorOnReply() {
        AtomicInteger cnt = new AtomicInteger();
        eb.addInterceptor(sc -> {
            if (sc.message().body().equals("armadillo")) {
                assertEquals(0, cnt.get());
            } else if (sc.message().body().equals("echidna")) {
                assertEquals(1, cnt.get());
            } else {
                fail("wrong body");
            }
            cnt.incrementAndGet();
            sc.next();
        });
        eb.consumer("some-address", msg -> {
            assertEquals("armadillo", msg.body());
            assertEquals(1, cnt.get());
            msg.reply("echidna");
        });
        eb.send("some-address", "armadillo", reply -> {
            assertEquals("echidna", reply.result().body());
            assertEquals(2, cnt.get());
            testComplete();
        });
        await();
    }

    @Test
    public void testExceptionInInterceptor() {
        AtomicInteger cnt = new AtomicInteger();

        Handler<SendContext> eb1 = sc -> {
            cnt.incrementAndGet();
            conekt.runOnContext(v -> sc.next());
            throw new RuntimeException("foo");
        };

        Handler<SendContext> eb2 = sc -> {
            cnt.incrementAndGet();
            sc.next();
        };

        eb.addInterceptor(eb1).addInterceptor(eb2);

        eb.consumer("some-address", msg -> {
            assertEquals("armadillo", msg.body());
            assertEquals(2, cnt.get());
            testComplete();
        });
        eb.send("some-address", "armadillo");
        await();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        eb = conekt.eventBus();
    }
}
