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

package io.advantageous.conekt.spi.metrics;

import io.advantageous.conekt.Context;
import io.advantageous.conekt.IoActor;
import io.advantageous.conekt.datagram.DatagramSocket;
import io.advantageous.conekt.datagram.DatagramSocketOptions;
import io.advantageous.conekt.eventbus.EventBus;
import io.advantageous.conekt.http.HttpClient;
import io.advantageous.conekt.http.HttpClientOptions;
import io.advantageous.conekt.http.HttpServer;
import io.advantageous.conekt.metrics.Measured;
import io.advantageous.conekt.net.*;
import io.advantageous.conekt.http.HttpServerOptions;

/**
 * The main Vert.x metrics SPI which Vert.x will use internally. This interface serves two purposes, one
 * to be called by Vert.x itself for events like verticles deployed, timers created, etc. The other
 * to provide Vert.x with other metrics SPI's which will be used for specific components i.e.
 * {@link HttpServer}, {@link EventBusMetrics}, etc.
 *
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
public interface ConektMetrics extends Metrics, Measured {

    /**
     * Called when a ioActor is deployed in Vert.x .<p/>
     * <p>
     * This method is invoked with {@link Context} and thread of the deployed ioActor and therefore
     * might be  different on every invocation.
     *
     * @param ioActor the ioActor which was deployed
     */
    void verticleDeployed(IoActor ioActor);

    /**
     * Called when a ioActor is undeployed in Vert.x .<p/>
     * <p>
     * This method is invoked with {@link Context} and thread of the deployed ioActor and therefore
     * might be  different on every invocation, however these are the same than the {@link #verticleDeployed} invocation.
     *
     * @param ioActor the ioActor which was undeployed
     */
    void verticleUndeployed(IoActor ioActor);

    /**
     * Called when a timer is created
     * <p>
     * No specific thread and context can be expected when this method is called.
     *
     * @param id the id of the timer
     */
    void timerCreated(long id);

    /**
     * Called when a timer has ended (setTimer) or has been cancelled.<p/>
     * <p>
     * No specific thread and context can be expected when this method is called.
     *
     * @param id        the id of the timer
     * @param cancelled if the timer was cancelled by the user
     */
    void timerEnded(long id, boolean cancelled);

    /**
     * Provides the event bus metrics SPI when the event bus is created.<p/>
     * <p>
     * No specific thread and context can be expected when this method is called.<p/>
     * <p>
     * This method should be called only once.
     *
     * @param eventBus the Vert.x event bus
     * @return the event bus metrics SPI
     */
    EventBusMetrics createMetrics(EventBus eventBus);

    /**
     * Provides the http server metrics SPI when an http server is created.<p/>
     * <p>
     * No specific thread and context can be expected when this method is called.<p/>
     * <p>
     * Note: this method can be called more than one time for the same {@code localAddress} when a server is
     * scaled, it is the responsibility of the metrics implementation to eventually merge metrics. In this case
     * the provided {@code server} argument can be used to distinguish the different {@code HttpServerMetrics}
     * instances.
     *
     * @param server       the Vert.x http server
     * @param localAddress localAddress the local address the net socket is listening on
     * @param options      the options used to create the {@link HttpServer}
     * @return the http server metrics SPI
     */
    HttpServerMetrics<?, ?, ?> createMetrics(HttpServer server, SocketAddress localAddress, HttpServerOptions options);

    /**
     * Provides the http client metrics SPI when an http client has been created.<p/>
     * <p>
     * No specific thread and context can be expected when this method is called.
     *
     * @param client  the Vert.x http client
     * @param options the options used to create the {@link HttpClient}
     * @return the http client metrics SPI
     */
    HttpClientMetrics<?, ?, ?> createMetrics(HttpClient client, HttpClientOptions options);

    /**
     * Provides the net server metrics SPI when a net server is created.<p/>
     * <p>
     * No specific thread and context can be expected when this method is called.<p/>
     * <p>
     * Note: this method can be called more than one time for the same {@code localAddress} when a server is
     * scaled, it is the responsibility of the metrics implementation to eventually merge metrics. In this case
     * the provided {@code server} argument can be used to distinguish the different {@code TCPMetrics}
     * instances.
     *
     * @param server       the Vert.x net server
     * @param localAddress localAddress the local address the net socket is listening on
     * @param options      the options used to create the {@link NetServer}
     * @return the net server metrics SPI
     */
    TCPMetrics<?> createMetrics(NetServer server, SocketAddress localAddress, NetServerOptions options);

    /**
     * Provides the net client metrics SPI when a net client is created.<p/>
     * <p>
     * No specific thread and context can be expected when this method is called.
     *
     * @param client  the Vert.x net client
     * @param options the options used to create the {@link NetClient}
     * @return the net client metrics SPI
     */
    TCPMetrics<?> createMetrics(NetClient client, NetClientOptions options);

    /**
     * Provides the datagram/udp metrics SPI when a datagram socket is created.<p/>
     * <p>
     * No specific thread and context can be expected when this method is called.
     *
     * @param socket  the Vert.x datagram socket
     * @param options the options used to create the {@link DatagramSocket}
     * @return the datagram metrics SPI
     */
    DatagramSocketMetrics createMetrics(DatagramSocket socket, DatagramSocketOptions options);

    /**
     * Metrics cannot use the event bus in their constructor as the event bus is not yet initialized. When the event
     * bus is initialized, this method is called with the event bus instance as parameter. By default, this method does
     * nothing.
     *
     * @param bus the event bus
     */
    default void eventBusInitialized(EventBus bus) {
        // Do nothing by default.
    }
}
