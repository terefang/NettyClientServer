package com.github.terefang.ncs.server;

import com.github.terefang.ncs.common.*;
import com.github.terefang.ncs.common.packet.NcsKeepAlivePacket;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.server.tcp.NcsTcpServerChannelInitializer;
import com.github.terefang.ncs.server.udp.NcsClientUdpConnection;
import com.github.terefang.ncs.server.udp.NcsUdpServerChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
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
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NcsServerServiceImpl implements NcsServerService, NcsKeepAliveListener
{
    private EventLoopGroup _eventLoopGroup;
    private ServerBootstrap _tcpBootstrap;
    private ChannelFuture _future;

    private Bootstrap _udpBootstrap;
    private ChannelGroup _udpChannelGroup;

    NcsServerConfiguration configuration;
    private ScheduledExecutorService _exec;

    public void setConfiguration(NcsServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public NcsServerConfiguration getConfiguration() {
        return configuration;
    }

    List<NcsClientConnection> clientConnections = new Vector<>();
    List<InetSocketAddress> clientUdpConnections = new Vector<>();

    public List<InetSocketAddress> getClientUdpConnections() {
        return clientUdpConnections;
    }

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
        try {
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


        /*
        // local binding information
        if(this.configuration.getEndpointAddress()==null)
        {
            _tcpBootstrap.localAddress(new InetSocketAddress(this.configuration.getEndpointPort()));
        }
        else
        {
            _tcpBootstrap.localAddress(new InetSocketAddress(this.configuration.getEndpointAddress(), this.configuration.getEndpointPort()));
        }
        */

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
                if(this.configuration.getEndpointAddress()==null)
                {
                    _future = _tcpBootstrap.bind(this.configuration.getEndpointPort());
                }
                else
                {
                    _future = _tcpBootstrap.bind(this.configuration.getEndpointAddress(), this.configuration.getEndpointPort());
                }
            }
            return _future;
        }
        finally
        {
            if(this.configuration.getClientKeepAliveTimeout()>0 && this.configuration.getClientKeepAliveCounterMax()>0)
            {
                this._exec = Executors.newSingleThreadScheduledExecutor();
                _exec.scheduleAtFixedRate(() -> {
                    // Send out KeepAlives
                    if(this.configuration.isUseUdp())
                    {
                        for(InetSocketAddress _peer : this.getClientUdpConnections())
                        {
                            this.doAliveCriteriaAndAlert(null, _peer);
                        }
                    }
                    else
                    {
                        for(NcsClientConnection _peer : this.listActivePeers())
                        {
                            this.doAliveCriteriaAndAlert(_peer, _peer.getPeer().asInetSocketAddress());
                        }
                    }

                }, this.configuration.getClientKeepAliveTimeout()*5, this.configuration.getClientKeepAliveTimeout(), TimeUnit.MILLISECONDS);
            }
        }
    }

    private void doAliveCriteriaAndAlert(NcsClientConnection _peerConn, InetSocketAddress _address)
    {
        NcsEndpoint _endp = NcsEndpoint.from(_address);
        NcsClientStatsHolder _stats = this.getOrCreateClientStats(_address);
        int _j = _stats.getSentKeepAlives().get();

        if(_j>this.configuration.getClientKeepAliveCounterMax() && (this.configuration.getStateListener() instanceof NcsKeepAliveFailListener))
        {
            if(this.configuration.isUseUdp())
            {
                ((NcsKeepAliveFailListener)this.configuration.getStateListener()).onKeepAliveFail(this._udpConnection, this.configuration.getClientKeepAliveTimeout(), _j, _endp);
                if(this.configuration.isClientKeepAliveUdpAutoDisconnect())
                {
                    this.getClientUdpConnections().remove(_address);
                    this.clientStatsHolder.remove(_address);
                }
            }
            else
            {
                ((NcsKeepAliveFailListener)this.configuration.getStateListener()).onKeepAliveFail(_peerConn, this.configuration.getClientKeepAliveTimeout(), _j, _endp);
                if(this.configuration.isClientKeepAliveTcpAutoDisconnect())
                {
                    try { _peerConn.getChannel().close(); } catch (Exception _xe) { /*IGNORE*/}
                }
            }
            _stats.resetAlives();
        }
        else
        {
            NcsPacket _pkt = NcsKeepAlivePacket.create(System.currentTimeMillis(), _address);
            if(this.configuration.isUseUdp())
            {
                this.getChannel().writeAndFlush(_pkt);
            }
            else
            {
                _peerConn.sendAndFlush(_pkt);
            }
            _stats.incrSent();
        }
    }

    @Override
    public ChannelFuture stop()
    {
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

    @Override
    public List<InetSocketAddress> listActiveUdpPeers() {
        return Collections.unmodifiableList(this.clientUdpConnections);
    }

    Map<InetSocketAddress, NcsClientStatsHolder> clientStatsHolder = new HashMap<>();

    @Override
    public NcsClientStatsHolder getClientStats(InetSocketAddress _address) {
        return this.clientStatsHolder.get(_address);
    }

    public NcsClientStatsHolder getOrCreateClientStats(InetSocketAddress _address)
    {
        if(!this.clientStatsHolder.containsKey(_address))
        {
            NcsClientStatsHolder _h = new NcsClientStatsHolder();
            this.clientStatsHolder.put(_address, _h);
        }
        return this.clientStatsHolder.get(_address);
    }

    public boolean hasClientStats(InetSocketAddress _address) {
        return this.clientStatsHolder.containsKey(_address);
    }

    NcsClientUdpConnection _udpConnection = null;

    @Override
    public Channel getChannel() {
        return _future.channel();
    }

    @Override
    public void onKeepAlivePacket(NcsConnection _connection, NcsPacket _packet)
    {
        NcsClientStatsHolder _stats = this.getOrCreateClientStats(_packet.getAddress());
        _stats.resetAlives();
        _stats.recalcRTT(System.currentTimeMillis(), ((NcsKeepAlivePacket)_packet).getTimestamp());
    }
}
