package com.github.terefang.ncs.server.impl;

import com.github.terefang.ncs.common.*;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.common.NcsPacketListener;
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

public class NcsServerServiceImpl implements NcsServerService
{
    private EventLoopGroup _tcpEventLoopGroup;
    private ServerBootstrap _tcpBootstrap;
    private ChannelFuture _future;

    NcsServerConfiguration configuration;

    public void setConfiguration(NcsServerConfiguration configuration) {
        this.configuration = configuration;
    }

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
        _tcpBootstrap.childHandler(new NcsServerChannelInitializer(this.configuration));

        // start and bind
        _future = _tcpBootstrap.bind();
        return _future;
    }

    @Override
    public ChannelFuture stop() {
        return _future.channel().close();
    }

    public void setPacketFactory(NcsPacketFactory packetFactory) {
        configuration.setPacketFactory(packetFactory);
    }

    public void setPacketListener(NcsPacketListener packetListener) {
        configuration.setPacketListener(packetListener);
    }

    public void setStateListener(NcsStateListener stateListener) {
        configuration.setStateListener(stateListener);
    }

    public void setTimeout(int timeout) {
        configuration.setTimeout(timeout);
    }

    public void setMaxFrameLength(int maxFrameLength) {
        configuration.setMaxFrameLength(maxFrameLength);
    }

    public void setRecvBufferSize(int recvBufferSize) {
        configuration.setRecvBufferSize(recvBufferSize);
    }

    public void setSendBufferSize(int sendBufferSize) {
        configuration.setSendBufferSize(sendBufferSize);
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        configuration.setTcpNoDelay(tcpNoDelay);
    }

    public void setKeepAlive(boolean keepAlive) {
        configuration.setKeepAlive(keepAlive);
    }

    public void setLinger(int linger) {
        configuration.setLinger(linger);
    }

    @SneakyThrows
    public void setUseEpoll(boolean _use) {
        configuration.setUseEpoll(_use);
    }

    @SneakyThrows
    public void setEndpoint(String _s, int _p) {
        configuration.setEndpoint(_s, _p);
    }
}
