package com.github.terefang.ncs.common.cbor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import com.github.terefang.ncs.common.packet.AbstractOpcodeNcsPacket;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

import java.io.*;

public class OpcodedCborPacket<T> extends AbstractOpcodeNcsPacket
{
    static ObjectMapper _mapper = new CBORMapper();

    Class<T> _typeRef;

    @SneakyThrows
    public OpcodedCborPacket(Class<T> _clazz) {
        _typeRef = _clazz;
    }

    T _object;

    public T getObject()
    {
        return _object;
    }

    public void setObject(T _object)
    {
        this._object = _object;
    }

    @SneakyThrows
    public void parse(final ByteBuf _buf)
    {
        setOpcode(_buf.readInt());
        byte[] _bytes = new byte[_buf.readableBytes()];
        _buf.readBytes(_bytes);
        T _val = _mapper.readValue(_bytes, _typeRef);
        this.setObject(_val);
    }

    @Override
    @SneakyThrows
    public byte[] serialize() {
        ByteArrayOutputStream _baos = new ByteArrayOutputStream();
        DataOutputStream _dos = new DataOutputStream(_baos);
        _dos.writeInt(getOpcode());
        _mapper.writeValue((OutputStream) _dos,this.getObject());
        _dos.flush();
        return _baos.toByteArray();
    }


}
