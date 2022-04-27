package com.github.terefang.ncs.common.impl;

import com.github.terefang.ncs.common.NcsPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractNcsPacket implements NcsPacket
{
    @Override
    public ByteBuf encodeToBuffer(ChannelHandlerContext _ctx)
    {
        byte[] _bytes = this.serialize();
        ByteBuf _buf = _ctx.alloc().buffer(_bytes.length+4);
        _buf.writeInt(_bytes.length);
        _buf.writeBytes(_bytes);
        return _buf;
    }

    public abstract byte[] serialize();
}
