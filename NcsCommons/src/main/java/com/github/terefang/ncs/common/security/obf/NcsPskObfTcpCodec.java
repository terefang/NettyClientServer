package com.github.terefang.ncs.common.security.obf;

import com.github.terefang.ncs.common.NcsHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.AttributeKey;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.List;

/**
 * will pseudo encrypt/decrypt frames and check for integrity
 */
public class NcsPskObfTcpCodec extends MessageToMessageCodec<ByteBuf, ByteBuf> implements NcsPskObfCodec {

    public static final AttributeKey<NcsPskObfCodecState> OBF_CODEC_STATE = AttributeKey.valueOf(NcsPskObfCodecState.class.getCanonicalName());

    NcsPskObfCodecState _state = null;

    @Override
    public boolean isTolerant() {
        return _state.tolerant;
    }

    @Override
    public void setTolerant(boolean tolerant) {
        _state.tolerant = tolerant;
    }

    /**
     * create thw codec based on the parameters
     * @param _sharedSecret     a string based shared secret
     * @param _max              the max frame size
     * @param _useObf           if frame obfuscation should be used
     * @param _useCRC           if crc/mac should be used
     * @return the codec
     */
    @SneakyThrows
    public static NcsPskObfTcpCodec from(String _sharedSecret, int _max, boolean _useObf, boolean _useCRC)
    {
        NcsPskObfTcpCodec _codec = new NcsPskObfTcpCodec();
        _codec._state = NcsPskObfCodecState.from(_sharedSecret, _max, _useObf, _useCRC);
        return _codec;
    }

    /**
     * is called from the pipeline to encode (ie. obfuscate) the protocol frame
     * @param _ctx  the channel
     * @param _msg  the frame
     * @param _out  the queue of the pipeline
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext _ctx, ByteBuf _msg, List<Object> _out) throws Exception
    {
        try
        {
            ByteBuf _next = NcsPskObfCodecUtil.obfuscate(_msg, this._state._pad, this._state._mac, this._state.useObf, this._state.useCRC, this._state._rng);
            _out.add(_next);
        }
        catch (Exception _xe)
        {
            if(!_state.tolerant) throw _xe;
        }
    }

    /**
     * is called from the pipeline to decode (ie. deobfuscate) the protocol frame
     * @param _ctx  the channel
     * @param _msg  the frame
     * @param _out  the queue of the pipeline
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext _ctx, ByteBuf _msg, List<Object> _out) throws Exception {
        try
        {
            ByteBuf _next = NcsPskObfCodecUtil.defuscate(_msg, this._state._pad, this._state._mac, this._state.useObf, this._state.useCRC, this._state._rng);
            _out.add(_next);
        }
        catch (Exception _xe)
        {
            if(!_state.tolerant) throw _xe;
        }
    }

}
