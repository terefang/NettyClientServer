package com.github.terefang.ncs.common;

import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;

public interface NcsStateListener
{
    /**
     * called on a new client connection established
     * @param _connection       the new connection/channel
     */
    void onConnect(NcsConnection _connection);

    /**
     * called on client disconnection
     * @param _connection       the new connection/channel
     */
    void onDisconnect(NcsConnection _connection);

    /**
     * called on uncaught error on the connection/channel
     * @param _connection       the new connection/channel
     * @param _cause            the cause
     */
    void onError(NcsConnection _connection, Throwable _cause);

}