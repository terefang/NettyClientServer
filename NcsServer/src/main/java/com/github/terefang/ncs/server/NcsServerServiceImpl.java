package com.github.terefang.ncs.server;

import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.server.NcsClientConnection;
import com.github.terefang.ncs.server.NcsServerConfiguration;
import com.github.terefang.ncs.server.NcsServerService;
import com.github.terefang.ncs.server.tcp.NcsTcpServerChannelInitializer;
import com.github.terefang.ncs.server.udp.NcsUdpServerChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class NcsServerServiceImpl implements NcsServerService
{
    private EventLoopGroup _eventLoopGroup;
    private ServerBootstrap _tcpBootstrap;
    private ChannelFuture _future;

    private Bootstrap _udpBootstrap;
    private ChannelGroup _udpChannelGroup;

    NcsServerConfiguration configuration;

    public void setConfiguration(NcsServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public NcsServerConfiguration getConfiguration() {
        return configuration;
    }

    List<NcsClientConnection> clientConnections = new Vector<NcsClientConnection>();

    public List<NcsClientConnection> getClientConnections() {
        return clientConnections;
    }

    @Override
    public NcsPacket createPacket()
    {
        // use registered packet factory to create possible custom user packets
        return this.configuration.getPacketFactory().create();
    }

    @Override
    @SneakyThrows
    public ChannelFuture start()
    {
        // initialize worker thread groups
        if(this.configuration.isUseEpoll())
        {
            _eventLoopGroup = new EpollEventLoopGroup(this.configuration.getWorkers());
        }
        else
        {
            _eventLoopGroup = new NioEventLoopGroup(this.configuration.getWorkers());
        }

        if(this.configuration.isUseUdp())
        {
            this._udpChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

            // build server bootstrap config
            _udpBootstrap = new Bootstrap();
            _udpBootstrap.group(_eventLoopGroup);
            _udpBootstrap.channel(this.configuration.isUseEpoll()
                    ? EpollDatagramChannel.class
                    : NioDatagramChannel.class);

            // local binding information
            if(this.configuration.getEndpointAddress()==null)
            {
                _udpBootstrap.localAddress(new InetSocketAddress(this.configuration.getEndpointPort()));
            }
            else
            {
                _udpBootstrap.localAddress(new InetSocketAddress(this.configuration.getEndpointAddress(), this.configuration.getEndpointPort()));
            }

            // set connection characteristics
            _udpBootstrap.option(ChannelOption.AUTO_CLOSE, true);
            _udpBootstrap.option(ChannelOption.SO_RCVBUF, this.configuration.getRecvBufferSize());
            _udpBootstrap.option(ChannelOption.SO_SNDBUF, this.configuration.getSendBufferSize());

            // protocol channel initializer
            _udpBootstrap.handler(new NcsUdpServerChannelInitializer(this, this.configuration));

            // start and bind
            _future = _udpBootstrap.bind().sync();
            this._udpChannelGroup.add(_future.channel());
        }
        else
        {
            // build server bootstrap config
            _tcpBootstrap = new ServerBootstrap();
            _tcpBootstrap.group(_eventLoopGroup);
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
            _tcpBootstrap.childHandler(new NcsTcpServerChannelInitializer(this, this.configuration));

            // start and bind
            _future = _tcpBootstrap.bind();
        }
        return _future;
    }

    @Override
    public ChannelFuture stop() {
        if(this.configuration.isUseUdp())
        {
            return (ChannelFuture) _udpChannelGroup.close();
        }
        else
        {
            return _future.channel().close();
        }
    }

    @Override
    public List<NcsClientConnection> listActivePeers() {
        return Collections.unmodifiableList(this.clientConnections);
    }

}
