package com.github.terefang.ncs.lib.packet.xuidoc;

import com.github.terefang.ncs.common.NcsCodecHelper;
import com.github.terefang.ncs.common.XUID;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;

public class OpcodePojoNcsPacketFactory implements NcsPacketFactory
{
    Map<XUID,OpcodePojoNcsPacketFactoryInterface> _reg = new HashMap<>();

    public void register(XUID _id,OpcodePojoNcsPacketFactoryInterface _m)
    {
        this._reg.put(_id, _m);
    }

    @Override
    public NcsPacket unpack(ByteBuf _buf) {
        XUID _id = NcsCodecHelper.decodeXUID(_buf,0);
        if(this._reg.containsKey(_id))
        {
            return this._reg.get(_id).unpack(_buf);
        }
        throw new IllegalArgumentException(_id.asString());
    }

    @Override
    public ByteBuf pack(NcsPacket _pkt, ByteBufAllocator _alloc) {
        byte[] _msg = ((OpcodePojoNcsPacketBase)_pkt).serialize();
        ByteBuf _buf = _alloc.buffer(_msg.length);
        _buf.writeBytes(_msg);
        return _buf;
    }

    @Override
    public NcsPacket create() {
        return null;
    }
}
