package com.github.terefang.ncs.common.impl;

import com.github.terefang.ncs.common.NcsCompressionMethod;
import com.github.terefang.ncs.common.NcsConfiguration;
import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.packet.NcsPacketDecoder;
import com.github.terefang.ncs.common.packet.NcsPacketEncoder;
import com.github.terefang.ncs.common.pskobf.NcsPskObfCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.compression.*;

import java.util.List;

public class NcsChannelInitializer extends ChannelInitializer<Channel> {

    private NcsConfiguration _config;
    public NcsChannelInitializer(NcsConfiguration _config)
    {
        super();
        this._config=_config;
    }

    @Override
    protected void initChannel(Channel _ch) throws Exception
    {
        ChannelPipeline _pl = _ch.pipeline();
        if(this._config.getMaxFrameLength()>=65536)
        {
            _pl.addLast("protocol-frame-decoder", new LengthFieldBasedFrameDecoder(this._config.getMaxFrameLength(), 0, 4, 0, 4));
            _pl.addLast("protocol-frame-encoder", new LengthFieldPrepender(4, false));
        }
        else
        {
            _pl.addLast("protocol-frame-decoder", new LengthFieldBasedFrameDecoder(this._config.getMaxFrameLength(), 0, 2, 0, 2));
            _pl.addLast("protocol-frame-encoder", new LengthFieldPrepender(2, false));
        }

        if(!_config.getCompressionMethod().equals(NcsCompressionMethod.NONE))
        {
            switch (_config.getCompressionMethod())
            {
                case BZIP2:
                {
                    _pl.addLast("compression-frame-decoder", new Bzip2Decoder());
                    _pl.addLast("compression-frame-encoder", new Bzip2Encoder(_config.getCompressionMaxLevel()));
                    break;
                }
                case SNAPPY:
                {
                    _pl.addLast("compression-frame-decoder", new SnappyFrameDecoder());
                    _pl.addLast("compression-frame-encoder", new SnappyFrameEncoder());
                    break;
                }
                case FASTLZ:
                {
                    _pl.addLast("compression-frame-decoder", new FastLzFrameDecoder(true));
                    _pl.addLast("compression-frame-encoder", new FastLzFrameEncoder(true));
                    break;
                }
                case LZF:
                {
                    _pl.addLast("compression-frame-decoder", new LzfDecoder());
                    _pl.addLast("compression-frame-encoder", new LzfEncoder());
                    break;
                }
                case LZ4:
                {
                    _pl.addLast("compression-frame-decoder", new Lz4FrameDecoder());
                    _pl.addLast("compression-frame-encoder", new Lz4FrameEncoder());
                    break;
                }
                case ZLIB:
                default:
                {
                    _pl.addLast("compression-frame-decoder", new JdkZlibDecoder());
                    _pl.addLast("compression-frame-encoder", new JdkZlibEncoder(_config.getCompressionMaxLevel()));
                    break;
                }
            }
        }

        // server output
        _pl.addLast("protocol-packet-encoder", new NcsPacketEncoder(this._config.getPacketFactory()));
        _pl.addLast("protocol-packet-decoder", new NcsPacketDecoder(this._config.getPacketFactory()));

    }
}
