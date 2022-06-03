package com.github.terefang.ncs.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.net.InetSocketAddress;

public interface NcsPacket
{
    InetSocketAddress getAddress();
    void setAddress(InetSocketAddress _address);

    ByteBuf encodeToBuffer(ByteBufAllocator _alloc);
    ByteBuf encodeToBuffer();

    default <T> T castTo(Class<T> _clazz)
    {
        return (T)this;
    }
}
