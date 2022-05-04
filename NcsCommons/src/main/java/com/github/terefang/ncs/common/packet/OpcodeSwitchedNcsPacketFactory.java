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

    int opcodeSize = 4;

    public int getOpcodeSize() {
        return opcodeSize;
    }

    public void setOpcodeSize(int opcodeSize) {
        this.opcodeSize = opcodeSize;
    }

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
        int _opcode = 0 ;
        // we keep the buffer untouched
        switch (this.opcodeSize)
        {
            case 1: _opcode = _buf.getByte(0) & 0xff; break;
            case 2: _opcode = _buf.getShort(0) & 0xffff; break;
            case 4: _opcode = _buf.getInt(0); break;
            default: throw new IllegalArgumentException("illegal opcode size "+this.opcodeSize);
        }

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
    public ByteBuf pack(NcsPacket _pkt, ByteBufAllocator _alloc)
    {
        if(_pkt instanceof AbstractOpcodeNcsPacket)
        {
            int _opcode = ((AbstractOpcodeNcsPacket)_pkt).getOpcode();
            if(_registry.containsKey(_opcode))
            {
                return _registry.get(_opcode).pack(_pkt, _alloc);
            }
            throw new IllegalArgumentException("illegal opcode in packet "+_opcode);
        }
        return _pkt.encodeToBuffer(_alloc);
    }

    @Override
    @SneakyThrows
    public NcsPacket create() { throw new IllegalAccessException("not implemented"); };
}
