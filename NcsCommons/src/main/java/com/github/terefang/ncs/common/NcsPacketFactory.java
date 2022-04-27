package com.github.terefang.ncs.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface NcsPacketFactory {
    NcsPacket unpack(ByteBuf _buf);
    ByteBuf pack(NcsPacket _pkt, ByteBufAllocator alloc);
}
