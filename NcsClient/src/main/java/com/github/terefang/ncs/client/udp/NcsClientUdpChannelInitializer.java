package com.github.terefang.ncs.client.udp;

import com.github.terefang.ncs.client.NcsClientConfiguration;
import com.github.terefang.ncs.common.security.NcsPskObfUdpCodec;
import com.github.terefang.ncs.common.udp.NcsUdpChannelInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

public class NcsClientUdpChannelInitializer extends NcsUdpChannelInitializer
{
    NcsClientConfiguration _config;

    public NcsClientUdpChannelInitializer(NcsClientConfiguration _config)
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
        NcsServerUdpConnection _nc = NcsServerUdpConnection.from(this._config.getPacketListener(), this._config.getStateListener(), _ch);

        super.initChannel(_ch);

        if((this._config.isUsePskOBF() || this._config.isUsePskMac()) && this._config.getPskSharedSecret()!=null)
        {
            NcsPskObfUdpCodec _cdc = NcsPskObfUdpCodec.from(this._config.getPskSharedSecret(), this._config.getMaxFrameLength(), this._config.isUsePskOBF(), this._config.isUsePskMac());
            _nc.setPskObfCodec(_cdc);
            _pl.addFirst("frame-obfuscator", _cdc);
        }

        // pojo codec
        _pl.addLast("packet-handler", _nc);
    }
}
