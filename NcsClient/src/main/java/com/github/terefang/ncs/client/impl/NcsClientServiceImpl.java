package com.github.terefang.ncs.client.impl;

import com.github.terefang.ncs.client.NcsClientConfiguration;
import com.github.terefang.ncs.client.NcsClientService;
import com.github.terefang.ncs.common.NcsPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;

import java.util.concurrent.Future;

public class NcsClientServiceImpl implements NcsClientService
{
    NcsClientConfiguration configuration;
    private NioEventLoopGroup _workerGroup = new NioEventLoopGroup();
    private Bootstrap _bootstrap;
    private ChannelFuture _future;

    public void setConfiguration(NcsClientConfiguration _config) {
        this.configuration = _config;
    }

    @Override
    public Future connect() throws Exception {
        _bootstrap = new Bootstrap();
        _bootstrap.group(_workerGroup);
        _bootstrap.channel(NioSocketChannel.class);
        _bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        _bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.configuration.getTimeout());
        _bootstrap.handler(new NcsClientChannelInitializer(
                this.configuration.getMaxFrameLength(),
                this.configuration.getPacketFactory(),
                this.configuration.getPacketListener(),
                this.configuration.getStateListener()));

        _future =  _bootstrap.connect(this.configuration.getEndpointAddress(), this.configuration.getEndpointPort());
        return _future;
    }

    @Override
    public Future disconnect() throws Exception {
        return _future.channel().disconnect();
    }

    @Override
    @SneakyThrows
    public void shutdown() {
        _workerGroup.shutdownGracefully().sync();
    }

    @Override
    public void send(NcsPacket _pkt) {
        _future.channel().write(_pkt);
    }

    @Override
    public void sendAndFlush(NcsPacket _pkt) {
        _future.channel().writeAndFlush(_pkt);
    }
}
