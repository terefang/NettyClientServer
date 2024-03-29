package com.github.terefang.ncs.server.tcp;

import com.github.jgonian.ipmath.Ipv4;
import com.github.jgonian.ipmath.Ipv4Range;
import com.github.terefang.ncs.common.tcp.NcsTcpChannelInitializer;
import com.github.terefang.ncs.common.security.obf.NcsPskObfTcpCodec;
import com.github.terefang.ncs.server.NcsServerConfiguration;
import com.github.terefang.ncs.server.NcsServerServiceImpl;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;

public class NcsTcpServerChannelInitializer extends NcsTcpChannelInitializer
{
    NcsServerConfiguration _config;
    NcsServerServiceImpl _server;

    public NcsTcpServerChannelInitializer(NcsServerServiceImpl _server, NcsServerConfiguration _config)
    {
        super(_config);
        this._config = _config;
        this._server = _server;
    }

    protected void initChannel(Channel _ch) throws Exception
    {
        String _ip = ((InetSocketAddress)_ch.remoteAddress()).getAddress().getHostAddress();
        Ipv4 _ipv4 = Ipv4.of(_ip);
        if(_config.getBannedAddresses().size()>0)
        {
            if(_config.getBannedAddresses().contains(_ipv4))
            {
                _ch.close();
                return;
            }
        }

        if(_config.getBannedNetworks().size()>0)
        {
            for(Ipv4Range _net : _config.getBannedNetworks())
            {
                if(_net.contains(_ipv4))
                {
                    _ch.close();
                    return;
                }
            }
        }

        // server input
        final NcsClientTcpConnection _nc = NcsClientTcpConnection.from(this._server, this._config.getPacketListener(), this._config.getStateListener(), _ch);

        ChannelPipeline _pl = _ch.pipeline();

        SSLEngine _engine = this._config.getTlsServerEngine();
        if(_engine!=null)
        {
            _pl.addLast("ssl-tls-layer", new SslHandler(_engine));
        }
        super.initChannel(_ch);

        if((this._config.isUsePskOBF() || this._config.isUsePskMac()) && this._config.getPskSharedSecret()!=null)
        {
            NcsPskObfTcpCodec _cdc = NcsPskObfTcpCodec.from(this._config.getPskSharedSecret(), this._config.getMaxFrameLength(), this._config.isUsePskOBF(), this._config.isUsePskMac());
            _nc.setPskObfCodec(_cdc);
            _pl.addAfter("protocol-frame-encoder", "frame-obfuscator", _cdc);
        }

        // pojo codec
        _pl.addLast("packet-handler", _nc);
    }
}
