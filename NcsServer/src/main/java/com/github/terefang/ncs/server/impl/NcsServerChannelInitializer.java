package com.github.terefang.ncs.server.impl;

import com.github.terefang.ncs.common.packet.NcsPacketDecoder;
import com.github.terefang.ncs.common.packet.NcsPacketEncoder;
import com.github.terefang.ncs.common.pskobf.NcsPskObfCodec;
import com.github.terefang.ncs.server.NcsServerConfiguration;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class NcsServerChannelInitializer extends ChannelInitializer<Channel>
{
    NcsServerConfiguration _config;

    public NcsServerChannelInitializer(NcsServerConfiguration _config)
    {
        this._config = _config;
    }

    protected void initChannel(Channel _ch) throws Exception
    {
        // server input
        ChannelPipeline _pl = _ch.pipeline();

        SSLEngine _engine = this._config.getTlsServerEngine();
        if(_engine!=null)
        {
            _pl.addLast("ssl-tls-layer", new SslHandler(_engine));
        }

        if(this._config.getMaxFrameLength()>=65536)
        {
            _pl.addLast("protocol-frame-decoder", new LengthFieldBasedFrameDecoder(this._config.getMaxFrameLength(), 0, 4, 0, 4));
            _pl.addLast("protocol-frame-encoder", new LengthFieldPrepender(4, false));
        }
        else
        {
            _pl.addLast("protocol-frame-decoder", new LengthFieldBasedFrameDecoder(this._config.getMaxFrameLength(), 0, 2, 0, 2));
            _pl.addLast("protocol-frame-encoder", new LengthFieldPrepender(2, false));
        }

        if(this._config.isUsePskOBF() && this._config.getPskSharedSecret()!=null)
        {
            _pl.addLast("frame-obfuscator", NcsPskObfCodec.from(this._config.getPskSharedSecret(), this._config.getMaxFrameLength()));
        }

        // server output
        _pl.addLast("protocol-packet-encoder", new NcsPacketEncoder(this._config.getPacketFactory()));
        _pl.addLast("protocol-packet-decoder", new NcsPacketDecoder(this._config.getPacketFactory()));

        // pojo codec
        _pl.addLast("packet-handler", NcsClientConnectionImpl.from(this._config.getPacketListener(), this._config.getStateListener(), _ch));
    }
}
