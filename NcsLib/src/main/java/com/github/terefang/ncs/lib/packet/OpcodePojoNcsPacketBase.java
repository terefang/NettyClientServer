package com.github.terefang.ncs.lib.packet;

import com.github.terefang.ncs.common.XUID;
import com.github.terefang.ncs.common.packet.AbstractNcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;

public abstract class OpcodePojoNcsPacketBase extends AbstractNcsPacket implements NcsPacket
{
    XUID opcode;
    public byte[] _buf;

    public XUID getOpcode() {
        return opcode;
    }

    public void setOpcode(XUID opcode) {
        this.opcode = opcode;
    }
}
