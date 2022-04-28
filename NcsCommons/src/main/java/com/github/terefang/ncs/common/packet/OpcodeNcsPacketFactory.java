package com.github.terefang.ncs.common.packet;

public abstract class OpcodeNcsPacketFactory implements NcsPacketFactory
{
    int opcode;

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }
}
