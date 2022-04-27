package com.github.terefang.ncs.server.impl;

import com.github.terefang.ncs.common.*;
import com.github.terefang.ncs.server.NcsServerConfiguration;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.SslHandler;

public class NcsServerChannelInitializer extends ChannelInitializer<NioSocketChannel>
{
    NcsServerConfiguration _config;

    public NcsServerChannelInitializer(NcsServerConfiguration _config)
    {
        this._config = _config;
    }

    protected void initChannel(NioSocketChannel _ch) throws Exception
    {
        // server input
        ChannelPipeline _pl = _ch.pipeline();

        if(this._config.getTlsEngine()!=null)
        {
            _pl.addLast(new SslHandler(this._config.getTlsEngine()));
        }
        if(this._config.getMaxFrameLength()>=65536)
        {
            _pl.addLast(new LengthFieldBasedFrameDecoder(this._config.getMaxFrameLength(), 0, 4, 0, 4));
            _pl.addLast(new LengthFieldPrepender(4, false));
        }
        else
        {
            _pl.addLast(new LengthFieldBasedFrameDecoder(this._config.getMaxFrameLength(), 0, 2, 0, 2));
            _pl.addLast(new LengthFieldPrepender(2, false));
        }
        //_ch.pipeline().addLast(new LoggingHandler(LogLevel.WARN));
        // server output
        _pl.addLast(new NcsPacketEncoder(this._config.getPacketFactory()));
        _pl.addLast(new NcsPacketDecoder(this._config.getPacketFactory()));

        // pojo codec
        _pl.addLast(new NcsServerPacketHandlerImpl(_ch, this._config.getPacketListener(), this._config.getStateListener()));
    }
}
