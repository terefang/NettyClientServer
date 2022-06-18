package com.github.terefang.ncs.common.passwd;

import com.github.terefang.ncs.common.util.B64;
import com.github.terefang.ncs.common.util.PasswdUtil;
import lombok.SneakyThrows;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class SRP6Crypt
{
	public static final String SRP6_CRYPT_PREFIX = "{srp6}";
	public static final void main(String[] args) throws Exception
	{
		System.out.println(SRP6Crypt.generate(MessageDigest.getInstance("SHA-256"), "admin:s3cr3t", new byte[8]));
		System.out.println(SRP6Crypt.generate(MessageDigest.getInstance("SHA-256"), "admin:s3cr3t", 8));
		System.out.println(SRP6Crypt.generate(MessageDigest.getInstance("SHA-256"), "s3cr3t", new byte[8]));
		System.out.println(SRP6Crypt.generate(MessageDigest.getInstance("SHA-256"), "s3cr3t", 8));
	}
	
	public static boolean checkPassword( String srpString, String password) throws Exception
	{
		return checkPassword(srpString, password.toCharArray());
	}
	
	public static boolean checkPassword( String srpString, char[] password) throws Exception
	{
		if(!srpString.startsWith(SRP6_CRYPT_PREFIX))
		{
			return false;
		}
		String[] p = srpString.substring(6).split("\\$",3);
		MessageDigest md = MessageDigest.getInstance(p[0]);
		byte[] salt = PasswdUtil.fromHex(p[1].toCharArray());
		
		return generate(md, new String(password), salt).equals(srpString);
	}

	@SneakyThrows
	public static String generate(String password)
	{
		return generate(password, B64.getRandomSalt(8).getBytes(StandardCharsets.US_ASCII));
	}

	@SneakyThrows
	public static String generate(String password, byte[] salt)
	{
		String[] u = password.split(":", 2);
		return generate(MessageDigest.getInstance("SHA-256"), (u.length==1 ? null : u[0].toCharArray()), (u.length==1 ? u[0].toCharArray() : u[1].toCharArray()), salt);
	}

	public static String generate(MessageDigest messageDigest,  String password, byte[] salt)
	{
		String[] u = password.split(":", 2);
		return generate(messageDigest, (u.length==1 ? null : u[0].toCharArray()), (u.length==1 ? u[0].toCharArray() : u[1].toCharArray()), salt);
	}

	public static String generate(MessageDigest messageDigest,  String password, int strong ) throws Exception
	{
		return generate(messageDigest, password, SecureRandom.getInstanceStrong(), strong);
	}
	
	public static String generate(MessageDigest messageDigest,  String password, SecureRandom random, int strong ) throws Exception
	{
		String[] u = password.split(":", 2);
		byte[] salt = random.generateSeed(strong);
		return generate(messageDigest, (u.length==1 ? null : u[0].toCharArray()), (u.length==1 ? u[0].toCharArray() : u[1].toCharArray()), salt);
	}
	
	public static String generate(MessageDigest messageDigest, char[] identity,  char[] password, byte[] salt)
	{
		return SRP6_CRYPT_PREFIX+
				messageDigest.getAlgorithm()
				+"$"+PasswdUtil.toHex(salt)
				+"$"+PasswdUtil.toHex(calculateV(messageDigest, salt, (identity==null ? null : new String(identity).getBytes(StandardCharsets.UTF_8)), new String(password).getBytes(StandardCharsets.UTF_8)));
	}
	
	
	private static BigInteger ZERO = BigInteger.valueOf(0);
	private static BigInteger ONE = BigInteger.valueOf(1);
	
	public static BigInteger calculateK(MessageDigest messageDigest, BigInteger N, BigInteger g)
	{
		return hashPaddedPair(messageDigest, N, N, g);
	}
	
	public static BigInteger calculateU(MessageDigest messageDigest, BigInteger N, BigInteger A, BigInteger B)
	{
		return hashPaddedPair(messageDigest, N, A, B);
	}
	
	public static BigInteger calculateX(MessageDigest messageDigest, BigInteger N, byte[] salt, byte[] identity, byte[] password)
	{
		return new BigInteger(1, calculateV(messageDigest, salt, identity, password));
	}
	
	public static byte[] calculateV(MessageDigest messageDigest, byte[] salt, byte[] identity, byte[] password)
	{
		byte[] output = new byte[messageDigest.getDigestLength()];
		
		if(identity != null)
		{
			messageDigest.update(identity, 0, identity.length);
			messageDigest.update((byte) ':');
		}
		messageDigest.update(password, 0, password.length);
		output = messageDigest.digest();
		
		messageDigest.update(salt, 0, salt.length);
		messageDigest.update(output, 0, output.length);
		output = messageDigest.digest();
		
		return output;
	}
	
	public static BigInteger generatePrivateValue(MessageDigest messageDigest, BigInteger N, BigInteger g, SecureRandom random)
	{
		int minBits = Math.min(256, N.bitLength() / 2);
		BigInteger min = ONE.shiftLeft(minBits - 1);
		BigInteger max = N.subtract(ONE);
		
		return createRandomInRange(min, max, random);
	}
	
	public static BigInteger validatePublicValue(BigInteger N, BigInteger val)
			throws Exception
	{
		val = val.mod(N);
		
		// Check that val % N != 0
		if (val.equals(ZERO))
		{
			throw new Exception("Invalid public value: 0");
		}
		
		return val;
	}
	
	/**
	 * Computes the client evidence message (M1) according to the standard routine:
	 * M1 = H( A | B | S )
	 * @param messageDigest The MessageDigest used as the hashing function H
	 * @param N Modulus used to get the pad length
	 * @param A The public client value
	 * @param B The public server value
	 * @param S The secret calculated by both sides
	 * @return M1 The calculated client evidence message
	 */
	public static BigInteger calculateM1(MessageDigest messageDigest, BigInteger N, BigInteger A, BigInteger B, BigInteger S) {
		BigInteger M1 = hashPaddedTriplet(messageDigest,N,A,B,S);
		return M1;
	}
	
	/**
	 * Computes the server evidence message (M2) according to the standard routine:
	 * M2 = H( A | M1 | S )
	 * @param messageDigest The MessageDigest used as the hashing function H
	 * @param N Modulus used to get the pad length
	 * @param A The public client value
	 * @param M1 The client evidence message
	 * @param S The secret calculated by both sides
	 * @return M2 The calculated server evidence message
	 */
	public static BigInteger calculateM2(MessageDigest messageDigest, BigInteger N, BigInteger A, BigInteger M1, BigInteger S){
		BigInteger M2 = hashPaddedTriplet(messageDigest,N,A,M1,S);
		return M2;
	}
	
	/**
	 * Computes the final Key according to the standard routine: Key = H(S)
	 * @param messageDigest The MessageDigest used as the hashing function H
	 * @param N Modulus used to get the pad length
	 * @param S The secret calculated by both sides
	 * @return the final Key value.
	 */
	public static BigInteger calculateKey(MessageDigest messageDigest, BigInteger N, BigInteger S) {
		int padLength = (N.bitLength() + 7) / 8;
		byte[] _S = getPadded(S,padLength);
		messageDigest.update(_S, 0, _S.length);
		
		byte[] output = new byte[messageDigest.getDigestLength()];
		output = messageDigest.digest();
		return new BigInteger(1, output);
	}
	
	private static BigInteger hashPaddedTriplet(MessageDigest messageDigest, BigInteger N, BigInteger n1, BigInteger n2, BigInteger n3){
		int padLength = (N.bitLength() + 7) / 8;
		
		byte[] n1_bytes = getPadded(n1, padLength);
		byte[] n2_bytes = getPadded(n2, padLength);
		byte[] n3_bytes = getPadded(n3, padLength);
		
		messageDigest.update(n1_bytes, 0, n1_bytes.length);
		messageDigest.update(n2_bytes, 0, n2_bytes.length);
		messageDigest.update(n3_bytes, 0, n3_bytes.length);
		
		byte[] output = messageDigest.digest();
		
		return new BigInteger(1, output);
	}
	
	private static BigInteger hashPaddedPair(MessageDigest messageDigest, BigInteger N, BigInteger n1, BigInteger n2)
	{
		int padLength = (N.bitLength() + 7) / 8;
		
		byte[] n1_bytes = getPadded(n1, padLength);
		byte[] n2_bytes = getPadded(n2, padLength);
		
		messageDigest.update(n1_bytes, 0, n1_bytes.length);
		messageDigest.update(n2_bytes, 0, n2_bytes.length);
		
		byte[] output = messageDigest.digest();
		
		return new BigInteger(1, output);
	}
	
	private static byte[] getPadded(BigInteger n, int length)
	{
		byte[] bs = asUnsignedByteArray(n);
		if (bs.length < length)
		{
			byte[] tmp = new byte[length];
			System.arraycopy(bs, 0, tmp, length - bs.length, bs.length);
			bs = tmp;
		}
		return bs;
	}
	
	public static byte[] asUnsignedByteArray(
			BigInteger value)
	{
		byte[] bytes = value.toByteArray();
		
		if (bytes[0] == 0)
		{
			byte[] tmp = new byte[bytes.length - 1];
			
			System.arraycopy(bytes, 1, tmp, 0, tmp.length);
			
			return tmp;
		}
		
		return bytes;
	}
	
	private static final int MAX_ITERATIONS = 1000;
	
	public static BigInteger createRandomInRange(
			BigInteger      min,
			BigInteger      max,
			SecureRandom    random)
	{
		int cmp = min.compareTo(max);
		if (cmp >= 0)
		{
			if (cmp > 0)
			{
				throw new IllegalArgumentException("'min' may not be greater than 'max'");
			}
			
			return min;
		}
		
		if (min.bitLength() > max.bitLength() / 2)
		{
			return createRandomInRange(ZERO, max.subtract(min), random).add(min);
		}
		
		for (int i = 0; i < MAX_ITERATIONS; ++i)
		{
			BigInteger x = new BigInteger(max.bitLength(), random);
			if (x.compareTo(min) >= 0 && x.compareTo(max) <= 0)
			{
				return x;
			}
		}
		
		// fall back to a faster (restricted) method
		return new BigInteger(max.subtract(min).bitLength() - 1, random).add(min);
	}
}

