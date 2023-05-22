package com.example.demo.security.jwt.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.ResourceBundle;

public class SecurityCipher {

	private static final int GCM_TAG_LENGTH = 16;
	private static final int GCM_IV_LENGTH = 12;
	private static final String KEY_VALUE_CIPHER;

	private static Logger log = LoggerFactory.getLogger(SecurityCipher.class);
	private static SecretKeySpec secretKey;

	static {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("application");
		KEY_VALUE_CIPHER = resourceBundle.getString("cipher.secret");
	}

	private SecurityCipher() {
		throw new AssertionError("Static!");
	}

	public static void setKey(String algorithm) {
		MessageDigest sha;
		try {
			byte[] key = KEY_VALUE_CIPHER.getBytes(StandardCharsets.UTF_8);
			sha = MessageDigest.getInstance(algorithm);
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			log.error("Cryptographic algorithm SHA-256 is not available", e);
		}
	}

	public static Cipher setCipher(int mode, String transformation, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
		setKey("SHA-256");

		Cipher cipher = Cipher.getInstance(transformation);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
		cipher.init(mode, secretKey, gcmParameterSpec);

		return cipher;
	}

	public static String encrypt(String strToEncrypt) {
		if (strToEncrypt == null) {
			return null;
		}

		try {
			//setKey("SHA-256");

			byte[] iv = new byte[GCM_IV_LENGTH];
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(iv);

			Cipher cipher = setCipher(Cipher.ENCRYPT_MODE, "AES/GCM/NoPadding", iv);

			//Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			//GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
			//cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
			byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));

			byte[] result = new byte[iv.length + cipherText.length];
			System.arraycopy(iv, 0, result, 0, iv.length);
			System.arraycopy(cipherText, 0, result, iv.length, cipherText.length);

			return Base64.getEncoder().encodeToString(result);
		} catch (Exception e) {
			log.error("Encryption error", e);
		}
		return null;
	}

	public static String decrypt(String strToDecrypt) {
		if (strToDecrypt == null) {
			return null;
		}

		try {
			//setKey("SHA-256");

			byte[] cipherText = Base64.getDecoder().decode(strToDecrypt);
			byte[] iv = new byte[GCM_IV_LENGTH];
			System.arraycopy(cipherText, 0, iv, 0, iv.length);

			Cipher cipher = setCipher(Cipher.DECRYPT_MODE, "AES/GCM/NoPadding", iv);

			//Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			//GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
			//cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
			byte[] result = cipher.doFinal(cipherText, iv.length, cipherText.length - iv.length);

			return new String(result, StandardCharsets.UTF_8);
		} catch (Exception e) {
			log.error("Decryption error", e);
		}
		return null;
	}
}
