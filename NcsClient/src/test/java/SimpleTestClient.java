import com.github.terefang.ncs.client.NcsClientConfiguration;
import com.github.terefang.ncs.client.NcsClientHelper;
import com.github.terefang.ncs.client.NcsClientService;
import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsPacket;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.SimpleBytesNcsPacket;
import lombok.SneakyThrows;

public class SimpleTestClient  implements NcsPacketListener, NcsStateListener
{
    @SneakyThrows
    public static void main(String[] args) {
        SimpleTestClient _main = new SimpleTestClient();

        NcsClientService _client = NcsClientHelper.createSimpleClient("127.0.0.1", 56789, _main, _main);

        _client.connectNow();

        SimpleBytesNcsPacket _pkt = (SimpleBytesNcsPacket) _client.createPacket();
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
