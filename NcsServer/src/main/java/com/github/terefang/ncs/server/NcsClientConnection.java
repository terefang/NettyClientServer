package com.github.terefang.ncs.server;

import com.github.terefang.ncs.common.NcsConnection;

public interface NcsClientConnection extends NcsConnection {
    boolean isUdp();
    long getCurrentRTT();
    long getHistoricRTT();
}
