package com.github.terefang.ncs.common;

import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class SimpleBytesNcsPacket extends AbstractNcsPacket implements NcsPacket
{
    byte[] _buf;

    public static SimpleBytesNcsPacket create()
    {
        SimpleBytesNcsPacket _sbnp = new SimpleBytesNcsPacket();
        return _sbnp;
    }

    public static SimpleBytesNcsPacket from(byte[] _buf)
    {
        SimpleBytesNcsPacket _sbnp = new SimpleBytesNcsPacket();
        _sbnp._buf = new byte[_buf.length];
        System.arraycopy(_buf, 0, _sbnp._buf, 0, _buf.length);
        return _sbnp;
    }

    public static SimpleBytesNcsPacket from(ByteBuf _buf)
    {
        SimpleBytesNcsPacket _sbnp = new SimpleBytesNcsPacket();
        _sbnp._buf = new byte[_buf.readableBytes()];
        _buf.readBytes(_sbnp._buf);
        return _sbnp;
    }

    @Override
    @SneakyThrows
    public byte[] serialize()
    {
        return _buf;
    }

    ByteArrayOutputStream _tempOut;
    DataOutputStream _tempDataOut;

    public void startEncoding()
    {
        _tempOut = new ByteArrayOutputStream();
        _tempDataOut = new DataOutputStream(_tempOut);
    }

    @SneakyThrows
    public void finishEncoding()
    {
        _tempDataOut.close();
        _buf = _tempOut.toByteArray();
        _tempDataOut = null;
        _tempOut = null;
    }

    @SneakyThrows
    public void encodeString(String _s)
    {
        _tempDataOut.writeUTF(_s);
        _tempDataOut.flush();
    }

    @SneakyThrows
    public void encodeInt(int _i)
    {
        _tempDataOut.writeInt(_i);
        _tempDataOut.flush();
    }

    @SneakyThrows
    public void encodeLong(long _l)
    {
        _tempDataOut.writeLong(_l);
        _tempDataOut.flush();
    }

    @SneakyThrows
    public void encodeFloat(float _f)
    {
        _tempDataOut.writeFloat(_f);
        _tempDataOut.flush();
    }

    @SneakyThrows
    public void encodeDouble(double _d)
    {
        _tempDataOut.writeDouble(_d);
        _tempDataOut.flush();
    }

    @SneakyThrows
    public void encodeBoolean(boolean _b)
    {
        _tempDataOut.writeBoolean(_b);
        _tempDataOut.flush();
    }

    @SneakyThrows
    public void encodeBytes(byte[] _b)
    {
        _tempDataOut.writeInt(_b.length);
        _tempDataOut.write(_b);
        _tempDataOut.flush();
    }

    ByteArrayInputStream _tempIn;
    DataInputStream _tempDataIn;

    public void startDecoding()
    {
        _tempIn = new ByteArrayInputStream(this._buf);
        _tempDataIn = new DataInputStream(_tempIn);
    }

    @SneakyThrows
    public void finishDecoding()
    {
        _tempDataIn.close();
        _tempDataIn = null;
        _tempIn = null;
    }

    @SneakyThrows
    public String decodeString()
    {
        return _tempDataIn.readUTF();
    }

    @SneakyThrows
    public byte[] decodeBytes()
    {
        byte[] _buf = new byte[_tempDataIn.readInt()];
        _tempDataIn.read(_buf);
        return _buf;
    }

    @SneakyThrows
    public int decodeInt()
    {
        return _tempDataIn.readInt();
    }

    @SneakyThrows
    public long decodeLong()
    {
        return _tempDataIn.readLong();
    }

    @SneakyThrows
    public float decodeFloat()
    {
        return _tempDataIn.readFloat();
    }

    @SneakyThrows
    public double decodeDouble()
    {
        return _tempDataIn.readDouble();
    }

    @SneakyThrows
    public boolean decodeBoolean()
    {
        return _tempDataIn.readBoolean();
    }
}
