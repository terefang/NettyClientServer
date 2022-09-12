package local.ncs;

import com.github.terefang.ncs.common.NcsConnection;
import com.github.terefang.ncs.common.NcsEndpoint;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleTestServerHandler
{
    public void onPacket(NcsConnection _connection, SimpleBytesNcsPacket _packet) {
        if(_connection.isUdp())
        {
            log.info("PACKET " + _packet.getAddress().toString());
        }
        else
        {
            log.info("PACKET " + _connection.getPeer().asString());
        }
        log.info(_packet.asHexString());
    }

    public void onConnect(NcsConnection _connection)
    {
        if(_connection.isUdp())
        {
            log.info("CONNECT");
        }
        else
        {
            log.info("CONNECT " + _connection.getPeer().asString());
        }
    }

    public void onDisconnect(NcsConnection _connection)
    {
        if(_connection.isUdp())
        {
            log.info("DISCONNECT");
        }
        else
        {
            log.info("DISCONNECT " + _connection.getPeer().asString());
        }
    }

    public void onError(NcsConnection _connection, Throwable _cause)
    {
        if(_connection.isUdp())
        {
            log.info("ERROR -- " + _cause.getMessage(), _cause);
        }
        else
        {
            log.info("ERROR " + _connection.getPeer().asString() + " -- " + _cause.getMessage(), _cause);
        }
    }

    public void onKeepAliveFail(NcsConnection _connection, long _timeout, long _fails, NcsEndpoint _endpoint) {
        log.info("KEEP-ALIVE-FAIL -> "+_endpoint.asString());
    }

}
