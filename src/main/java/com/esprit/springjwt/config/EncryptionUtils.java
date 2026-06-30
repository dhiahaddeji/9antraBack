package com.esprit.springjwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
@Service
public class EncryptionUtils {
    private static final String ALGORITHM = "AES";

    //@Value("${encryption.key}")
    private static  String STATIC_KEY="1234567890123456" ;

    // Method to generate a new AES key
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // 128 bits is 16 bytes
        return keyGen.generateKey();
    }

    // Method to encrypt data using AES
    public static String encrypt(String data, SecretKey secretKey) throws Exception {
        // Create a SecretKeySpec from the secretKey
        SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);

        // Create Cipher instance and initialize it for encryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // Encrypt the data
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // Encode the encrypted bytes to Base64 for safe transmission
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Method to decrypt AES encrypted data
    public static String decrypt(String encryptedData, SecretKey secretKey) throws Exception {
        // Create a SecretKeySpec from the secretKey
        SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);

        // Create Cipher instance and initialize it for decryption
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Decode Base64 encoded encrypted data
        byte[] decoded = Base64.getDecoder().decode(encryptedData);

        // Decrypt the data
        byte[] decrypted = cipher.doFinal(decoded);

        // Return the decrypted data as a UTF-8 string
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // Method to encrypt the session-based key with the static key
    public static String encryptKey(SecretKey sessionKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec(STATIC_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(sessionKey.getEncoded());
        return Base64.getEncoder().encodeToString(encrypted);
    }

}
