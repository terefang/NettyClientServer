package com.github.terefang.ncs.common;

import com.github.terefang.ncs.common.packet.NcsPacket;

public interface NcsPacketListener<T extends NcsPacket>
{
    /**
     * called with received packet
     * @param _connection       the connection/channel the packet arrived on
     * @param _packet           the packet
     */
    void onPacket(NcsConnection _connection, T _packet);
}
