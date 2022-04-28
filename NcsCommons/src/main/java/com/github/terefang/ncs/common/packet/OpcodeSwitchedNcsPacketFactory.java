package com.github.terefang.ncs.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

/**
 * allows switching packet factories based on initial opcode in the decoding buffer
 */
public class OpcodeSwitchedNcsPacketFactory implements NcsPacketFactory
{
    Map<Integer,OpcodeNcsPacketFactory> _registry = new HashMap<>();

    /**
     * register a opcode based packet factory
     * @param _f the packet factory
     */
    public void registerPacketFactory(OpcodeNcsPacketFactory _f)
    {
        _registry.put(_f.getOpcode(), _f);
    }

    /**
     * unpack the buffer into a packet switching on opcode
     * @param _buf the buffer to unpack
     * @return a decoded packet
     */
    @Override
    public NcsPacket unpack(ByteBuf _buf)
    {
        // we keep the buffer untouched
        int _opcode = _buf.getInt(0);
        if(_registry.containsKey(_opcode))
        {
            return _registry.get(_opcode).unpack(_buf);
        }
        throw new IllegalArgumentException("illegal opcode in packet "+_opcode);
    }

    /**
     * pack the packet into a buffer
     * @param _pkt the packet
     * @param _alloc the allocator
     * @return the buffer
     */
    @Override
    public ByteBuf pack(NcsPacket _pkt, ByteBufAllocator _alloc) {
        return _pkt.encodeToBuffer(_alloc);
    }

    @Override
    @SneakyThrows
    public NcsPacket create() { throw new IllegalAccessException("not implemented"); };
}
