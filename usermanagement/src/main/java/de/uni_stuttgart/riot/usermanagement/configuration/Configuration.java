package de.uni_stuttgart.riot.usermanagement.configuration;

/**
 * Draft of a configuration file for the user management. Everything configurable in the user management should be configured here.
 * 
 * TODO Later the configuration could be read from a properties-file or from the database, but in the beginning it will be hard coded in
 * this file. It would be also good if there was a central configuration for the whole server project.
 */
public class Configuration {

    /*
     * Static variables with the standard values. These variables should later be replaced by a properties-file or database entries.
     */

    /** To use the "/" or the "[" or the "]" character the character needs to be escaped (Example: \/). */
    private static final String PASSWORD_VALIDATOR_ALLOWED_SPECIAL_CHARACTERS = "\\]\\[?\\/<~#`'!@$%^&§*()+-=}|:;,>'{";

    /** The standard minimal length of the password. */
    private static final int PASSWORD_VALIDATOR_MIN_LENGTH = 6;

    /** The standard maximal length of the password. */
    private static final int PASSWORD_VALIDATOR_MAX_LENGTH = 20;

    /** The standard count of numbers which must be present in a password. */
    private static final int PASSWORD_VALIDATOR_NUMBER_COUNT = 1;

    /** The standard count of special characters which must be present in a password. */
    private static final int PASSWORD_VALIDATOR_SPECIAL_CHARS_COUNT = 1;

    /** The standard count of lower case characters which must be present in a password. */
    private static final int PASSWORD_VALIDATOR_LOWERCASE_CHARS_COUNT = 1;

    /** The standard count of upper case characters which must be present in a password. */
    private static final int PASSWORD_VALIDATOR_UPPERCASE_CHARS_COUNT = 1;

    /*
     * The actual variables, which contain the configured value
     */

    /** The password validation min password length. */
    private static int passwordValidationMinPasswordLength = PASSWORD_VALIDATOR_MIN_LENGTH;

    /** The password validation max password length. */
    private static int passwordValidationMaxPasswordLength = PASSWORD_VALIDATOR_MAX_LENGTH;

    /** The password validationmin amount numbers. */
    private static int passwordValidationminAmountNumbers = PASSWORD_VALIDATOR_NUMBER_COUNT;

    /** The min amount lower case letters. */
    private static int passwordValidationMinAmountLowerCaseLetters = PASSWORD_VALIDATOR_LOWERCASE_CHARS_COUNT;

    /** The min amount upper case letters. */
    private static int passwordValidationMinAmountUpperCaseLetters = PASSWORD_VALIDATOR_UPPERCASE_CHARS_COUNT;

    /** The min amount special characters. */
    private static int passwordValidationMinAmountSpecialCharacters = PASSWORD_VALIDATOR_SPECIAL_CHARS_COUNT;

    /** The allowed special characters. */
    private static String passwordValidationAllowedSpecialCharacters = PASSWORD_VALIDATOR_ALLOWED_SPECIAL_CHARACTERS;

    /** The allowed special characters. */
    private String allowedSpecialCharacters;

    /**
     * Instantiates a new configuration.
     */
    private Configuration() {

    }

    /*
     * Getter and setter for the configured values
     */

    public String getAllowedSpecialCharacters() {
        return allowedSpecialCharacters;
    }

    public void setAllowedSpecialCharacters(String allowedSpecialCharacters) {
        this.allowedSpecialCharacters = allowedSpecialCharacters;
    }

    /**
     * Gets the password validation min password length.
     *
     * @return the password validation min password length
     */
    public static int getPasswordValidationMinPasswordLength() {
        return passwordValidationMinPasswordLength;
    }

    /**
     * Sets the password validation min password length.
     *
     * @param passwordValidationMinPasswordLength
     *            the new password validation min password length
     */
    public static void setPasswordValidationMinPasswordLength(int passwordValidationMinPasswordLength) {
        Configuration.passwordValidationMinPasswordLength = passwordValidationMinPasswordLength;
    }

