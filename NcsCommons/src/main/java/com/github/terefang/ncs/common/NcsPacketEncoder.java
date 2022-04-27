package com.github.terefang.ncs.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class NcsPacketEncoder extends MessageToMessageEncoder<NcsPacket>
{
    @Override
    protected void encode(ChannelHandlerContext _channelHandlerContext, NcsPacket _ncsPacket, List<Object> _list) throws Exception
    {
        ByteBuf _buf = _ncsPacket.encodeToBuffer(_channelHandlerContext);
        _list.add(_buf);
    }
}
