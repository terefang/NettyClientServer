package com.github.terefang.ncs.common.packet;

import com.github.terefang.ncs.common.packet.NcsPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface NcsPacketFactory {
    NcsPacket unpack(ByteBuf _buf);
    ByteBuf pack(NcsPacket _pkt, ByteBufAllocator alloc);
    public NcsPacket create();
}
