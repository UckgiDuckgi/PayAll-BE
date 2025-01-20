package com.example.PayAll_BE.config.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
public class CryptoUtil {
	private static final String SECRET_KEY = "MySecretKey12345"; // 16바이트 키 (128비트)

	// AES 암호화
	public static String encrypt(String input) throws Exception {
		SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		byte[] encryptedBytes = cipher.doFinal(input.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	// AES 복호화
	public static String decrypt(String encryptedInput) throws Exception {
		SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedInput));
		return new String(decryptedBytes);
	}
}
