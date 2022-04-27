package com.github.terefang.ncs.server;

import com.github.terefang.ncs.server.impl.NcsServerServiceImpl;
import io.netty.channel.ChannelFuture;
import lombok.SneakyThrows;

import java.util.concurrent.Future;

public interface NcsServerService
{
    public static NcsServerService build(NcsServerConfiguration _config)
    {
        NcsServerServiceImpl _nss = new NcsServerServiceImpl();
        _nss.setConfiguration(_config);
        return _nss;
    }

    Future start();

    @SneakyThrows
    default void startNow()
    {
        ((ChannelFuture)this.start()).sync();
    }

    Future stop();

    @SneakyThrows
    default void stopNow()
    {
        ((ChannelFuture)stop()).sync();
    }
}
