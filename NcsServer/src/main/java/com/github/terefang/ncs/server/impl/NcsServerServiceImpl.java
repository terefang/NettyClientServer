package com.github.terefang.ncs.server.impl;

import com.github.terefang.ncs.common.*;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.server.NcsClientConnection;
import com.github.terefang.ncs.server.NcsServerConfiguration;
import com.github.terefang.ncs.server.NcsServerService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class NcsServerServiceImpl implements NcsServerService
{
    private EventLoopGroup _tcpEventLoopGroup;
    private ServerBootstrap _tcpBootstrap;
    private ChannelFuture _future;

    NcsServerConfiguration configuration;

    public void setConfiguration(NcsServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public NcsServerConfiguration getConfiguration() {
        return configuration;
    }

    List<NcsClientConnection> clientConnections = new Vector<NcsClientConnection>();

    @Override
    public NcsPacket createPacket()
    {
        // use registered packet factory to create possible custom user packets
        return this.configuration.getPacketFactory().create();
    }

    @Override
    public ChannelFuture start()
    {
        // initialize worker thread groups
        if(this.configuration.isUseEpoll())
        {
            _tcpEventLoopGroup = new EpollEventLoopGroup(this.configuration.getWorkers());
        }
        else
        {
            _tcpEventLoopGroup = new NioEventLoopGroup(this.configuration.getWorkers());
        }

        // build server bootstrap config
        _tcpBootstrap = new ServerBootstrap();
        _tcpBootstrap.group(_tcpEventLoopGroup);
        _tcpBootstrap.channel(this.configuration.isUseEpoll()
                        ? EpollServerSocketChannel.class
                        : NioServerSocketChannel.class);

        // local binding information
        if(this.configuration.getEndpointAddress()==null)
        {
            _tcpBootstrap.localAddress(new InetSocketAddress(this.configuration.getEndpointPort()));
        }
        else
        {
            _tcpBootstrap.localAddress(new InetSocketAddress(this.configuration.getEndpointAddress(), this.configuration.getEndpointPort()));
        }

        // set connection characteristics
        _tcpBootstrap.option(ChannelOption.SO_REUSEADDR, true);
        _tcpBootstrap.option(ChannelOption.SO_BACKLOG, this.configuration.getBacklog());
        _tcpBootstrap.option(ChannelOption.SO_RCVBUF, this.configuration.getRecvBufferSize());
        _tcpBootstrap.childOption(ChannelOption.SO_SNDBUF, this.configuration.getSendBufferSize());
        _tcpBootstrap.childOption(ChannelOption.TCP_NODELAY, this.configuration.isTcpNoDelay());
        _tcpBootstrap.childOption(ChannelOption.SO_KEEPALIVE, this.configuration.isKeepAlive());
        _tcpBootstrap.childOption(ChannelOption.SO_LINGER, this.configuration.getLinger());

        // protocol channel initializer
        _tcpBootstrap.childHandler(new NcsServerChannelInitializer(this, this.configuration));

        // start and bind
        _future = _tcpBootstrap.bind();
        return _future;
    }

    @Override
    public ChannelFuture stop() {
        return _future.channel().close();
    }

    @Override
    public List<NcsClientConnection> listActivePeers() {
        return Collections.unmodifiableList(this.clientConnections);
    }

}
