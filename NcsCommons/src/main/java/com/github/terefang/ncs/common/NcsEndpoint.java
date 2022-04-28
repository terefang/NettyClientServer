package com.github.terefang.ncs.common;

import lombok.Data;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * representation of an endpoint usually an address plus port
 */
@Data
public class NcsEndpoint
{
    InetAddress address;
    int port = -1;

    /**
     * create an undefined endpoint
     * @return      the endpoint
     */
    @SneakyThrows
    public static NcsEndpoint create()
    {
        return new NcsEndpoint();
    }

    /**
     * create an endpoint from parameters
     * @param address   the endpoints address
     * @param port      the endpoints port
     * @return  the endpoint
     */
    @SneakyThrows
    public static NcsEndpoint from(InetAddress address, int port)
    {
        NcsEndpoint _ep = new NcsEndpoint();
        _ep.address = address;
        _ep.port = port;
        return _ep;
    }

    /**
     * create an endpoint from parameters
     * @param address   the endpoints socket-address
     * @return  the endpoint
     */
    @SneakyThrows
    public static NcsEndpoint from(InetSocketAddress address)
    {
        NcsEndpoint _ep = new NcsEndpoint();
        _ep.address = address.getAddress();
        _ep.port = address.getPort();
        return _ep;
    }

    /**
     * create an endpoint from parameters
     * @param address   the endpoints address
     * @param port      the endpoints port
     * @return  the endpoint
     */
    @SneakyThrows
    public static NcsEndpoint from(String address, int port)
    {
        NcsEndpoint _ep = new NcsEndpoint();
        _ep.address = InetAddress.getByName(address);
        _ep.port = port;
        return _ep;
    }

    /**
     * sets the endpoint from parameters
     * @param address   the endpoints address
     * @param port      the endpoints port
     */
    @SneakyThrows
    public void set(InetAddress address, int port)
    {
        this.address = address;
        this.port = port;
    }

    /**
     * sets the endpoint from parameters
     * @param address   the endpoints socket-address
     */
    @SneakyThrows
    public void set(InetSocketAddress address)
    {
        this.address = address.getAddress();
        this.port = address.getPort();
    }

    /**
     * sets the endpoint from parameters
     * @param address   the endpoints address
     * @param port      the endpoints port
     */
    @SneakyThrows
    public void set(String address, int port)
    {
        this.address = InetAddress.getByName(address);
        this.port = port;
    }

    /**
     * get the endpoints address part as string
     * @return the address
     */
    public String getAddressString()
    {
        if(this.address==null)
            return "null";

        return this.address.getHostAddress();
    }

    /**
     * get the endpoint represented as a string
     * @return the endpoint representation
     */
    public String asString()
    {
        return getAddressString()+":"+port;
    }
}
