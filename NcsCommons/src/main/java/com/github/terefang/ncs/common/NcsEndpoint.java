package com.github.terefang.ncs.common;

import lombok.Data;
import lombok.SneakyThrows;

import java.net.InetAddress;

@Data
public class NcsEndpoint
{
    InetAddress address;
    int port;

    @SneakyThrows
    public static NcsEndpoint from(InetAddress address, int port)
    {
        NcsEndpoint _ep = new NcsEndpoint();
        _ep.address = address;
        _ep.port = port;
        return _ep;
    }

    @SneakyThrows
    public static NcsEndpoint from(String address, int port)
    {
        NcsEndpoint _ep = new NcsEndpoint();
        _ep.address = InetAddress.getByName(address);
        _ep.port = port;
        return _ep;
    }

    public String getAddressString()
    {
        if(this.address==null)
            return "null";

        return this.address.getHostAddress();
    }
}
