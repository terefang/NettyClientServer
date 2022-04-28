package com.github.terefang.ncs.common.packet;

import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class SimpleBytesNcsPacketFactory implements NcsPacketFactory
{
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
