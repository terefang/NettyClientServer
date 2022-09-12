package com.github.terefang.ncs.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

import java.net.InetSocketAddress;

public class NcsKeepAlivePacket extends AbstractNcsPacket implements NcsPacket
{
    public static final int PACKET_SIZE = 24;
    long timestamp;

    public static NcsPacket create(ByteBuf buf, InetSocketAddress _address)
    {
        NcsKeepAlivePacket _ret = new NcsKeepAlivePacket();
        _ret.timestamp = buf.getLong(16);
        _ret.setAddress(_address);
        return _ret;
    }

    public static NcsPacket create(long _l, InetSocketAddress _address)
    {
        NcsKeepAlivePacket _ret = new NcsKeepAlivePacket();
        _ret.timestamp = _l;
        _ret.setAddress(_address);
        return _ret;
    }

    public static NcsPacket create(ByteBuf buf)
    {
        NcsKeepAlivePacket _ret = new NcsKeepAlivePacket();
        _ret.timestamp = buf.getLong(16);
        return _ret;
    }

    public static NcsPacket create(long _l)
    {
        NcsKeepAlivePacket _ret = new NcsKeepAlivePacket();
        _ret.timestamp = _l;
        return _ret;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public byte[] serialize() {
        return ByteBufUtil.getBytes(ByteBufAllocator.DEFAULT.buffer(24).writeLong(-1L).writeLong(-1L).writeLong(timestamp));
    }
}
