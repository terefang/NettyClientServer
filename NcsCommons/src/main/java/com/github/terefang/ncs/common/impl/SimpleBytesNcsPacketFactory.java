package com.github.terefang.ncs.common.impl;

import com.github.terefang.ncs.common.NcsPacket;
import com.github.terefang.ncs.common.NcsPacketFactory;
import io.netty.buffer.ByteBuf;

public class SimpleBytesNcsPacketFactory implements NcsPacketFactory
{
    @Override
    public NcsPacket create(ByteBuf _buf)
    {
        return SimpleBytesNcsPacket.from(_buf);
    }
}
