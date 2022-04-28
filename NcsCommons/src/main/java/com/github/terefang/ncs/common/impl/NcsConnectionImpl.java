package com.github.terefang.ncs.common.impl;

import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.pskobf.NcsPskObfCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

import java.util.Objects;

public abstract class NcsConnectionImpl extends SimpleChannelInboundHandler<NcsPacket> implements NcsConnection
{
    Object _context;
    NcsEndpoint _peer;
    Channel _channel;
    NcsPacketListener _packetListener;
    NcsStateListener _stateListener;
    NcsPskObfCodec _codec;

    public NcsPskObfCodec getPskObfCodec() {
        return _codec;
    }

    public void setPskObfCodec(NcsPskObfCodec _codec) {
        this._codec = _codec;
    }

    public void setPeer(NcsEndpoint _peer)
    {
        this._peer = _peer;
    }

    public void setChannel(Channel _channel)
    {
        this._channel = _channel;
    }

    public void send(NcsPacket _pkt)
    {
        this._channel.write(_pkt);
    }

    public void sendAndFlush(NcsPacket _pkt)
    {
        this._channel.writeAndFlush(_pkt);
    }

    @Override
    public void setContext(Object _context) {
        this._context=_context;
    }

    @Override
    public <T> T getContext(Class<T> _clazz) {
        return (T) this._context;
    }

    @Override
    public NcsEndpoint getPeer()
    {
        if((this._peer==null) && (this._channel instanceof SocketChannel))
        {
            SocketChannel _sch = (SocketChannel) this._channel;
            if(_sch.remoteAddress()!=null)
            {
                this._peer = NcsEndpoint.from(_sch.remoteAddress());
            }
        }

        if(this._peer==null)
        {
            return NcsEndpoint.create();
        }
        return this._peer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NcsConnectionImpl that = (NcsConnectionImpl) o;
        return _channel.equals(that._channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_channel);
    }

    public void setPacketListener(NcsPacketListener _packetListener)
    {
        this._packetListener = _packetListener;
    }

    public void setStateListener(NcsStateListener _stateListener)
    {
        this._stateListener = _stateListener;
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
        if(this._packetListener!=null)
        {
            this._packetListener.onPacket(this, _ncsPacket);
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
            _stateListener.onConnect(this);
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
            _stateListener.onDisconnect(this);
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
            _stateListener.onError(this, _cause);
        _channelHandlerContext.close();
    }

}
