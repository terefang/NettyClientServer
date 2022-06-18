package com.github.terefang.ncs.lib.packet.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.terefang.ncs.common.packet.AbstractNcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacket;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

public class KryoPacket extends AbstractNcsPacket implements NcsPacket
{
    byte[] payload;
    /**
     * create a packet
     * @return the packet
     */
    public static KryoPacket create()
    {
        KryoPacket _pkt = new KryoPacket();
        return _pkt;
    }

    /**
     * create a packet from an array of bytes
     * @param _buf  byte array
     * @return the packet
     */
    public static KryoPacket from(byte[] _buf)
    {
        KryoPacket _pkt = KryoPacket.create();
        _pkt.payload = _buf;
        return _pkt;
    }

    /**
     * create a packet from a buffer of bytes
     * @param _buf  byte buffer
     * @return the packet
     */
    public static KryoPacket from(ByteBuf _buf)
    {
        KryoPacket _pkt = KryoPacket.create();
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
     * retrieve the payload object via kryo object-mapping
     * @param _kryo the Kryo object-mapper
     * @param _clazz the target class of the object to be retrieved
     * @return the payload object
     */
    public <T> T getPayload(Kryo _kryo, Class<T> _clazz)
    {
        return _kryo.readObject(new Input(this.payload), _clazz);
    }

    /**
     * retrieve the payload object via kryo object-mapping
     * @param _kryo the Kryo object-mapper
     * @return the payload object
     */
    public Object getPayload(Kryo _kryo)
    {
        return _kryo.readClassAndObject(new Input(this.payload));
    }

    /**
     * set the payload object via kryo object-mapping
     * @param _kryo the Kryo object-mapper
     * @param _object the payload object to be serialized
     */
    public void setPayload(Kryo _kryo, Object _object)
    {
        Output _out = new Output(1024, -1);
        _kryo.writeObject(_out, _object);
        _out.flush();
        this.payload = _out.toBytes();
    }
}
