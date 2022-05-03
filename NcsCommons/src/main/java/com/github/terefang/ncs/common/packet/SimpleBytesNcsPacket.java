package com.github.terefang.ncs.common.packet;

import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;


/**
 * a simple byte[] packet representation that allows to encode and decode common simple types
 */
public class SimpleBytesNcsPacket extends AbstractNcsPacket implements NcsPacket
{
    byte[] _buf;

    /**
     * create a packet
     * @return the packet
     */
    public static SimpleBytesNcsPacket create()
    {
        SimpleBytesNcsPacket _sbnp = new SimpleBytesNcsPacket();
        return _sbnp;
    }

    /**
     * create a packet from an array of bytes
     * @param _buf  byte array
     * @return the packet
     */
    public static SimpleBytesNcsPacket from(byte[] _buf)
    {
        SimpleBytesNcsPacket _sbnp = new SimpleBytesNcsPacket();
        _sbnp._buf = new byte[_buf.length];
        System.arraycopy(_buf, 0, _sbnp._buf, 0, _buf.length);
        return _sbnp;
    }

    /**
     * create a packet from a buffer of bytes
     * @param _buf  byte buffer
     * @return the packet
     */
    public static SimpleBytesNcsPacket from(ByteBuf _buf)
    {
        SimpleBytesNcsPacket _sbnp = new SimpleBytesNcsPacket();
        _sbnp._buf = new byte[_buf.readableBytes()];
        _buf.readBytes(_sbnp._buf);
        return _sbnp;
    }

    /**
     * serializes the packet out to a byte array
     * @return the byte array
     */
    @Override
    @SneakyThrows
    public byte[] serialize()
    {
        return _buf;
    }

    ByteArrayOutputStream _tempOut;
    DataOutputStream _tempDataOut;

    /**
     * starts a data encoding to build the packet
     */
    public void startEncoding()
    {
        _tempOut = new ByteArrayOutputStream();
        _tempDataOut = new DataOutputStream(_tempOut);
    }

    /**
     * finishes data encoding to build the packet
     */
    @SneakyThrows
    public void finishEncoding()
    {
        _tempDataOut.close();
        _buf = _tempOut.toByteArray();
        _tempDataOut = null;
        _tempOut = null;
    }

    /**
     * encode a string into the packet
     * @param _s
     */
    @SneakyThrows
    public void encodeString(String _s)
    {
        _tempDataOut.writeUTF(_s);
        _tempDataOut.flush();
    }

    /**
     * encode a int32 into the packet
     * @param _i
     */
    @SneakyThrows
    public void encodeInt(int _i)
    {
        _tempDataOut.writeInt(_i);
        _tempDataOut.flush();
    }

    /**
     * encode a int64 into the packet
     * @param _l
     */
    @SneakyThrows
    public void encodeLong(long _l)
    {
        _tempDataOut.writeLong(_l);
        _tempDataOut.flush();
    }

    /**
     * encode a float32 into the packet
     * @param _f
     */
    @SneakyThrows
    public void encodeFloat(float _f)
    {
        _tempDataOut.writeFloat(_f);
        _tempDataOut.flush();
    }

    /**
     * encode a float64 into the packet
     * @param _d
     */
    @SneakyThrows
    public void encodeDouble(double _d)
    {
        _tempDataOut.writeDouble(_d);
        _tempDataOut.flush();
    }

    /**
     * encode a boolean into the packet
     * @param _b
     */
    @SneakyThrows
    public void encodeBoolean(boolean _b)
    {
        _tempDataOut.writeBoolean(_b);
        _tempDataOut.flush();
    }

    /**
     * encode a byte array into the packet
     * @param _b
     */
    @SneakyThrows
    public void encodeBytes(byte[] _b)
    {
        _tempDataOut.writeInt(_b.length);
        _tempDataOut.write(_b);
        _tempDataOut.flush();
    }

    ByteArrayInputStream _tempIn;
    DataInputStream _tempDataIn;

    /**
     * starts a data decoding from the packet
     */
    public void startDecoding()
    {
        _tempIn = new ByteArrayInputStream(this._buf);
        _tempDataIn = new DataInputStream(_tempIn);
    }

    /**
     * finishes a data decoding from the packet
     */
    @SneakyThrows
    public void finishDecoding()
    {
        _tempDataIn.close();
        _tempDataIn = null;
        _tempIn = null;
    }

    /**
     * decode a string from the packet
     * @return the string
     */
    @SneakyThrows
    public String decodeString()
    {
        return _tempDataIn.readUTF();
    }

    /**
     * decode a byte array from the packet
     * @return the byte array
     */
    @SneakyThrows
    public byte[] decodeBytes()
    {
        byte[] _buf = new byte[_tempDataIn.readInt()];
        _tempDataIn.read(_buf);
        return _buf;
    }

    /**
     * decode a int32 from the packet
     * @return the int
     */
    @SneakyThrows
    public int decodeInt()
    {
        return _tempDataIn.readInt();
    }

    /**
     * decode a int64 from the packet
     * @return the long
     */
    @SneakyThrows
    public long decodeLong()
    {
        return _tempDataIn.readLong();
    }

    /**
     * decode a float32 from the packet
     * @return the float
     */
    @SneakyThrows
    public float decodeFloat()
    {
        return _tempDataIn.readFloat();
    }

    /**
     * decode a float64 from the packet
     * @return the double
     */
    @SneakyThrows
    public double decodeDouble()
    {
        return _tempDataIn.readDouble();
    }

    /**
     * decode a boolean from the packet
     * @return the boolean
     */
    @SneakyThrows
    public boolean decodeBoolean()
    {
        return _tempDataIn.readBoolean();
    }
}