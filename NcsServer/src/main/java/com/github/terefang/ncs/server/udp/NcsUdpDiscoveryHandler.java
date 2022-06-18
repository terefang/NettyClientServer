package com.github.terefang.ncs.server.udp;

import com.github.jgonian.ipmath.Ipv4;
import com.github.jgonian.ipmath.Ipv4Range;
import com.github.terefang.ncs.common.NcsHelper;
import com.github.terefang.ncs.server.NcsServerConfiguration;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NcsUdpDiscoveryHandler extends MessageToMessageCodec<DatagramPacket, DatagramPacket>
{
    private NcsServerConfiguration _config;

    public static NcsUdpDiscoveryHandler from(NcsServerConfiguration _config)
    {
        NcsUdpDiscoveryHandler _f = new NcsUdpDiscoveryHandler();
        _f._config = _config;
        return _f;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception
    {
        out.add(new DatagramPacket(msg.content().copy(), msg.recipient(), msg.sender()));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception
    {
        String _ip = msg.sender().getAddress().getHostAddress();

        ByteBuf _buf = msg.content();
        long _i1 = _buf.getLong(0);
        long _i2 = _buf.getLong(8);
        if((_i1 == NcsHelper.SPECIAL_PACKET_ID) && (_i2 == NcsHelper.DISCOVERY_PACKET_ID))
        {
            ByteBuf _buf2 = ctx.alloc().buffer(16);
            _buf2.writeLong( NcsHelper.SPECIAL_PACKET_ID);

            if(this._config.getDiscoveryConnectUrl()==null)
            {
                _buf2.writeLong(NcsHelper.FOUND_PACKET_ID);
            }
            else
            {
                _buf2.writeLong(NcsHelper.FOUND_WITH_URL_PACKET_ID);
            }

            if(this._config.getDiscoveryConnectName()==null)
            {
                this._config.setDiscoveryConnectName(InetAddress.getLocalHost().getHostName());
            }

            {
                byte[] _str = this._config.getDiscoveryConnectName().getBytes(StandardCharsets.UTF_8);
                _buf2.writeInt(_str.length);
                _buf2.writeBytes(_str);
            }

            if(this._config.getDiscoveryConnectUrl()!=null)
            {
                byte[] _str = this._config.getDiscoveryConnectUrl().getBytes(StandardCharsets.UTF_8);
                _buf2.writeInt(_str.length);
                _buf2.writeBytes(_str);
            }

            ctx.writeAndFlush(new DatagramPacket(_buf2, msg.sender()));
            return;
        }

        out.add(new DatagramPacket(_buf.copy(), msg.recipient(), msg.sender()));
    }
}
