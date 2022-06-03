package com.github.terefang.ncs.client.tcp;

import com.github.terefang.ncs.client.NcsServerConnection;
import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.tcp.NcsTcpConnection;
import io.netty.channel.Channel;

import java.net.InetAddress;

public class NcsServerTcpConnection extends NcsTcpConnection implements NcsServerConnection
{
    public NcsServerTcpConnection()
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
    public static NcsServerTcpConnection from(NcsPacketListener _pl, NcsStateListener _sl, Channel _ch, InetAddress address, int port)
    {
        NcsServerTcpConnection _nc = new NcsServerTcpConnection();
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
    public static NcsServerTcpConnection from(NcsPacketListener _pl, NcsStateListener _sl, Channel _ch)
    {
        NcsServerTcpConnection _nc = new NcsServerTcpConnection();
        _nc.setChannel(_ch);
        _nc.setPacketListener(_pl);
        _nc.setStateListener(_sl);
        return _nc;
    }

    @Override
    public boolean isUdp() {
        return false;
    }
}
