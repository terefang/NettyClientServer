package com.github.terefang.ncs.client;

import com.github.terefang.ncs.common.NcsConfiguration;
import com.github.terefang.ncs.common.security.NcsSslTlsHelper;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

public class NcsClientConfiguration extends NcsConfiguration
{
    /**
     * creates a default client configuration
     * @return the configuration
     */
    public static NcsClientConfiguration create() {
        return new NcsClientConfiguration();
    }

    /**
     * returns a (possible cached) ssl engine based on the given configuration parameters
     * @return the ssl engine
     */
    public SSLEngine getTlsClientEngine()
    {
        if(!this.isTlsEnabled()) return null;

        System.setProperty("java.security.properties", "java.security.crypto.policy-unlimited");

        if(getSslContext()==null)
        {
            this.setSslContext(NcsSslTlsHelper.createSslContext(this));
        }
        SSLParameters _param = NcsSslTlsHelper.createClientSslParameter(this);
        SSLEngine _engine = NcsSslTlsHelper.createClientSslEngine(this, getSslContext(), _param);
        return _engine;
    }

}
