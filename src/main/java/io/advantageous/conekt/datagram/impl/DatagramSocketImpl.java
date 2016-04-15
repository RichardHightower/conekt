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
package io.advantageous.conekt.datagram.impl;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.advantageous.conekt.AsyncResult;
import io.advantageous.conekt.Future;
import io.advantageous.conekt.Handler;
import io.advantageous.conekt.buffer.Buffer;
import io.advantageous.conekt.datagram.DatagramSocket;
import io.advantageous.conekt.datagram.DatagramSocketOptions;
import io.advantageous.conekt.datagram.PacketWritestream;
import io.advantageous.conekt.impl.Arguments;
import io.advantageous.conekt.impl.ContextImpl;
import io.advantageous.conekt.impl.VertxInternal;
import io.advantageous.conekt.net.NetworkOptions;
import io.advantageous.conekt.net.impl.ConnectionBase;
import io.advantageous.conekt.net.impl.SocketAddressImpl;
import io.advantageous.conekt.spi.metrics.DatagramSocketMetrics;
import io.advantageous.conekt.spi.metrics.Metrics;
import io.advantageous.conekt.spi.metrics.MetricsProvider;
import io.advantageous.conekt.spi.metrics.NetworkMetrics;

import java.net.*;
import java.util.Objects;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class DatagramSocketImpl extends ConnectionBase implements DatagramSocket, MetricsProvider {

    private Handler<io.advantageous.conekt.datagram.DatagramPacket> packetHandler;

    public DatagramSocketImpl(VertxInternal vertx, DatagramSocketOptions options) {
        super(vertx, createChannel(options.isIpV6() ? io.advantageous.conekt.datagram.impl.InternetProtocolFamily.IPv6 : io.advantageous.conekt.datagram.impl.InternetProtocolFamily.IPv4,
                new DatagramSocketOptions(options)), vertx.getOrCreateContext(), options);
        ContextImpl creatingContext = vertx.getContext();
        if (creatingContext != null && creatingContext.isMultiThreadedWorkerContext()) {
            throw new IllegalStateException("Cannot use DatagramSocket in a multi-threaded worker verticle");
        }
        channel().config().setOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, true);
        context.nettyEventLoop().register(channel);
        channel.pipeline().addLast("handler", new DatagramServerHandler(this));
        channel().config().setMaxMessagesPerRead(1);
    }

    private static NioDatagramChannel createChannel(io.advantageous.conekt.datagram.impl.InternetProtocolFamily family,
                                                    DatagramSocketOptions options) {
        NioDatagramChannel channel;
        if (family == null) {
            channel = new NioDatagramChannel();
        } else {
            switch (family) {
                case IPv4:
                    channel = new NioDatagramChannel(InternetProtocolFamily.IPv4);
                    break;
                case IPv6:
                    channel = new NioDatagramChannel(InternetProtocolFamily.IPv6);
                    break;
                default:
                    channel = new NioDatagramChannel();
            }
        }
        if (options.getSendBufferSize() != -1) {
            channel.config().setSendBufferSize(options.getSendBufferSize());
        }
        if (options.getReceiveBufferSize() != -1) {
            channel.config().setReceiveBufferSize(options.getReceiveBufferSize());
            channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(options.getReceiveBufferSize()));
        }
        channel.config().setReuseAddress(options.isReuseAddress());
        if (options.getTrafficClass() != -1) {
            channel.config().setTrafficClass(options.getTrafficClass());
        }
        channel.config().setBroadcast(options.isBroadcast());
        channel.config().setLoopbackModeDisabled(options.isLoopbackModeDisabled());
        if (options.getMulticastTimeToLive() != -1) {
            channel.config().setTimeToLive(options.getMulticastTimeToLive());
        }
        if (options.getMulticastNetworkInterface() != null) {
            try {
                channel.config().setNetworkInterface(NetworkInterface.getByName(options.getMulticastNetworkInterface()));
            } catch (SocketException e) {
                throw new IllegalArgumentException("Could not find network interface with name " + options.getMulticastNetworkInterface());
            }
        }
        return channel;
    }

    @Override
    protected NetworkMetrics createMetrics(NetworkOptions options) {
        return vertx.metricsSPI().createMetrics(this, (DatagramSocketOptions) options);
    }

    @Override
    protected Object metric() {
        return null;
    }

    @Override
    public DatagramSocket listenMulticastGroup(String multicastAddress, Handler<AsyncResult<DatagramSocket>> handler) {
        try {
            addListener(channel().joinGroup(InetAddress.getByName(multicastAddress)), handler);
        } catch (UnknownHostException e) {
            notifyException(handler, e);
        }
        return this;
    }

    @Override
    public DatagramSocket listenMulticastGroup(String multicastAddress, String networkInterface, String source, Handler<AsyncResult<DatagramSocket>> handler) {
        try {
            InetAddress sourceAddress;
            if (source == null) {
                sourceAddress = null;
            } else {
                sourceAddress = InetAddress.getByName(source);
            }
            addListener(channel().joinGroup(InetAddress.getByName(multicastAddress),
                    NetworkInterface.getByName(networkInterface), sourceAddress), handler);
        } catch (Exception e) {
            notifyException(handler, e);
        }
        return this;
    }

    @Override
    public DatagramSocket unlistenMulticastGroup(String multicastAddress, Handler<AsyncResult<DatagramSocket>> handler) {
        try {
            addListener(channel().leaveGroup(InetAddress.getByName(multicastAddress)), handler);
        } catch (UnknownHostException e) {
            notifyException(handler, e);
        }
        return this;
    }

    @Override
    public DatagramSocket unlistenMulticastGroup(String multicastAddress, String networkInterface, String source, Handler<AsyncResult<DatagramSocket>> handler) {
        try {
            InetAddress sourceAddress;
            if (source == null) {
                sourceAddress = null;
            } else {
                sourceAddress = InetAddress.getByName(source);
            }
            addListener(channel().leaveGroup(InetAddress.getByName(multicastAddress),
                    NetworkInterface.getByName(networkInterface), sourceAddress), handler);
        } catch (Exception e) {
            notifyException(handler, e);
        }
        return this;
    }

    @Override
    public DatagramSocket blockMulticastGroup(String multicastAddress, String networkInterface, String sourceToBlock, Handler<AsyncResult<DatagramSocket>> handler) {
        try {
            InetAddress sourceAddress;
            if (sourceToBlock == null) {
                sourceAddress = null;
            } else {
                sourceAddress = InetAddress.getByName(sourceToBlock);
            }
            addListener(channel().block(InetAddress.getByName(multicastAddress),
                    NetworkInterface.getByName(networkInterface), sourceAddress), handler);
        } catch (Exception e) {
            notifyException(handler, e);
        }
        return this;
    }

    @Override
    public DatagramSocket blockMulticastGroup(String multicastAddress, String sourceToBlock, Handler<AsyncResult<DatagramSocket>> handler) {
        try {
            addListener(channel().block(InetAddress.getByName(multicastAddress), InetAddress.getByName(sourceToBlock)), handler);
        } catch (UnknownHostException e) {
            notifyException(handler, e);
        }
        return this;
    }

    @Override
    public DatagramSocket listen(int port, String address, Handler<AsyncResult<DatagramSocket>> handler) {
        return listen(new SocketAddressImpl(port, address), handler);
    }

    @Override
    public synchronized DatagramSocket handler(Handler<io.advantageous.conekt.datagram.DatagramPacket> handler) {
        this.packetHandler = handler;
        return this;
    }

    @Override
    public synchronized DatagramSocket endHandler(Handler<Void> endHandler) {
        this.closeHandler = endHandler;
        return this;
    }

    @Override
    public synchronized DatagramSocket exceptionHandler(Handler<Throwable> handler) {
        this.exceptionHandler = handler;
        return this;
    }

    private DatagramSocket listen(io.advantageous.conekt.net.SocketAddress local, Handler<AsyncResult<DatagramSocket>> handler) {
        Objects.requireNonNull(handler, "no null handler accepted");
        InetSocketAddress is = new InetSocketAddress(local.host(), local.port());
        ChannelFuture future = channel().bind(is);
        addListener(future, ar -> {
            if (ar.succeeded()) {
                ((DatagramSocketMetrics) metrics).listening(local);
            }
            handler.handle(ar);
        });
        return this;
    }

    @SuppressWarnings("unchecked")
    final void addListener(ChannelFuture future, Handler<AsyncResult<DatagramSocket>> handler) {
        if (handler != null) {
            future.addListener(new DatagramChannelFutureListener<>(this, handler, context));
        }
    }

    @SuppressWarnings("unchecked")
    public DatagramSocket pause() {
        doPause();
        return this;
    }

    @SuppressWarnings("unchecked")
    public DatagramSocket resume() {
        doResume();
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DatagramSocket send(Buffer packet, int port, String host, Handler<AsyncResult<DatagramSocket>> handler) {
        Objects.requireNonNull(host, "no null host accepted");
        ChannelFuture future = channel().writeAndFlush(new DatagramPacket(packet.getByteBuf(), new InetSocketAddress(host, port)));
        addListener(future, handler);
        if (metrics.isEnabled()) {
            metrics.bytesWritten(null, new SocketAddressImpl(port, host), packet.length());
        }

        return this;
    }

    @Override
    public PacketWritestream sender(int port, String host) {
        Arguments.requireInRange(port, 0, 65535, "port p must be in range 0 <= p <= 65535");
        Objects.requireNonNull(host, "no null host accepted");
        return new PacketWriteStreamImpl(this, port, host);
    }

    @Override
    public DatagramSocket send(String str, int port, String host, Handler<AsyncResult<DatagramSocket>> handler) {
        return send(Buffer.buffer(str), port, host, handler);
    }

    @Override
    public DatagramSocket send(String str, String enc, int port, String host, Handler<AsyncResult<DatagramSocket>> handler) {
        return send(Buffer.buffer(str, enc), port, host, handler);
    }

    @Override
    public void close(final Handler<AsyncResult<Void>> handler) {
        // make sure everything is flushed out on close
        endReadAndFlush();
        metrics.close();
        ChannelFuture future = channel.close();
        if (handler != null) {
            future.addListener(new DatagramChannelFutureListener<>(null, handler, context));
        }
    }

    @Override
    public boolean isMetricsEnabled() {
        return metrics != null && metrics.isEnabled();
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
    }

    protected DatagramChannel channel() {
        return (DatagramChannel) channel;
    }

    private void notifyException(final Handler<AsyncResult<DatagramSocket>> handler, final Throwable cause) {
        context.executeFromIO(() -> handler.handle(Future.failedFuture(cause)));
    }

    @Override
    protected void finalize() throws Throwable {
        // Make sure this gets cleaned up if there are no more references to it
        // so as not to leave connections and resources dangling until the system is shutdown
        // which could make the JVM run out of file handles.
        close();
        super.finalize();
    }

    protected void handleClosed() {
        checkContext();
        super.handleClosed();
    }

    synchronized void handlePacket(io.advantageous.conekt.datagram.DatagramPacket packet) {
        if (metrics.isEnabled()) {
            metrics.bytesRead(null, packet.sender(), packet.data().length());
        }
        if (packetHandler != null) {
            packetHandler.handle(packet);
        }
    }

    @Override
    protected void handleInterestedOpsChanged() {
    }
}
