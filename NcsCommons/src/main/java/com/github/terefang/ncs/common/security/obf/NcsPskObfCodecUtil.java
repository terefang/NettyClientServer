package com.github.terefang.ncs.common.security.obf;

import com.github.terefang.ncs.common.NcsHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.List;

/**
 * will pseudo encrypt/decrypt frames and check for integrity
 */
public class NcsPskObfCodecUtil
{
    public static String SALT = "ac12c1f1-5551-483a-9f71-19dc4f9e3321";
    public static int MAC_LEN = 2;
    public static int IV_LEN = 6;

    @SneakyThrows
    public static ByteBuf obfuscate(ByteBuf _src, byte[] _pad, int _mac, boolean _useObf, boolean _useCRC, SecureRandom _rng)
    {
        int _len = _src.readableBytes();
        if(_len==0) return _src.alloc().buffer(0,0);

        byte[] _tmp = new byte[_len];
        _src.readBytes(_tmp, 0, _len);

        byte[] _tmpIV = _useObf ? _rng.generateSeed(IV_LEN) : null;
        byte[] _tmpMac = _useCRC ? NcsHelper.crc16((_useObf ? NcsHelper.crc16i(_mac, _tmpIV): _mac), _tmp) : null;

        final ByteBuf _buf = _src.alloc().buffer(_tmp.length, _tmp.length+MAC_LEN+IV_LEN);

        if(_useObf)
        {
            byte[] _tmpPad = NcsHelper.pbkdf2_sha256(_pad, _tmpIV, 1, _tmp.length);
            for(int _i = 0; _i<_len; _i++)
            {
                _tmp[_i] = (byte) (_tmp[_i] ^ _tmpPad[_i]);
            }
            _buf.writeBytes(_tmpIV);
        }

        _buf.writeBytes(_tmp);

        if(_useCRC)
        {
            _buf.writeBytes(_tmpMac, 0, MAC_LEN);
        }

        return _buf;
    }

    public static ByteBuf defuscate(ByteBuf _src, byte[] _pad, int _mac, boolean _useObf, boolean _useCRC, SecureRandom _rng)
    {
        int _len = _src.readableBytes();
        if(_len==0) return _src.alloc().buffer(0,0);
        if(_len < ((_useCRC ? MAC_LEN : 0) + (_useObf ? IV_LEN : 0))) throw new IllegalArgumentException("invalid mac/iv in packet");

        byte[] _tmp = new byte[_len-((_useCRC ? MAC_LEN : 0) + (_useObf ? IV_LEN : 0))];
        byte[] _tmpMac = _useCRC ? new byte[MAC_LEN] : null;
        byte[] _tmpIV = _useObf ? new byte[IV_LEN] : null;

        if(_useObf) _src.readBytes(_tmpIV, 0, _tmpIV.length);

        _src.readBytes(_tmp, 0, _tmp.length);

        if(_useCRC)
        {
            _src.readBytes(_tmpMac, 0, MAC_LEN);
        }

        if(_useObf)
        {
            byte[] _tmpPad = NcsHelper.pbkdf2_sha256(_pad, _tmpIV, 1, _tmp.length);

            for (int _i = 0; _i < _tmp.length; _i++) {
                _tmp[_i] = (byte) (_tmp[_i] ^ _tmpPad[_i]);
            }
        }

        if(_useCRC)
        {
            byte[] _test = NcsHelper.crc16((_useObf ? NcsHelper.crc16i(_mac, _tmpIV): _mac), _tmp);
            for(int _i = 0; _i<_tmpMac.length; _i++)
            {
                if(_tmpMac[_i]!=_test[_i]) throw new IllegalArgumentException("invalid mac in packet");
            }
        }

        final ByteBuf _buf = _src.alloc().buffer(_len);
        _buf.writeBytes(_tmp);
        return _buf;
    }
}
