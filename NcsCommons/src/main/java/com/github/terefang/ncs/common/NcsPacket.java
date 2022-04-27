package com.github.terefang.ncs.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface NcsPacket {
    ByteBuf encodeToBuffer(ChannelHandlerContext _channelHandlerContext);
}
