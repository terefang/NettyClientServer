package com.github.terefang.ncs.client.impl;

import com.github.terefang.ncs.client.NcsClientConfiguration;
import com.github.terefang.ncs.common.impl.NcsChannelInitializer;
import com.github.terefang.ncs.common.packet.NcsPacketDecoder;
import com.github.terefang.ncs.common.packet.NcsPacketEncoder;
import com.github.terefang.ncs.common.pskobf.NcsPskObfCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class NcsClientChannelInitializer extends NcsChannelInitializer
{
    NcsClientConfiguration _config;

    public NcsClientChannelInitializer(NcsClientConfiguration _config)
    {
        super(_config);
        this._config = _config;
    }

    /**
     * initializes the netty pipeline on the given channel for
     * possible ssl wrap
     * frame decoding
     * packet decoding
     * @param _ch       the channel
     * @throws Exception
     */
    @Override
    protected void initChannel(Channel _ch) throws Exception
    {
        ChannelPipeline _pl = _ch.pipeline();
        NcsServerConnectionImpl _nc = NcsServerConnectionImpl.from(this._config.getPacketListener(), this._config.getStateListener(), _ch);

        // push a ssl-layer if we have a valid ssl engine
        SSLEngine _engine = this._config.getTlsClientEngine();
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
