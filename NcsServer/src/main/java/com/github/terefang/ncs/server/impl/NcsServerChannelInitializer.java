package com.github.terefang.ncs.server.impl;

import com.github.terefang.ncs.common.impl.NcsChannelInitializer;
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

public class NcsServerChannelInitializer extends NcsChannelInitializer
{
    NcsServerConfiguration _config;

    public NcsServerChannelInitializer(NcsServerConfiguration _config)
    {
        super(_config);
        this._config = _config;
    }

    protected void initChannel(Channel _ch) throws Exception
    {
        // server input
        NcsClientConnectionImpl _nc = NcsClientConnectionImpl.from(this._config.getPacketListener(), this._config.getStateListener(), _ch);

        ChannelPipeline _pl = _ch.pipeline();

        SSLEngine _engine = this._config.getTlsServerEngine();
        if(_engine!=null)
        {
            _pl.addLast("ssl-tls-layer", new SslHandler(_engine));
        }

        super.initChannel(_ch);

        if((this._config.isUsePskOBF() || this._config.isUsePskMac()) && this._config.getPskSharedSecret()!=null)
        {
            NcsPskObfCodec _cdc = NcsPskObfCodec.from(this._config.getPskSharedSecret(), this._config.getMaxFrameLength(), this._config.isUsePskOBF(), this._config.isUsePskMac());
            _nc.setPskObfCodec(_cdc);
            _pl.addAfter("protocol-packet-decoder", "frame-obfuscator", _cdc);
        }


        // pojo codec
        _pl.addLast("packet-handler", _nc);
    }
}
