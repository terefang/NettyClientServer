package com.github.terefang.ncs.client.impl;

import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * handles callbacks as registerd by the user
 */
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

    /**
     * called from netty pipeline with a decoded packet
     * calls onPacket in the registerd listener
     * @param _channelHandlerContext    the channel the packet arrived on
     * @param _ncsPacket                the packet arrived
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext _channelHandlerContext, NcsPacket _ncsPacket) throws Exception
    {
        if(this._listener!=null)
        {
            this._listener.onPacket(this._connection, _ncsPacket);
        }
    }

    /**
     * called from netty pipeline on connection to the server
     * calls onConnect in the registerd listener
     * @param _channelHandlerContext    the channel
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext _channelHandlerContext) throws Exception
    {
        super.channelRegistered(_channelHandlerContext);
        if(_stateListener!=null)
            _stateListener.onConnect(_connection);
    }

    /**
     * called from netty pipeline on disconnection from the server
     * calls onDisconnect in the registerd listener
     * @param _channelHandlerContext    the channel
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext _channelHandlerContext) throws Exception
    {
        super.channelUnregistered(_channelHandlerContext);
        if(_stateListener!=null)
            _stateListener.onDisconnect(_connection);
    }

    /**
     * called from netty pipeline on uncaught errors
     * calls onError in the registerd listener
     * will force connection close after notification
     * @param _channelHandlerContext    the channel
     * @param _cause                    the error
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext _channelHandlerContext, Throwable _cause) throws Exception {
        if(_stateListener!=null)
            _stateListener.onError(_connection, _cause);
        _channelHandlerContext.close();
    }
}
