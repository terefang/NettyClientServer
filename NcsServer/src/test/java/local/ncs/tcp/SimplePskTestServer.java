package local.ncs.tcp;

import com.github.terefang.ncs.common.*;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;
import com.github.terefang.ncs.server.NcsServerHelper;
import com.github.terefang.ncs.server.NcsServerService;
import local.ncs.SimpleTestServerHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimplePskTestServer implements NcsPacketListener<SimpleBytesNcsPacket>, NcsStateListener, NcsKeepAliveFailListener
{
    /**
     * create a simple test server
     * @param args
     */
    public static void main(String[] args) {

        // basic acllback handler
        SimplePskTestServer _main = new SimplePskTestServer();

        // configure simple server
        NcsServerService _svc = NcsServerHelper.createSimpleServer(56789, _main, _main);

        _svc.getConfiguration().setSharedSecret("07cwI&Y4gLXtJrQdfYWcKey!cseY9jB0Q*bveiT$zi6LX7%xMuGm!hzW%rQj%8Wf");
        _svc.getConfiguration().setUsePskOBF(true);
        _svc.getConfiguration().setUsePskMac(true);


        // use optimized linux epoll transport
        _svc.getConfiguration().setUseEpoll(true);

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
        _connection.getContext(SimpleTestServerHandler.class).onPacket(_connection, _packet);

        // assemble and send ack packet
        SimpleBytesNcsPacket _pkt = SimpleBytesNcsPacket.create();
        _pkt.startEncoding();
        _pkt.encodeString("ACK");
        _pkt.finishEncoding();
        _connection.sendAndFlush(_pkt);
    }

    /**
     * called on a new client connection established
     * @param _connection       the new connection/channel
     */
    @Override
    public void onConnect(NcsConnection _connection)
    {
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
        // get custom/user context and call some method
        _connection.getContext(SimpleTestServerHandler.class).onError(_connection, _cause);
    }

    @Override
    public void onKeepAliveFail(NcsConnection _connection, long _timeout, long _fails, NcsEndpoint _endpoint)
    {
        System.err.println(String.format("K_A_FAIL %s , %d %d", _endpoint.asString(), _timeout, _fails));
    }
}
