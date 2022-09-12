package com.github.terefang.ncs.server;

import com.github.jgonian.ipmath.Ipv4;
import com.github.jgonian.ipmath.Ipv4Range;
import com.github.terefang.ncs.common.NcsConfiguration;
import com.github.terefang.ncs.common.NcsHelper;
import com.github.terefang.ncs.common.security.tls.NcsClientCertificateVerifier;
import lombok.Data;
import lombok.SneakyThrows;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.net.InetAddress;
import java.util.TreeSet;

@Data
public class NcsServerConfiguration extends NcsConfiguration
{

    boolean handleDiscovery = false;
    String discoveryConnectUrl = null;
    String discoveryConnectName = null;

    int clientKeepAliveTimeout = -1;
    int clientKeepAliveCounterMax = -1;
    boolean clientKeepAliveTcpAutoDisconnect = false;
    boolean clientKeepAliveUdpAutoDisconnect = false;

    public static NcsServerConfiguration create()
    {
        NcsServerConfiguration ret = new NcsServerConfiguration();
        ret.setClientMode(false);
        return ret;
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
            this.setSslContext(NcsHelper.createSslContext(this, (this.fqdn==null ? InetAddress.getLocalHost().getCanonicalHostName() : this.fqdn), this.clientCertificateVerifier));
        }
        SSLParameters _param = NcsHelper.createServerSslParameter(this);
        SSLEngine _engine = NcsHelper.createServerSslEngine(this, getSslContext(), _param);
        return _engine;
    }

    NcsClientCertificateVerifier clientCertificateVerifier;

    TreeSet<Ipv4> bannedAddresses = new TreeSet<>();
    TreeSet<Ipv4Range> bannedNetworks = new TreeSet<>();

    public void banAddress(String _addr)
    {
        this.bannedAddresses.add(Ipv4.of(_addr));
    }

    public void unbanAddress(String _addr)
    {
        this.bannedAddresses.remove(Ipv4.of(_addr));
    }

    public void banNetwork(String _addr)
    {
        this.bannedNetworks.add(Ipv4Range.parse(_addr));
    }

    public void unbanNetwork(String _addr)
    {
        this.bannedNetworks.remove(Ipv4Range.parse(_addr));
    }


}
