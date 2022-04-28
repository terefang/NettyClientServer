package com.github.terefang.ncs.common;

import lombok.Data;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@Data
public class NcsEndpoint
{
    InetAddress address;
    int port = -1;

    @SneakyThrows
    public static NcsEndpoint create()
    {
        return new NcsEndpoint();
    }

    @SneakyThrows
    public static NcsEndpoint from(InetAddress address, int port)
    {
        NcsEndpoint _ep = new NcsEndpoint();
        _ep.address = address;
        _ep.port = port;
        return _ep;
    }

    @SneakyThrows
    public static NcsEndpoint from(InetSocketAddress address)
    {
        NcsEndpoint _ep = new NcsEndpoint();
        _ep.address = address.getAddress();
        _ep.port = address.getPort();
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

    @SneakyThrows
    public void set(InetAddress address, int port)
    {
        this.address = address;
        this.port = port;
    }

    @SneakyThrows
    public void set(InetSocketAddress address)
    {
        this.address = address.getAddress();
        this.port = address.getPort();
    }

    @SneakyThrows
    public void set(String address, int port)
    {
        this.address = InetAddress.getByName(address);
        this.port = port;
    }

    public String getAddressString()
    {
        if(this.address==null)
            return "null";

        return this.address.getHostAddress();
    }

    public String asString()
    {
        return getAddressString()+":"+port;
    }
}
