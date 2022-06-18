package com.github.terefang.ncs.server.udp;

import com.github.jgonian.ipmath.Ipv4;
import com.github.jgonian.ipmath.Ipv4Range;
import com.github.terefang.ncs.server.NcsServerConfiguration;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.List;

public class NcsUdpFilter extends MessageToMessageCodec<DatagramPacket, DatagramPacket>
{
    public static NcsUdpFilter from(NcsServerConfiguration _config)
    {
        NcsUdpFilter _f = new NcsUdpFilter();
        _f._config = _config;
        return _f;
    }

    NcsServerConfiguration _config;

    @Override
    protected void encode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception
    {
        out.add(new DatagramPacket(msg.content().copy(), msg.recipient(), msg.sender()));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception
    {
        if(msg.sender().getAddress() instanceof Inet4Address)
        {
            String _ip = msg.sender().getAddress().getHostAddress();
            Ipv4 _ipv4 = Ipv4.of(_ip);
            if(_config.getBannedAddresses().size()>0)
            {
                if(_config.getBannedAddresses().contains(_ipv4))
                {
                    return;
                }
            }

            if(_config.getBannedNetworks().size()>0)
            {
                for(Ipv4Range _net : _config.getBannedNetworks())
                {
                    if(_net.contains(_ipv4))
                    {
                        return;
                    }
                }
            }
        }

        out.add(new DatagramPacket(msg.content().copy(), msg.recipient(), msg.sender()));
    }
}
