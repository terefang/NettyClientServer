package com.github.terefang.ncs.common;

import com.github.terefang.ncs.common.packet.NcsPacket;

public interface NcsKeepAliveFailListener
{
    void onKeepAliveFail(NcsConnection _connection, long _timeout, long _fails, NcsEndpoint _endpoint);
}