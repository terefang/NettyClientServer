package local.ncs;

import com.github.terefang.ncs.common.NcsHelper;

public class TestSimpleDiscoverHost {
    public static void main(String[] args)
    {
        NcsHelper.simpleDiscoverHost(56789, 10000, (name, uri) -> {
            System.err.println(name);
            System.err.println(uri.toASCIIString());
        });
    }
}
