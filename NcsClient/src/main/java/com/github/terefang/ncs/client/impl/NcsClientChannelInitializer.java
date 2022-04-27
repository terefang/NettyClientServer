package com.github.terefang.ncs.client.impl;

import com.github.terefang.ncs.client.NcsClientConfiguration;
import com.github.terefang.ncs.common.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.nio.channels.SocketChannel;

public class NcsClientChannelInitializer extends ChannelInitializer<NioSocketChannel>
{
    NcsClientConfiguration _config;

    public NcsClientChannelInitializer(NcsClientConfiguration _config) {
        this._config = _config;
    }

    @Override
    protected void initChannel(NioSocketChannel _ch) throws Exception
    {
        ChannelPipeline _pl = _ch.pipeline();
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
        _pl.addLast(new NcsPacketEncoder(this._config.getPacketFactory()));
        _pl.addLast(new NcsPacketDecoder(this._config.getPacketFactory()));

        // pojo codec
        _pl.addLast(new NcsClientPacketHandlerImpl(_ch, this._config.getPacketListener(), this._config.getStateListener()));
    }
}
