package com.github.terefang.ncs.server.udp;

import com.github.terefang.ncs.common.security.obf.NcsPskObfUdpCodec;
import com.github.terefang.ncs.common.udp.NcsUdpChannelInitializer;
import com.github.terefang.ncs.server.NcsServerConfiguration;
import com.github.terefang.ncs.server.NcsServerServiceImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

public class NcsUdpServerChannelInitializer extends NcsUdpChannelInitializer
{
    NcsServerConfiguration _config;
    NcsServerServiceImpl _server;

    public NcsUdpServerChannelInitializer(NcsServerServiceImpl _server, NcsServerConfiguration _config)
    {
        super(_config);
        this._config = _config;
        this._server = _server;
    }

    protected void initChannel(Channel _ch) throws Exception
    {
        // server input
        final NcsClientUdpConnection _nc = NcsClientUdpConnection.from(this._server, this._config.getPacketListener(), this._config.getStateListener(), _ch);

        ChannelPipeline _pl = _ch.pipeline();

        super.initChannel(_ch);

        if((this._config.isUsePskOBF() || this._config.isUsePskMac()) && this._config.getPskSharedSecret()!=null)
        {
            NcsPskObfUdpCodec _cdc = NcsPskObfUdpCodec.from(this._config.getPskSharedSecret(), this._config.getMaxFrameLength(), this._config.isUsePskOBF(), this._config.isUsePskMac());
            _nc.setPskObfCodec(_cdc);
            _pl.addFirst("frame-obfuscator", _cdc);
        }

        _pl.addFirst("ip-filter", NcsUdpFilter.from(_config));

        // pojo codec
        _pl.addLast("packet-handler", _nc);
    }
}
