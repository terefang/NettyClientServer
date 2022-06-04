package local.ncs;


import com.github.terefang.ncs.common.XUID;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.SneakyThrows;

public class TestXUID
{
    int _i;
    byte _b;
    char _c;
    short _s;
    String _text;
    long _l;

    @SneakyThrows
    public static void main(String[] args)
    {
        XUID _id = XUID.create();
        System.err.println(_id.asString());
        System.err.println(_id.asUUID().toString());

        ByteBuf _buf = ByteBufAllocator.DEFAULT.buffer(16);
        _id.writeTo(_buf);

        _id = XUID.from(_buf);
        System.err.println(_id.asString());
        System.err.println(_id.asUUID().toString());
    }

}
