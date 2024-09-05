package code.encryption;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class crypt_and_decrypt {

    /**
     * Decrypts an AES encrypted text using the given key.
     *
     * @param encryptedText The AES encrypted text to be decrypted.
     * @param key The key used for decryption.
     * @return The decrypted text.
     * @throws Exception If an error occurs during decryption.
     */
    public static String decryptAES(String encryptedText, String key) throws Exception {
        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decryptedByte = cipher.doFinal(Base64.getDecoder().decode(encryptedText));

        return new String(decryptedByte);
    }

    /**
     * Encrypts a string using AES encryption with the given secret key.
     *
     * @param strToEncrypt The string to be encrypted.
     * @param secret The secret key used for encryption.
     * @return The encrypted string.
     */
    public static String encrypt(String strToEncrypt, String secret) {
        try {
            Key key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Erreur lors du chiffrement: " + e);
        }
        return null;
    }

}
