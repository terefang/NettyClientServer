package com.github.terefang.ncs.server;

import com.github.terefang.ncs.common.packet.NcsPacketFactory;
import com.github.terefang.ncs.common.NcsPacketListener;
import com.github.terefang.ncs.common.NcsStateListener;
import com.github.terefang.ncs.common.packet.SimpleBytesNcsPacketFactory;
import lombok.SneakyThrows;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Vector;

public class NcsServerHelper
{
    public static final int DEFAULT_MAX_FRAME_LENGTH = 8192;

    /**
     * creates a netty server, with DEFAULT_MAX_FRAME_LENGTH and SimpleBytesNcsPacketFactory
     * @param _local        the local address to bind to
     * @param _port         the port to bind to
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createSimpleServer(String _local, int _port, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(_local, _port, DEFAULT_MAX_FRAME_LENGTH, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    /**
     * creates a netty server, with SimpleBytesNcsPacketFactory
     * @param _local        the local address to bind to
     * @param _port         the port to bind to
     * @param _maxFrameLength   maximum protocol frame size
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createSimpleServer(String _local, int _port, int _maxFrameLength, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(_local, _port, _maxFrameLength, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    /**
     * creates a netty server, with SimpleBytesNcsPacketFactory
     * @param _port         the port to bind to
     * @param _maxFrameLength   maximum protocol frame size
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createSimpleServer(int _port, int _maxFrameLength, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(null, _port, _maxFrameLength, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    /**
     * creates a netty server, with DEFAULT_MAX_FRAME_LENGTH and SimpleBytesNcsPacketFactory
     * @param _port         the port to bind to
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createSimpleServer(int _port, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(null, _port, DEFAULT_MAX_FRAME_LENGTH, new SimpleBytesNcsPacketFactory(), _plistener, _slistenner);
    }

    /**
     * creates a netty server
     * @param _port         the port to bind to
     * @param _maxFrameLength   maximum protocol frame size
     * @param _factory      the packet encoder/decoder factory to use
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createServer(int _port, int _maxFrameLength, NcsPacketFactory _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(null, _port, _maxFrameLength, _factory, _plistener, _slistenner);
    }

    /**
     * creates a netty server, with DEFAULT_MAX_FRAME_LENGTH
     * @param _port         the port to bind to
     * @param _factory      the packet encoder/decoder factory to use
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createServer(int _port, NcsPacketFactory _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(null, _port, DEFAULT_MAX_FRAME_LENGTH, _factory, _plistener, _slistenner);
    }

    /**
     * creates a netty server, with DEFAULT_MAX_FRAME_LENGTH
     * @param _local        the local address to bind to
     * @param _port         the port to bind to
     * @param _factory      the packet encoder/decoder factory to use
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createServer(String _local, int _port, NcsPacketFactory _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        return createServer(_local, _port, DEFAULT_MAX_FRAME_LENGTH, _factory, _plistener, _slistenner);
    }

    /**
     * creates a netty server, with DEFAULT_MAX_FRAME_LENGTH and SimpleBytesNcsPacketFactory
     * @param _local        the local address to bind to
     * @param _port         the port to bind to
     * @param _maxFrameLength   maximum protocol frame size
     * @param _factory      the packet encoder/decoder factory to use
     * @param _plistener    the callback listener for receiving packets from the peer
     * @param _slistenner   the callback listener for receiving connection state
     * @return a server-service
     */
    public static final NcsServerService createServer(String _local, int _port, int _maxFrameLength, NcsPacketFactory _factory, NcsPacketListener _plistener, NcsStateListener _slistenner)
    {
        NcsServerConfiguration _config = NcsServerConfiguration.create();
        // local address/port to bind to
        _config.setEndpointAddress(_local);
        _config.setEndpointPort(_port);

        // protocol frame length!
        _config.setMaxFrameLength(_maxFrameLength);

        // set reasonable defaults
        _config.setTimeout(3);
        _config.setBacklog(100);
        _config.setWorkers(10);

        // set factories and listeners
        _config.setPacketFactory(_factory);
        _config.setPacketListener(_plistener);
        _config.setStateListener(_slistenner);

        // instantiate
        return NcsServerService.build(_config);
    }

    @SneakyThrows
    public static final X509Certificate createClientCertificate(String _clientDN, KeyPair _clientKey, String _caDN, KeyPair _caKey, Date _notBefore, Date _notAfter)
    {
        X500Name _subject = new X500Name(_clientDN);
        X500Name _issuer = new X500Name(_caDN);

        return createClientCertificate(BigInteger.valueOf(System.currentTimeMillis()), _subject,_clientKey,_issuer,_caKey,_notBefore,_notAfter);
    }

    @SneakyThrows
    public static final X509Certificate createClientCertificate(String _serial, String _clientDN, KeyPair _clientKey, String _caDN, KeyPair _caKey, Date _notBefore, Date _notAfter)
    {
        X500Name _subject = new X500Name(_clientDN);
        X500Name _issuer = new X500Name(_caDN);

        return createClientCertificate(new BigInteger(0, _serial.getBytes(StandardCharsets.UTF_8)), _subject,_clientKey,_issuer,_caKey,_notBefore,_notAfter);
    }

