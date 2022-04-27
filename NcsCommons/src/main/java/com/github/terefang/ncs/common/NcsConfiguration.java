package com.github.terefang.ncs.common;

import lombok.Data;
import lombok.SneakyThrows;

import javax.net.ssl.SSLEngine;
import java.net.InetAddress;

@Data
public class NcsConfiguration
{
    InetAddress endpointAddress;
    int endpointPort;
    int timeout;
    NcsPacketFactory packetFactory;
    NcsPacketListener packetListener;
    NcsStateListener stateListener;
    int maxFrameLength = 65536;

    SSLEngine tlsEngine;

    @SneakyThrows
    public void setEndpointAddress(String _s)
    {
        this.endpointAddress = InetAddress.getByName(_s);
    }
}
