package com.github.terefang.ncs.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public abstract class OpcodeNcsPacketFactory<T> implements NcsPacketFactory
{
    int opcode;

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    // remember there is this opcode (int) in front of the data
    public abstract NcsPacket unpack(ByteBuf _buf);

    // remember to have the opcode (int) in front of the data
    public abstract ByteBuf pack(NcsPacket _pkt, ByteBufAllocator alloc);

    public abstract NcsPacket create();
}
