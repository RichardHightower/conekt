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
import io.advantageous.conekt.DeploymentOptions;
import io.advantageous.conekt.dns.*;
import io.advantageous.conekt.test.fakedns.FakeDNSServer;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class DNSTest extends VertxTestBase {

    private FakeDNSServer dnsServer;

    @Test
    public void testIllegalArguments() throws Exception {
        DnsClient dns = prepareDns(FakeDNSServer.testResolveAAAA("::1"));

        TestUtils.assertNullPointerException(() -> dns.lookup(null, ar -> {
        }));
        TestUtils.assertNullPointerException(() -> dns.lookup4(null, ar -> {
        }));
        TestUtils.assertNullPointerException(() -> dns.lookup6(null, ar -> {
        }));
        TestUtils.assertNullPointerException(() -> dns.resolveA(null, ar -> {
        }));
        TestUtils.assertNullPointerException(() -> dns.resolveAAAA(null, ar -> {
        }));
        TestUtils.assertNullPointerException(() -> dns.resolveCNAME(null, ar -> {
        }));
        TestUtils.assertNullPointerException(() -> dns.resolveMX(null, ar -> {
        }));
        TestUtils.assertNullPointerException(() -> dns.resolveTXT(null, ar -> {
        }));
        TestUtils.assertNullPointerException(() -> dns.resolvePTR(null, ar -> {
        }));
        TestUtils.assertNullPointerException(() -> dns.resolveNS(null, ar -> {
        }));
        TestUtils.assertNullPointerException(() -> dns.resolveSRV(null, ar -> {
        }));

        dnsServer.stop();
    }

    @Test
    public void testResolveA() throws Exception {
        final String ip = "10.0.0.1";
        DnsClient dns = prepareDns(FakeDNSServer.testResolveA(ip));

        dns.resolveA("conekt.io", ar -> {
            List<String> result = ar.result();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(ip, result.get(0));
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testResolveAAAA() throws Exception {
        DnsClient dns = prepareDns(FakeDNSServer.testResolveAAAA("::1"));

        dns.resolveAAAA("conekt.io", ar -> {
            List<String> result = ar.result();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals("0:0:0:0:0:0:0:1", result.get(0));
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testResolveMX() throws Exception {
        final String mxRecord = "mail.conekt.io";
        final int prio = 10;
        DnsClient dns = prepareDns(FakeDNSServer.testResolveMX(prio, mxRecord));

        dns.resolveMX("conekt.io", ar -> {
            List<MxRecord> result = ar.result();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            MxRecord record = result.get(0);
            assertEquals(prio, record.priority());
            assertEquals(record.name(), mxRecord);
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testResolveTXT() throws Exception {
        final String txt = "conekt is awesome";
        DnsClient dns = prepareDns(FakeDNSServer.testResolveTXT(txt));

        dns.resolveTXT("conekt.io", ar -> {
            List<String> result = ar.result();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(txt, result.get(0));
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testResolveNS() throws Exception {
        final String ns = "ns.conekt.io";
        DnsClient dns = prepareDns(FakeDNSServer.testResolveNS(ns));

        dns.resolveNS("conekt.io", ar -> {
            List<String> result = ar.result();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(ns, result.get(0));
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testResolveCNAME() throws Exception {
        final String cname = "cname.conekt.io";
        DnsClient dns = prepareDns(FakeDNSServer.testResolveCNAME(cname));

        dns.resolveCNAME("conekt.io", ar -> {
            List<String> result = ar.result();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            String record = result.get(0);
            assertFalse(record.isEmpty());
            assertEquals(cname, record);
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testResolvePTR() throws Exception {
        final String ptr = "ptr.conekt.io";
        DnsClient dns = prepareDns(FakeDNSServer.testResolvePTR(ptr));

        dns.resolvePTR("10.0.0.1.in-addr.arpa", ar -> {
            String result = ar.result();
            assertNotNull(result);
            assertEquals(ptr, result);
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testResolveSRV() throws Exception {
        final int priority = 10;
        final int weight = 1;
        final int port = 80;
        final String target = "conekt.io";

        DnsClient dns = prepareDns(FakeDNSServer.testResolveSRV(priority, weight, port, target));

        dns.resolveSRV("conekt.io", ar -> {
            List<SrvRecord> result = ar.result();
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());

            SrvRecord record = result.get(0);

            assertEquals(priority, record.priority());
            assertEquals(weight, record.weight());
            assertEquals(port, record.port());
            assertEquals(target, record.target());

            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testLookup4() throws Exception {
        final String ip = "10.0.0.1";
        DnsClient dns = prepareDns(FakeDNSServer.testLookup4(ip));

        dns.lookup4("conekt.io", ar -> {
            String result = ar.result();
            assertNotNull(result);
            assertEquals(ip, result);
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testLookup6() throws Exception {
        DnsClient dns = prepareDns(FakeDNSServer.testLookup6());

        dns.lookup6("conekt.io", ar -> {
            String result = ar.result();
            assertNotNull(result);
            assertEquals("0:0:0:0:0:0:0:1", result);
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testLookup() throws Exception {
        final String ip = "10.0.0.1";
        DnsClient dns = prepareDns(FakeDNSServer.testLookup(ip));

        dns.lookup("conekt.io", ar -> {
            String result = ar.result();
            assertNotNull(result);
            assertEquals(ip, result);
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testLookupNonExisting() throws Exception {
        DnsClient dns = prepareDns(FakeDNSServer.testLookupNonExisting());
        dns.lookup("gfegjegjf.sg1", ar -> {
            DnsException cause = (DnsException) ar.cause();
            assertEquals(DnsResponseCode.NXDOMAIN, cause.code());
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testReverseLookupIpv4() throws Exception {
        String address = "10.0.0.1";
        final String ptr = "ptr.conekt.io";
        DnsClient dns = prepareDns(FakeDNSServer.testReverseLookup(ptr));

        dns.reverseLookup(address, ar -> {
            String result = ar.result();
            assertNotNull(result);
            assertEquals(ptr, result);
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testReverseLookupIpv6() throws Exception {
        final String ptr = "ptr.conekt.io";

        DnsClient dns = prepareDns(FakeDNSServer.testReverseLookup(ptr));

        dns.reverseLookup("::1", ar -> {
            String result = ar.result();
            assertNotNull(result);
            assertEquals(ptr, result);
            testComplete();
        });
        await();
        dnsServer.stop();
    }

    @Test
    public void testUseInMultithreadedWorker() throws Exception {
        class MyIoActor extends AbstractIoActor {
            @Override
            public void start() {
                TestUtils.assertIllegalStateException(() -> conekt.createDnsClient(1234, "localhost"));
                testComplete();
            }
        }
        MyIoActor verticle = new MyIoActor();
        conekt.deployVerticle(verticle, new DeploymentOptions().setWorker(true).setMultiThreaded(true));
        await();
    }

    private DnsClient prepareDns(FakeDNSServer server) throws Exception {
        dnsServer = server;
        dnsServer.start();
        InetSocketAddress addr = (InetSocketAddress) dnsServer.getTransports()[0].getAcceptor().getLocalAddress();
        return conekt.createDnsClient(addr.getPort(), addr.getAddress().getHostAddress());
    }

}
