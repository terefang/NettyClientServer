package com.github.terefang.ncs.client;

import com.github.terefang.ncs.client.impl.NcsClientServiceImpl;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.common.packet.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import io.netty.channel.ChannelFuture;
import lombok.SneakyThrows;

import javax.net.ssl.SSLEngine;
import java.util.concurrent.Future;

public interface NcsClientService
{
    public static NcsClientService build(NcsClientConfiguration _config)
    {
        NcsClientServiceImpl _nss = new NcsClientServiceImpl();
        _nss.setConfiguration(_config);
        return _nss;
    }

    NcsPacket createPacket();

    Future connect() throws Exception;
    Future disconnect() throws Exception;

    @SneakyThrows
    default void connectNow()
    {
        ((ChannelFuture)this.connect()).sync();
    }

    @SneakyThrows
    default void disconnectNow()
    {
        ((ChannelFuture)this.disconnect()).sync();
    }

    void shutdown();

    void send(NcsPacket _pkt);

    void flush();
    void sendAndFlush(NcsPacket _pkt);

    public void setTimeout(int timeout);
    public void setTcpNoDelay(boolean tcpNoDelay);
    public void setStateListener(NcsStateListener stateListener);
    public void setSendBufferSize(int sendBufferSize);
    public void setRecvBufferSize(int recvBufferSize);
    public void setPacketListener(NcsPacketListener packetListener);
    public void setPacketFactory(NcsPacketFactory packetFactory);
    public void setMaxFrameLength(int maxFrameLength);
    public void setLinger(int linger);
    public void setKeepAlive(boolean keepAlive);
    public void setEndpoint(String _s, int _p);
}
