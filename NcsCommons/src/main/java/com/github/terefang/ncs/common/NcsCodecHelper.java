package com.github.terefang.ncs.common;

import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Vector;

public class NcsCodecHelper {
    /**
     * A variable-length unsigned integer using base128 encoding. 1-byte groups
     * consist of 1-bit flag of continuation and 7-bit value chunk, and are ordered
     * "most significant group first", i.e. in "big-endian" manner.
     * <p>
     * This particular encoding is specified and used in:
     * <p>
     * * Standard MIDI file format
     * * ASN.1 BER encoding
     * * RAR 5.0 file format
     * <p>
     * More information on this encoding is available at
     * https://en.wikipedia.org/wiki/Variable-length_quantity
     * <p>
     * This particular implementation supports serialized values to up 4 bytes long.
     */
    public static int decodeVarUInt128(ByteBuf _buf, int _index) {
        int _ret = 0;
        for (int _i = 0; _i < 5; _i++) {
            byte _b = _buf.getByte(_index + _i);
            _ret = (_ret << 7) | (_b & 0x7f);
            if ((_b & 0x80) == 0) {
                return _ret;
            }
        }
        throw new IllegalArgumentException("illegal opcode in packet (varint)" + _ret);
    }

    public static int encodeVarUInt128(ByteBuf _buf, int _index, int _v) {
        int _bytes = ((32 - Integer.numberOfLeadingZeros(_v)) / 7) + 1;
        for (int _i = _bytes - 1, _j = 0; _i >= 0; _i--, _j++) {
            int _out = (_v >>> (_i * 7)) & 0x7f;
            _buf.setByte(_index + _j, ((_i > 0 ? 0x80 : 0) | _out));
        }
        return _bytes;
    }

    public static long decodeVarULong128(ByteBuf _buf, int _index) {
        long _ret = 0;
        for (int _i = 0; _i < 10; _i++) {
            byte _b = _buf.getByte(_index + _i);
            _ret = (_ret << 7) | (_b & 0x7f);
            if ((_b & 0x80) == 0) {
                return _ret;
            }
        }
        throw new IllegalArgumentException("illegal opcode in packet (varlong)" + _ret);
    }

    public static int encodeVarULong128(ByteBuf _buf, int _index, long _v) {
        int _bytes = ((64 - Long.numberOfLeadingZeros(_v)) / 7) + 1;
        for (int _i = _bytes - 1, _j = 0; _i >= 0; _i--, _j++) {
            int _out = (int) ((_v >>> (_i * 7)) & 0x7f);
            _buf.setByte(_index + _j, ((_i > 0 ? 0x80 : 0) | _out));
        }
        return _bytes;
    }

    public static int readVarUInt128(ByteBuf _buf) {
        int _ret = 0;
        for (int _i = 0; _i < 5; _i++) {
            byte _b = _buf.readByte();
            _ret = (_ret << 7) | (_b & 0x7f);
            if ((_b & 0x80) == 0) {
                return _ret;
            }
        }
        throw new IllegalArgumentException("illegal varint " + _ret);
    }

    public static int writeVarUInt128(ByteBuf _buf, int _v) {
        int _bytes = ((32 - Integer.numberOfLeadingZeros(_v)) / 7) + 1;
        for (int _i = _bytes - 1, _j = 0; _i >= 0; _i--, _j++) {
            int _out = (_v >>> (_i * 7)) & 0x7f;
            _buf.writeByte((_i > 0 ? 0x80 : 0) | _out);
        }
        return _bytes;
    }

    public static long readVarULong128(ByteBuf _buf) {
        long _ret = 0;
        for (int _i = 0; _i < 10; _i++) {
            byte _b = _buf.readByte();
            _ret = (_ret << 7) | (_b & 0x7f);
            if ((_b & 0x80) == 0) {
                return _ret;
            }
        }
        throw new IllegalArgumentException("illegal varlong" + _ret);
    }

    public static int writeVarULong128(ByteBuf _buf, long _v) {
        int _bytes = ((64 - Long.numberOfLeadingZeros(_v)) / 7) + 1;
        for (int _i = _bytes - 1, _j = 0; _i >= 0; _i--, _j++) {
            int _out = (int) ((_v >>> (_i * 7)) & 0x7f);
            _buf.writeByte((_i > 0 ? 0x80 : 0) | _out);
        }
        return _bytes;
    }

