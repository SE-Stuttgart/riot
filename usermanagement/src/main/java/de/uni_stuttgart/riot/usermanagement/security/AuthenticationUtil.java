package de.uni_stuttgart.riot.usermanagement.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.base.Stopwatch;

import org.apache.shiro.crypto.hash.Sha512Hash;

import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class AuthenticationUtil {
    private static SecureRandom random = new SecureRandom();

    /**
     * Generate a new access token.
     * 
     * @return The token
     */
    public static String generateAccessToken() {
        return new BigInteger(130, random).toString(32); // generate 130 bit token
    }

    /**
     * Generate a new salt that can be used for the hashing algorithm.
     * 
     * @return The salt
     */
    public static String generateSalt() {
        return new BigInteger(256, random).toString(32); // generate 256 bit salt
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

    public static void main(String[] args) {
        // generate salt and hashes for the "insertTestValues.sql"

        Collection<UMUser> users = new ArrayList<UMUser>();
        users.add(new UMUser("Yoda", "YodaPW", generateSalt(), 200000));
        users.add(new UMUser("R2D2", "R2D2PW", generateSalt(), 200000));
        users.add(new UMUser("Vader", "VaderPW", generateSalt(), 200000));

        for (UMUser user : users) {
            Stopwatch watch = new Stopwatch();
            watch.start();
            String pw = getHashedString(user.getHashedPassword(), user.getPasswordSalt(), user.getHashIterations());

            System.out.println("Time to hash (" + user.getHashIterations() + " hash iterations): " + watch.elapsed(TimeUnit.MILLISECONDS) + " ms");
            System.out.println("Username: " + user.getUsername());
            System.out.println("Raw PW: " + user.getHashedPassword());
            System.out.println("Salt: " + user.getPasswordSalt());
            System.out.println("Hashed PW: " + pw);
            System.out.println();
        }

    }
}
