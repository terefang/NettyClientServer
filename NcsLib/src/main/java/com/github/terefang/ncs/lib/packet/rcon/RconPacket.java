package com.github.terefang.ncs.lib.packet.rcon;

import com.github.terefang.ncs.common.packet.AbstractNcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class RconPacket extends AbstractNcsPacket implements NcsPacket
{
    public static final int TYPE_SERVERDATA_AUTH = 3;
    public static final int TYPE_SERVERDATA_AUTH_RESPONSE = 2;
/* This packet is a notification of the connection's current auth status.
   If the server returns a packet with the same request ID, auth was successful.
   If you get a request ID of -1 (0xFF FF FF FF, auth failed (wrong password).
*/
    public static final int TYPE_SERVERDATA_EXECCOMMAND = 2;
    public static final int TYPE_SERVERDATA_MULTI_RESPONSE_VALUE = 1;
    public static final int TYPE_SERVERDATA_RESPONSE_VALUE = 0;

    /**
     * create a packet
     * @return the packet
     */
    public static RconPacket create()
    {
        RconPacket _pkt = new RconPacket();
        return _pkt;
    }

    /**
     * create a packet from an array of bytes
     * @param _buf  byte array
     * @return the packet
     */
    public static RconPacket from(byte[] _buf)
    {
        RconPacket _pkt = RconPacket.create();
        _pkt.populate(_buf);
        return _pkt;
    }

    @SneakyThrows
    private void populate(byte[] _buf)
    {
        int _len = _buf.length-9;
        ByteArrayInputStream _bain = new ByteArrayInputStream(_buf);
        LittleEndianDataInputStream _din = new LittleEndianDataInputStream(_bain);

        this.requestId = _din.readInt();
        this.requestType = _din.readInt();
        byte [] _str = new byte[_len];
        _din.read(_str);
        this.payload = new String(_str, StandardCharsets.ISO_8859_1);
    }

    /**
     * create a packet from a buffer of bytes
     * @param _buf  byte buffer
     * @return the packet
     */
    public static RconPacket from(ByteBuf _buf)
    {
        RconPacket _pkt = RconPacket.create();
        byte[] _tbuf = new byte[_buf.readableBytes()];
        _buf.readBytes(_tbuf);
        _pkt.populate(_tbuf);
        return _pkt;
    }

    @Override
    @SneakyThrows
    public byte[] serialize()
    {
        ByteArrayOutputStream _baos = new ByteArrayOutputStream();
        LittleEndianDataOutputStream _dos = new LittleEndianDataOutputStream(_baos);
        _dos.writeInt(this.requestId);
        _dos.writeInt(this.requestType);
        _dos.write(this.payload.getBytes(StandardCharsets.ISO_8859_1));
        _dos.writeByte(0);
        _dos.writeByte(0);
        _dos.flush();
        return _baos.toByteArray();
    }

    int requestId;
    int requestType;
    String payload;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
