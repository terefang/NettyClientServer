package com.github.terefang.ncs.client;

import com.github.terefang.ncs.client.impl.NcsClientServiceImpl;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import io.netty.channel.ChannelFuture;
import lombok.SneakyThrows;

import java.util.concurrent.Future;

public interface NcsClientService
{
    /**
     * builds a client service based on the given configuration
     * @param _config       the configuration
     * @return a client service
     */
    public static NcsClientService build(NcsClientConfiguration _config)
    {
        NcsClientServiceImpl _nss = new NcsClientServiceImpl();
        _nss.setConfiguration(_config);
        return _nss;
    }

    /**
     * builds a client service based on default configuration
     * @return a client service
     */
    public static NcsClientService create()
    {
        NcsClientServiceImpl _nss = new NcsClientServiceImpl();
        _nss.setConfiguration(NcsClientConfiguration.create());
        return _nss;
    }

    /**
     * creates a packet based on the registered factory
     * @return a packet
     */
    NcsPacket createPacket();

    /**
     * connects to the server asynchroniously
     * @return a future
     * @throws Exception
     */
    Future connect() throws Exception;

    /**
     * disconnects from the server asynchroniously
     * @return a future
     * @throws Exception
     */
    Future disconnect() throws Exception;

    /**
     * connects to the server and whaits for completion
     */
    @SneakyThrows
    default void connectNow()
    {
        ((ChannelFuture)this.connect()).sync();
    }

    /**
     * disconnects from the server and whaits for completion
     */
    @SneakyThrows
    default void disconnectNow()
    {
        ((ChannelFuture)this.disconnect()).sync();
    }

    /**
     * shutdown the internal worker queue so this service cannot be reused
     */
    void shutdown();

    /**
     * puts packet into the output queue to be sent to the server
     * @param _pkt  the packet
     */
    void send(NcsPacket _pkt);

    /**
     * flushes the output queue to be sent to the server
     */
    void flush();

    /**
     * puts packet into the output queue and flushes the queue
     * @param _pkt  the packet
     */
    void sendAndFlush(NcsPacket _pkt);

    /**
     * checks if the client is still connected to the server
     * @return
     */
    boolean isConnected();

    public NcsClientConfiguration getConfiguration();
    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setTimeout(int timeout);

    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setTcpNoDelay(boolean tcpNoDelay);

    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setStateListener(NcsStateListener stateListener);

    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setSendBufferSize(int sendBufferSize);

    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setRecvBufferSize(int recvBufferSize);

    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setPacketListener(NcsPacketListener packetListener);

    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setPacketFactory(NcsPacketFactory packetFactory);

    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setMaxFrameLength(int maxFrameLength);

    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setLinger(int linger);

    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setKeepAlive(boolean keepAlive);

    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setEndpoint(String _s, int _p);

    /**
     * convenience handler to set config option -- see NcsClientConfiguration for details
     */
    public void setSharedSecret(String _s);
}
