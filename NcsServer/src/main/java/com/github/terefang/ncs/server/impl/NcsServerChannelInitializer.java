package com.github.terefang.ncs.server.impl;

import com.github.terefang.ncs.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class NcsServerChannelInitializer extends ChannelInitializer<NioSocketChannel>
{
    int _maxFrameLength;
    NcsPacketFactory _packetFactory;
    NcsPacketListener _packetListener;
    NcsStateListener _stateListener;

    public NcsServerChannelInitializer(int _maxFrameLength, NcsPacketFactory _packetFactory, NcsPacketListener _packetListener, NcsStateListener _stateListener) {
        this._maxFrameLength=_maxFrameLength;
        this._packetFactory=_packetFactory;
        this._packetListener=_packetListener;
        this._stateListener=_stateListener;
    }

    protected void initChannel(NioSocketChannel _ch) throws Exception
    {
        // server input
        ChannelPipeline _pl = _ch.pipeline();
        if(_maxFrameLength>=65536)
        {
            _pl.addLast(new LengthFieldBasedFrameDecoder(_maxFrameLength, 0, 4, 0, 4));
            _pl.addLast(new LengthFieldPrepender(4, false));
        }
        else
        {
            _pl.addLast(new LengthFieldBasedFrameDecoder(_maxFrameLength, 0, 2, 0, 2));
            _pl.addLast(new LengthFieldPrepender(2, false));
        }
        //_ch.pipeline().addLast(new LoggingHandler(LogLevel.WARN));
        // server output
        _pl.addLast(new NcsPacketEncoder(_packetFactory));
        _pl.addLast(new NcsPacketDecoder(_packetFactory));

        // pojo codec
        _pl.addLast(new NcsServerPacketHandlerImpl(_ch, _packetListener, _stateListener));
    }
}
