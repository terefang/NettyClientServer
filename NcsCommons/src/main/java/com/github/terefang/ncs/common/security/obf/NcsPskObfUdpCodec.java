package com.github.terefang.ncs.common.security.obf;

import com.github.terefang.ncs.common.NcsHelper;
import com.github.terefang.ncs.common.crypto.PBKDF;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.List;

/**
 * will pseudo encrypt/decrypt frames and check for integrity
 */
public class NcsPskObfUdpCodec extends MessageToMessageCodec<DatagramPacket, DatagramPacket> implements NcsPskObfCodec
{
    NcsPskObfCodecState _state;

    @Override
    public NcsPskObfCodecState getState() {
        return _state;
    }

    @Override
    public void setState(NcsPskObfCodecState state) {
        this._state = state;
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
    public static NcsPskObfUdpCodec from(String _sharedSecret, int _max, boolean _useObf, boolean _useCRC)
    {
        NcsPskObfUdpCodec _codec = new NcsPskObfUdpCodec();
        _codec.newState(_sharedSecret, _max, _useObf, _useCRC);
        return _codec;
    }

    /**
     * is called from the pipeline to encode (ie. obfuscate) the protocol frame
     * @param _ctx  the channel
     * @param _pkt  the packet
     * @param _out  the queue of the pipeline
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext _ctx, DatagramPacket _pkt, List<Object> _out) throws Exception
    {
        try
        {
            ByteBuf _next = NcsPskObfCodecUtil.obfuscate(_pkt.content(), this.getState());
            _out.add(new DatagramPacket(_next, _pkt.recipient(), _pkt.sender()));
        }
        catch (Exception _xe)
        {
            if(!isTolerant()) throw _xe;
        }
    }

    /**
     * is called from the pipeline to decode (ie. deobfuscate) the protocol frame
     * @param _ctx  the channel
     * @param _pkt  the packet
     * @param _out  the queue of the pipeline
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext _ctx, DatagramPacket _pkt, List<Object> _out) throws Exception {
        try
        {
            ByteBuf _next = NcsPskObfCodecUtil.defuscate(_pkt.content(), this.getState());
            _out.add(new DatagramPacket(_next, _pkt.recipient(), _pkt.sender()));
        }
        catch (Exception _xe)
        {
            if(!isTolerant()) throw _xe;
        }
    }
}