    /**
     * Gets the password validation max password length.
     *
     * @return the password validation max password length
     */
    public static int getPasswordValidationMaxPasswordLength() {
        return passwordValidationMaxPasswordLength;
    }

    /**
     * Sets the password validation max password length.
     *
     * @param passwordValidationMaxPasswordLength
     *            the new password validation max password length
     */
    public static void setPasswordValidationMaxPasswordLength(int passwordValidationMaxPasswordLength) {
        Configuration.passwordValidationMaxPasswordLength = passwordValidationMaxPasswordLength;
    }

    /**
     * Gets the password validationmin amount numbers.
     *
     * @return the password validationmin amount numbers
     */
    public static int getPasswordValidationMinAmountNumbers() {
        return passwordValidationminAmountNumbers;
    }

    /**
     * Sets the password validationmin amount numbers.
     *
     * @param passwordValidationminAmountNumbers
     *            the new password validationmin amount numbers
     */
    public static void setPasswordValidationminAmountNumbers(int passwordValidationminAmountNumbers) {
        Configuration.passwordValidationminAmountNumbers = passwordValidationminAmountNumbers;
    }

    /**
     * Gets the password validation min amount lower case letters.
     *
     * @return the password validation min amount lower case letters
     */
    public static int getPasswordValidationMinAmountLowerCaseLetters() {
        return passwordValidationMinAmountLowerCaseLetters;
    }

    /**
     * Sets the password validation min amount lower case letters.
     *
     * @param passwordValidationMinAmountLowerCaseLetters
     *            the new password validation min amount lower case letters
     */
    public static void setPasswordValidationMinAmountLowerCaseLetters(int passwordValidationMinAmountLowerCaseLetters) {
        Configuration.passwordValidationMinAmountLowerCaseLetters = passwordValidationMinAmountLowerCaseLetters;
    }

    /**
     * Gets the password validation min amount upper case letters.
     *
     * @return the password validation min amount upper case letters
     */
    public static int getPasswordValidationMinAmountUpperCaseLetters() {
        return passwordValidationMinAmountUpperCaseLetters;
    }

    /**
     * Sets the password validation min amount upper case letters.
     *
     * @param passwordValidationMinAmountUpperCaseLetters
     *            the new password validation min amount upper case letters
     */
    public static void setPasswordValidationMinAmountUpperCaseLetters(int passwordValidationMinAmountUpperCaseLetters) {
        Configuration.passwordValidationMinAmountUpperCaseLetters = passwordValidationMinAmountUpperCaseLetters;
    }

    /**
     * Gets the password validation min amount special characters.
     *
     * @return the password validation min amount special characters
     */
    public static int getPasswordValidationMinAmountSpecialCharacters() {
        return passwordValidationMinAmountSpecialCharacters;
    }

    /**
     * Sets the password validation min amount special characters.
     *
     * @param passwordValidationMinAmountSpecialCharacters
     *            the new password validation min amount special characters
     */
    public static void setPasswordValidationMinAmountSpecialCharacters(int passwordValidationMinAmountSpecialCharacters) {
        Configuration.passwordValidationMinAmountSpecialCharacters = passwordValidationMinAmountSpecialCharacters;
    }

    /**
     * Gets the password validation allowed special characters.
     *
     * @return the password validation allowed special characters
     */
    public static String getPasswordValidationAllowedSpecialCharacters() {
        return passwordValidationAllowedSpecialCharacters;
    }

    /**
     * Sets the password validation allowed special characters.
     *
     * @param passwordValidationAllowedSpecialCharacters
     *            the new password validation allowed special characters
     */
    public static void setPasswordValidationAllowedSpecialCharacters(String passwordValidationAllowedSpecialCharacters) {
        Configuration.passwordValidationAllowedSpecialCharacters = passwordValidationAllowedSpecialCharacters;
    }

}
