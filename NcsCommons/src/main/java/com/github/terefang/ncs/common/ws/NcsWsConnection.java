package com.github.terefang.ncs.common.ws;

import com.github.terefang.ncs.common.AbstractNcsConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import io.netty.channel.socket.SocketChannel;

import java.util.Objects;

public abstract class NcsWsConnection extends AbstractNcsConnection
{
    Object _context;
    NcsEndpoint _peer;

    public void setPeer(NcsEndpoint _peer)
    {
        this._peer = _peer;
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
        if((this._peer==null) && (this.getChannel() instanceof SocketChannel))
        {
            SocketChannel _sch = (SocketChannel) this.getChannel();
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
        NcsWsConnection that = (NcsWsConnection) o;
        return this.getChannel().equals(that.getChannel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getChannel());
    }

}
