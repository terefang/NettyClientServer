package com.github.terefang.ncs.server;

import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacketFactory;

public class NcsServerHelper
{
    public static final int DEFAULT_MAX_FRAME_LENGTH = 8192;

    /**
     * creates a netty server, with DEFAULT_MAX_FRAME_LENGTH and SimpleBytesNcsPacketFactory
     * @param _local        the local address to bind to
     * @param _port         the port to bind to
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createSimpleServer(String _local, int _port, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(_local, _port, DEFAULT_MAX_FRAME_LENGTH, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    /**
     * creates a netty server, with SimpleBytesNcsPacketFactory
     * @param _local        the local address to bind to
     * @param _port         the port to bind to
     * @param _maxFrameLength   maximum protocol frame size
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createSimpleServer(String _local, int _port, int _maxFrameLength, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(_local, _port, _maxFrameLength, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    /**
     * creates a netty server, with SimpleBytesNcsPacketFactory
     * @param _port         the port to bind to
     * @param _maxFrameLength   maximum protocol frame size
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createSimpleServer(int _port, int _maxFrameLength, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(null, _port, _maxFrameLength, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    /**
     * creates a netty server, with DEFAULT_MAX_FRAME_LENGTH and SimpleBytesNcsPacketFactory
     * @param _port         the port to bind to
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createSimpleServer(int _port, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(null, _port, DEFAULT_MAX_FRAME_LENGTH, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    /**
     * creates a netty server
     * @param _port         the port to bind to
     * @param _maxFrameLength   maximum protocol frame size
     * @param _factory      the packet encoder/decoder factory to use
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createServer(int _port, int _maxFrameLength, NcsPacketFactory _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(null, _port, _maxFrameLength, _factory, _plistener, _slistenner);
    }

    /**
     * creates a netty server, with DEFAULT_MAX_FRAME_LENGTH
     * @param _port         the port to bind to
     * @param _factory      the packet encoder/decoder factory to use
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createServer(int _port, NcsPacketFactory _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(null, _port, DEFAULT_MAX_FRAME_LENGTH, _factory, _plistener, _slistenner);
    }

    /**
     * creates a netty server, with DEFAULT_MAX_FRAME_LENGTH
     * @param _local        the local address to bind to
     * @param _port         the port to bind to
     * @param _factory      the packet encoder/decoder factory to use
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createServer(String _local, int _port, NcsPacketFactory _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(_local, _port, DEFAULT_MAX_FRAME_LENGTH, _factory, _plistener, _slistenner);
    }

    /**
     * creates a netty server, with DEFAULT_MAX_FRAME_LENGTH and SimpleBytesNcsPacketFactory
     * @param _local        the local address to bind to
     * @param _port         the port to bind to
     * @param _maxFrameLength   maximum protocol frame size
     * @param _factory      the packet encoder/decoder factory to use
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createServer(String _local, int _port, int _maxFrameLength, NcsPacketFactory _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        NcsServerConfiguration _config = NcsServerConfiguration.create();
        // local address/port to bind to
        _config.setEndpointAddress(_local);
        _config.setEndpointPort(_port);

        // protocol frame length!
        _config.setMaxFrameLength(_maxFrameLength);

        // set reasonable defaults
        _config.setTimeout(3);
        _config.setBacklog(100);
        _config.setWorkers(10);

        // set factories and listeners
        _config.setPacketFactory(_factory);
        _config.setPacketListener(_plistener);
        _config.setStateListener(_slistenner);

        // instantiate
        return NcsServerService.build(_config);
    }
}
