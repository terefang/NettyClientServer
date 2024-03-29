package local.ncs.udp;

import com.github.terefang.ncs.common.*;
import com.github.terefang.ncs.common.packet.NcsKeepAlivePacket;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;
import com.github.terefang.ncs.server.NcsServerHelper;
import com.github.terefang.ncs.server.NcsServerService;
import local.ncs.SimpleTestServerHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SimpleTestUdpKAServer implements NcsPacketListener<SimpleBytesNcsPacket>, NcsStateListener, NcsKeepAliveFailListener
{
    SimpleTestServerHandler _handler = new SimpleTestServerHandler();
    List<InetSocketAddress> _peers = new Vector<>();
    /**
     * create a simple test server
     * @param args
     */
    public static void main(String[] args) {

        // basic acllback handler
        SimpleTestUdpKAServer _main = new SimpleTestUdpKAServer();

        // configure simple server
        NcsServerService _svc = NcsServerHelper.createSimpleUdpServer(56789, _main, _main);

        //_svc.getConfiguration().setSharedSecret("07cwI&Y4gLXtJrQdfYWcKey!cseY9jB0Q*bveiT$zi6LX7%xMuGm!hzW%rQj%8Wf");
        //_svc.getConfiguration().setHandleDiscovery(true);
        // use optimized linux epoll transport
        _svc.getConfiguration().setUseEpoll(true);
        _svc.getConfiguration().setClientKeepAliveTimeout(1000);
        _svc.getConfiguration().setClientKeepAliveCounterMax(3);
        _svc.getConfiguration().setClientKeepAliveUdpAutoDisconnect(true);

        _svc.startNow();

    }

    /**
     * called with received packet
     * @param _connection       the connection/channel the packet arrived on
     * @param _packet           the packet
     */
    @Override
    public void onPacket(NcsConnection _connection, SimpleBytesNcsPacket _packet)
    {
        // get custom/user context and call some method
        if(_connection.isUdp())
        {
            _handler.onPacket(_connection, _packet);
            if(!_peers.contains(_packet.getAddress()))
            {
                _peers.add(_packet.getAddress());
            }
        }
        else
        {
            _connection.getContext(SimpleTestServerHandler.class).onPacket(_connection, _packet);
        }
        // assemble and send ack packet
        SimpleBytesNcsPacket _pkt = SimpleBytesNcsPacket.create();
        _pkt.startEncoding();
        _pkt.encodeString("ACK");
        _pkt.finishEncoding();
        _pkt.setAddress(_packet.getAddress());
        _connection.sendAndFlush(_pkt);
    }

    /**
     * called on a new client connection established
     * @param _connection       the new connection/channel
     */
    @Override
    public void onConnect(NcsConnection _connection)
    {
        if(_connection.isUdp()) return;
        // setting a custom/user context object for later retrival
        _connection.setContext(new SimpleTestServerHandler());

        // assemble and send initial packet on connect
        SimpleBytesNcsPacket _pkt = SimpleBytesNcsPacket.create();
        _pkt.startEncoding();
        _pkt.encodeBytes("HELO".getBytes());
        _pkt.encodeInt(65535);
        _pkt.encodeBoolean(true);
        _pkt.finishEncoding();
        _connection.sendAndFlush(_pkt);

        // get custom/user context and call some method
        _connection.getContext(SimpleTestServerHandler.class).onConnect(_connection);
    }

    /**
     * called on client disconnection
     * @param _connection       the new connection/channel
     */
    @Override
    public void onDisconnect(NcsConnection _connection)
    {
        if(_connection.isUdp()) return;
        // get custom/user context and call some method
        _connection.getContext(SimpleTestServerHandler.class).onDisconnect(_connection);
    }

    /**
     * called on uncaught error on the connection/channel
     * @param _connection       the new connection/channel
     * @param _cause            the cause
     */
    @Override
    public void onError(NcsConnection _connection, Throwable _cause)
    {
        if(_connection.isUdp())
        {
            log.warn(_cause.getMessage(), _cause);
            return;
        }
        // get custom/user context and call some method
        _connection.getContext(SimpleTestServerHandler.class).onError(_connection, _cause);
    }

    @Override
    public void onKeepAliveFail(NcsConnection _connection, long _timeout, long _fails, NcsEndpoint _endpoint) {
        log.info("KEEP-ALIVE-FAIL: "+_endpoint.asString());
    }
}
