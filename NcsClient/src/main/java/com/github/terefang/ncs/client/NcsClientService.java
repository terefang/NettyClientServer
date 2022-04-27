package com.github.terefang.ncs.client;

import com.github.terefang.ncs.client.impl.NcsClientServiceImpl;
import com.github.terefang.ncs.common.NcsPacket;
import io.netty.channel.ChannelFuture;
import lombok.SneakyThrows;

import java.util.concurrent.Future;

public interface NcsClientService
{
    public static NcsClientService build(NcsClientConfiguration _config)
    {
        NcsClientServiceImpl _nss = new NcsClientServiceImpl();
        _nss.setConfiguration(_config);
        return _nss;
    }

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

    void sendAndFlush(NcsPacket _pkt);
}
