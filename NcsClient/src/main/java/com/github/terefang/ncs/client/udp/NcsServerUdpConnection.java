package com.github.terefang.ncs.client.udp;

import com.github.terefang.ncs.client.NcsServerConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.udp.NcsUdpConnection;
import io.netty.channel.Channel;

public class NcsServerUdpConnection extends NcsUdpConnection implements NcsServerConnection
{
    public NcsServerUdpConnection()
    {
        super();
    }

    /**
     * creates a connection from parameters
     * @param _ch       the netty channel representing the connection
     * @return          the connection
     */
    public static NcsServerUdpConnection from(NcsPacketListener _pl, NcsStateListener _sl, Channel _ch)
    {
        NcsServerUdpConnection _nc = new NcsServerUdpConnection();
        _nc.setChannel(_ch);
        _nc.setPacketListener(_pl);
        _nc.setStateListener(_sl);
        return _nc;
    }

    @Override
    public void setContext(Object _context) {
        throw new UnsupportedOperationException("not implemented -- in NcsServerUdpConnection");
    }

    @Override
    public <T> T getContext(Class<T> _clazz) {
        throw new UnsupportedOperationException("not implemented -- in NcsServerUdpConnection");
    }

    @Override
    public NcsEndpoint getPeer() {
        throw new UnsupportedOperationException("not implemented -- in NcsServerUdpConnection");
    }

    @Override
    public boolean isUdp() {
        return true;
    }
}
