package com.github.terefang.ncs.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface NcsPacketFactory<O extends AbstractNcsPacket> {
    NcsPacket unpack(ByteBuf _buf);
    ByteBuf pack(NcsPacket _pkt, ByteBufAllocator alloc);
    public NcsPacket create();
}
