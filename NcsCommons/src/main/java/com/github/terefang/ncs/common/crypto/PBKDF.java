package com.github.terefang.ncs.common.crypto;


import com.github.terefang.ncs.common.util.B32;
import lombok.SneakyThrows;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

/**
 * An implementation of the Password-Based Key Derivation Function as specified
 * in RFC 2898.
 *
 * @author  Will Glozer
 */
public class PBKDF
{
    public static int ITERATION_COUNT_1K = 1024;
    public static int ITERATION_COUNT_1000 = 0x3e8;
    /**
     * Implementation of PBKDF2 (RFC2898).
     *
     * @param   alg     HMAC algorithm to use.
     * @param   P       Password.
     * @param   S       Salt.
     * @param   c       Iteration count.
     * @param   dkLen   Intended length, in octets, of the derived key.
     *
     * @return  The derived key.
     *
     */
    @SneakyThrows
    public static byte[]
    pbkdf2(String alg, byte[] P, byte[] S, int c, int dkLen)
    {
        Mac mac = Mac.getInstance(alg);
        mac.init(new SecretKeySpec(P, alg));
        int hLen = mac.getMacLength();
        byte[] DK = new byte[dkLen==-1 ? hLen : dkLen];
        pbkdf2(mac, S, c, DK, DK.length);
        return DK;
    }

    @SneakyThrows
    public static String
    pbkdf2_base32(String alg, byte[] P, byte[] S, int c, int dkLen)
    {
        return B32.encode(pbkdf2(alg, P, S, c, dkLen));
    }

    /**
     * Implementation of PBKDF2 (RFC2898).
     *
     * @param   mac     Pre-initialized {@link Mac} instance to use.
     * @param   S       Salt.
     * @param   c       Iteration count.
     * @param   DK      Byte array that derived key will be placed in.
     * @param   dkLen   Intended length, in octets, of the derived key.
     *
     * @throws  GeneralSecurityException
     */
    @SneakyThrows
    public static void
    pbkdf2(Mac mac, byte[] S, int c, byte[] DK, int dkLen)
    {
        int hLen = mac.getMacLength();

        if (dkLen > (Math.pow(2, 32) - 1) * hLen)
        {
            throw new GeneralSecurityException("Requested key length too long");
        }

        if(dkLen == -1)
        {
            dkLen = hLen;
        }

        byte[] U      = new byte[hLen];
        byte[] T      = new byte[hLen];
        byte[] block1 = new byte[S.length + 4];

        int l = (int) Math.ceil((double) dkLen / hLen);
        int r = dkLen - (l - 1) * hLen;

        System.arraycopy(S, 0, block1, 0, S.length);

        for (int i = 1; i <= l; i++)
        {
            block1[S.length + 0] = (byte) (i >> 24 & 0xff);
            block1[S.length + 1] = (byte) (i >> 16 & 0xff);
            block1[S.length + 2] = (byte) (i >> 8  & 0xff);
            block1[S.length + 3] = (byte) (i >> 0  & 0xff);

            mac.update(block1);
            mac.doFinal(U, 0);
            System.arraycopy(U, 0, T, 0, hLen);

            for (int j = 1; j < c; j++)
            {
                mac.update(U);
                mac.doFinal(U, 0);

                for (int k = 0; k < hLen; k++)
                {
                    T[k] ^= U[k];
                }
            }

            System.arraycopy(T, 0, DK, (i - 1) * hLen, (i == l ? r : hLen));
        }
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha256(byte[] P, byte[] S, int c, int dkLen)
    {
        return pbkdf2("HmacSHA256", P, S, c, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha256(String P, String S, int c, int dkLen)
    {
        return pbkdf2("HmacSHA256", P.getBytes(), S.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha512(byte[] P, byte[] S, int c, int dkLen)
    {
        return pbkdf2("HmacSHA512", P, S, c, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha512(String P, String S, int c, int dkLen)
    {
        return pbkdf2("HmacSHA512", P.getBytes(), S.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha1(byte[] P, byte[] S, int c, int dkLen)
    {
        return pbkdf2("HmacSHA1", P, S, c, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha1(String P, String S, int c, int dkLen)
    {
        return pbkdf2("HmacSHA1", P.getBytes(), S.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha256_base32(String P, String S, int c, int dkLen)
    {
        return pbkdf2_base32("HmacSHA256", P.getBytes(), S.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha512_base32(String P, String S, int c, int dkLen)
    {
        return pbkdf2_base32("HmacSHA512", P.getBytes(), S.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha1_base32(String P, String S, int c, int dkLen)
    {
        return pbkdf2_base32("HmacSHA1", P.getBytes(), S.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha256(String P, int c, int dkLen)
    {
        return pbkdf2("HmacSHA256", P.getBytes(), P.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha512(String P, int c, int dkLen)
    {
        return pbkdf2("HmacSHA512", P.getBytes(), P.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha1(String P, int c, int dkLen)
    {
        return pbkdf2("HmacSHA1", P.getBytes(), P.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha256_base32(String P, int c, int dkLen)
    {
        return pbkdf2_base32("HmacSHA256", P.getBytes(), P.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha512_base32(String P, int c, int dkLen)
    {
        return pbkdf2_base32("HmacSHA512", P.getBytes(), P.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha1_base32(String P, int c, int dkLen)
    {
        return pbkdf2_base32("HmacSHA1", P.getBytes(), P.getBytes(), c, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha256(String P, int dkLen)
    {
        return pbkdf2_sha256(P, ITERATION_COUNT_1K, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha512(String P, int dkLen)
    {
        return pbkdf2_sha512(P, ITERATION_COUNT_1K, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha1(String P, int dkLen)
    {
        return pbkdf2_sha1(P, ITERATION_COUNT_1K, dkLen);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha256_base32(String P, int dkLen)
    {
        return pbkdf2_sha256_base32(P, ITERATION_COUNT_1K, dkLen);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha512_base32(String P, int dkLen)
    {
        return pbkdf2_sha512_base32(P, ITERATION_COUNT_1K, dkLen);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha1_base32(String P, int dkLen)
    {
        return pbkdf2_sha1_base32(P, ITERATION_COUNT_1K, dkLen);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha256(String P)
    {
        return pbkdf2_sha256(P, ITERATION_COUNT_1K, 64);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha512(String P)
    {
        return pbkdf2_sha512(P, ITERATION_COUNT_1K, 64);
    }

    @SneakyThrows
    public static byte[]
    pbkdf2_sha1(String P)
    {
        return pbkdf2_sha1(P, ITERATION_COUNT_1K, 64);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha256_base32(String P)
    {
        return pbkdf2_sha256_base32(P, ITERATION_COUNT_1K, 64);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha512_base32(String P)
    {
        return pbkdf2_sha512_base32(P, ITERATION_COUNT_1K, 64);
    }

    @SneakyThrows
    public static String
    pbkdf2_sha1_base32(String P)
    {
        return pbkdf2_sha1_base32(P, ITERATION_COUNT_1K, 64);
    }

    @SneakyThrows
    public static void main(String[] args) throws Exception
    {
        System.err.println(pbkdf2_sha1_base32("s3cr3t", "saltsalt", ITERATION_COUNT_1000, 32));
    }
}