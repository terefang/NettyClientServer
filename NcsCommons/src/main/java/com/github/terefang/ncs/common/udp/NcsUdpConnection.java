package com.github.terefang.ncs.common.udp;

import com.github.terefang.ncs.common.AbstractNcsConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Objects;

public abstract class NcsUdpConnection extends AbstractNcsConnection
{
    HashMap<InetSocketAddress,Object> _contexts = new HashMap<>();

    public void setContext(InetSocketAddress _k, Object _context) {
        this._contexts.put(_k, _context);
    }

    public <T> T getContext(InetSocketAddress _k, Class<T> _clazz) {
        return (T) this._contexts.get(_k);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NcsUdpConnection that = (NcsUdpConnection) o;
        return this.getChannel().equals(that.getChannel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getChannel());
    }
}
