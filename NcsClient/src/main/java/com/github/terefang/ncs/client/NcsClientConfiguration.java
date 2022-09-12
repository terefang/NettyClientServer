package com.github.terefang.ncs.client;

import com.github.terefang.ncs.common.NcsConfiguration;
import com.github.terefang.ncs.common.NcsHelper;
import lombok.SneakyThrows;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.net.InetAddress;

public class NcsClientConfiguration extends NcsConfiguration
{
    /**
     * creates a default client configuration
     * @return the configuration
     */
    public static NcsClientConfiguration create() {
        NcsClientConfiguration ret = new NcsClientConfiguration();
        ret.setClientMode(true);
        return ret;
    }

    /**
     * returns a (possible cached) ssl engine based on the given configuration parameters
     * @return the ssl engine
     */
    @SneakyThrows
    public SSLEngine getTlsClientEngine()
    {
        if(!this.isTlsEnabled()) return null;

        System.setProperty("java.security.properties", "java.security.crypto.policy-unlimited");

        if(getSslContext()==null)
        {
            this.setSslContext(NcsHelper.createSslContext(this, InetAddress.getLocalHost().getCanonicalHostName(), null));
        }
        SSLParameters _param = NcsHelper.createClientSslParameter(this);
        SSLEngine _engine = NcsHelper.createClientSslEngine(this, getSslContext(), _param);
        return _engine;
    }

}
