import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleTestServerHandler {
    public void onPacket(NcsConnection _connection, SimpleBytesNcsPacket _packet) {
        log.info("PACKET " + _connection.getPeer().asString());
    }

    public void onConnect(NcsConnection _connection) {
        log.info("CONNECT " + _connection.getPeer().asString());
    }

    public void onDisconnect(NcsConnection _connection) {
        log.info("DISCONNECT " + _connection.getPeer().asString());
    }

    public void onError(NcsConnection _connection, Throwable _cause) {
        log.info("ERROR " + _connection.getPeer().asString() + " -- " + _cause.getMessage());
    }
}
