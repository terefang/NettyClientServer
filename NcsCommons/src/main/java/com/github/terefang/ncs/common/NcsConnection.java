package com.github.terefang.ncs.common;

public interface NcsConnection {
    void setContext(Object _context);
    <T> T getContext(Class<T> _clazz);
    NcsEndpoint getPeer();

    void send(NcsPacket _pkt);
    void sendAndFlush(NcsPacket _pkt);
}
