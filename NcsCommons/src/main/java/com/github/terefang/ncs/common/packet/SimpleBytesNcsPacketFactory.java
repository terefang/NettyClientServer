package com.github.terefang.ncs.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class SimpleBytesNcsPacketFactory implements NcsPacketFactory<AbstractNcsPacket> {
    @Override
    public NcsPacket unpack(ByteBuf _buf)
    {
        return SimpleBytesNcsPacket.from(_buf);
    }

    @Override
    public ByteBuf pack(NcsPacket _pkt, ByteBufAllocator _alloc) {
        return _pkt.encodeToBuffer(_alloc);
    }

    @Override
    public NcsPacket create() { return SimpleBytesNcsPacket.create(); };
}
