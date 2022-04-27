package com.github.terefang.ncs.server.impl;

import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsPacket;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NcsServerPacketHandlerImpl extends SimpleChannelInboundHandler<NcsPacket>
{
    NcsPacketListener _listener;
    NcsStateListener _stateListener;
    NcsConnection _connection;
    public NcsServerPacketHandlerImpl(NioSocketChannel _ch, NcsPacketListener _listener, NcsStateListener _stateListener)
    {
        super();
        this._listener = _listener;
        this._stateListener = _stateListener;
        this._connection = NcsClientConnectionImpl.from(_ch, _ch.remoteAddress().getAddress(), _ch.remoteAddress().getPort());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext _channelHandlerContext, NcsPacket _ncsPacket) throws Exception
    {
        if(this._listener!=null)
        {
            this._listener.onPacket(this._connection, _ncsPacket);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception
    {
        super.channelRegistered(ctx);
        if(_stateListener!=null)
            _stateListener.onConnect(_connection);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception
    {
        super.channelUnregistered(ctx);
        if(_stateListener!=null)
            _stateListener.onDisconnect(_connection);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(_stateListener!=null)
            _stateListener.onError(_connection, cause);
        //super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
