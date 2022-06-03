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

    boolean useUdp = false;

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
    X509Certificate[] tlsChain;

    String[] tlsCiphers = {
            "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
            "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
            "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
            "TLS_DHE_RSA_WITH_AES_128_CBC_SHA"
    };

    String[] tlsProtocols = { "TLSv1.2" };

    /**
     * usePskOBF ... use given shared secret to obfuscate packet
     */
    boolean usePskOBF = false;

    /**
     * usePskMac ... use given shared secret to verify packet
     */
    boolean usePskMac = false;

    /**
     * pskSharedSecret ... the shared secret to use
     */
    String pskSharedSecret = "530d07e5-cba8-476b-aed8-0114b8e550d6";

    /**
     * compressionMethod ... the compression method to use,
     *     NONE,
     *     BZIP2,
     *     ZLIB,
     *     SNAPPY,
     *     LZF,
     *     LZ4,
     *     FASTLZ
     */
    NcsCompressionMethod compressionMethod = NcsCompressionMethod.NONE;

    /**
     * compressionMaxLevel ... the compression level to use 0-9 (if applicable)
     */
    int compressionMaxLevel = 1;

    public NcsEndpoint getEndpoint()
    {
        return NcsEndpoint.from(this.endpointAddress, this.endpointPort);
    }

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
            this.setUsePskMac(false);
        }
        else
        {
            this.setUsePskOBF(true);
            this.setUsePskMac(true);
        }
    }

    public void setSharedSecret(String _s, boolean _obf, boolean _mac)
    {
        this.setPskSharedSecret(_s);
        this.setUsePskOBF(_obf);
        this.setUsePskMac(_mac);
    }
}
