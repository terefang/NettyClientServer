package com.github.terefang.ncs.server.impl;

import com.github.terefang.ncs.common.*;
import com.github.terefang.ncs.server.NcsServerConfiguration;
import com.github.terefang.ncs.server.NcsServerService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

public class NcsServerServiceImpl implements NcsServerService
{
    private NioEventLoopGroup _tcpEventLoopGroup;
    private ServerBootstrap _tcpBootstrap;

    NcsServerConfiguration configuration;

    public void setConfiguration(NcsServerConfiguration configuration) {
        this.configuration = configuration;
    }


    @Override
    public Future start()
    {
        _tcpEventLoopGroup = new NioEventLoopGroup(this.configuration.getWorkers());

        _tcpBootstrap = new ServerBootstrap();
        _tcpBootstrap.group(_tcpEventLoopGroup);
        _tcpBootstrap.channel(NioServerSocketChannel.class);

        if(this.configuration.getEndpointAddress()==null)
        {
            _tcpBootstrap.localAddress(new InetSocketAddress(this.configuration.getEndpointPort()));
        }
        else
        {
            _tcpBootstrap.localAddress(new InetSocketAddress(this.configuration.getEndpointAddress(), this.configuration.getEndpointPort()));
        }

        _tcpBootstrap.option(ChannelOption.SO_BACKLOG, this.configuration.getBacklog());
        _tcpBootstrap.option(ChannelOption.SO_REUSEADDR, true);
        //_tcpBootstrap.option(ChannelOption.TCP_NODELAY, true);
        //_tcpBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        //_tcpBootstrap.option(ChannelOption.SO_LINGER, 0);

        _tcpBootstrap.childHandler(new NcsServerChannelInitializer(
                this.configuration.getMaxFrameLength(),
                this.configuration.getPacketFactory(),
                this.configuration.getPacketListener(),
                this.configuration.getStateListener()));

        return _tcpBootstrap.bind();
    }

    @Override
    public Future stop() {
        return null;
    }
}