    @SneakyThrows
    public static int readVarUInt128(InputStream _buf) {
        int _ret = 0;
        for (int _i = 0; _i < 5; _i++) {
            byte _b = (byte) _buf.read();
            _ret = (_ret << 7) | (_b & 0x7f);
            if ((_b & 0x80) == 0) {
                return _ret;
            }
        }
        throw new IllegalArgumentException("illegal varint " + _ret);
    }

    @SneakyThrows
    public static int writeVarUInt128(OutputStream _buf, int _v) {
        int _bytes = ((32 - Integer.numberOfLeadingZeros(_v)) / 7) + 1;
        for (int _i = _bytes - 1, _j = 0; _i >= 0; _i--, _j++) {
            int _out = (_v >>> (_i * 7)) & 0x7f;
            _buf.write((_i > 0 ? 0x80 : 0) | _out);
        }
        return _bytes;
    }

    @SneakyThrows
    public static long readVarULong128(InputStream _buf) {
        long _ret = 0;
        for (int _i = 0; _i < 10; _i++) {
            byte _b = (byte) _buf.read();
            _ret = (_ret << 7) | (_b & 0x7f);
            if ((_b & 0x80) == 0) {
                return _ret;
            }
        }
        throw new IllegalArgumentException("illegal varlong" + _ret);
    }

    @SneakyThrows
    public static int writeVarULong128(OutputStream _buf, long _v) {
        int _bytes = ((64 - Long.numberOfLeadingZeros(_v)) / 7) + 1;
        for (int _i = _bytes - 1, _j = 0; _i >= 0; _i--, _j++) {
            int _out = (int) ((_v >>> (_i * 7)) & 0x7f);
            _buf.write((_i > 0 ? 0x80 : 0) | _out);
        }
        return _bytes;
    }

    /* ----- FLOAT16 ----- */

    public static float decodeFloat16(ByteBuf _buf, int _index) {
        return NcsHelper.toFloatFrom16(_buf.getShort(_index));
    }

    public static int encodeFloat16(ByteBuf _buf, int _index, float _v) {
        _buf.setShort(_index, NcsHelper.fromFloatTo16(_v));
        return 2;
    }

    public static float readFloat16(ByteBuf _buf) {
        return NcsHelper.toFloatFrom16(_buf.readShort());
    }

    public static int writeFloat16(ByteBuf _buf, float _v) {
        _buf.writeShort(NcsHelper.fromFloatTo16(_v));
        return 2;
    }

    @SneakyThrows
    public static float readFloat16(DataInputStream _buf) {
        return NcsHelper.toFloatFrom16(_buf.readShort());
    }

    @SneakyThrows
    public static int writeFloat16(DataOutputStream _buf, float _v) {
        _buf.writeShort(NcsHelper.fromFloatTo16(_v));
        return 2;
    }

    /* ----- STRING ----- */

    @SneakyThrows
    public static void writeString(ByteBuf _buf, String _s) {
        byte[] _utf8 = _s.getBytes(StandardCharsets.UTF_8);
        _buf.writeInt(_utf8.length);
        _buf.writeBytes(_utf8);
    }

    @SneakyThrows
    public static int encodeString(ByteBuf _buf, int _index, String _s) {
        byte[] _utf8 = _s.getBytes(StandardCharsets.UTF_8);
        _buf.setInt(_index, _utf8.length);
        _buf.setBytes(_index+4, _utf8);
        return _utf8.length+4;
    }

    @SneakyThrows
    public static void writeString(DataOutputStream _buf, String _s) {
        byte[] _utf8 = _s.getBytes(StandardCharsets.UTF_8);
        _buf.writeInt(_utf8.length);
        _buf.write(_utf8);
    }

