package com.github.terefang.ncs.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface NcsPacket {
    ByteBuf encodeToBuffer(ByteBufAllocator _alloc);
    ByteBuf encodeToBuffer();
}
