package com.github.terefang.ncs.lib.packet.rcon;

import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class RconPacketFactory implements NcsPacketFactory
{
    @Override
    public NcsPacket unpack(ByteBuf _buf) {
        return RconPacket.from(_buf);
    }

    @Override
    public ByteBuf pack(NcsPacket _pkt, ByteBufAllocator _alloc) {
        byte[] _msg = ((RconPacket)_pkt).serialize();
        ByteBuf _buf = _alloc.buffer(_msg.length);
        _buf.writeBytes(_msg);
        return _buf;
    }

    @Override
    public NcsPacket create() {
        return RconPacket.create();
    }
}
