package com.github.terefang.ncs.server.impl;

import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.NcsPacket;
import com.github.terefang.ncs.common.impl.NcsConnectionImpl;
import com.github.terefang.ncs.server.NcsClientConnection;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;

public class NcsClientConnectionImpl extends NcsConnectionImpl implements NcsClientConnection
{
    public NcsClientConnectionImpl(NcsEndpoint _peer)
    {
        super(_peer);
    }

    public static NcsConnection from(NioSocketChannel _ch, InetAddress address, int port)
    {
        NcsClientConnectionImpl _nc = new NcsClientConnectionImpl(NcsEndpoint.from(address, port));
        _nc.setChannel(_ch);
        return _nc;
    }

    public static NcsConnection from(NioSocketChannel _ch)
    {
        NcsClientConnectionImpl _nc = new NcsClientConnectionImpl(NcsEndpoint.from(_ch.remoteAddress().getAddress(), _ch.remoteAddress().getPort()));
        _nc.setChannel(_ch);
        return _nc;
    }
}
