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
     * @param _slist
     */
    @SneakyThrows
    public void encodeStrings(String[] _slist)
    {
        encodeVarUInt(_slist.length);
        for(String _s : _slist)
        {
            encodeVarString(_s);
        }
    }

    /**
     * encode an int array into the packet
     * @param _list
     */
    @SneakyThrows
    public void encodeInts(int[] _list)
    {
        encodeVarUInt(_list.length);
        for(int _i : _list)
        {
            encodeInt(_i);
        }
    }

    /**
     * encode a string list into the packet
     * @param _slist
     */
    @SneakyThrows
    public void encodeStrings(List<String> _slist)
    {
        encodeVarUInt(_slist.size());
        for(String _s : _slist)
        {
            encodeVarString(_s);
        }
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
        return _tempDataIn.readUTF();
    }

    /**
     * decode a string from the packet in base128/UTF8
     * @return the string
     */
    @SneakyThrows
    public String decodeVarString()
    {
        byte[] _c = new byte[NcsCodecHelper.readVarUInt128(_tempDataIn)];
        _tempDataIn.read(_c);
        return new String(_c, StandardCharsets.UTF_8);
    }

    /**
     * decode a string list from the packet
     * @return the list
     */
    @SneakyThrows
    public List<String> decodeStringList()
    {
        List<String> _ret = new Vector<>();
        int _len = decodeVarUInt();
        for(int _i = 0; _i<_len; _i++)
        {
            _ret.add(decodeVarString());
        }
        return _ret;
    }

    /**
     * decode an int32 array from the packet
     * @return the array
     */
    @SneakyThrows
    public int[] decodeIntArray()
    {
        int _len = decodeVarUInt();
        int[] _ret = new int[_len];
        for(int _i = 0; _i<_len; _i++)
        {
            _ret[_i]= decodeInt();
        }
        return _ret;
    }

    /**
     * decode a string array from the packet
     * @return the array
     */
    @SneakyThrows
    public String[] decodeStringArray()
    {
        int _len = decodeVarUInt();
        String[] _ret = new String[_len];
        for(int _i = 0; _i<_len; _i++)
        {
            _ret[_i]=decodeVarString();
        }
        return _ret;
    }

    /**
     * decode a string from the packet in Pascal-format/ASCII
     * @return the string
     */
    @SneakyThrows
    public String decodePascalString()
    {
        byte[] _c = new byte[_tempDataIn.readByte() & 0xff];
        _tempDataIn.read(_c);
        return new String(_c, StandardCharsets.US_ASCII);
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
     * decode a byte array from the packet
     * @return the byte array
     */
    @SneakyThrows
    public byte[] decodeVarBytes()
    {
        byte[] _buf = new byte[NcsCodecHelper.readVarUInt128(_tempDataIn)];
        _tempDataIn.read(_buf);
        return _buf;
    }

    /**
     * decode an int32 from the packet
     * @return the int
     */
    @SneakyThrows
    public byte decodeByte()
    {
        return _tempDataIn.readByte();
    }

    /**
     * decode an int32 from the packet
     * @return the int
     */
    @SneakyThrows
    public int decodeInt()
    {
        return _tempDataIn.readInt();
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
    public long decodeLong()
    {
        return _tempDataIn.readLong();
    }

    /**
     * decode an int64 from the packet in base128
     * @return the long
     */
    @SneakyThrows
    public long decodeVarULong()
    {
        return NcsCodecHelper.readVarULong128(_tempDataIn);
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
