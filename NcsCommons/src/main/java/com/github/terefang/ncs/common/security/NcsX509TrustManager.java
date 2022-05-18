package com.github.terefang.ncs.common.security;

import com.github.terefang.ncs.common.NcsConfiguration;
import lombok.SneakyThrows;

import javax.net.ssl.X509TrustManager;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class NcsX509TrustManager implements X509TrustManager
{
    private NcsConfiguration _config;
    private NcsClientCertificateVerifier _clientCertificateVerifier;

    public static final NcsX509TrustManager from(NcsConfiguration _config, NcsClientCertificateVerifier _clientCertificateVerifier)
    {
        NcsX509TrustManager _tm = new NcsX509TrustManager();
        _tm._config = _config;
        _tm._clientCertificateVerifier = _clientCertificateVerifier;
        return _tm;
    }

    @Override
    @SneakyThrows
    public void checkClientTrusted(X509Certificate[] _chain, String authType) throws CertificateException
    {
        if(this._clientCertificateVerifier != null)
        {
            if(_chain==null)
            {
                throw new CertificateException("client not authorized (no certificate)");
            }

            if(_chain.length==0)
            {
                throw new CertificateException("client not authorized (no certificate)");
            }

            String _dn = _chain[0].getSubjectDN().getName();
            String _idn = _chain[0].getIssuerDN().getName();

            boolean _knownIssuer = false;
            X509Certificate _sa=null;
            for(X509Certificate _cert : getAcceptedIssuers())
            {
                if(_idn.equalsIgnoreCase(_cert.getSubjectDN().getName()))
                {
                    _knownIssuer = true;
                    _sa = _cert;
                }
            }

            if(!_knownIssuer)
            {
                throw new CertificateException("client not authorized (unknown issuer) "+_idn);
            }

            if(_sa!=null)
            {
                try
                {
                    _chain[0].verify(_sa.getPublicKey());
                }
                catch(NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException | CertificateException _xe)
                {
                    throw new CertificateException("client not authorized (verify failed) "+_dn);
                }
            }

            if(!this._clientCertificateVerifier.verify(_chain[0].getSerialNumber(), _dn, _chain[0].getNotBefore(), _chain[0].getNotAfter()))
            {
                throw new CertificateException("client not authorized "+_dn);
            }
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
        if(_config.getTlsChain()!=null)
        {
            return _config.getTlsChain();
        }
        return new X509Certificate[] { _config.getTlsCertificate() };
    }
}
