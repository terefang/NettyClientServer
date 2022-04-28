package com.github.terefang.ncs.client.impl;

import com.github.terefang.ncs.client.NcsServerConnection;
import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.impl.NcsConnectionImpl;
import io.netty.channel.Channel;

import java.net.InetAddress;

public class NcsServerConnectionImpl extends NcsConnectionImpl implements NcsServerConnection
{
    public NcsServerConnectionImpl()
    {
        super();
    }

    /**
     * creates a connection from parameters
     * @param _ch       the netty channel representing the connection
     * @param address   the peer address
     * @param port      the peer port
     * @return          the connection
     */
    public static NcsConnection from(NcsPacketListener _pl, NcsStateListener _sl, Channel _ch, InetAddress address, int port)
    {
        NcsServerConnectionImpl _nc = new NcsServerConnectionImpl();
        _nc.setPeer(NcsEndpoint.from(address, port));
        _nc.setChannel(_ch);
        _nc.setPacketListener(_pl);
        _nc.setStateListener(_sl);
        return _nc;
    }

    /**
     * creates a connection from parameters
     * @param _ch       the netty channel representing the connection
     * @return          the connection
     */
    public static NcsServerConnectionImpl from(NcsPacketListener _pl, NcsStateListener _sl, Channel _ch)
    {
        NcsServerConnectionImpl _nc = new NcsServerConnectionImpl();
        _nc.setChannel(_ch);
        _nc.setPacketListener(_pl);
        _nc.setStateListener(_sl);
        return _nc;
    }
}
