package com.github.terefang.ncs.common.security.tls;

import java.math.BigInteger;
import java.util.Date;

public interface NcsClientCertificateVerifier
{
    boolean verify(BigInteger _serial, String _name, Date _notBefore, Date _notAfter);
}
