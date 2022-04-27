package com.github.terefang.ncs.client.impl;

import com.github.terefang.ncs.common.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.channels.SocketChannel;

public class NcsClientChannelInitializer extends ChannelInitializer<NioSocketChannel>
{
    int _maxFrameLength;
    NcsPacketFactory _packetFactory;
    NcsPacketListener _packetListener;
    NcsStateListener _stateListener;

    public NcsClientChannelInitializer(int _maxFrameLength, NcsPacketFactory _packetFactory, NcsPacketListener _packetListener, NcsStateListener _stateListener) {
        this._maxFrameLength=_maxFrameLength;
        this._packetFactory=_packetFactory;
        this._packetListener=_packetListener;
        this._stateListener=_stateListener;
    }

    @Override
    protected void initChannel(NioSocketChannel _ch) throws Exception
    {
        _ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(_maxFrameLength, 0, 4, 0, 4));

        _ch.pipeline().addLast(new NcsPacketEncoder());
        _ch.pipeline().addLast(new NcsPacketDecoder(_packetFactory));

        // pojo codec
        _ch.pipeline().addLast(new NcsClientPacketHandlerImpl(_ch, _packetListener, _stateListener));
    }
}
