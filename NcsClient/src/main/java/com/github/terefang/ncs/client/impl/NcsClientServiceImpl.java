package com.github.terefang.ncs.client.impl;

import com.github.terefang.ncs.client.NcsClientConfiguration;
import com.github.terefang.ncs.client.NcsClientService;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
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

        _bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.configuration.getTimeout());

        _bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        _bootstrap.option(ChannelOption.SO_RCVBUF, this.configuration.getRecvBufferSize());

        _bootstrap.option(ChannelOption.SO_SNDBUF, this.configuration.getSendBufferSize());
        _bootstrap.option(ChannelOption.TCP_NODELAY, this.configuration.isTcpNoDelay());
        _bootstrap.option(ChannelOption.SO_KEEPALIVE, this.configuration.isKeepAlive());
        _bootstrap.option(ChannelOption.SO_LINGER, this.configuration.getLinger());

        _bootstrap.handler(new NcsClientChannelInitializer(this.configuration));

        _future =  _bootstrap.connect(this.configuration.getEndpointAddress(), this.configuration.getEndpointPort());
        return _future;
    }

    @Override
    public NcsPacket createPacket() {
        return this.configuration.getPacketFactory().create();
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
    public boolean isConnected()
    {
        if(_future==null) return false;

        if(_future.channel()==null) return false;

        if(_future.channel().isRegistered()==false) return false;

        return true;
    }

    @Override
    public void send(NcsPacket _pkt) {
        _future.channel().write(_pkt);
    }

    @Override
    public void sendAndFlush(NcsPacket _pkt) {
        _future.channel().writeAndFlush(_pkt);
    }

    @Override
    public void flush() {
        _future.channel().flush();
    }

    public void setTimeout(int timeout) {
        configuration.setTimeout(timeout);
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        configuration.setTcpNoDelay(tcpNoDelay);
    }

    public void setStateListener(NcsStateListener stateListener) {
        configuration.setStateListener(stateListener);
    }

    public void setSendBufferSize(int sendBufferSize) {
        configuration.setSendBufferSize(sendBufferSize);
    }

    public void setRecvBufferSize(int recvBufferSize) {
        configuration.setRecvBufferSize(recvBufferSize);
    }

    public void setPacketListener(NcsPacketListener packetListener) {
        configuration.setPacketListener(packetListener);
    }

    public void setPacketFactory(NcsPacketFactory packetFactory) {
        configuration.setPacketFactory(packetFactory);
    }

    public void setMaxFrameLength(int maxFrameLength) {
        configuration.setMaxFrameLength(maxFrameLength);
    }

    public void setLinger(int linger) {
        configuration.setLinger(linger);
    }

    public void setKeepAlive(boolean keepAlive) {
        configuration.setKeepAlive(keepAlive);
    }

    @SneakyThrows
    public void setEndpoint(String _s, int _p) {
        configuration.setEndpoint(_s, _p);
    }

    @Override
    public void setSharedSecret(String _s)
    {
        this.configuration.setPskSharedSecret(_s);
        if(_s==null)
        {
            this.configuration.setUsePskOBF(false);
        }
        else
        {
            this.configuration.setUsePskOBF(true);
        }
    }

    public NcsClientConfiguration getConfiguration() { return this.configuration; }
}
