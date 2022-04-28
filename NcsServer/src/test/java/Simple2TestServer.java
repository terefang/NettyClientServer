import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.packet.NcsPacket;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacketFactory;
import com.github.terefang.ncs.server.NcsServerHelper;
import com.github.terefang.ncs.server.NcsServerService;

public class Simple2TestServer implements NcsPacketListener, NcsStateListener
{
    /**
     * create a simple test server
     * @param args
     */
    public static void main(String[] args) {

        // basic acllback handler
        Simple2TestServer _main = new Simple2TestServer();

        // configure simple server
        NcsServerService _svc = NcsServerHelper.createServer(56789, new SimpleBytesNcsPacketFactory(), _main, _main);

        //_svc.setSharedSecret(null);
        _svc.setSharedSecret("07cwI&Y4gLXtJrQdfYWcKey!cseY9jB0Q*bveiT$zi6LX7%xMuGm!hzW%rQj%8Wf");

        // use optimized linux epoll transport
        _svc.setUseEpoll(true);

        _svc.startNow();
    }

    /**
     * called with received packet
     * @param _connection       the connection/channel the packet arrived on
     * @param _packet           the packet
     */
    @Override
    public void onPacket(NcsConnection _connection, NcsPacket _packet)
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

    static class SimpleTestServerHandler
    {
        public void onPacket(NcsConnection _connection, NcsPacket _packet)
        {
            System.err.println("PACKET "+_connection.getPeer().asString());

            // packet is decoded via SimpleBytesNcsPacketFactory
            // so we cast to SimpleBytesNcsPacket
            SimpleBytesNcsPacket _pkt = _packet.castTo(SimpleBytesNcsPacket.class);
            _pkt.startDecoding();
            int opCode = _pkt.decodeInt();
            _pkt.finishDecoding();
        }

        public void onConnect(NcsConnection _connection)
        {
            System.err.println("CONNECT "+_connection.getPeer().asString());
        }

        public void onDisconnect(NcsConnection _connection)
        {
            System.err.println("DISCONNECT "+_connection.getPeer().asString());
        }

        public void onError(NcsConnection _connection, Throwable _cause) {
            System.err.println("ERROR "+_connection.getPeer().asString());
            _cause.printStackTrace(System.err);
        }
    }

    /** test for commandline -- send 3 pkt with size=1 -- only valid for max-frame<65535

        echo -e "\x00\x01\x01\x00\x00\x00\x01\x01" | nc -w 3 127.0.0.1 56789 | hexdump -C

            00000000  00 0d 00 00 00 04 48 45  4c 4f 00 00 ff ff 01 00  |......HELO......|
            00000010  05 00 03 41 43 4b 00 05  00 03 41 43 4b 00 05 00  |...ACK....ACK...|
            00000020  03 41 43 4b                                       |.ACK|
     */
}
