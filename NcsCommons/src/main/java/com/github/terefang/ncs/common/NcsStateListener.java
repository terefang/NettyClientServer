package com.github.terefang.ncs.common;

public interface NcsStateListener {
    void onConnect(NcsConnection _connection);
    void onDisconnect(NcsConnection _connection);
    void onError(NcsConnection _connection, Throwable _cause);
}
