package de.uni_stuttgart.riot.userManagement.security;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class TokenUtil {
	private static SecureRandom random = new SecureRandom();

	public static String generateToken() {
		return new BigInteger(130, random).toString(32);
	}
}
