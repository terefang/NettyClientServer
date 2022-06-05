package com.github.terefang.ncs.client.tcp;

import com.github.terefang.ncs.client.NcsClientConfiguration;
import com.github.terefang.ncs.common.tcp.NcsTcpChannelInitializer;
import com.github.terefang.ncs.common.security.obf.NcsPskObfTcpCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class NcsClientTcpChannelInitializer extends NcsTcpChannelInitializer
{
    NcsClientConfiguration _config;

    public NcsClientTcpChannelInitializer(NcsClientConfiguration _config)
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
        NcsServerTcpConnection _nc = NcsServerTcpConnection.from(this._config.getPacketListener(), this._config.getStateListener(), _ch);

        // push a ssl-layer if we have a valid ssl engine
        SSLEngine _engine = this._config.getTlsClientEngine();
        if(_engine!=null)
        {
            _pl.addLast("ssl-tls-layer", new SslHandler(_engine));
        }

        super.initChannel(_ch);

        if((this._config.isUsePskOBF() || this._config.isUsePskMac()) && this._config.getPskSharedSecret()!=null)
        {
            NcsPskObfTcpCodec _cdc = NcsPskObfTcpCodec.from(this._config.getPskSharedSecret(), this._config.getMaxFrameLength(), this._config.isUsePskOBF(), this._config.isUsePskMac());
            _nc.setPskObfCodec(_cdc);
            _pl.addAfter("protocol-packet-decoder", "frame-obfuscator", _cdc);
        }

        // pojo codec
        _pl.addLast("packet-handler", _nc);
    }
}
