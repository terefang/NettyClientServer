package com.github.terefang.ncs.common.cbor;

import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.OpcodeNcsPacketFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public abstract class OpcodedCborPacketFactory<T> extends OpcodeNcsPacketFactory
{
    @Override
    public NcsPacket unpack(ByteBuf _buf) {
        return unpack(-1, _buf);
    }

    @Override
    public NcsPacket unpack(int _opcode, ByteBuf _buf)
    {
        OpcodedCborPacket<T> _pkt = (OpcodedCborPacket<T>) create();
        _pkt.parse(_buf);
        return _pkt;
    }

    @Override
    public ByteBuf pack(NcsPacket _pkt, ByteBufAllocator _alloc)
    {
        ByteBuf _buf = _pkt.encodeToBuffer(_alloc);
        _buf.setInt(0, getOpcode());
        return _buf;
    }

    public OpcodedCborPacket<T> createPacket(Class<T> _clazz) {
        return new OpcodedCborPacket<T>(this.getOpcode(), _clazz);
    }
}
