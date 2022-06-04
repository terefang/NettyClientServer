package com.github.terefang.ncs.common.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class NcsPacketEncoder extends MessageToMessageEncoder<NcsPacket>
{
    NcsPacketFactory<AbstractNcsPacket> _factory;

    public NcsPacketEncoder(NcsPacketFactory<AbstractNcsPacket> _factory) {
        super();
        this._factory = _factory;
    }

    @Override
    protected void encode(ChannelHandlerContext _channelHandlerContext, NcsPacket _ncsPacket, List<Object> _list) throws Exception
    {
        ByteBuf _buf = this._factory.pack(_ncsPacket, _channelHandlerContext.alloc());
        _list.add(_buf);
    }
}
