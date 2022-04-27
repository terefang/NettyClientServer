package com.github.terefang.ncs.client.impl;

import com.github.terefang.ncs.client.NcsServerConnection;
import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.impl.NcsConnectionImpl;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;

public class NcsServerConnectionImpl extends NcsConnectionImpl implements NcsServerConnection
{
    public NcsServerConnectionImpl(NcsEndpoint _peer)
    {
        super(_peer);
    }

    public static NcsConnection from(NioSocketChannel _ch, InetAddress address, int port)
    {
        NcsServerConnectionImpl _nc = new NcsServerConnectionImpl(NcsEndpoint.from(address, port));
        _nc.setChannel(_ch);
        return _nc;
    }

    public static NcsConnection from(NioSocketChannel _ch)
    {
        NcsServerConnectionImpl _nc = new NcsServerConnectionImpl(null);
        _nc.setChannel(_ch);
        return _nc;
    }
}
