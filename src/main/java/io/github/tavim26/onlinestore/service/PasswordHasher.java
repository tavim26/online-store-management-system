package io.github.tavim26.onlinestore.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hashes passwords with PBKDF2-HMAC-SHA256 and a random salt.
 * Stored format: iterations:salt:hash (salt and hash in Base64).
 */
final class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    static String hash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = derive(password, salt, ITERATIONS, HASH_BITS);
        Base64.Encoder base64 = Base64.getEncoder();
        return ITERATIONS + ":" + base64.encodeToString(salt) + ":" + base64.encodeToString(hash);
    }

    static boolean verify(String password, String stored) {
        try {
            String[] parts = stored.split(":");
            if (parts.length != 3) {
                return false;
            }
            int iterations = Integer.parseInt(parts[0]);
            Base64.Decoder base64 = Base64.getDecoder();
            byte[] salt = base64.decode(parts[1]);
            byte[] expected = base64.decode(parts[2]);
            byte[] actual = derive(password, salt, iterations, expected.length * 8);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException e) {
            return false; // malformed stored hash
        }
    }

    private static byte[] derive(String password, byte[] salt, int iterations, int bits) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, bits);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 is not available in this JVM", e);
        } finally {
            spec.clearPassword();
        }
    }
}