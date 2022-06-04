package com.github.terefang.ncs.common.packet;

import com.github.terefang.ncs.common.NcsCodecHelper;
import com.github.terefang.ncs.common.NcsHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Vector;


/**
 * a simple byte[] packet representation that allows to encode and decode common simple types
 */
public class SimpleBytesNcsPacket extends AbstractNcsPacket implements NcsPacket
{
    public byte[] _buf;

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

    public String asHexString()
    {
        return ByteBufUtil.hexDump(_buf);
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
        startEncoding(0);
    }

    @SneakyThrows
    /**
     * starts a data encoding to build the packet at _offset
     * @param _offset
     */
    public void startEncoding(int _offset)
    {
        _tempOut = new ByteArrayOutputStream();
        _tempDataOut = new DataOutputStream(_tempOut);
        if(_offset>0)
        {
            _tempDataOut.write(new byte[_offset]);
        }
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
        NcsCodecHelper.writeString(_tempDataOut, _s);
    }

    /**
     * encode a string into the packet base128/UTF8
     * @param _s
     */
    @SneakyThrows
    public void encodeVarString(String _s)
    {
        NcsCodecHelper.writeVarString(_tempDataOut, _s);
    }

    /**
     * encode a string into the packet in Pascal-format/ASCII
     * @param _s
     */
    @SneakyThrows
    public void encodePascalString(String _s)
    {
        NcsCodecHelper.writePascalString(_tempDataOut, _s);
    }

    /**
     * encode an int8 into the packet
     * @param _i
     */
    @SneakyThrows
    public void encodeByte(byte _i)
    {
        NcsCodecHelper.writeByte(_tempDataOut,_i);
    }

    /**
     * encode an int32 into the packet
     * @param _i
     */
    @SneakyThrows
    public void encodeInt(int _i)
    {
        NcsCodecHelper.writeInt(_tempDataOut,_i);
    }

    /**
     * encode an int32 into the packet in base128
     * @param _i
     */
    @SneakyThrows
    public void encodeVarUInt(int _i)
    {
        NcsCodecHelper.writeVarUInt128(_tempDataOut, _i);
    }

    /**
     * encode an int64 into the packet
     * @param _l
     */
    @SneakyThrows
    public void encodeLong(long _l)
    {
        NcsCodecHelper.writeLong(_tempDataOut, _l);
    }

    /**
     * encode an int64 into the packet in base128
     * @param _l
     */
    @SneakyThrows
    public void encodeVarULong(long _l)
    {
        NcsCodecHelper.writeVarULong128(_tempDataOut, _l);
    }

    /**
     * encode a float32 into the packet
     * @param _f
     */
    @SneakyThrows
    public void encodeFloat(float _f)
    {
        NcsCodecHelper.writeFloat(_tempDataOut, _f);
    }

    /**
     * encode a float16 into the packet
     * @param _f
     */
    @SneakyThrows
    public void encodeFloat16(float _f)
    {
        NcsCodecHelper.writeFloat16(_tempDataOut,_f);
    }

    /**
     * encode a float64 into the packet
     * @param _d
     */
    @SneakyThrows
    public void encodeDouble(double _d)
    {
        NcsCodecHelper.writeDouble(_tempDataOut, _d);
    }

    /**
     * encode a boolean into the packet
     * @param _b
     */
    @SneakyThrows
    public void encodeBoolean(boolean _b)
    {
        NcsCodecHelper.writeBoolean(_tempDataOut, _b);
    }

    /**
     * encode a byte array into the packet
     * @param _b
     */
    @SneakyThrows
    public void encodeBytes(byte[] _b)
    {
        NcsCodecHelper.writeBytes(_tempDataOut,_b);
    }

    /**
     * encode a byte array into the packet
     * @param _b
     */
    @SneakyThrows
    public void encodeVarBytes(byte[] _b)
    {
        NcsCodecHelper.writeVarBytes(_tempDataOut, _b);
    }

    /**
     * encode a string array into the packet
     * @param _list
     */
    @SneakyThrows
    public void encodeStrings(String[] _list)
    {
        NcsCodecHelper.writeStrings(this._tempDataOut, _list);
    }

    /**
     * encode an int array into the packet
     * @param _list
     */
    @SneakyThrows
    public void encodeInts(int[] _list)
    {
        NcsCodecHelper.writeInts(this._tempDataOut, _list);
    }

