package com.github.terefang.ncs.common;

import com.github.terefang.ncs.common.NcsPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public abstract class AbstractNcsPacket implements NcsPacket
{
    @Override
    public ByteBuf encodeToBuffer(ByteBufAllocator _alloc)
    {
        byte[] _bytes = this.serialize();
        ByteBuf _buf = _alloc.buffer(_bytes.length+4);
        _buf.writeInt(_bytes.length);
        _buf.writeBytes(_bytes);
        return _buf;
    }

    @Override
    public ByteBuf encodeToBuffer()
    {
        byte[] _bytes = this.serialize();
        ByteBuf _buf = ByteBufAllocator.DEFAULT.buffer(_bytes.length+4);
        _buf.writeInt(_bytes.length);
        _buf.writeBytes(_bytes);
        return _buf;
    }

    public abstract byte[] serialize();
}
