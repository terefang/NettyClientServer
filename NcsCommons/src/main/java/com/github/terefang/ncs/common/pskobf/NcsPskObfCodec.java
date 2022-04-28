package com.github.terefang.ncs.common.pskobf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.SneakyThrows;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.List;

public class NcsPskObfCodec extends MessageToMessageCodec<ByteBuf, ByteBuf>
{
    public static String SALT1 = "ac12c1f1-5551-483a-9f71-19dc4f9e3321";
    public static String SALT2 = "8d6b07bf-c4e7-4536-bf9c-20c2bef6db0f";
    public static int MAC_LEN = 2;
    byte[] _pad;
    byte[] _mac;
    public static NcsPskObfCodec from(String _sharedSecret, int _max)
    {
        NcsPskObfCodec _codec = new NcsPskObfCodec();
        _codec._pad = pbkdf2_sha256(_sharedSecret, SALT1, 1<<10, _max);
        _codec._mac = pbkdf2_sha256(_sharedSecret, SALT2, 1<<10, MAC_LEN);
        return _codec;
    }

    @Override
    protected void encode(ChannelHandlerContext _ctx, ByteBuf _msg, List<Object> _out) throws Exception {
        _out.add(obfuscate(_msg, this._pad, this._mac));
    }

    @Override
    protected void decode(ChannelHandlerContext _ctx, ByteBuf _msg, List<Object> _out) throws Exception {
        _out.add(defuscate(_msg, this._pad, this._mac));
    }

    public static ByteBuf obfuscate(ByteBuf _src, byte[] _pad, byte[] _mac)
    {
        int _len = _src.readableBytes();
        if(_len==0) return _src.alloc().buffer(0,0);

        byte[] _tmp = new byte[_len];
        _src.readBytes(_tmp, 0, _len);

        byte[] _tmpMac = sha1HMac(_mac, _tmp);

        for(int _i = 0; _i<_len; _i++)
        {
            _tmp[_i] = (byte) (_tmp[_i] ^ _pad[_i]);
        }

        final ByteBuf _buf = _src.alloc().buffer(_tmp.length+_tmpMac.length);
        _buf.writeBytes(_tmp);
        _buf.writeBytes(_tmpMac);
        return _buf;
    }

    public static ByteBuf defuscate(ByteBuf _src, byte[] _pad, byte[] _mac)
    {
        int _len = _src.readableBytes();
        if(_len==0) return _src.alloc().buffer(0,0);

        byte[] _tmp = new byte[_len-MAC_LEN];
        byte[] _tmpMac = new byte[MAC_LEN];
        _src.readBytes(_tmp, 0, _len);
        _src.readBytes(_tmpMac, 0, MAC_LEN);

        for(int _i = 0; _i<_len; _i++)
        {
            _tmp[_i] = (byte) (_tmp[_i] ^ _pad[_i]);
        }

        byte[] _test = sha1HMac(_mac, _tmp);
        for(int _i = 0; _i<_tmpMac.length; _i++)
        {
            if(_tmpMac[_i]!=_test[_i]) throw new IllegalArgumentException("invalid mac in packet");
        }

        final ByteBuf _buf = _src.alloc().buffer(_len);
        _buf.writeBytes(_tmp);
        return _buf;
    }

    @SneakyThrows
    public static byte[] sha1HMac(byte[] _key, byte[] _buffer)
    {
        return hashMac("HMacSHA1", _key, _buffer);
    }

    @SneakyThrows
    public static byte[] hashMac(String _name, byte[] _key, byte[]... _buffer)
    {
        final SecretKeySpec _keySpec = new SecretKeySpec(_key, _name);
        final Mac _mac = Mac.getInstance(_name);
        _mac.init(_keySpec);
        for(byte[] _b : _buffer)
        {
            _mac.update(_b);
        }
        return _mac.doFinal();
    }

    public static byte[] pbkdf2_sha256(String _Password, String _Salt, int _counter, int _derivedKeyLen)
    {
        return pbkdf2("HmacSHA256", _Password.getBytes(), _Salt.getBytes(), _counter, _derivedKeyLen);
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
