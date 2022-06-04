package com.github.terefang.ncs.common;

import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Objects;
import java.util.UUID;

public class XUID {
    long _n1;
    int _n2;
    int _n3;

    public static XUID create()
    {
        return from(UUID.randomUUID());
    }

    public static XUID from(UUID _i)
    {
        return from(_i.getMostSignificantBits(), _i.getLeastSignificantBits());
    }

    public static XUID from(long _i1, long _i2)
    {
        XUID _xuid = new XUID();
        _xuid._n1 = _i1;
        _xuid._n2 = (int) ((_i2 >>> 32) & 0xffffffffL);
        _xuid._n3 = (int) (_i2 & 0xffffffffL);
        return _xuid;
    }

    public static XUID from(long _i1, int _i2, int _i3)
    {
        XUID _xuid = new XUID();
        _xuid._n1 = _i1;
        _xuid._n2 = _i2;
        _xuid._n3 = _i3;
        return _xuid;
    }

    public static XUID from(ByteBuf _buf)
    {
        return from(_buf.readLong(), _buf.readLong());
    }

    public static XUID from(ByteBuf _buf, int _index)
    {
        return from(_buf.getLong(_index), _buf.getLong(_index+8));
    }

    @SneakyThrows
    public static XUID from(DataInputStream _buf)
    {
        return from(_buf.readLong(), _buf.readLong());
    }

    @SneakyThrows
    public void writeTo(DataOutputStream _buf)
    {
        _buf.write(this.asBytes());
    }

    public void writeTo(ByteBuf _buf)
    {
        _buf.writeBytes(this.asBytes());
    }

    public int encodeTo(ByteBuf _buf, int _index)
    {
        _buf.setBytes(_index, this.asBytes());
        return 16;
    }

    public String asString()
    {
        return String.format("%s-%s-%s-%s",
                Long.toString((_n1 >>> 32)& 0xffffffffL, 36).toUpperCase(),
                Long.toString(_n1& 0xffffffffL, 36).toUpperCase(),
                Long.toString(((long)_n2) & 0xffffffffL, 36).toUpperCase(),
                Long.toString(((long)_n3)& 0xffffffffL, 36).toUpperCase());
    }

    public UUID asUUID()
    {
        return new UUID(_n1, ((((long)_n2) & 0xffffffffL) << 32) | (((long)_n3) & 0xffffffffL) );
    }

    public byte[] asBytes()
    {
        byte[] _ret = new byte[16];
        _ret[0] = (byte)((_n1 >>> 56) & 0xff);
        _ret[1] = (byte)((_n1 >>> 48) & 0xff);
        _ret[2] = (byte)((_n1 >>> 40) & 0xff);
        _ret[3] = (byte)((_n1 >>> 32) & 0xff);
        _ret[4] = (byte)((_n1 >>> 24) & 0xff);
        _ret[5] = (byte)((_n1 >>> 16) & 0xff);
        _ret[6] = (byte)((_n1 >>> 8) & 0xff);
        _ret[7] = (byte)(_n1  & 0xff);

        _ret[8] = (byte)((_n2 >>> 24) & 0xff);
        _ret[9] = (byte)((_n2 >>> 16) & 0xff);
        _ret[10] = (byte)((_n2 >>> 8) & 0xff);
        _ret[11] = (byte)(_n2 & 0xff);

        _ret[12] = (byte)((_n3 >>> 24) & 0xff);
        _ret[13] = (byte)((_n3 >>> 16) & 0xff);
        _ret[14] = (byte)((_n3 >>> 8) & 0xff);
        _ret[15] = (byte)(_n3 & 0xff);
        return _ret;
    }

    public XUID incr()
    {
        XUID _xuid = new XUID();
        _xuid._n1 = this._n1;
        _xuid._n2 = this._n2;
        _xuid._n3 = this._n3+1;
        return _xuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XUID xuid = (XUID) o;
        return _n1 == xuid._n1 && _n2 == xuid._n2 && _n3 == xuid._n3;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_n1, _n2, _n3);
    }
}
