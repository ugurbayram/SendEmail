package com.luxoft.email.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;
/**
 * created by @ubayram
 * 17 June 2019
 */

public class Encrypt {
    private final static Logger LOGGER = Logger.getLogger(Encrypt.class.getName());

    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(String myKey, String algorithm) {
        MessageDigest sha;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, algorithm);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public static String encrypt(String strToEncrypt, String secret, String algorithm) {
        try {
            String cipherInstance = algorithm + "/ECB/PKCS5Padding";
            setKey(secret, algorithm);
            Cipher cipher = Cipher.getInstance(cipherInstance);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            LOGGER.severe("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret, String algorithm) {
        try {
            String cipherInstance = algorithm + "/ECB/PKCS5Padding";
            setKey(secret, algorithm);
            Cipher cipher = Cipher.getInstance(cipherInstance);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            LOGGER.severe("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
