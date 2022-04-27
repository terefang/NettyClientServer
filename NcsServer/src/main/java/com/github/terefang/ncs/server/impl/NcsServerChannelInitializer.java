package com.github.terefang.ncs.server.impl;

import com.github.terefang.ncs.common.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

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
        _ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(_maxFrameLength, 0, 4, 0, 4));
        //_ch.pipeline().addLast(new LoggingHandler(LogLevel.WARN));
        // server output
        _ch.pipeline().addLast(new NcsPacketEncoder());
        _ch.pipeline().addLast(new NcsPacketDecoder(_packetFactory));

        // pojo codec
        _ch.pipeline().addLast(new NcsServerPacketHandlerImpl(_ch, _packetListener, _stateListener));
    }
}
