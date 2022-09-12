package com.github.terefang.ncs.common;

import com.github.terefang.ncs.common.packet.NcsPacket;

public interface NcsKeepAliveListener
{
    void onKeepAlivePacket(NcsConnection _connection, NcsPacket _packet);
}