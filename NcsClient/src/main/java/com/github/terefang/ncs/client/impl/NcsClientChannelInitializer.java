package com.github.terefang.ncs.client.impl;

import com.github.terefang.ncs.client.NcsClientConfiguration;
import com.github.terefang.ncs.common.packet.NcsPacketDecoder;
import com.github.terefang.ncs.common.packet.NcsPacketEncoder;
import com.github.terefang.ncs.common.pskobf.NcsPskObfCodec;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class NcsClientChannelInitializer extends ChannelInitializer<NioSocketChannel>
{
    NcsClientConfiguration _config;

    public NcsClientChannelInitializer(NcsClientConfiguration _config) {
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
    protected void initChannel(NioSocketChannel _ch) throws Exception
    {
        ChannelPipeline _pl = _ch.pipeline();
        NcsServerConnectionImpl _nc = NcsServerConnectionImpl.from(this._config.getPacketListener(), this._config.getStateListener(), _ch);
        // push a ssl-layer if we have a valid ssl engine
        SSLEngine _engine = this._config.getTlsClientEngine();
        if(_engine!=null)
        {
            _pl.addLast("ssl-tls-layer", new SslHandler(_engine));
        }

        // select a frame format based on max-frame-size
        if(this._config.getMaxFrameLength()>=65536)
        {
            _pl.addLast("frame-decoder", new LengthFieldBasedFrameDecoder(this._config.getMaxFrameLength(), 0, 4, 0, 4));
            _pl.addLast("frame-encoder", new LengthFieldPrepender(4, false));
        }
        else
        {
            _pl.addLast(new LengthFieldBasedFrameDecoder(this._config.getMaxFrameLength(), 0, 2, 0, 2));
            _pl.addLast(new LengthFieldPrepender(2, false));
        }

        if(this._config.isUsePskOBF() && this._config.getPskSharedSecret()!=null)
        {
            NcsPskObfCodec _cdc = NcsPskObfCodec.from(this._config.getPskSharedSecret(), this._config.getMaxFrameLength());
            _nc.setPskObfCodec(_cdc);
            _pl.addLast("frame-obfuscator", _cdc);
        }

        _pl.addLast("packet-encoder", new NcsPacketEncoder(this._config.getPacketFactory()));
        _pl.addLast("packet-decoder", new NcsPacketDecoder(this._config.getPacketFactory()));

        // pojo codec
        _pl.addLast("packet-handler", _nc);
    }
}
