package com.github.terefang.ncs.common.udp;

import com.github.terefang.ncs.common.packet.AbstractNcsPacket;
import com.github.terefang.ncs.common.packet.NcsKeepAlivePacket;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.net.InetSocketAddress;
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
        NcsPacket _npkt = null;
        if(isKeepAlivePacket(_pkt.content()))
        {
            _npkt =  NcsKeepAlivePacket.create(_pkt.content());
        }
        else
        {
            _npkt = this._factory.unpack(_pkt.content());
        }
        _npkt.setAddress(_pkt.sender());
        _list.add(_npkt);
    }

    private boolean isKeepAlivePacket(ByteBuf buf)
    {
        return (buf.readableBytes() == NcsKeepAlivePacket.PACKET_SIZE) && (buf.getLong(0) == -1L) && (buf.getLong(8) == -1L);
    }
}
