package com.github.terefang.ncs.common.udp;

import com.github.terefang.ncs.common.packet.AbstractNcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class NcsUdpPacketDecoder extends MessageToMessageDecoder<DatagramPacket>
{
    NcsPacketFactory<AbstractNcsPacket> _factory;

    public NcsUdpPacketDecoder(NcsPacketFactory<AbstractNcsPacket> _factory) {
        super();
        this._factory = _factory;
    }

    @Override
    protected void decode(ChannelHandlerContext _ctx, DatagramPacket _pkt, List<Object> _list) throws Exception
    {
        NcsPacket _npkt = this._factory.unpack(_pkt.content());
        _npkt.setAddress(_pkt.sender());
        _list.add(_npkt);
    }
}
