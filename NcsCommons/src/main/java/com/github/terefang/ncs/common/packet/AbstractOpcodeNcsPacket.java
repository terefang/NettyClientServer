package com.github.terefang.ncs.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public abstract class AbstractOpcodeNcsPacket extends AbstractNcsPacket
{
    int opcode;

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }
}
