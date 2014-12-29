package de.uni_stuttgart.riot.usermanagement.security;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.apache.shiro.crypto.hash.Sha512Hash;

/**
 * Helper class for authentication (cryptography).
 * 
 * @author Niklas Schnabel
 *
 */
public class AuthenticationUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int ACCESS_TOKEN_BITS = 130;
    private static final int SALT_BITS = 130;
    private static final int RADIX = 32;

    private AuthenticationUtil() {
    }

    /**
     * Generate a new access token.
     * 
     * @return The token
     */
    public static String generateAccessToken() {
        return new BigInteger(ACCESS_TOKEN_BITS, RANDOM).toString(RADIX); // generate 130 bit token
    }

    /**
     * Generate a new salt that can be used for the hashing algorithm.
     * 
     * @return The salt
     */
    public static String generateSalt() {
        return new BigInteger(SALT_BITS, RANDOM).toString(RADIX); // generate 256 bit salt
    }

    /**
     * Hash a given value with the given salt with the given number of iterations.
     * 
     * @param valueToHash
     *            The value to hash
     * @param salt
     *            The salt to use
     * @param hashIterations
     *            The number of times to iterate
     * @return The hased value (Base64 encoded)
     */
    public static String getHashedString(String valueToHash, String salt, int hashIterations) {
        Sha512Hash passwordHash = new Sha512Hash(valueToHash, salt, hashIterations);
        return passwordHash.toBase64(); // using Base64 to get a more compact form of the hash
    }

}
