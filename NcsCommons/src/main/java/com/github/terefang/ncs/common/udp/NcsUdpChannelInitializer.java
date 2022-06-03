package com.github.terefang.ncs.common.udp;

import com.github.terefang.ncs.common.NcsConfiguration;

import com.github.terefang.ncs.common.packet.NcsPacketEncoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;


public class NcsUdpChannelInitializer extends ChannelInitializer<Channel> {

    private NcsConfiguration _config;
    public NcsUdpChannelInitializer(NcsConfiguration _config)
    {
        super();
        this._config=_config;
    }

    @Override
    protected void initChannel(Channel _ch) throws Exception
    {
        ChannelPipeline _pl = _ch.pipeline();

        // server output
        _pl.addLast("protocol-packet-encoder", new NcsUdpPacketEncoder(this._config.getPacketFactory()));
        _pl.addLast("protocol-packet-decoder", new NcsUdpPacketDecoder(this._config.getPacketFactory()));
    }
}
