package com.github.terefang.ncs.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public abstract class OpcodeNcsPacketFactory implements NcsPacketFactory<AbstractNcsPacket> {
    int opcode;

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    // remember there is this opcode (1-4 bytes) in front of the data
    public abstract NcsPacket unpack(ByteBuf _buf);

    // for varint opcodes
    public abstract NcsPacket unpack(int _opcode, ByteBuf _buf);

    // remember to have the opcode (1-4 bytes) in front of the data
    public abstract ByteBuf pack(NcsPacket _pkt, ByteBufAllocator alloc);

    public abstract NcsPacket create();
}
