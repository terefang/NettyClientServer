package com.github.terefang.ncs.client;

import com.github.terefang.ncs.common.packet.AbstractNcsPacket;
import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacketFactory;

public class NcsClientHelper
{
    public static final int DEFAULT_MAX_FRAME_LENGTH = 8192;

    /**
     * creates a netty client
     * @param _server        the servers address to connect to
     * @param _port         the port to bind to
     * @param _maxFrameLength   maximum protocol frame size
     * @param _factory      the packet encoder/decoder factory to use
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsClientService createClient(String _server, int _port, int _maxFrameLength, NcsPacketFactory<AbstractNcsPacket> _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        NcsClientConfiguration _config = NcsClientConfiguration.create();
        // ser to connect to
        _config.setEndpointAddress(_server);
        _config.setEndpointPort(_port);

        // protocol frame length
        _config.setMaxFrameLength(_maxFrameLength);

        // reasonable defaults
        _config.setTimeout(3000);

        // factories and listeners
        _config.setPacketFactory(_factory);
        _config.setPacketListener(_plistener);
        _config.setStateListener(_slistenner);

        // instantiate
        return NcsClientService.build(_config);
    }

    /**
     * creates a netty client, with SimpleBytesNcsPacketFactory
     * @param _server        the servers address to connect to
     * @param _port         the port to bind to
     * @param _maxFrameLength   maximum protocol frame size
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsClientService createSimpleClient(String _server, int _port, int _maxFrameLength, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createClient(_server, _port, _maxFrameLength, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    /**
     * creates a netty client, with DEFAULT_MAX_FRAME_LENGTH and SimpleBytesNcsPacketFactory
     * @param _server        the servers address to connect to
     * @param _port         the port to bind to
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsClientService createSimpleClient(String _server, int _port, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createClient(_server, _port, DEFAULT_MAX_FRAME_LENGTH, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    /* ----- UDP ----- */

    public static final NcsClientService createSimpleUdpClient(String _server, int _port, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createUdpClient(_server, _port, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    public static final NcsClientService createUdpClient(String _server, int _port, NcsPacketFactory<AbstractNcsPacket> _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        NcsClientConfiguration _config = NcsClientConfiguration.create();
        // ser to connect to
        _config.setEndpointAddress(_server);
        _config.setEndpointPort(_port);

        // reasonable defaults
        _config.setTimeout(3000);

        // factories and listeners
        _config.setPacketFactory(_factory);
        _config.setPacketListener(_plistener);
        _config.setStateListener(_slistenner);

        _config.setUseUdp(true);

        // instantiate
        return NcsClientService.build(_config);
    }

}
