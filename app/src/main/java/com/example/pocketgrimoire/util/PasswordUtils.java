package com.example.pocketgrimoire.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * A class for handling password salting and hashing using PBKDF2
 * This class provides static methods to generate cryptographically secure salts
 * hash passwords, and verify passwords against their stored hash and salt
 */
public final class PasswordUtils {

    // PBKDF2 Hashing Algorithm
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    // The size of the salt in bytes
    private static final int SALT_SIZE = 16;
    // A higher number of iterations of the hashing algorithm increases the time it takes to
    // brute-force the hash
    // 65536 is a widely recommended minimum # of iterations using PBKDF2
    private static final int ITERATIONS = 65536;
    // The length of the final hash key in bits
    private static final int KEY_LENGTH = 128;

    /**
     * Private constructor to prevent instantiation of this utility class
     */
    private PasswordUtils() {
        // This class should not be instantiated
    }

    /**
     * Generates a cryptographically secure random salt
     *
     * @return A Base64 encoded string representation of the salt
     */
    public static String generateSalt() {
        // SecureRandom is the Java class for cryptographically secure RNG (Random Number Generation)
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_SIZE];
        random.nextBytes(salt);
        // The salt is Base64 encoded to safely store it as a String in the database.
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password with a given salt using the PBKDF2 algorithm
     *
     * @param password The plain-text password to hash
     * @param salt     The Base64 encoded salt string to use for hashing
     * @return A Base64 encoded string representation of the hashed password
     */
    public static String hashPassword(String password, String salt) {
        try {
            // Decode the Base64 salt back into a byte array
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            // PBEKeySpec defines the parameters required to use PBKDF2
            KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            // Get an instance of the SecretKeyFactory for our chosen algorithm
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            // Generate the hash
            byte[] hash = factory.generateSecret(spec).getEncoded();
            // Encode the resulting hash in Base64 to store it as a String
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // First error - algorithm not available: https://developer.android.com/reference/java/security/NoSuchAlgorithmException
            // Second error - secret key is invalid: https://developer.android.com/reference/java/security/NoSuchAlgorithmException
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies a password against a stored hash and salt
     *
     * @param providedPassword The plain-text password to verify
     * @param storedHash       The Base64 encoded hash from the database
     * @param storedSalt       The Base64 encoded salt from the database
     * @return true if the password is correct, false otherwise
     */
    public static boolean verifyPassword(String providedPassword, String storedHash, String storedSalt) {
        // Re-hash the provided password using the same salt
        String newHash = hashPassword(providedPassword, storedSalt);
        // Compare the newly generated hash with the one stored in the database
        // MessageDigest.isEqual() is a constant-time byte comparison method to prevent timing attacks
        return MessageDigest.isEqual(newHash.getBytes(), storedHash.getBytes());
    }
}
