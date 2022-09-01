package com.github.terefang.ncs.server.ws;

import com.github.terefang.ncs.common.NcsCompressionMethod;
import com.github.terefang.ncs.common.NcsConfiguration;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class NcsWsServerChannelInitializer extends ChannelInitializer<Channel> {

    private NcsConfiguration _config;
    public NcsWsServerChannelInitializer(NcsConfiguration _config)
    {
        super();
        this._config=_config;
    }

    @Override
    protected void initChannel(Channel _ch) throws Exception
    {
        ChannelPipeline _pl = _ch.pipeline();

        _pl.addLast("protocol-frame-coder", new HttpObjectAggregator(this._config.getMaxFrameLength()));

        if(!_config.getCompressionMethod().equals(NcsCompressionMethod.NONE))
        {
            _pl.addLast("compression-frame-coder", new WebSocketServerCompressionHandler());
        }

        // server output
        //_pl.addLast("protocol-packet-encoder", new NcsWsServerPacketEncoder(this._config.getPacketFactory()));
        //_pl.addLast("protocol-packet-decoder", new NcsWsServerPacketDecoder(this._config.getPacketFactory()));
    }
}
