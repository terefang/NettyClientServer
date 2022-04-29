package com.github.terefang.ncs.server;

import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.server.impl.NcsServerServiceImpl;
import io.netty.channel.ChannelFuture;
import lombok.SneakyThrows;

public interface NcsServerService
{
    /**
     * build a server service from the given configuration
     * @param _config       the config
     * @return a server service
     */
    public static NcsServerService build(NcsServerConfiguration _config)
    {
        NcsServerServiceImpl _nss = new NcsServerServiceImpl();
        _nss.setConfiguration(_config);
        return _nss;
    }

    /**
     * build a server service using default configuration
     * @return a server service
     */
    public static NcsServerService create()
    {
        NcsServerServiceImpl _nss = new NcsServerServiceImpl();
        _nss.setConfiguration(NcsServerConfiguration.create());
        return _nss;
    }


    public NcsServerConfiguration getConfiguration();

    /**
     * create a packet with the registered packet factory
     * @return a packet
     */
    NcsPacket createPacket();

    /**
     * starts the server service asynchoniously.
     * @return a future
     */
    ChannelFuture start();

    /**
     * starts the server and waiting startup completion
     */
    @SneakyThrows
    default void startNow()
    {
        ((ChannelFuture)this.start()).sync();
    }

    /**
     * stops the server service asynchoniously.
     * @return a future
     */
    ChannelFuture stop();

    /**
     * stops the server service and waiting for shutdown completion
     */
    @SneakyThrows
    default void stopNow()
    {
        ((ChannelFuture)stop()).sync();
    }

    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setPacketFactory(NcsPacketFactory packetFactory);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setPacketListener(NcsPacketListener packetListener);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setStateListener(NcsStateListener stateListener);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setTimeout(int timeout);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setMaxFrameLength(int maxFrameLength);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setRecvBufferSize(int recvBufferSize);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setSendBufferSize(int sendBufferSize);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setTcpNoDelay(boolean tcpNoDelay);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setKeepAlive(boolean keepAlive);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setUseEpoll(boolean _use);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setLinger(int linger);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setEndpoint(String _s, int _p);
    /**
     * convenience handler to set config option -- see NcsServerConfiguration for details
     */
    public void setSharedSecret(String _s);
}
