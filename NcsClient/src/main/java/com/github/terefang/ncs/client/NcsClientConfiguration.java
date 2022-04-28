package com.github.terefang.ncs.client;

import com.github.terefang.ncs.common.NcsConfiguration;

import javax.net.ssl.SSLEngine;

public class NcsClientConfiguration extends NcsConfiguration
{
    public static NcsClientConfiguration create() {
        return new NcsClientConfiguration();
    }

    public SSLEngine getTlsClientEngine()
    {
        if(getSslEngine()==null && this.isTlsEnabled())
        {

        }
        return getSslEngine();
    }

}
