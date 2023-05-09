package com.example.demo.security.jwt.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.ResourceBundle;

public class SecurityCipher {

	private static final int GCM_TAG_LENGTH = 16;
	private static final int GCM_IV_LENGTH = 12;
	private static final String KEY_VALUE_CIPHER;

	private static SecretKeySpec secretKey;

	static {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("application");
		KEY_VALUE_CIPHER = resourceBundle.getString("cipher.secret");
	}

	private SecurityCipher() {
		throw new AssertionError("Static!");
	}

	public static void setKey() {
		MessageDigest sha;
		try {
			byte[] key = KEY_VALUE_CIPHER.getBytes(StandardCharsets.UTF_8);
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static String encrypt(String strToEncrypt) {
		if (strToEncrypt == null) {
			return null;
		}

		try {
			setKey();

			byte[] iv = new byte[GCM_IV_LENGTH];
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(iv);

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
			byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));

			byte[] result = new byte[iv.length + cipherText.length];
			System.arraycopy(iv, 0, result, 0, iv.length);
			System.arraycopy(cipherText, 0, result, iv.length, cipherText.length);

			return Base64.getEncoder().encodeToString(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(String strToDecrypt) {
		if (strToDecrypt == null) {
			return null;
		}

		try {
			setKey();

			byte[] cipherText = Base64.getDecoder().decode(strToDecrypt);
			byte[] iv = new byte[GCM_IV_LENGTH];
			System.arraycopy(cipherText, 0, iv, 0, iv.length);

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
			byte[] result = cipher.doFinal(cipherText, iv.length, cipherText.length - iv.length);

			return new String(result, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
