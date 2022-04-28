package com.github.terefang.ncs.server.impl;

import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.impl.NcsConnectionImpl;
import com.github.terefang.ncs.server.NcsClientConnection;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import java.net.InetAddress;

public class NcsClientConnectionImpl extends NcsConnectionImpl implements NcsClientConnection
{
    public NcsClientConnectionImpl()
    {
        super();
    }

    public static NcsConnection from(Channel _ch, InetAddress address, int port)
    {
        NcsClientConnectionImpl _nc = new NcsClientConnectionImpl();
        _nc.setPeer(NcsEndpoint.from(address, port));
        _nc.setChannel(_ch);
        return _nc;
    }

    public static NcsConnection from(Channel _ch)
    {
        NcsClientConnectionImpl _nc = new NcsClientConnectionImpl();
        _nc.setChannel(_ch);
        if(_ch instanceof SocketChannel)
        {
            SocketChannel _sch = (SocketChannel)_ch;
            if(_sch.remoteAddress()!=null)
            {
                _nc.setPeer(NcsEndpoint.from(_sch.remoteAddress().getAddress(), _sch.remoteAddress().getPort()));
            }
        }
        return _nc;
    }
}
