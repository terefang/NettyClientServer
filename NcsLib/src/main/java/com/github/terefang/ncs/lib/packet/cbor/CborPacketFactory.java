package com.github.terefang.ncs.lib.packet.cbor;

import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.lib.packet.kryo.KryoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class CborPacketFactory implements NcsPacketFactory
{
    @Override
    public NcsPacket unpack(ByteBuf _buf) {
        return KryoPacket.from(_buf);
    }

    @Override
    public ByteBuf pack(NcsPacket _pkt, ByteBufAllocator _alloc) {
        byte[] _msg = ((CborPacket)_pkt).serialize();
        ByteBuf _buf = _alloc.buffer(_msg.length);
        _buf.writeBytes(_msg);
        return _buf;
    }

    @Override
    public NcsPacket create() {
        return CborPacket.create();
    }
}
