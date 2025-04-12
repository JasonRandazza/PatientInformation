package org.example.patientinformation.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

    private static final int SALT_LENGTH = 16; //128-bit salt
    private static final int ITERATIONS = 65536; // Number of iterations
    private static final int KEY_LENGTH = 256; //256-bit key
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256"; // Hashing algorithm

    // Hash a password and return it with salt
    public static String hashPassword(String password) throws Exception {
        byte[] salt = generateSalt();
        byte[] hash = pbkdf2(password.toCharArray(), salt);

        // Combine salt & hash and encode to string
        byte[] saltAndHash = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
        System.arraycopy(hash, 0, saltAndHash, salt.length, hash.length);

        return Base64.getEncoder().encodeToString(saltAndHash);
    }

    // Verify a password against a stored hash
    public static boolean verifyPassword(String password, String stored) throws Exception {
        byte[] saltAndHash = Base64.getDecoder().decode(stored);
        byte[] salt = new byte[SALT_LENGTH];
        byte[] storedHash = new byte[saltAndHash.length - SALT_LENGTH];

        System.arraycopy(saltAndHash, 0, salt, 0, SALT_LENGTH);
        System.arraycopy(saltAndHash, SALT_LENGTH, storedHash, 0, storedHash.length);

        byte[] computedHash = pbkdf2(password.toCharArray(), salt);

        return slowEquals(storedHash, computedHash);
    }

    // Generate secure random salt
    private static byte[] generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        sr.nextBytes(salt);
        return salt;
    }

    // PBKDF2 hashing function
    private static byte[] pbkdf2(char[] password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    // Compare two byte arrays in constant time to prevent timing attacks
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff ==0;
    }
}
