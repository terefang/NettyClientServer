package com.github.terefang.ncs.lib.packet.cbor;

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import com.github.terefang.ncs.common.packet.AbstractNcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacket;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

public class CborPacket extends AbstractNcsPacket implements NcsPacket
{
   byte[] payload;
    /**
     * create a packet
     * @return the packet
     */
    public static CborPacket create()
    {
        CborPacket _pkt = new CborPacket();
        return _pkt;
    }

    /**
     * create a packet from an array of bytes
     * @param _buf  byte array
     * @return the packet
     */
    public static CborPacket from(byte[] _buf)
    {
        CborPacket _pkt = CborPacket.create();
        _pkt.payload = _buf;
        return _pkt;
    }

    /**
     * create a packet from a buffer of bytes
     * @param _buf  byte buffer
     * @return the packet
     */
    public static CborPacket from(ByteBuf _buf)
    {
        CborPacket _pkt = CborPacket.create();
        _pkt.payload = new byte[_buf.readableBytes()];
        _buf.readBytes(_pkt.payload);
        return _pkt;
    }

    /**
     * create the packate representation as  bytes
     * @return the bytes
     */
    @Override
    @SneakyThrows
    public byte[] serialize()
    {
        return this.payload;
    }

    /**
     * retrieve the payload object via object-mapping
     * @param _cbor the object-mapper
     * @param _clazz the target class of the object to be retrieved
     * @return the payload object
     */
    @SneakyThrows
    public <T> T getPayload(CBORMapper _cbor, Class<T> _clazz)
    {
        return _cbor.readValue(this.payload, _clazz);
    }

    /**
     * set the payload object via object-mapping
     * @param _cbor the object-mapper
     * @param _object the payload object to be serialized
     */
    @SneakyThrows
    public void setPayload(CBORMapper _cbor, Object _object)
    {
        this.payload = _cbor.writeValueAsBytes(_object);
    }

}
