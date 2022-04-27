package com.github.terefang.ncs.server;

import com.github.terefang.ncs.common.NcsPacketFactory;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.SimpleBytesNcsPacketFactory;

public class NcsServerHelper
{
    public static final NcsServerService createSimpleServer(String _local, int _port, int _maxFrameLength, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(_local, _port, _maxFrameLength, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    public static final NcsServerService createSimpleServer(int _port, int _maxFrameLength, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(null, _port, _maxFrameLength, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    public static final NcsServerService createServer(int _port, int _maxFrameLength, NcsPacketFactory _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(null, _port, _maxFrameLength, _factory, _plistener, _slistenner);
    }

    public static final NcsServerService createServer(String _local, int _port, int _maxFrameLength, NcsPacketFactory _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        NcsServerConfiguration _config = NcsServerConfiguration.create();
        _config.setEndpointAddress(_local);
        _config.setEndpointPort(_port);

        _config.setMaxFrameLength(_maxFrameLength);
        _config.setTimeout(3);
        _config.setBacklog(100);
        _config.setWorkers(10);

        _config.setPacketFactory(_factory);
        _config.setPacketListener(_plistener);
        _config.setStateListener(_slistenner);

        return NcsServerService.build(_config);
    }
}
