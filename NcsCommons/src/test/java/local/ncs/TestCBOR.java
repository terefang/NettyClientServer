package local.ncs;

import com.github.terefang.ncs.common.cbor.OpcodedCborPacket;
import com.github.terefang.ncs.common.cbor.OpcodedCborPacketFactory;
import com.github.terefang.ncs.common.packet.NcsPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

@Data
@ToString
public class TestCBOR
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
        CborPF _f = new CborPF();
        _f.setOpcode(0x42);

        OpcodedCborPacket<TestCBOR> _pkt = (OpcodedCborPacket<TestCBOR>) _f.create();
        _pkt.setObject(new TestCBOR());
        _pkt.getObject()._i = 0xdeadbeef;
        _pkt.getObject()._c = 0x42;
        _pkt.getObject()._s = (short) 0xaffe;
        _pkt.getObject()._l = Long.MAX_VALUE;
        _pkt.getObject()._text = "Helo CBOR!";

        ByteBuf _bytes = _f.pack(_pkt, ByteBufAllocator.DEFAULT);
        System.err.println(ByteBufUtil.prettyHexDump(_bytes));
        OpcodedCborPacket<TestCBOR> _pkt2 = (OpcodedCborPacket<TestCBOR>) _f.unpack(_bytes);
        System.err.println(_pkt2.getObject().toString());
    }

    static class CborPF extends OpcodedCborPacketFactory<TestCBOR> {
        @Override
        public NcsPacket create() {
            return createPacket(TestCBOR.class);
        }
    }

    /*               +-------------------------------------------------+
                     |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
            +--------+-------------------------------------------------+----------------+
            |00000000| 00 00 00 42 bf 62 5f 69 3a 21 52 41 10 62 5f 62 |...B.b_i:!RA.b_b|
            |00000010| 00 62 5f 63 61 42 62 5f 73 39 50 01 65 5f 74 65 |.b_caBb_s9P.e_te|
            |00000020| 78 74 6a 48 65 6c 6f 20 43 42 4f 52 21 62 5f 6c |xtjHelo CBOR!b_l|
            |00000030| 1b 7f ff ff ff ff ff ff ff ff                   |..........      |
            +--------+-------------------------------------------------+----------------+
    */
}
