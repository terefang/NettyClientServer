package com.github.terefang.ncs.common.security.tls;

import lombok.SneakyThrows;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class NcsX509ExtendedKeyManager extends X509ExtendedKeyManager
{
    X509Certificate[] _chain;
    String[] _aliases;
    PrivateKey _pk;

    public static NcsX509ExtendedKeyManager from(PrivateKey _pk, X509Certificate _crt,  String _alias)
    {
        NcsX509ExtendedKeyManager _ekm = new NcsX509ExtendedKeyManager();
        _ekm._pk = _pk;
        _ekm._aliases = new String[]{ _alias };
        _ekm._chain = new X509Certificate[]{ _crt };
        return _ekm;
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return _aliases;
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return _aliases[0];
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return _aliases;
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return _aliases[0];
    }

    @Override
    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
        return _aliases[0];
    }

    @Override
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        return _aliases[0];
    }

    @SneakyThrows
    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return _chain;
    }

    @SneakyThrows
    @Override
    public PrivateKey getPrivateKey(String alias) {
        return _pk;
    }
}