package com.github.terefang.ncs.server;

import com.github.terefang.ncs.common.NcsConfiguration;
import lombok.Data;

import javax.net.ssl.SSLEngine;

@Data
public class NcsServerConfiguration extends NcsConfiguration
{
    public static NcsServerConfiguration create()
    {
        return new NcsServerConfiguration();
    }

    int workers = Runtime.getRuntime().availableProcessors()*2+1;
    int backlog = 1<<10;

    boolean useEpoll = false;

    public SSLEngine getTlsServerEngine()
    {
        if(getSslEngine()==null && this.isTlsEnabled())
        {

        }
        return getSslEngine();
    }

}
