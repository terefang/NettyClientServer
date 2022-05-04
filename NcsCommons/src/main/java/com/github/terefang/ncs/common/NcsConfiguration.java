package com.github.terefang.ncs.common;

import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import lombok.Data;
import lombok.SneakyThrows;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.net.InetAddress;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Data
public class NcsConfiguration
{
    InetAddress endpointAddress;
    int endpointPort;
    NcsPacketFactory packetFactory;
    NcsPacketListener packetListener;
    NcsStateListener stateListener;

    int timeout = 1<<8;
    int maxFrameLength = 1<<13;

    int recvBufferSize = 1<<20;
    int sendBufferSize = 1<<20;

    boolean tcpNoDelay = true;
    boolean keepAlive = true;
    int linger = 0;

    SSLEngine sslEngine;
    SSLContext sslContext;
    boolean tlsEnabled = false;
    boolean tlsVerifyPeer = false;
    String tlsFingerprint;

    PrivateKey tlsKey;
    X509Certificate tlsCertificate;

    String[] tlsCiphers = {
            "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
            "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
            "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
            "TLS_DHE_RSA_WITH_AES_128_CBC_SHA"
    };

    String[] tlsProtocols = { "TLSv1.2" };

    boolean usePskOBF = false;
    boolean usePskMac = false;

    String pskSharedSecret = "530d07e5-cba8-476b-aed8-0114b8e550d6";


    NcsCompressionMethod compressionMethod = NcsCompressionMethod.NONE;
    int compressionMaxLevel = 1;

    @SneakyThrows
    public void setEndpointAddress(String _s)
    {
        this.endpointAddress = InetAddress.getByName(_s);
    }

    @SneakyThrows
    public void setEndpoint(String _s, int _p)
    {
        this.endpointAddress = InetAddress.getByName(_s);
        this.endpointPort = _p;
    }

    public void setSharedSecret(String _s)
    {
        this.setPskSharedSecret(_s);
        if(_s==null)
        {
            this.setUsePskOBF(false);
        }
        else
        {
            this.setUsePskOBF(true);
        }
    }
}
