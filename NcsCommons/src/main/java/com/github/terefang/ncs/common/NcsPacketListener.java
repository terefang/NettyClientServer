package com.github.terefang.ncs.common;

public interface NcsPacketListener {
    void onPacket(NcsConnection _connection, NcsPacket _packet);
}
