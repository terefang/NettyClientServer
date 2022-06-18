package com.github.terefang.ncs.common.util;

import com.github.terefang.ncs.common.passwd.BCrypt;
import com.github.terefang.ncs.common.passwd.SRP6Crypt;
import com.github.terefang.ncs.common.passwd.Sha2Crypt;
import lombok.SneakyThrows;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class PasswdUtil
{
    public static final boolean matchPassword(String _identity, String _givenPassword, long _tick, String _encPassword)
    {
        try
        {
            if (_encPassword.startsWith("{plain}"))
            {
                return _encPassword.substring(7).equalsIgnoreCase(_givenPassword);
            }
            else
            if(_encPassword.startsWith(BCrypt.PREFIX_BCRYPT))
            {
                return BCrypt.checkPassword(_givenPassword, _encPassword);
            }
            else
            if(_encPassword.startsWith(Sha2Crypt.SHA256_PREFIX))
            {
                return Sha2Crypt.sha256Crypt(_givenPassword.getBytes(StandardCharsets.UTF_8), _encPassword).equalsIgnoreCase(_encPassword);
            }
            else
            if(_encPassword.startsWith(Sha2Crypt.SHA512_PREFIX))
            {
                return Sha2Crypt.sha512Crypt(_givenPassword.getBytes(StandardCharsets.UTF_8), _encPassword).equalsIgnoreCase(_encPassword);
            }
            else
            if(_encPassword.startsWith(SRP6Crypt.SRP6_CRYPT_PREFIX))
            {
                return SRP6Crypt.checkPassword(_encPassword, _givenPassword);
            }
            else
            {
                return false;
            }
        }
        catch(Exception _xe)
        {
        }
        return false;
    }

    public static final boolean matchPassword(String _givenPassword, String _encPassword)
    {
        return matchPassword("-nil-", _givenPassword, -1,_encPassword);
    }

    public static String hashPassword(String _givenPassword)
    {
        return SRP6Crypt.generate(_givenPassword);
        //return BCrypt.generate(_givenPassword);
    }

    static char[] HEXDIGITS = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    public static byte[] fromHex(String _hex)
    {
        return fromHex(_hex.toCharArray());
    }

    public static byte[] fromHex(char[] _hex)
    {
        byte[] _ret = new byte[_hex.length/2];
        for(int _i=0; _i<_ret.length; _i++)
        {
            _ret[_i] = (byte) ((toDigit(_hex[_i*2])<<4) | toDigit(_hex[(_i*2)+1]));
        }
        return _ret;
    }

    @SneakyThrows
    static int toDigit(final char ch)
    {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new IllegalArgumentException("Illegal hexadecimal character " + ch);
        }
        return digit;
    }

    public static String toHex(Long _n)
    {
        return toHex(BigInteger.valueOf(_n).toByteArray());
    }

    public static String toHex(byte[] _buf)
    {
        char[] _out = new char[_buf.length*2];
        for(int _i=0; _i<_buf.length; _i++)
        {
            _out[_i*2] = HEXDIGITS[(_buf[_i]>>>4) & 0xf];
            _out[_i*2+1] = HEXDIGITS[_buf[_i] & 0xf];
        }
        return new String(_out);
    }

    public static String toHex(String _buf)
    {
        return toHex(_buf.getBytes());
    }
}
