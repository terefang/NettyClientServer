package local.ncs;

import com.esotericsoftware.kryo.Kryo;
import com.github.terefang.ncs.client.NcsClientHelper;
import com.github.terefang.ncs.client.NcsClientService;
import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.lib.packet.kryo.KryoPacket;
import com.github.terefang.ncs.lib.packet.kryo.KryoPacketFactory;
import com.github.terefang.ncs.server.NcsServerHelper;
import com.github.terefang.ncs.server.NcsServerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestKryoClient implements NcsPacketListener<KryoPacket>, NcsStateListener
{
    public Kryo _kryo;

    @SneakyThrows
    public static void main(String[] args) {

        // basic acllback handler
        TestKryoClient _main = new TestKryoClient();
        _main._kryo = new Kryo();
        _main._kryo.register(TestPojo.class, 101);

        // configure simple server
        NcsClientService _svc = NcsClientHelper.createSimpleClient("127.0.0.1",56789, _main, _main);
        _svc.getConfiguration().setPacketFactory(new KryoPacketFactory());

        _svc.connectNow();

        Thread.sleep(3000L);

        _svc.disconnectNow();
        _svc.shutdown();
    }

    @Override
    public void onPacket(NcsConnection _connection, KryoPacket _packet)
    {
        log.info("PACKET "+_packet.getPayload(this._kryo, TestPojo.class).toString());
    }

    @Override
    public void onConnect(NcsConnection _connection)
    {
        log.info("CONNECT");

        TestPojo _pojo = new TestPojo();
        _pojo.setS("HELO");

        KryoPacket _pkt = KryoPacket.create();
        _pkt.setPayload(this._kryo, _pojo);
        _connection.sendAndFlush(_pkt);
    }

    @Override
    public void onDisconnect(NcsConnection _connection)
    {
        log.info("DISCONNECT");
    }

    @Override
    public void onError(NcsConnection _connection, Throwable _cause)
    {
        log.info("ERROR");
    }

}
