package com.github.terefang.ncs.common.security.obf;

import com.github.terefang.ncs.common.NcsHelper;
import lombok.Data;
import lombok.SneakyThrows;

import java.security.SecureRandom;

@Data
public class NcsPskObfCodecState
{
    public byte[] _pad;
    public int _mac;
    public boolean useObf;
    public boolean useCRC;
    public boolean tolerant = true;
    public SecureRandom _rng;

    @SneakyThrows
    public static final NcsPskObfCodecState from(String _sharedSecret, int _max, boolean _useObf, boolean _useCRC)
    {
        NcsPskObfCodecState _s = new NcsPskObfCodecState();
        _s._pad = NcsHelper.pbkdf2_sha256(_sharedSecret, NcsPskObfCodecUtil.SALT, 1<<10, _max);
        _s.useObf = _useObf;
        _s.useCRC = _useCRC;
        _s._mac = NcsHelper.crc16i(0, _s._pad);
        _s._rng = SecureRandom.getInstanceStrong();
        return _s;
    }
}
