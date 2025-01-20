package com.example.PayAll_BE.config.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;

public class CryptoUtil {
	private static String SECRET_KEY;

	public static void setSecretKey(String secretKey) {
		SECRET_KEY = secretKey;
	}

	public static String encrypt(String input) throws Exception {
		SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		byte[] encryptedBytes = cipher.doFinal(input.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	public static String decrypt(String encryptedInput) throws Exception {
		SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedInput));
		return new String(decryptedBytes);
	}
}
