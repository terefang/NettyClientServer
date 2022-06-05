package com.github.terefang.ncs.common.security.obf;

import com.github.terefang.ncs.common.NcsHelper;
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
    byte[] _pad;
    int _mac;
    boolean useObf;
    boolean useCRC;
    boolean tolerant = true;
    SecureRandom _rng;

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
        _codec._pad = NcsHelper.pbkdf2_sha256(_sharedSecret, NcsPskObfCodecUtil.SALT, 1<<10, _max);
        _codec.useObf = _useObf;
        _codec.useCRC = _useCRC;
        _codec._mac = NcsHelper.crc16i(0, _codec._pad);
        _codec._rng = SecureRandom.getInstanceStrong();
        return _codec;
    }

    public boolean isTolerant() {
        return tolerant;
    }

    public void setTolerant(boolean tolerant) {
        this.tolerant = tolerant;
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
            ByteBuf _next = NcsPskObfCodecUtil.obfuscate(_pkt.content(), this._pad, this._mac, this.useObf, this.useCRC, this._rng);
            _out.add(new DatagramPacket(_next, _pkt.recipient(), _pkt.sender()));
        }
        catch (Exception _xe)
        {
            if(!tolerant) throw _xe;
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
            ByteBuf _next = NcsPskObfCodecUtil.defuscate(_pkt.content(), this._pad, this._mac, this.useObf, this.useCRC, this._rng);
            _out.add(new DatagramPacket(_next, _pkt.recipient(), _pkt.sender()));
        }
        catch (Exception _xe)
        {
            if(!tolerant) throw _xe;
        }
    }
}
