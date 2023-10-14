import com.github.terefang.ncs.idl.NcsIdlParser;
import lombok.SneakyThrows;

import java.io.FileReader;

public class TestIdlParse
{
    public static String IDL_FILE = "NcsIdlSpec/src/test/resources/test.idl";

    @SneakyThrows
    public static void main(String[] args) {
        NcsIdlParser p = new NcsIdlParser(new FileReader(IDL_FILE));
        System.out.println(p.parse());
    }
}
