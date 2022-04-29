package com.github.terefang.ncs.common.security;

import com.github.terefang.ncs.common.NcsConfiguration;
import io.netty.handler.ssl.util.FingerprintTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.SneakyThrows;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class NcsSslTlsHelper
{
    @SneakyThrows
    public static SelfSignedCertificate createCertificate(String _fqdn)
    {
        return new SelfSignedCertificate(_fqdn);
    }

    @SneakyThrows
    public static SSLContext createSslContext(NcsConfiguration _config, String _fqdn)
    {
        if(!_config.isTlsEnabled()) return null;

        TrustManager[] _tm = null;
        if(_config.isTlsVerifyPeer() && _config.getTlsFingerprint()!=null)
        {
            _tm = new FingerprintTrustManagerFactory(_config.getTlsFingerprint()).getTrustManagers() ;
        }
        else
        {
            _tm = new X509TrustManager[]{ new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
        }

        SSLContext _sslCtx = SSLContext.getInstance("TLSv1.3");
        if(_config.getTlsCertificate()==null || _config.getTlsKey()==null)
        {
            SelfSignedCertificate _cert = createCertificate(_fqdn);
            _config.setTlsCertificate(_cert.cert());
            _config.setTlsKey(_cert.key());
        }

        X509ExtendedKeyManager _ekm = NcsX509ExtendedKeyManager.from(_config.getTlsKey(), _config.getTlsCertificate(), "default");
        _sslCtx.init(new KeyManager[] { _ekm }, _tm, null);

        return _sslCtx;
    }

    public static SSLParameters createClientSslParameter(NcsConfiguration _config)
    {
        SSLParameters _param = new SSLParameters();
        _param.setNeedClientAuth(false);
        _param.setWantClientAuth(false);
        _param.setCipherSuites(_config.getTlsCiphers());
        _param.setProtocols(_config.getTlsProtocols());
        return _param;
    }

    public static SSLEngine createClientSslEngine(NcsConfiguration _config, SSLContext _ctx, SSLParameters _param)
    {
        SSLEngine _engine = _ctx.createSSLEngine();
        _engine.setUseClientMode(true);
        _engine.setSSLParameters(_param);
        return _engine;
    }

    public static SSLParameters createServerSslParameter(NcsConfiguration _config)
    {
        SSLParameters _param = new SSLParameters();
        if(_config.isTlsVerifyPeer())
        {
            _param.setNeedClientAuth(true);
            _param.setWantClientAuth(true);
        }
        _param.setCipherSuites(_config.getTlsCiphers());
        _param.setProtocols(_config.getTlsProtocols());
        return _param;
    }

    public static SSLEngine createServerSslEngine(NcsConfiguration _config, SSLContext _ctx, SSLParameters _param)
    {
        SSLEngine _engine = _ctx.createSSLEngine();
        _engine.setUseClientMode(false);
        _engine.setSSLParameters(_param);
        return _engine;
    }
}
