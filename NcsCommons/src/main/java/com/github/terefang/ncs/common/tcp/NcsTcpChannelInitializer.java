package com.github.terefang.ncs.common.tcp;

import com.github.terefang.ncs.common.NcsCompressionMethod;
import com.github.terefang.ncs.common.NcsConfiguration;
import com.github.terefang.ncs.common.packet.NcsPacketDecoder;
import com.github.terefang.ncs.common.packet.NcsPacketEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.*;

public class NcsTcpChannelInitializer extends ChannelInitializer<Channel> {

    private NcsConfiguration _config;
    public NcsTcpChannelInitializer(NcsConfiguration _config)
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
            ByteToMessageDecoder _dec = null;
            MessageToByteEncoder<ByteBuf> _enc = null;
            switch (_config.getCompressionMethod())
            {
                case BZIP2:
                {
                    _dec = new Bzip2Decoder();
                    _enc = new Bzip2Encoder(_config.getCompressionMaxLevel());
                    break;
                }
                case SNAPPY:
                {
                    _dec = new SnappyFrameDecoder();
                    _enc = new SnappyFrameEncoder();
                    break;
                }
                case FASTLZ:
                {
                    _dec = new FastLzFrameDecoder(true);
                    _enc = new FastLzFrameEncoder(true);
                    break;
                }
                case LZF:
                {
                    _dec = new LzfDecoder();
                    _enc = new LzfEncoder();
                    break;
                }
                case LZ4:
                {
                    _dec = new Lz4FrameDecoder();
                    _enc = new Lz4FrameEncoder();
                    break;
                }
                case ZLIB:
                default:
                {
                    _dec = new JdkZlibDecoder();
                    _enc = new JdkZlibEncoder(_config.getCompressionMaxLevel());
                    break;
                }
            }
            _pl.addLast("compression-frame-decoder", _dec);
            _pl.addLast("compression-frame-encoder", _enc);
        }

        // server output
        _pl.addLast("protocol-packet-encoder", new NcsPacketEncoder(this._config.getPacketFactory()));
        _pl.addLast("protocol-packet-decoder", new NcsPacketDecoder(this._config.getPacketFactory()));
    }
}
