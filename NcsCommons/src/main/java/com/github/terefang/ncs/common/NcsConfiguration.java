package com.github.terefang.ncs.common;

import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.common.packet.NcsPacketListener;
import lombok.Data;
import lombok.SneakyThrows;

import javax.net.ssl.SSLEngine;
import java.net.InetAddress;

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
    boolean tlsEnabled = false;

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
}
