package com.github.terefang.ncs.common;

import com.github.terefang.ncs.common.packet.NcsPacket;

public interface NcsStateListener {
    void onConnect(NcsConnection _connection);
    void onDisconnect(NcsConnection _connection);
    void onError(NcsConnection _connection, Throwable _cause);
    /*
    void onNeedAuthorization(NcsConnection _connection);
    void onNeedAuthorization(NcsConnection _connection, NcsPacket _pkt);
    void onNeedLogin(NcsConnection _connection);
    void onNeedLogin(NcsConnection _connection, NcsPacket _pkt);
     */
}
