package com.github.terefang.ncs.common.packet;

import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.packet.NcsPacket;

public interface NcsPacketListener {
    void onPacket(NcsConnection _connection, NcsPacket _packet);
}
