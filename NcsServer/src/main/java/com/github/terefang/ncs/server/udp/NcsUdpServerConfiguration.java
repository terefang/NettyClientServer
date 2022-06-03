package com.github.terefang.ncs.server.udp;

import com.github.terefang.ncs.server.NcsServerConfiguration;

public class NcsUdpServerConfiguration extends NcsServerConfiguration
{
    public static NcsUdpServerConfiguration create()
    {
        return new NcsUdpServerConfiguration();
    }

    @Override
    public boolean isUseUdp() {
        return true;
    }
}
