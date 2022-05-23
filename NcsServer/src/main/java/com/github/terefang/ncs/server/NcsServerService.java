package com.github.terefang.ncs.server;

import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.server.impl.NcsServerServiceImpl;
import io.netty.channel.ChannelFuture;
import lombok.SneakyThrows;

import java.util.List;

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
     * returns list of active ppers
     * @return the list
     */
    List<NcsClientConnection> listActivePeers();
}
