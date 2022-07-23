package com.github.terefang.ncs.common;

import com.github.terefang.ncs.common.security.tls.NcsClientCertificateVerifier;
import com.github.terefang.ncs.common.security.tls.NcsX509ExtendedKeyManager;
import com.github.terefang.ncs.common.security.tls.NcsX509TrustManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.util.FingerprintTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.SneakyThrows;

import javax.net.ssl.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class NcsHelper {
    public static long SPECIAL_PACKET_ID = -1L;
    public static long DISCOVERY_PACKET_ID = 0x444953434f564552L;
    public static long FOUND_PACKET_ID = 0x464f554e4400L;
    public static long FOUND_WITH_URL_PACKET_ID = 0x464f554e4401L;

    @SneakyThrows
    public static SelfSignedCertificate createCertificate(String _fqdn) {
        return new SelfSignedCertificate(_fqdn);
    }

    @SneakyThrows
    public static SSLContext createSslContext(final NcsConfiguration _config, String _fqdn, final NcsClientCertificateVerifier clientCertificateVerifier) {
        if (!_config.isTlsEnabled()) return null;

        SSLContext _sslCtx = SSLContext.getInstance("TLSv1.3");
        if (_config.getTlsCertificate() == null || _config.getTlsKey() == null) {
            SelfSignedCertificate _cert = createCertificate(_fqdn);
            _config.setTlsCertificate(_cert.cert());
            _config.setTlsKey(_cert.key());
        }

        TrustManager[] _tm = null;
        if (_config.isTlsVerifyPeer() && _config.getTlsFingerprint() != null) {
            _tm = new FingerprintTrustManagerFactory(_config.getTlsFingerprint()).getTrustManagers();
        } else {
            _tm = new X509TrustManager[]{NcsX509TrustManager.from(_config, clientCertificateVerifier)};
        }

        X509ExtendedKeyManager _ekm = NcsX509ExtendedKeyManager.from(_config.getTlsKey(), _config.getTlsCertificate(), "default");
        _sslCtx.init(new KeyManager[]{_ekm}, _tm, null);

        return _sslCtx;
    }

    public static SSLParameters createClientSslParameter(NcsConfiguration _config) {
        SSLParameters _param = new SSLParameters();
        _param.setNeedClientAuth(false);
        _param.setWantClientAuth(false);
        _param.setCipherSuites(_config.getTlsCiphers());
        _param.setProtocols(_config.getTlsProtocols());
        return _param;
    }

    public static SSLEngine createClientSslEngine(NcsConfiguration _config, SSLContext _ctx, SSLParameters _param) {
        SSLEngine _engine = _ctx.createSSLEngine();
        _engine.setUseClientMode(true);
        _engine.setSSLParameters(_param);
        return _engine;
    }

    public static SSLParameters createServerSslParameter(NcsConfiguration _config) {
        SSLParameters _param = new SSLParameters();
        if (_config.isTlsVerifyPeer()) {
            _param.setNeedClientAuth(true);
            _param.setWantClientAuth(true);
        }
        _param.setCipherSuites(_config.getTlsCiphers());
        _param.setProtocols(_config.getTlsProtocols());
        return _param;
    }

    public static SSLEngine createServerSslEngine(NcsConfiguration _config, SSLContext _ctx, SSLParameters _param) {
        SSLEngine _engine = _ctx.createSSLEngine();
        _engine.setUseClientMode(false);
        _engine.setSSLParameters(_param);
        return _engine;
    }

    /**
     * CRC table -- CRC-16-IBM; also known as CRC-16 and CRC-16-ANSI; poly = 0x8005
     * <p>
     * https://en.wikipedia.org/wiki/Cyclic_redundancy_check
     * <p>
     * https://github.com/ETLCPP/crc-table-generator
     * <p>
     * BUT NOT CRC-16-CCITT; known as CRC-CCITT; poly = 0x1021
     * <p>
     * // initialize static lookup table
     * for (int i = 0; i < 256; i++)
     * {
     * int crc = i << 8;
     * for (int j = 0; j < 8; j++)
     * {
     * if ((crc & 0x8000) == 0x8000)
     * {
     * crc = (crc << 1) ^ poly;
     * } else {
     * crc = (crc << 1);
     * }
     * }
     * table[i] = crc & 0xffff;
     * }
     */
    static int[] CRC_TABLE = {
            0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
            0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
            0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
            0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
            0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
            0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
            0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
            0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
            0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
            0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
            0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
            0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
            0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
            0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
            0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
            0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
            0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
            0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
            0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
            0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
            0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
            0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
            0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
            0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
            0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
            0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
            0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
            0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
            0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
            0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
            0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
            0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,
    };

    /**
     * calculate crc16 as an integer
     *
     * @param _start  the start value for the crc (usually 0x0000 or 0x8000)
     * @param _buffer the buffer the be crc'd
     * @return the crc in integer format
     */
    @SneakyThrows
    public static int crc16i(int _start, byte[] _buffer) {
        int _crc = _start;
        for (byte _b : _buffer) {
            _crc = (_crc >>> 8) ^ CRC_TABLE[(_crc ^ _b) & 0xff];
        }
        return (_crc & 0xffff);
    }


    public static final String HMAC_SHA1 = "HmacSHA1";
    public static final String HMAC_SHA256 = "HmacSHA256";
    public static final String HMAC_SHA512 = "HmacSHA512";

    /**
     * calculate crc16 as a byte array
     *
     * @param _start  the start value for the crc (usually 0x0000 or 0x8000)
     * @param _buffer the buffer the be crc'd
     * @return the crc in 2-byte array format
     */
    public static byte[] crc16(int _start, byte[] _buffer) {
        int _crc = crc16i(_start, _buffer);
        return new byte[]{(byte) (_crc & 0xff), (byte) ((_crc >>> 8) & 0xff)};
    }

    // from https://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
    // ignores the higher 16 bits
    public static float toFloatFrom16(short hbits) {
        int mant = hbits & 0x03ff;            // 10 bits mantissa
        int exp = hbits & 0x7c00;            // 5 bits exponent
        if (exp == 0x7c00)                   // NaN/Inf
            exp = 0x3fc00;                    // -> NaN/Inf
        else if (exp != 0)                   // normalized value
        {
            exp += 0x1c000;                   // exp - 15 + 127
            if (mant == 0 && exp > 0x1c400)  // smooth transition
                return Float.intBitsToFloat((hbits & 0x8000) << 16
                        | exp << 13 | 0x3ff);
        } else if (mant != 0)                  // && exp==0 -> subnormal
        {
            exp = 0x1c400;                    // make it normal
            do {
                mant <<= 1;                   // mantissa * 2
                exp -= 0x400;                 // decrease exp by 1
            } while ((mant & 0x400) == 0); // while not normal
            mant &= 0x3ff;                    // discard subnormal bit
        }                                     // else +/-0 -> +/-0
        return Float.intBitsToFloat(          // combine all parts
                (hbits & 0x8000) << 16          // sign  << ( 31 - 15 )
                        | (exp | mant) << 13);         // value << ( 23 - 10 )
    }

    // returns all higher 16 bits as 0 for all results
    public static short fromFloatTo16(float fval) {
        int fbits = Float.floatToIntBits(fval);
        int sign = fbits >>> 16 & 0x8000;          // sign only
        int val = (fbits & 0x7fffffff) + 0x1000; // rounded value

        if (val >= 0x47800000)               // might be or become NaN/Inf
        {                                     // avoid Inf due to rounding
            if ((fbits & 0x7fffffff) >= 0x47800000) {                                 // is or must become NaN/Inf
                if (val < 0x7f800000)        // was value but too large
                    return (short) (sign | 0x7c00);     // make it +/-Inf
                return (short) (sign | 0x7c00 |        // remains +/-Inf or NaN
                        (fbits & 0x007fffff) >>> 13); // keep NaN (and Inf) bits
            }
            return (short) (sign | 0x7bff);             // unrounded not quite Inf
        }
        if (val >= 0x38800000)               // remains normalized value
            return (short) (sign | val - 0x38000000 >>> 13); // exp - 127 + 15
        if (val < 0x33000000)                // too small for subnormal
            return (short) sign;                      // becomes +/-0
        val = (fbits & 0x7fffffff) >>> 23;  // tmp exp for subnormal calc
        return (short) (sign | ((fbits & 0x7fffff | 0x800000) // add subnormal bit
                + (0x800000 >>> val - 102)     // round depending on cut off
                >>> 126 - val));   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
    }

    public static interface SimpleDiscoverHostCallback
    {
        default void onDiscoveryPacket(InetAddress _address, int _port, DatagramPacket _dp)
        {
            if(_dp.getLength() < 16) return;

            ByteBuf _buf = ByteBufAllocator.DEFAULT.buffer(_dp.getLength());
            _buf.writeBytes(_dp.getData());
            long _i1 = _buf.readLong();
            long _i2 = _buf.readLong();
            if(_i1 == SPECIAL_PACKET_ID)
            {
                byte[] _str = new byte[_buf.readInt()];
                _buf.readBytes(_str);
                String _name = new String(_str, StandardCharsets.UTF_8);

                if(_i2 == FOUND_PACKET_ID)
                {
                    if(_address instanceof Inet6Address)
                    {
                        this.onDiscoveryPacket(_name, URI.create(String.format("udp://[%s]:%d", _address.getHostAddress(), _port)));
                    }
                    else
                    {
                        this.onDiscoveryPacket(_name, URI.create(String.format("udp://%s:%d", _address.getHostAddress(), _port)));
                    }
                }
                else
                if(_i2 == FOUND_WITH_URL_PACKET_ID)
                {
                    _str = new byte[_buf.readInt()];
                    _buf.readBytes(_str);
                    this.onDiscoveryPacket(_name, URI.create(new String(_str, StandardCharsets.UTF_8)));
                }
            }
        }

        void onDiscoveryPacket(String _name, URI _serverUri);
    }

    @SneakyThrows
    public static final void simpleDiscoverHost(final int _port, final int _timeout, final SimpleDiscoverHostCallback _Callback)
    {
        DatagramSocket _ds = new DatagramSocket();
        _ds.setSoTimeout(_timeout);
        _ds.setBroadcast(true);

        ByteBuf _buf = ByteBufAllocator.DEFAULT.buffer(16);
        _buf.writeLong(SPECIAL_PACKET_ID);
        _buf.writeLong(DISCOVERY_PACKET_ID);

        final byte[] _data = new byte[16];
        _buf.readBytes(_data);

        Thread _thr = new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                DatagramPacket _dp = new DatagramPacket(new byte[8192], 8192);
                long _end = System.currentTimeMillis() + (_timeout);
                do {
                    try
                    {
                        _ds.receive(_dp);
                        _Callback.onDiscoveryPacket(_dp.getAddress(), _dp.getPort(), _dp);
                    }
                    catch (SocketTimeoutException _ste)
                    {
                        //IGONRE
                    }
                }
                while (_end > System.currentTimeMillis());

                _ds.close();
            }
        });

        _thr.start();

        for(int _i = 0; _i<4; _i++)
        {
            for (NetworkInterface _iface : Collections.list(NetworkInterface.getNetworkInterfaces()))
            {
                for (InterfaceAddress _address : _iface.getInterfaceAddresses())
                {
                    _ds.send(new DatagramPacket(_data, _data.length, new InetSocketAddress(_address.getBroadcast(), _port)));
                }
            }
            Thread.sleep(_timeout >> 2);
        }
    }

    @SneakyThrows
    public static final void simpleDiscoverHost(final InetAddress _address, final int _port, final int _timeout, final SimpleDiscoverHostCallback _Callback)
    {
        DatagramSocket _ds = new DatagramSocket();
        _ds.setSoTimeout(_timeout);

        ByteBuf _buf = ByteBufAllocator.DEFAULT.buffer(16);
        _buf.writeLong(SPECIAL_PACKET_ID);
        _buf.writeLong(DISCOVERY_PACKET_ID);

        final byte[] _data = new byte[16];
        _buf.readBytes(_data);

        Thread _thr = new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                DatagramPacket _dp = new DatagramPacket(new byte[8192], 8192);
                long _end = System.currentTimeMillis() + (_timeout);
                do {
                    try
                    {
                        _ds.receive(_dp);
                        _Callback.onDiscoveryPacket(_dp.getAddress(), _dp.getPort(), _dp);
                    }
                    catch (SocketTimeoutException _ste)
                    {
                        //IGONRE
                    }
                }
                while (_end > System.currentTimeMillis());

                _ds.close();
            }
        });

        _thr.start();

        for(int _i = 0; _i<4; _i++)
        {
            _ds.send(new DatagramPacket(_data, _data.length, new InetSocketAddress(_address, _port)));
            Thread.sleep(_timeout >> 2);
        }
    }
}
