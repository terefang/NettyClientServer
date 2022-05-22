package com.github.terefang.ncs.lib.packet;

import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;

public abstract class OpcodePojoNcsPacketBase extends SimpleBytesNcsPacket implements NcsPacket
{
    int opcode;

    public int getOpcode() {
        return opcode;
    }

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }
}
