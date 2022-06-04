package com.github.terefang.ncs.common.udp;

import com.github.terefang.ncs.common.packet.AbstractNcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class NcsUdpPacketEncoder extends MessageToMessageEncoder<NcsPacket>
{
    NcsPacketFactory<AbstractNcsPacket> _factory;

    public NcsUdpPacketEncoder(NcsPacketFactory<AbstractNcsPacket> _factory) {
        super();
        this._factory = _factory;
    }

    @Override
    protected void encode(ChannelHandlerContext _channelHandlerContext, NcsPacket _ncsPacket, List<Object> _list) throws Exception
    {
        ByteBuf _buf = this._factory.pack(_ncsPacket, _channelHandlerContext.alloc());
        DatagramPacket _pkt = new DatagramPacket(_buf, _ncsPacket.getAddress());
        _list.add(_pkt);
    }
}
