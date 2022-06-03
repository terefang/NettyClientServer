package com.github.terefang.ncs.server.tcp;

import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.tcp.NcsTcpConnection;
import com.github.terefang.ncs.server.NcsClientConnection;
import com.github.terefang.ncs.server.NcsServerServiceImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

import java.net.InetAddress;

public class NcsClientTcpConnection extends NcsTcpConnection implements NcsClientConnection
{
    public NcsClientTcpConnection()
    {
        super();
    }
    NcsServerServiceImpl _server;

    public static NcsClientTcpConnection from(NcsServerServiceImpl _server, NcsPacketListener _pl, NcsStateListener _sl, Channel _ch, InetAddress address, int port)
    {
        NcsClientTcpConnection _nc = new NcsClientTcpConnection();
        _nc.setPeer(NcsEndpoint.from(address, port));
        _nc.setPacketListener(_pl);
        _nc.setStateListener(_sl);
        _nc.setChannel(_ch);
        _nc._server = _server;
        return _nc;
    }

    public static NcsClientTcpConnection from(NcsServerServiceImpl _server, NcsPacketListener _pl, NcsStateListener _sl, Channel _ch)
    {
        NcsClientTcpConnection _nc = new NcsClientTcpConnection();
        _nc.setPacketListener(_pl);
        _nc.setStateListener(_sl);
        _nc.setChannel(_ch);
        if(_ch instanceof SocketChannel)
        {
            SocketChannel _sch = (SocketChannel)_ch;
            if(_sch.remoteAddress()!=null)
            {
                _nc.setPeer(NcsEndpoint.from(_sch.remoteAddress().getAddress(), _sch.remoteAddress().getPort()));
            }
        }
        _nc._server = _server;
        return _nc;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext _channelHandlerContext) throws Exception
    {
        this._server.getClientConnections().add(this);
        super.channelRegistered(_channelHandlerContext);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext _channelHandlerContext) throws Exception
    {
        super.channelUnregistered(_channelHandlerContext);
        this._server.getClientConnections().remove(this);
    }

    @Override
    protected void finalize() throws Throwable
    {
        this.setPeer(null);
        this.setPacketListener(null);
        this.setStateListener(null);
        this.setChannel(null);
        this._server=null;
        super.finalize();
    }

    @Override
    public boolean isUdp() {
        return false;
    }
}