    @SneakyThrows
    public static final X509Certificate createClientCertificate(BigInteger _serial, X500Name _subject, KeyPair _clientKey, X500Name _issuer, KeyPair _caKey, Date _notBefore, Date _notAfter)
    {
        X509CertInfo _info = new X509CertInfo();
        _info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        _info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(_serial));
        try {
            _info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(_subject));
        } catch (CertificateException ignore) {
            _info.set(X509CertInfo.SUBJECT, _subject);
        }
        try {
            _info.set(X509CertInfo.ISSUER, new CertificateIssuerName(_issuer));
        } catch (CertificateException ignore) {
            _info.set(X509CertInfo.ISSUER, _issuer);
        }
        _info.set(X509CertInfo.VALIDITY, new CertificateValidity(_notBefore, _notAfter));
        _info.set(X509CertInfo.KEY, new CertificateX509Key(_clientKey.getPublic()));
        _info.set(X509CertInfo.ALGORITHM_ID,
                new CertificateAlgorithmId(new AlgorithmId(AlgorithmId.sha256WithRSAEncryption_oid)));


        // set extensions
        CertificateExtensions _exts = new CertificateExtensions();
        _exts.set(BasicConstraintsExtension.NAME, new BasicConstraintsExtension(true, false, -1));
        // Extensions[5]: X509v3 Extended Key Usage
        Vector<ObjectIdentifier> _keyUsages = new Vector<>();
        // serverAuth
        //_keyUsages.addElement(ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 1 }));
        // clientAuth
        _keyUsages.addElement(ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 3, 2 }));
        _exts.set(ExtendedKeyUsageExtension.NAME, new ExtendedKeyUsageExtension(_keyUsages));
        KeyUsageExtension _keyUsage = new KeyUsageExtension();
        _keyUsage.set(KeyUsageExtension.DIGITAL_SIGNATURE, Boolean.TRUE);
        _keyUsage.set(KeyUsageExtension.NON_REPUDIATION, Boolean.FALSE);
        _keyUsage.set(KeyUsageExtension.KEY_ENCIPHERMENT, Boolean.TRUE);
        _keyUsage.set(KeyUsageExtension.DATA_ENCIPHERMENT, Boolean.FALSE);
        _keyUsage.set(KeyUsageExtension.KEY_AGREEMENT, Boolean.FALSE);
        _keyUsage.set(KeyUsageExtension.KEY_CERTSIGN, Boolean.FALSE);
        _keyUsage.set(KeyUsageExtension.CRL_SIGN, Boolean.FALSE);
        _keyUsage.set(KeyUsageExtension.ENCIPHER_ONLY, Boolean.FALSE);
        _keyUsage.set(KeyUsageExtension.DECIPHER_ONLY, Boolean.FALSE);
        _exts.set(KeyUsageExtension.NAME, _keyUsage);

        _info.set(X509CertInfo.EXTENSIONS, _exts);

        // Sign the cert to identify the algorithm that's used.
        X509CertImpl _cert = new X509CertImpl(_info);
        _cert.sign(_caKey.getPrivate(), "SHA256withRSA");

        // Update the algorithm and sign again.
        _info.set(CertificateAlgorithmId.NAME + '.' + CertificateAlgorithmId.ALGORITHM, _cert.get(X509CertImpl.SIG_ALG));
        _cert = new X509CertImpl(_info);
        _cert.sign(_caKey.getPrivate(), "SHA256withRSA");
        _cert.verify(_caKey.getPublic());
        return _cert;
    }

    public static final Date[] createValidity(int _days)
    {
        Date _from = new Date(System.currentTimeMillis()-86400000l);
        Date _to = new Date(_from.getTime() + _days * 86400000l);
        return new Date[] { _from, _to };
    }

    @SneakyThrows
    public  static final KeyPair generateKeyPair(int _bits) {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(_bits, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    @SneakyThrows
    public static final X500Name createX500Name(String _cn, String _ou, String _o, String _l, String _st, String _co)
    {
        return new X500Name(_cn, _ou, _o, _l, _st, _co);
    }

    @SneakyThrows
    public static final X500Name createX500Name(String _cn, String _ou, String _o, String _co)
    {
        return new X500Name(_cn, _ou, _o, _co);
    }

    @SneakyThrows
    public static final X500Name createX500Name(String _cn, String _dc)
    {
        return new X500Name("cn="+_cn+",dc="+_dc);
    }

    @SneakyThrows
    public static final byte[] toBytes(X509Certificate _cert)
    {
        return _cert.getEncoded();
    }

    @SneakyThrows
    public static final byte[] toBytes(KeyPair _pair)
    {
        return _pair.getPrivate().getEncoded();
    }
}
