package com.github.terefang.ncs.common;

import com.github.terefang.ncs.common.packet.NcsPacket;

public interface NcsConnection {
    void setContext(Object _context);
    <T> T getContext(Class<T> _clazz);
    NcsEndpoint getPeer();

    void send(NcsPacket _pkt);
    void sendAndFlush(NcsPacket _pkt);


}
