package local.ncs;

import lombok.SneakyThrows;

import javax.net.ssl.SSLContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestCipherSuites
{


    @SneakyThrows
    public static void main(String[] args)
    {
        final String[] protocols = new String[] { "TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3" };
        for (final String protocol : protocols) {
            final SSLContext sslContext = SSLContext.getInstance(protocol);
            sslContext.init(null, null, null);
            final List<String> defaultCipherSuites = Arrays
                    .asList(sslContext.getDefaultSSLParameters().getCipherSuites());
            Collections.sort(defaultCipherSuites);
            final List<String> supportedCipherSuites = Arrays
                    .asList(sslContext.getSupportedSSLParameters().getCipherSuites());
            Collections.sort(supportedCipherSuites);
            for (final String cn : defaultCipherSuites) {
                System.out.println(protocol + ",default," + cn);
            }
            for (final String cn : supportedCipherSuites) {
                System.out.println(protocol + ",supported," + cn);
            }
        }
    }
}
