import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsPacket;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.impl.SimpleBytesNcsPacket;
import com.github.terefang.ncs.common.impl.SimpleBytesNcsPacketFactory;
import com.github.terefang.ncs.server.NcsServerConfiguration;
import com.github.terefang.ncs.server.NcsServerService;

public class SimpleTestServer implements NcsPacketListener, NcsStateListener
{
    public static void main(String[] args) {
        NcsServerConfiguration _config = NcsServerConfiguration.create();
        _config.setEndpointAddress(null);
        _config.setEndpointPort(56789);

        _config.setMaxFrameLength(65535);
        _config.setTimeout(3);
        _config.setBacklog(100);
        _config.setWorkers(10);

        _config.setPacketFactory(new SimpleBytesNcsPacketFactory());

        SimpleTestServer _main = new SimpleTestServer();
        _config.setPacketListener(_main);
        _config.setStateListener(_main);

        NcsServerService.build(_config).startNow();
    }

    @Override
    public void onPacket(NcsConnection _connection, NcsPacket _packet)
    {
        _connection.getContext(SimpleTestServerHandler.class).onPacket(_connection, _packet);
    }

    @Override
    public void onConnect(NcsConnection _connection)
    {
        _connection.setContext(new SimpleTestServerHandler());

        SimpleBytesNcsPacket _pkt = SimpleBytesNcsPacket.create();
        _pkt.startEncoding();
        _pkt.encodeBytes("HELO".getBytes());
        _pkt.encodeInt(65535);
        _pkt.encodeBoolean(true);
        _pkt.finishEncoding();
        _connection.sendAndFlush(_pkt);

        _connection.getContext(SimpleTestServerHandler.class).onConnect(_connection);
    }

    @Override
    public void onDisconnect(NcsConnection _connection)
    {
        _connection.getContext(SimpleTestServerHandler.class).onDisconnect(_connection);
    }

    @Override
    public void onError(NcsConnection _connection, Throwable _cause) {
        _connection.getContext(SimpleTestServerHandler.class).onError(_connection, _cause);
    }

    static class SimpleTestServerHandler
    {
        public void onPacket(NcsConnection _connection, NcsPacket _packet)
        {
            System.err.println("PACKET");
        }

        public void onConnect(NcsConnection _connection)
        {
            System.err.println("CONNECT "+_connection);
        }

        public void onDisconnect(NcsConnection _connection)
        {
            System.err.println("DISCONNECT "+_connection);
        }

        public void onError(NcsConnection _connection, Throwable _cause) {
            System.err.println("ERROR ");
            _cause.printStackTrace(System.err);
        }
    }
}
