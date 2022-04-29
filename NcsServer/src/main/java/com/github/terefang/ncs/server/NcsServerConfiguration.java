package com.github.terefang.ncs.server;

import com.github.terefang.ncs.common.NcsConfiguration;
import com.github.terefang.ncs.common.security.NcsSslTlsHelper;
import lombok.Data;
import lombok.SneakyThrows;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.net.InetAddress;

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

    String fqdn;

    @SneakyThrows
    public SSLEngine getTlsServerEngine()
    {
        if(!this.isTlsEnabled()) return null;

        System.setProperty("java.security.properties", "java.security.crypto.policy-unlimited");

        if(getSslContext()==null)
        {
            this.setSslContext(NcsSslTlsHelper.createSslContext(this, (this.fqdn==null ? InetAddress.getLocalHost().getCanonicalHostName() : this.fqdn)));
        }
        SSLParameters _param = NcsSslTlsHelper.createServerSslParameter(this);
        SSLEngine _engine = NcsSslTlsHelper.createServerSslEngine(this, getSslContext(), _param);
        return _engine;
    }

}
