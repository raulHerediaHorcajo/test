package com.example.demo.unit.security.jwt.util;

import com.example.demo.security.jwt.util.SecurityCipher;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import javax.crypto.Cipher;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SecurityCipherUnitTest {

    /*@Test
    void decrypt_givenEncryptedExample_ShouldSucceed() {
        //Constructor estatico
        //Fallo en setKey sha-256 nosuchAlgoritmic
        //encrypt
        //encrypt fallo
        //encrypt null
        //decrypt
        //decrypt fallo
        //decrypt null
    }

    @Test
    void decrypt_givenEncryptedExample_ShouldSucceed() {
        String example = "afasfdafafa=";
        String encodedExample = SecurityCipher.encrypt(example);

        String result = SecurityCipher.decrypt(encodedExample);

        assertThat(result).isNotNull();
        assertThat(result.length()).isEqualTo(48);
    }

    @Test
    void encrypt_givenExample_ShouldSucceed() {
        String example = "afasfdafafa=";

        String result = SecurityCipher.encrypt(example);

        assertThat(result).isNotNull();
        assertThat(result.length()).isEqualTo(48);
    }*/

    /*@Test
    void testConstructor() throws NoSuchMethodException, InstantiationException, IllegalAccessException {
        Constructor<SecurityCipher> constructor = SecurityCipher.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
            .isInstanceOf(InvocationTargetException.class)
            .hasRootCauseInstanceOf(AssertionError.class)
            .hasRootCauseMessage("Static!");
    }

    @Test
    void testEncryptDecrypt() {
        String originalText = "Hello, World!";
        String encryptedText = SecurityCipher.encrypt(originalText);
        String decryptedText = SecurityCipher.decrypt(encryptedText);

        assertEquals(originalText, decryptedText);
    }

    @Test
    void testEncryptWithNullInput() {
        assertThat(SecurityCipher.encrypt(null)).isNull();
    }

    @Test
    void testEncryptWithEmptyString() {
        assertThat(SecurityCipher.encrypt(""))
            .isNotNull()
            .isEqualTo("");
    }

    @Test
    void testDecryptWithNullInput() {
        assertThat(SecurityCipher.decrypt(null)).isNull();
    }

    @Test
    void testDecryptWithEmptyString() {
        assertThat(SecurityCipher.decrypt(""))
            .isNotNull()
            .isEqualTo("");
    }

    @Test
    void testEncryptException() throws Exception {
        Cipher mockCipher = spy(Cipher.getInstance("AES/GCM/NoPadding"));

        //when(mockCipher.doFinal(any(byte[].class))).thenThrow(new Exception("Encryption error"));
        doThrow(new Exception("Encryption error")).when(mockCipher).doFinal(any(byte[].class));

        String encryptedText = SecurityCipher.encrypt("Test");

        assertThat(encryptedText).isNull();
    }

    *//*@Test
    void testDecryptException() throws Exception {
        Cipher mockCipher = mock(Cipher.class);
        when(mockCipher.doFinal(any(byte[].class))).thenThrow(new Exception("Decryption error"));

        SecurityCipher.setKey();
        SecurityCipher cipherSpy = Mockito.spy(SecurityCipher.class);
        Mockito.doReturn(mockCipher).when(cipherSpy).getCipherInstance();

        String decryptedText = cipherSpy.decrypt("Test");

        Assertions.assertNull(decryptedText);
    }*//*



    @Test
    void testSetKeyNoSuchAlgorithmException() throws NoSuchFieldException, IllegalAccessException {
        Logger logger = mock(Logger.class);
        Field logField = SecurityCipher.class.getDeclaredField("log");
        logField.setAccessible(true);
        logField.set(null, logger);

        SecurityCipher.setKey("invalidAlgorithm");

        ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
        verify(logger).error(eq("Cryptographic algorithm SHA-256 is not available"), captor.capture());
        assertThat(captor.getValue())
            .isNotNull()
            .isInstanceOf(NoSuchAlgorithmException.class);
    }

    @Test
    void testSetKey() throws NoSuchAlgorithmException {
    }*/
}