    /**
     * encode a string list into the packet
     * @param _list
     */
    @SneakyThrows
    public void encodeStrings(List<String> _list)
    {
        NcsCodecHelper.writeStrings(this._tempDataOut, _list);
    }

    ByteArrayInputStream _tempIn;
    DataInputStream _tempDataIn;

    /**
     * starts a data decoding from the packet
     */
    public void startDecoding()
    {
        startDecoding(0);
    }

    /**
     * starts a data decoding from the packet at _offset
     * @param _offset
     */
    @SneakyThrows
    public void startDecoding(int _offset)
    {
        _tempIn = new ByteArrayInputStream(this._buf);
        _tempDataIn = new DataInputStream(_tempIn);
        if(_offset > 0)
        {
            _tempDataIn.skipBytes(_offset);
        }
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
        return NcsCodecHelper.readString(_tempDataIn);
    }

    /**
     * decode a string from the packet in base128/UTF8
     * @return the string
     */
    @SneakyThrows
    public String decodeVarString()
    {
        return NcsCodecHelper.readVarString(_tempDataIn);
    }

    /**
     * decode a string list from the packet
     * @return the list
     */
    @SneakyThrows
    public List<String> decodeStringList()
    {
        return NcsCodecHelper.readStrings(_tempDataIn);
    }

    /**
     * decode an int32 array from the packet
     * @return the array
     */
    @SneakyThrows
    public int[] decodeIntArray()
    {
        return NcsCodecHelper.readIntArray(_tempDataIn);
    }

    /**
     * decode a string array from the packet
     * @return the array
     */
    @SneakyThrows
    public String[] decodeStringArray()
    {
        return NcsCodecHelper.readStringArray(_tempDataIn);
    }

    /**
     * decode a string from the packet in Pascal-format/ASCII
     * @return the string
     */
    @SneakyThrows
    public String decodePascalString()
    {
        return NcsCodecHelper.readPascalString(_tempDataIn);
    }

    /**
     * decode a byte array from the packet
     * @return the byte array
     */
    @SneakyThrows
    public byte[] decodeBytes()
    {
        return NcsCodecHelper.readBytes(_tempDataIn);
    }

    /**
     * decode a byte array from the packet
     * @return the byte array
     */
    @SneakyThrows
    public byte[] decodeVarBytes()
    {
        return NcsCodecHelper.readVarBytes(_tempDataIn);
    }

    /**
     * decode an int8 from the packet
     * @return the int
     */
    @SneakyThrows
    public byte decodeByte()
    {
        return NcsCodecHelper.readByte(_tempDataIn);
    }

    /**
     * decode an int32 from the packet
     * @return the int
     */
    @SneakyThrows
    public int decodeInt()
    {
        return NcsCodecHelper.readInt(_tempDataIn);
    }

    /**
     * decode an int32 from the packet in base128
     * @return the int
     */
    @SneakyThrows
    public int decodeVarUInt()
    {
        return NcsCodecHelper.readVarUInt128(_tempDataIn);
    }

    /**
     * decode an int64 from the packet
     * @return the long
     */
    @SneakyThrows
    public long decodeLong(){
        return NcsCodecHelper.readLong(_tempDataIn);
    }

    /**
     * decode an int64 from the packet in base128
     * @return the long
     */
    @SneakyThrows
    public long decodeVarULong(){
        return NcsCodecHelper.readVarULong128(_tempDataIn);
    }

    /**
     * decode a float32 from the packet
     * @return the float
     */
    @SneakyThrows
    public float decodeFloat(){
        return NcsCodecHelper.readFloat(_tempDataIn);
    }

    /**
     * decode a float16 from the packet
     * @return the float
     */
    @SneakyThrows
    public float decodeFloat16()
    {
        return NcsCodecHelper.readFloat16(_tempDataIn);
    }

    /**
     * decode a float64 from the packet
     * @return the double
     */
    @SneakyThrows
    public double decodeDouble(){
        return NcsCodecHelper.readDouble(_tempDataIn);
    }

    /**
     * decode a boolean from the packet
     * @return the boolean
     */
    @SneakyThrows
    public boolean decodeBoolean(){
        return NcsCodecHelper.readBoolean(_tempDataIn);
    }
}
