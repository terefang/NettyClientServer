package com.github.terefang.ncs.client.impl;

import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NcsClientPacketHandlerImpl extends SimpleChannelInboundHandler<NcsPacket>
{
    NcsPacketListener _listener;
    NcsStateListener _stateListener;
    NcsConnection _connection;

    public NcsClientPacketHandlerImpl(NioSocketChannel _ch, NcsPacketListener _packetListener, NcsStateListener _stateListener) {
        super();
        this._listener = _packetListener;
        this._stateListener = _stateListener;
        this._connection = NcsServerConnectionImpl.from(_ch);
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