    @SneakyThrows
    public static String readString(ByteBuf _buf) {
        int _len = _buf.readInt();
        byte[] _utf8 = new byte[_len];
        _buf.readBytes(_utf8);
        return new String(_utf8, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static String decodeString(ByteBuf _buf, int _index)
    {
        int _len = _buf.getInt(_index);
        byte[] _utf8 = new byte[_len];
        _buf.getBytes(_index+4, _utf8);
        return new String(_utf8, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static String readString(DataInputStream _buf)
    {
        int _len = _buf.readInt();
        byte[] _utf8 = new byte[_len];
        _buf.read(_utf8);
        return new String(_utf8, StandardCharsets.UTF_8);
    }

    /* ----- VARSTRING ----- */

    @SneakyThrows
    public static void writeVarString(ByteBuf _buf, String _s)
    {
        byte[] _utf8 = _s.getBytes(StandardCharsets.UTF_8);
        writeVarUInt128(_buf, _utf8.length);
        _buf.writeBytes(_utf8);
    }

    @SneakyThrows
    public static int encodeVarString(ByteBuf _buf, int _index, String _s) {
        byte[] _utf8 = _s.getBytes(StandardCharsets.UTF_8);
        int _len = encodeVarUInt128(_buf, _index, _utf8.length);
        _buf.setBytes(_index+_len, _utf8);
        return _len + _utf8.length;
    }

    @SneakyThrows
    public static void writeVarString(DataOutputStream _buf, String _s) {
        byte[] _utf8 = _s.getBytes(StandardCharsets.UTF_8);
        writeVarUInt128(_buf, _utf8.length);
        _buf.write(_utf8);
    }

    @SneakyThrows
    public static String readVarString(ByteBuf _buf) {
        int _len = readVarUInt128(_buf);
        byte[] _utf8 = new byte[_len];
        _buf.readBytes(_utf8);
        return new String(_utf8, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static String readVarString(DataInputStream _buf)
    {
        int _len = readVarUInt128(_buf);
        byte[] _utf8 = new byte[_len];
        _buf.read(_utf8);
        return new String(_utf8, StandardCharsets.UTF_8);
    }

    /* ----- PASCALSTRING ----- */

    @SneakyThrows
    public static void writePascalString(ByteBuf _buf, String _s)
    {
        byte[] _c = _s.getBytes(StandardCharsets.US_ASCII);
        _buf.writeByte(_c.length & 0xff);
        _buf.writeBytes(_c);
    }

    @SneakyThrows
    public static int encodePascalString(ByteBuf _buf, int _index, String _s)
    {
        byte[] _c = _s.getBytes(StandardCharsets.US_ASCII);
        _buf.setByte(_index, _c.length & 0xff);
        _buf.setBytes(_index+1,_c);
        return _c.length+1;
    }

    @SneakyThrows
    public static void writePascalString(DataOutputStream _buf, String _s)
    {
        byte[] _c = _s.getBytes(StandardCharsets.US_ASCII);
        _buf.writeByte(_c.length & 0xff);
        _buf.write(_c);
        _buf.flush();
    }

    @SneakyThrows
    public static String readPascalString(ByteBuf _buf) {
        int _len = _buf.readByte() & 0xff;
        byte[] _utf8 = new byte[_len];
        _buf.readBytes(_utf8);
        return new String(_utf8, StandardCharsets.US_ASCII);
    }

    @SneakyThrows
    public static String decodePascalString(ByteBuf _buf, int _index)
    {
        int _len = _buf.getByte(_index) & 0xff;
        byte[] _utf8 = new byte[_len];
        _buf.getBytes(_index+1, _utf8);
        return new String(_utf8, StandardCharsets.US_ASCII);
    }

    @SneakyThrows
    public static String readPascalString(DataInputStream _buf)
    {
        int _len = _buf.readByte() & 0xff;
        byte[] _utf8 = new byte[_len];
        _buf.read(_utf8);
        return new String(_utf8, StandardCharsets.US_ASCII);
    }

    /* ----- STRING-ARRAY ----- */

    @SneakyThrows
    public static void writeStrings(ByteBuf _buf, String[] _list)
    {
        writeVarUInt128(_buf, _list.length);
        for(String _i : _list)
        {
            writeVarString(_buf, _i);
        }
    }

    @SneakyThrows
    public static int encodeStrings(ByteBuf _buf, int _index, String[] _list)
    {
        int _len = encodeVarUInt128(_buf, _index, _list.length);
        for(String _i : _list)
        {
            _len+= encodeVarString(_buf, _index+_len, _i);
        }
        return _len;
    }

    @SneakyThrows
    public static void writeStrings(DataOutputStream _buf, String[] _list)
    {
        writeVarUInt128(_buf, _list.length);
        for(String _i : _list)
        {
            writeVarString(_buf, _i);
        }
        _buf.flush();
    }

    @SneakyThrows
    public static void writeStrings(ByteBuf _buf, List<String> _list)
    {
        writeVarUInt128(_buf, _list.size());
        for(String _i : _list)
        {
            writeVarString(_buf, _i);
        }
    }

    @SneakyThrows
    public static int encodeStrings(ByteBuf _buf, int _index, List<String> _list)
    {
        int _len = encodeVarUInt128(_buf, _index, _list.size());
        for(String _i : _list)
        {
            _len+= encodeVarString(_buf, _index+_len, _i);
        }
        return _len;
    }

    @SneakyThrows
    public static void writeStrings(DataOutputStream _buf, List<String> _list)
    {
        writeVarUInt128(_buf, _list.size());
        for(String _i : _list)
        {
            writeVarString(_buf, _i);
        }
        _buf.flush();
    }

    @SneakyThrows
    public static List<String> readStrings(ByteBuf _buf)
    {
        List<String> _ret = new Vector<>();
        int _len = readVarUInt128(_buf);
        for(int _i = 0; _i<_len; _i++)
        {
            _ret.add(readVarString(_buf));
        }
        return _ret;
    }

    @SneakyThrows
    public static List<String> readStrings(DataInputStream _buf)
    {
        List<String> _ret = new Vector<>();
        int _len = readVarUInt128(_buf);
        for(int _i = 0; _i<_len; _i++)
        {
            _ret.add(readVarString(_buf));
        }
        return _ret;
    }

    @SneakyThrows
    public static String[] readStringArray(ByteBuf _buf)
    {
        int _len = readVarUInt128(_buf);
        String[] _ret = new String[_len];
        for(int _i = 0; _i<_len; _i++)
        {
            _ret[_i] = readVarString(_buf);
        }
        return _ret;
    }

    @SneakyThrows
    public static String[] readStringArray(DataInputStream _buf)
    {
        int _len = readVarUInt128(_buf);
        String[] _ret = new String[_len];
        for(int _i = 0; _i<_len; _i++)
        {
            _ret[_i] = readVarString(_buf);
        }
        return _ret;
    }

    /* ----- BYTE ----- */

    @SneakyThrows
    public static void writeByte(ByteBuf _buf, byte _i)
    {
        _buf.writeByte(_i);
    }

    @SneakyThrows
    public static int encodeByte(ByteBuf _buf, int _index, byte _i)
    {
        _buf.setByte(_index, _i);
        return 1;
    }

    @SneakyThrows
    public static void writeByte(DataOutputStream _buf, byte _i)
    {
        _buf.writeByte(_i);
        _buf.flush();
    }

    @SneakyThrows
    public static byte readByte(ByteBuf _buf)
    {
        return _buf.readByte();
    }

    @SneakyThrows
    public static byte readByte(DataInputStream _buf)
    {
        return _buf.readByte();
    }

    /* ----- INTEGER ----- */

    @SneakyThrows
    public static void writeInt(ByteBuf _buf, int _i)
    {
        _buf.writeInt(_i);
    }

    @SneakyThrows
    public static int encodeInt(ByteBuf _buf, int _index, int _i)
    {
        _buf.setInt(_index, _i);
        return 4;
    }

    @SneakyThrows
    public static void writeInt(DataOutputStream _buf, int _i)
    {
        _buf.writeInt(_i);
        _buf.flush();
    }

    @SneakyThrows
    public static int readInt(ByteBuf _buf)
    {
        return _buf.readInt();
    }

    @SneakyThrows
    public static int readInt(DataInputStream _buf)
    {
        return _buf.readInt();
    }

    /* ----- INTEGER-ARRAY ----- */

    @SneakyThrows
    public static void writeInts(ByteBuf _buf, int[] _list)
    {
        writeVarUInt128(_buf, _list.length);
        for(int _i : _list)
        {
            _buf.writeInt(_i);
        }
    }

    @SneakyThrows
    public static int encodeInts(ByteBuf _buf, int _index, int[] _list)
    {
        int _len = encodeVarUInt128(_buf, _index, _list.length);
        for(int _i : _list)
        {
            _len+= encodeInt(_buf, _index+_len, _i);
        }
        return _len;
    }

    @SneakyThrows
    public static void writeInts(DataOutputStream _buf, int[] _list)
    {
        writeVarUInt128(_buf, _list.length);
        for(int _i : _list)
        {
            _buf.writeInt(_i);
        }
        _buf.flush();
    }

    @SneakyThrows
    public static List<Integer> readIntegers(ByteBuf _buf)
    {
        List<Integer> _ret = new Vector<>();
        int _len = readVarUInt128(_buf);
        for(int _i = 0; _i<_len; _i++)
        {
            _ret.add(readInt(_buf));
        }
        return _ret;
    }

    @SneakyThrows
    public static List<Integer> readIntegers(DataInputStream _buf)
    {
        List<Integer> _ret = new Vector<>();
        int _len = readVarUInt128(_buf);
        for(int _i = 0; _i<_len; _i++)
        {
            _ret.add(readInt(_buf));
        }
        return _ret;
    }

    @SneakyThrows
    public static int[] readIntArray(ByteBuf _buf)
    {
        int _len = readVarUInt128(_buf);
        int[] _ret = new int[_len];
        for(int _i = 0; _i<_len; _i++)
        {
            _ret[_i] = readInt(_buf);
        }
        return _ret;
    }

    @SneakyThrows
    public static int[] readIntArray(DataInputStream _buf)
    {
        int _len = readVarUInt128(_buf);
        int[] _ret = new int[_len];
        for(int _i = 0; _i<_len; _i++)
        {
            _ret[_i] = readInt(_buf);
        }
        return _ret;
    }

    /* ----- LONG ----- */

    @SneakyThrows
    public static void writeLong(ByteBuf _buf, long _i)
    {
        _buf.writeLong(_i);
    }

    @SneakyThrows
    public static int encodeLong(ByteBuf _buf, int _index, long _i)
    {
        _buf.setLong(_index, _i);
        return 8;
    }

    @SneakyThrows
    public static void writeLong(DataOutputStream _buf, long _i)
    {
        _buf.writeLong(_i);
        _buf.flush();
    }

    @SneakyThrows
    public static long readLong(ByteBuf _buf)
    {
        return _buf.readLong();
    }

    @SneakyThrows
    public static long readLong(DataInputStream _buf)
    {
        return _buf.readLong();
    }

    /* ----- LONG-ARRAY ----- */

    @SneakyThrows
    public static void writeLongs(ByteBuf _buf, long[] _list)
    {
        writeVarUInt128(_buf, _list.length);
        for(long _i : _list)
        {
            _buf.writeLong(_i);
        }
    }

    @SneakyThrows
    public static int encodeLongs(ByteBuf _buf, int _index, long[] _list)
    {
        int _len = encodeVarUInt128(_buf, _index, _list.length);
        for(long _i : _list)
        {
            _len+= encodeLong(_buf, _index+_len, _i);
        }
        return _len;
    }

    @SneakyThrows
    public static void writeLongs(DataOutputStream _buf, long[] _list)
    {
        writeVarUInt128(_buf, _list.length);
        for(long _i : _list)
        {
            _buf.writeLong(_i);
        }
        _buf.flush();
    }

    /* ----- FLOAT ----- */

    @SneakyThrows
    public static void writeFloat(ByteBuf _buf, float _f)
    {
        _buf.writeFloat(_f);
    }

    @SneakyThrows
    public static int encodeFloat(ByteBuf _buf, int _index, float _f)
    {
        _buf.setFloat(_index, _f);
        return 4;
    }

    @SneakyThrows
    public static void writeFloat(DataOutputStream _buf, float _f)
    {
        _buf.writeFloat(_f);
        _buf.flush();
    }

    @SneakyThrows
    public static float readFloat(ByteBuf _buf)
    {
        return _buf.readFloat();
    }

    @SneakyThrows
    public static float readFloat(DataInputStream _buf)
    {
        return _buf.readFloat();
    }

    /* ----- DOUBLE ----- */

    @SneakyThrows
    public static void writeDouble(ByteBuf _buf, double _f)
    {
        _buf.writeDouble(_f);
    }

    @SneakyThrows
    public static int encodeDouble(ByteBuf _buf, int _index, double _f)
    {
        _buf.setDouble(_index, _f);
        return 8;
    }

    @SneakyThrows
    public static void writeDouble(DataOutputStream _buf, double _f)
    {
        _buf.writeDouble(_f);
        _buf.flush();
    }

    @SneakyThrows
    public static double readDouble(ByteBuf _buf)
    {
        return _buf.readDouble();
    }

    @SneakyThrows
    public static double readDouble(DataInputStream _buf)
    {
        return _buf.readDouble();
    }

    /* ----- BOOLEAN ----- */

    @SneakyThrows
    public static void writeBoolean(ByteBuf _buf, boolean _b)
    {
        _buf.writeByte(_b ? 1 : 0);
    }

    @SneakyThrows
    public static int encodeBoolean(ByteBuf _buf, int _index, boolean _b)
    {
        _buf.setByte(_index, _b ? 1 : 0);
        return 1;
    }

    @SneakyThrows
    public static void writeBoolean(DataOutputStream _buf, boolean _b)
    {
        _buf.writeByte(_b ? 1 : 0);
        _buf.flush();
    }

    @SneakyThrows
    public static boolean readBoolean(ByteBuf _buf)
    {
        return _buf.readByte() != 0;
    }

    @SneakyThrows
    public static boolean readBoolean(DataInputStream _buf)
    {
        return _buf.readByte() != 0;
    }

    /* ----- BYTES ----- */

    @SneakyThrows
    public static void writeBytes(ByteBuf _buf, byte[] _b)
    {
        writeInt(_buf, _b.length);
        _buf.writeBytes(_b);
    }

    @SneakyThrows
    public static int encodeBytes(ByteBuf _buf, int _index, byte[] _b)
    {
        int _len = encodeInt(_buf, _index, _b.length);
        _buf.setBytes(_index+_len, _b);
        return _len + _b.length;
    }

    @SneakyThrows
    public static void writeBytes(DataOutputStream _buf, byte[] _b)
    {
        writeInt(_buf, _b.length);
        _buf.write(_b);
        _buf.flush();
    }

    @SneakyThrows
    public static void writeVarBytes(ByteBuf _buf, byte[] _b)
    {
        writeVarUInt128(_buf, _b.length);
        _buf.writeBytes(_b);
    }

    @SneakyThrows
    public static int encodeVarBytes(ByteBuf _buf, int _index, byte[] _b)
    {
        int _len = encodeVarUInt128(_buf, _index, _b.length);
        _buf.setBytes(_index+_len, _b);
        return _len + _b.length;
    }

    @SneakyThrows
    public static void writeVarBytes(DataOutputStream _buf, byte[] _b)
    {
        writeVarUInt128(_buf, _b.length);
        _buf.write(_b);
        _buf.flush();
    }

    @SneakyThrows
    public static byte[] readBytes(ByteBuf _buf)
    {
        int _len = readInt(_buf);
        byte[] _ret = new byte[_len];
        _buf.readBytes(_ret);
        return _ret;
    }

    @SneakyThrows
    public static byte[] readBytes(DataInputStream _buf)
    {
        int _len = readInt(_buf);
        byte[] _ret = new byte[_len];
        _buf.read(_ret);
        return _ret;
    }

    @SneakyThrows
    public static byte[] readVarBytes(ByteBuf _buf)
    {
        int _len = readVarUInt128(_buf);
        byte[] _ret = new byte[_len];
        _buf.readBytes(_ret);
        return _ret;
    }

    @SneakyThrows
    public static byte[] readVarBytes(DataInputStream _buf)
    {
        int _len = readVarUInt128(_buf);
        byte[] _ret = new byte[_len];
        _buf.read(_ret);
        return _ret;
    }

    /* ----- XUID ----- */

    public static void writeXUID(DataOutputStream _buf, XUID _id)
    {
        _id.writeTo(_buf);
    }

    public static void writeXUID(ByteBuf _buf, XUID _id)
    {
        _id.writeTo(_buf);
    }

    public static int encodeXUID(ByteBuf _buf, int _index, XUID _id)
    {
        return _id.encodeTo(_buf, _index);
    }

    public static XUID readXUID(DataInputStream _buf)
    {
        return XUID.from(_buf);
    }

    public static XUID readXUID(ByteBuf _buf)
    {
        return XUID.from(_buf);
    }

    public static XUID decodeXUID(ByteBuf _buf, int _index)
    {
        return XUID.from(_buf, _index);
    }
}
