package com.github.terefang.ncs.lib.packet.xuidoc;

import com.github.terefang.ncs.common.packet.NcsPacket;
import io.netty.buffer.ByteBuf;

public interface OpcodePojoNcsPacketFactoryInterface
{
    NcsPacket unpack(ByteBuf _buf);
}
