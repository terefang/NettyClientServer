package com.github.terefang.ncs.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface NcsPacket {
    ByteBuf encodeToBuffer(ByteBufAllocator _alloc);
    ByteBuf encodeToBuffer();

    default <T> T castTo(Class<T> _clazz)
    {
        return (T)this;
    }
}
