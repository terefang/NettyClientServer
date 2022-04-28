package com.github.terefang.ncs.common.impl;

import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.packet.NcsPacket;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import java.net.InetAddress;
import java.util.Objects;

public abstract class NcsConnectionImpl implements NcsConnection
{
    Object _context;
    NcsEndpoint _peer;
    Channel _channel;

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
}
