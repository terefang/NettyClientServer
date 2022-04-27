package com.github.terefang.ncs.client;

import com.github.terefang.ncs.common.NcsPacketFactory;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.SimpleBytesNcsPacketFactory;

public class NcsClientHelper
{
    public static final int DEFAULT_MAx_FRAME_LENGTH = 8192;
    public static final NcsClientService createClient(String _server, int _port, int _maxFrameLength, NcsPacketFactory _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        NcsClientConfiguration _config = NcsClientConfiguration.create();
        _config.setEndpointAddress(_server);
        _config.setEndpointPort(_port);

        _config.setMaxFrameLength(_maxFrameLength);
        _config.setTimeout(3);

        _config.setPacketFactory(_factory);
        _config.setPacketListener(_plistener);
        _config.setStateListener(_slistenner);

        return NcsClientService.build(_config);
    }

    public static final NcsClientService createSimpleClient(String _server, int _port, int _maxFrameLength, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createClient(_server, _port, _maxFrameLength, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    public static final NcsClientService createSimpleClient(String _server, int _port, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createClient(_server, _port, DEFAULT_MAx_FRAME_LENGTH, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }
}
