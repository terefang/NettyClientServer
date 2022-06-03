package com.github.terefang.ncs.server.tcp;

import com.github.terefang.ncs.server.NcsServerConfiguration;

public class NcsTcpServerConfiguration extends NcsServerConfiguration
{
    public static NcsTcpServerConfiguration create()
    {
        return new NcsTcpServerConfiguration();
    }

    @Override
    public boolean isUseUdp() {
        return false;
    }
}
