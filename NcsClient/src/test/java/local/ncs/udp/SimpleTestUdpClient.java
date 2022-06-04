package local.ncs.udp;

import com.github.terefang.ncs.client.NcsClientHelper;
import com.github.terefang.ncs.client.NcsClientService;
import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;
import lombok.SneakyThrows;

public class SimpleTestUdpClient implements NcsPacketListener<SimpleBytesNcsPacket>, NcsStateListener
{
    @SneakyThrows
    public static void main(String[] args) {
        SimpleTestUdpClient _main = new SimpleTestUdpClient();

        // create simple server
        NcsClientService _client = NcsClientHelper.createSimpleUdpClient("127.0.0.1", 56789, _main, _main);

        _client.getConfiguration().setSharedSecret("07cwI&Y4gLXtJrQdfYWcKey!cseY9jB0Q*bveiT$zi6LX7%xMuGm!hzW%rQj%8Wf");

        // sync connect
        _client.connectNow();

        // create and send packet
        SimpleBytesNcsPacket _pkt = (SimpleBytesNcsPacket) _client.createPacket();
        _pkt.startEncoding();
        _pkt.encodeBytes("CONN".getBytes());
        _pkt.encodeInt(65535);
        _pkt.encodeBoolean(true);
        _pkt.finishEncoding();
        _client.sendAndFlush(_pkt);

        Thread.sleep(3000L);

        // disconnect sync and shutdown workers
        _client.disconnectNow();
        _client.shutdown();
    }

    @Override
    public void onPacket(NcsConnection _connection, SimpleBytesNcsPacket _packet)
    {
        System.err.println("PACKET "+_packet.getAddress().toString());
        System.err.println(_packet.asHexString());
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
        System.err.println("ERROR -- "+_cause.getMessage());
    }
}
