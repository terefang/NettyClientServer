package com.github.terefang.ncs.common.packet;

import com.github.terefang.ncs.common.packet.NcsPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.net.InetSocketAddress;

public abstract class AbstractNcsPacket implements NcsPacket
{
    InetSocketAddress address;

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public ByteBuf encodeToBuffer(ByteBufAllocator _alloc)
    {
        byte[] _bytes = this.serialize();
        ByteBuf _buf = _alloc.buffer(_bytes.length+4);
        _buf.writeBytes(_bytes);
        return _buf;
    }

    @Override
    public ByteBuf encodeToBuffer()
    {
        byte[] _bytes = this.serialize();
        ByteBuf _buf = ByteBufAllocator.DEFAULT.buffer(_bytes.length+4);
        _buf.writeBytes(_bytes);
        return _buf;
    }

    public abstract byte[] serialize();
}
