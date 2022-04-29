package com.github.terefang.ncs.common.pskobf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.SneakyThrows;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.List;

public class NcsPskObfCodec extends MessageToMessageCodec<ByteBuf, ByteBuf>
{
    public static String SALT = "ac12c1f1-5551-483a-9f71-19dc4f9e3321";
    public static int MAC_LEN = 2;
    public static int IV_LEN = 6;
    byte[] _pad;
    int _mac;
    boolean useObf;
    boolean useCRC;
    SecureRandom _rng;

    @SneakyThrows
    public static NcsPskObfCodec from(String _sharedSecret, int _max, boolean _useObf, boolean _useCRC)
    {
        NcsPskObfCodec _codec = new NcsPskObfCodec();
        _codec._pad = pbkdf2_sha256(_sharedSecret, SALT, 1<<10, _max);
        _codec.useObf = _useObf;
        _codec.useCRC = _useCRC;
        _codec._mac = crc16i(0, _codec._pad);
        _codec._rng = SecureRandom.getInstanceStrong();
        return _codec;
    }

    @Override
    protected void encode(ChannelHandlerContext _ctx, ByteBuf _msg, List<Object> _out) throws Exception {
        ByteBuf _next = obfuscate(_msg, this._pad, this._mac, this.useObf, this.useCRC, this._rng);
        _out.add(_next);
    }

    @Override
    protected void decode(ChannelHandlerContext _ctx, ByteBuf _msg, List<Object> _out) throws Exception {
        ByteBuf _next = defuscate(_msg, this._pad, this._mac, this.useObf, this.useCRC, this._rng);
        _out.add(_next);
    }

    @SneakyThrows
    public static ByteBuf obfuscate(ByteBuf _src, byte[] _pad, int _mac, boolean _useObf, boolean _useCRC, SecureRandom _rng)
    {
        int _len = _src.readableBytes();
        if(_len==0) return _src.alloc().buffer(0,0);

        byte[] _tmp = new byte[_len];
        _src.readBytes(_tmp, 0, _len);

        byte[] _tmpIV = _useObf ? _rng.generateSeed(IV_LEN) : null;
        byte[] _tmpMac = _useCRC ? crc16((_useObf ? crc16i(_mac, _tmpIV): _mac), _tmp) : null;

        final ByteBuf _buf = _src.alloc().buffer(_tmp.length, _tmp.length+MAC_LEN+IV_LEN);

        if(_useObf)
        {
            byte[] _tmpPad = pbkdf2_sha256(_pad, _tmpIV, 1, _tmp.length);
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
            byte[] _tmpPad = pbkdf2_sha256(_pad, _tmpIV, 1, _tmp.length);

            for (int _i = 0; _i < _tmp.length; _i++) {
                _tmp[_i] = (byte) (_tmp[_i] ^ _tmpPad[_i]);
            }
        }

        if(_useCRC)
        {
            byte[] _test = crc16((_useObf ? crc16i(_mac, _tmpIV): _mac), _tmp);
            for(int _i = 0; _i<_tmpMac.length; _i++)
            {
                if(_tmpMac[_i]!=_test[_i]) throw new IllegalArgumentException("invalid mac in packet");
            }
        }

        final ByteBuf _buf = _src.alloc().buffer(_len);
        _buf.writeBytes(_tmp);
        return _buf;
    }

    static int[] CRC_TABLE = {
            0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
            0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
            0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
            0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
            0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
            0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
            0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
            0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
            0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
            0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
            0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
            0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
            0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
            0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
            0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
            0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
            0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
            0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
            0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
            0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
            0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
            0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
            0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
            0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
            0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
            0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
            0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
            0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
            0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
            0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
            0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
            0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,
    };

    @SneakyThrows
    public static int crc16i(int _start, byte[] _buffer)
    {
        int _crc = _start;
        for (byte _b : _buffer) {
            _crc = (_crc >>> 8) ^ CRC_TABLE[(_crc ^ _b) & 0xff];
        }
        return (_crc & 0xffff);
    }

    public static byte[] crc16(int _start, byte[] _buffer)
    {
        int _crc = crc16i(_start, _buffer);
        return new byte[] { (byte)(_crc & 0xff), (byte)((_crc >>> 8) & 0xff) };
    }

    public static byte[] pbkdf2_sha256(String _Password, String _Salt, int _counter, int _derivedKeyLen)
    {
        return pbkdf2("HmacSHA256", _Password.getBytes(), _Salt.getBytes(), _counter, _derivedKeyLen);
    }

    public static byte[] pbkdf2_sha256(byte[] _Password, byte[] _Salt, int _counter, int _derivedKeyLen)
    {
        return pbkdf2("HmacSHA256", _Password, _Salt, _counter, _derivedKeyLen);
    }

    @SneakyThrows
    public static byte[] pbkdf2(String alg, byte[] P, byte[] S, int c, int dkLen)
    {
        Mac mac = Mac.getInstance(alg);
        mac.init(new SecretKeySpec(P, alg));
        int hLen = mac.getMacLength();
        byte[] DK = new byte[dkLen==-1 ? hLen : dkLen];
        pbkdf2(mac, S, c, DK, DK.length);
        return DK;
    }

    @SneakyThrows
    public static void pbkdf2(Mac mac, byte[] S, int c, byte[] DK, int dkLen)
    {
        int hLen = mac.getMacLength();

        if (dkLen > Integer.MAX_VALUE)
        {
            throw new GeneralSecurityException("Requested key length too long");
        }

        if(dkLen == -1)
        {
            dkLen = hLen;
        }

        byte[] U      = new byte[hLen];
        byte[] T      = new byte[hLen];
        byte[] block1 = new byte[S.length + 4];

        int l = (int) Math.ceil((double) dkLen / hLen);
        int r = dkLen - (l - 1) * hLen;

        System.arraycopy(S, 0, block1, 0, S.length);

        for (int i = 1; i <= l; i++)
        {
            block1[S.length + 0] = (byte) (i >> 24 & 0xff);
            block1[S.length + 1] = (byte) (i >> 16 & 0xff);
            block1[S.length + 2] = (byte) (i >> 8  & 0xff);
            block1[S.length + 3] = (byte) (i >> 0  & 0xff);

            mac.update(block1);
            mac.doFinal(U, 0);
            System.arraycopy(U, 0, T, 0, hLen);

            for (int j = 1; j < c; j++)
            {
                mac.update(U);
                mac.doFinal(U, 0);

                for (int k = 0; k < hLen; k++)
                {
                    T[k] ^= U[k];
                }
            }

            System.arraycopy(T, 0, DK, (i - 1) * hLen, (i == l ? r : hLen));
        }
    }
}
