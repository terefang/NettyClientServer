package local.ncs.tcp;

import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;
import com.github.terefang.ncs.server.NcsServerHelper;
import com.github.terefang.ncs.server.NcsServerService;
import local.ncs.SimpleTestServerHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleTestServer implements NcsPacketListener<SimpleBytesNcsPacket>, NcsStateListener
{
    /**
     * create a simple test server
     * @param args
     */
    public static void main(String[] args) {

        // basic acllback handler
        SimpleTestServer _main = new SimpleTestServer();

        // configure simple server
        NcsServerService _svc = NcsServerHelper.createSimpleServer(56789, _main, _main);

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

    /** test for commandline -- send 3 pkt with size=1 -- only valid for max-frame<65535 -- psk=null

        echo -e "\x00\x01\x01\x00\x00\x00\x01\x01" | nc -w 3 127.0.0.1 56789 | hexdump -C

            00000000  00 0d 00 00 00 04 48 45  4c 4f 00 00 ff ff 01 00  |......HELO......|
            00000010  05 00 03 41 43 4b 00 05  00 03 41 43 4b 00 05 00  |...ACK....ACK...|
            00000020  03 41 43 4b                                       |.ACK|
     */
}
