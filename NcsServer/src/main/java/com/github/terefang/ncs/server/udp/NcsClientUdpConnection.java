package com.github.terefang.ncs.server.udp;

import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.udp.NcsUdpConnection;
import com.github.terefang.ncs.server.NcsClientConnection;
import com.github.terefang.ncs.server.NcsServerServiceImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class NcsClientUdpConnection extends NcsUdpConnection implements NcsClientConnection
{
    public NcsClientUdpConnection()
    {
        super();
    }
    NcsServerServiceImpl _server;

    public static NcsClientUdpConnection from(NcsServerServiceImpl _server, NcsPacketListener _pl, NcsStateListener _sl, Channel _ch)
    {
        NcsClientUdpConnection _nc = new NcsClientUdpConnection();
        _nc.setPacketListener(_pl);
        _nc.setStateListener(_sl);
        _nc.setChannel(_ch);
        _nc._server = _server;
        return _nc;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext _channelHandlerContext) throws Exception
    {
        super.channelRegistered(_channelHandlerContext);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext _channelHandlerContext) throws Exception
    {
        super.channelUnregistered(_channelHandlerContext);
    }

    @Override
    protected void finalize() throws Throwable
    {
        this.setPacketListener(null);
        this.setStateListener(null);
        this.setChannel(null);
        this._server=null;
        super.finalize();
    }

    @Override
    public boolean isUdp() {
        return true;
    }

    @Override
    public void setContext(Object _context) {
        throw new UnsupportedOperationException("not implemented -- in NcsClientUdpConnection/setContext");
    }

    @Override
    public <T> T getContext(Class<T> _clazz) {
        throw new UnsupportedOperationException("not implemented -- in NcsClientUdpConnection/getContext");
    }

    @Override
    public NcsEndpoint getPeer() {
        throw new UnsupportedOperationException("not implemented -- in NcsClientUdpConnection/getPeer");
    }
}
