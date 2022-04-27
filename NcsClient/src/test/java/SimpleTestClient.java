import com.github.terefang.ncs.client.NcsClientConfiguration;
import com.github.terefang.ncs.client.NcsClientService;
import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsPacket;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.impl.SimpleBytesNcsPacket;
import com.github.terefang.ncs.common.impl.SimpleBytesNcsPacketFactory;
import lombok.SneakyThrows;

public class SimpleTestClient  implements NcsPacketListener, NcsStateListener
{
    @SneakyThrows
    public static void main(String[] args) {
        NcsClientConfiguration _config = NcsClientConfiguration.create();
        _config.setEndpointAddress("127.0.0.1");
        _config.setEndpointPort(56789);

        _config.setMaxFrameLength(65535);
        _config.setTimeout(3);

        _config.setPacketFactory(new SimpleBytesNcsPacketFactory());

        SimpleTestClient _main = new SimpleTestClient();
        _config.setPacketListener(_main);
        _config.setStateListener(_main);

        NcsClientService _client = NcsClientService.build(_config);
        _client.connectNow();

        SimpleBytesNcsPacket _pkt = SimpleBytesNcsPacket.create();
        _pkt.startEncoding();
        _pkt.encodeBytes("CONN".getBytes());
        _pkt.encodeInt(65535);
        _pkt.encodeBoolean(true);
        _pkt.finishEncoding();

        _client.sendAndFlush(_pkt);
        Thread.sleep(3000L);
        _client.disconnectNow();
        _client.shutdown();
    }

    @Override
    public void onPacket(NcsConnection _connection, NcsPacket _packet) {
        System.err.println("PACKET");
    }

    @Override
    public void onConnect(NcsConnection _connection) {
        System.err.println("CONNECT");
    }

    @Override
    public void onDisconnect(NcsConnection _connection) {
        System.err.println("DISCONNECT");
    }

    @Override
    public void onError(NcsConnection _connection, Throwable _cause) {
        System.err.println("ERROR");
    }
}
