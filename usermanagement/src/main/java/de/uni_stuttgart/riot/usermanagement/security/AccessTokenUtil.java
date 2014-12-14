package de.uni_stuttgart.riot.usermanagement.security;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class AccessTokenUtil {
    private static SecureRandom random = new SecureRandom();

    public static String generateToken() {
        return new BigInteger(130, random).toString(32);
    }
}
