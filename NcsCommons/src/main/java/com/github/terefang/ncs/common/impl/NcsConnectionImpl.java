package com.github.terefang.ncs.common.impl;

import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.NcsPacket;
import io.netty.channel.socket.nio.NioSocketChannel;

public abstract class NcsConnectionImpl implements NcsConnection
{
    public NcsConnectionImpl(NcsEndpoint _peer)
    {
        this._peer = _peer;
    }

    Object _context;
    NcsEndpoint _peer;
    NioSocketChannel _channel;

    public void setChannel(NioSocketChannel _channel)
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
    public NcsEndpoint getPeer() {
        return this._peer;
    }
}
