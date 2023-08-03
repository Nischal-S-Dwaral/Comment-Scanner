package uos.msc.project.documentation.coverage.comments.scanner.utils;

import uos.msc.project.documentation.coverage.comments.scanner.exceptions.InternalServerError;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utility class that provides methods for generating a secret key and performing AES encryption and decryption.
 */
public class EncryptionUtils {

    /**
     * The secret key used for encryption and decryption.
     */
    private static SecretKeySpec secretKey;
    /**
     * The encryption algorithm used (AES - Advanced Encryption Standard).
     */
    private static final String ALGORITHM = "AES";

    /**
     * Prepares the secret key from the provided string.
     *
     * @param myKey The string used to generate the secret key.
     * @throws InternalServerError if an internal error occurs during the preparation of the secret key.
     */
    private static void prepareSecreteKey(String myKey) {
        MessageDigest sha;
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerError(e.getMessage());
        }
    }

    /**
     * Encrypts the provided string using the specified secret key.
     *
     * @param strToEncrypt The string to be encrypted.
     * @param secret The secret key used for encryption.
     * @return The Base64-encoded encrypted representation of the input string.
     * @throws InternalServerError if an error occurs during the encryption process.
     */
    public static String encrypt(String strToEncrypt, String secret) {
        try {
            prepareSecreteKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new InternalServerError("Error while encrypting: "+e.getMessage());
        }
    }

    /**
     * Decrypts the provided string using the specified secret key.
     *
     * @param strToDecrypt The string to be decrypted (Base64-encoded).
     * @param secret The secret key used for decryption.
     * @return The decrypted string.
     * @throws InternalServerError if an error occurs during the decryption process.
     */
    public static String decrypt(String strToDecrypt, String secret) {
        try {
            prepareSecreteKey(secret);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            throw new InternalServerError("Error while decrypting: "+e.getMessage());
        }
    }
}
