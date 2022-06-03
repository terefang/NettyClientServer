package com.github.terefang.ncs.common;

import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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
    public static void writeVarString(ByteBuf _buf, String _s) {
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

}
