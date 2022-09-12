package local.ncs.tcp;

import com.github.terefang.ncs.client.NcsClientHelper;
import com.github.terefang.ncs.client.NcsClientService;
import com.github.terefang.ncs.common.*;
import com.github.terefang.ncs.common.packet.NcsKeepAlivePacket;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static javax.security.auth.login.Configuration.getConfiguration;

public class SimplePskClient implements NcsPacketListener<SimpleBytesNcsPacket>, NcsStateListener
{
    @SneakyThrows
    public static void main(String[] args) {
        SimplePskClient _main = new SimplePskClient();

        // create simple server
        NcsClientService _client = NcsClientHelper.createSimpleClient("127.0.0.1", 56789, _main, _main);

        _client.getConfiguration().setSharedSecret("07cwI&Y4gLXtJrQdfYWcKey!cseY9jB0Q*bveiT$zi6LX7%xMuGm!hzW%rQj%8Wf");
        _client.getConfiguration().setUsePskOBF(true);
        _client.getConfiguration().setUsePskMac(true);
        // sync connect
        _client.connectNow();

        ScheduledExecutorService _exec = Executors.newSingleThreadScheduledExecutor();
        _exec.scheduleAtFixedRate(() -> {

            // create and send packet
            SimpleBytesNcsPacket _pkt = (SimpleBytesNcsPacket) _client.createPacket();
            _pkt.startEncoding();
            _pkt.encodeBytes("CONN".getBytes());
            _pkt.encodeInt(65535);
            _pkt.encodeBoolean(true);
            _pkt.finishEncoding();
            _client.sendAndFlush(_pkt);
            System.err.println("-");
        }, 2000L, 2000L, TimeUnit.MILLISECONDS);

        Thread.sleep(300000L);

        // disconnect sync and shutdown workers
        _exec.shutdownNow();
        _client.disconnectNow();
        _client.shutdown();
    }

    @Override
    public void onPacket(NcsConnection _connection, SimpleBytesNcsPacket _packet)
    {
        System.err.println("PACKET "+_connection.getPeer().asString());
    }

    @Override
    public void onConnect(NcsConnection _connection) {
        System.err.println("CONNECT "+_connection.getPeer().asString());
    }

    @Override
    public void onDisconnect(NcsConnection _connection) {
        System.err.println("DISCONNECT "+_connection.getPeer().asString());
    }

    @Override
    public void onError(NcsConnection _connection, Throwable _cause) {
        System.err.println("ERROR "+_connection.getPeer().asString()+" -- "+_cause.getMessage());
    }
}
