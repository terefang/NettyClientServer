package com.github.terefang.ncs.common;

import io.netty.buffer.ByteBuf;

public interface NcsPacketFactory {
    NcsPacket create(ByteBuf buf);
}
