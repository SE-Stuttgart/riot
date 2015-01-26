package de.uni_stuttgart.riot.usermanagement.security;

import org.apache.commons.lang.StringUtils;

import de.uni_stuttgart.riot.server.commons.config.Configuration;
import de.uni_stuttgart.riot.server.commons.config.ConfigurationKey;
import de.uni_stuttgart.riot.usermanagement.security.exception.PasswordValidationException;

/**
 * This class can be used to check if a password satisfies given requirements.
 * 
 * @author Niklas Schnabel
 */
public class PasswordValidator {

    /** The min length. */
    private final int minLength;

    /** The max length. */
    private final int maxLength;

    /** The min amount numbers. */
    private final int minAmountNumbers;

    /** The min amount lower case letters. */
    private final int minAmountLowerCaseLetters;

    /** The min amount upper case letters. */
    private final int minAmountUpperCaseLetters;

    /** The min amount special characters. */
    private final int minAmountSpecialCharacters;

    /** The allowed special characters. */
    private final String allowedSpecialCharacters;

    /**
     * Creates a new password validator with the following properties:
     * <ul>
     * <li>Minimal password length: 6 characters
     * <li>Maximal password length: 20 characters
     * <li>Amount of needed numbers (0-9): 1
     * <li>Amount of needed lower case letters (a-z): 1
     * <li>Amount of needed upper case letters (A-Z): 1
     * <li>Amount of needed special characters: 1
     * </ul>
     * 
     * Allowed Special characters are: {@value #ALLOWED_SPECIAL_CHARACTERS}.
     *
     * @throws PasswordValidationException
     *             the password validation exception
     */
    public PasswordValidator() throws PasswordValidationException {
        maxLength = checkMaxLength(Configuration.getInt(ConfigurationKey.um_pwValidator_maxLength));
        minLength = checkMinLength(Configuration.getInt(ConfigurationKey.um_pwValidator_minLength));
        minAmountNumbers = checkMinAmountNumbers(Configuration.getInt(ConfigurationKey.um_pwValidator_numberCount));
        minAmountLowerCaseLetters = checkMinAmountLowerCaseLetters(Configuration.getInt(ConfigurationKey.um_pwValidator_lowerCaseCharsCount));
        minAmountUpperCaseLetters = checkMinAmountUpperCaseLetters(Configuration.getInt(ConfigurationKey.um_pwValidator_upperCaseCharCount));
        minAmountSpecialCharacters = checkMinAmountSpecialCharacters(Configuration.getInt(ConfigurationKey.um_pwValidator_specialCharsCount));
        allowedSpecialCharacters = checkAllowedSpecialCharacters(Configuration.getString(ConfigurationKey.um_pwValidator_allowedSpecialChars));

        checkCombinedMinAmounts(maxLength, minAmountNumbers, minAmountLowerCaseLetters, minAmountUpperCaseLetters, minAmountSpecialCharacters);
    }

    /**
     * Creates a new password validator.
     *
     * @param minLength
     *            The minimal length of the password
     * @param maxLength
     *            The maximal length of the password
     * @param minAmountNumbers
     *            The minimal amount of numbers (0-9) the password must contain
     * @param minAmountLowerCaseLetters
     *            The minimal amount of lower case letters (a-z) the password must contain
     * @param minAmountUpperCaseLetters
     *            The minimal amount of upper case letters (A-Z) the password must contain
     * @param minAmountSpecialCharacters
     *            The minimal amount of special characters the password must contain
     * @param allowedSpecialCharacters
     *            The allowed special characters. Usable characters are: {@value #ALLOWED_SPECIAL_CHARACTERS}
     * @throws PasswordValidationException
     *             the password validation exception
     */
    public PasswordValidator(int minLength, int maxLength, int minAmountNumbers, int minAmountLowerCaseLetters, int minAmountUpperCaseLetters, int minAmountSpecialCharacters, String allowedSpecialCharacters) throws PasswordValidationException {
        checkCombinedMinAmounts(maxLength, minAmountNumbers, minAmountLowerCaseLetters, minAmountUpperCaseLetters, minAmountSpecialCharacters);

        this.maxLength = checkMaxLength(maxLength);
        this.minLength = checkMinLength(minLength);
        this.minAmountNumbers = checkMinAmountNumbers(minAmountNumbers);
        this.minAmountLowerCaseLetters = checkMinAmountLowerCaseLetters(minAmountLowerCaseLetters);
        this.minAmountUpperCaseLetters = checkMinAmountUpperCaseLetters(minAmountUpperCaseLetters);
        this.minAmountSpecialCharacters = checkMinAmountSpecialCharacters(minAmountSpecialCharacters);
        this.allowedSpecialCharacters = checkAllowedSpecialCharacters(allowedSpecialCharacters);
    }

    /**
     * Check allowed special characters.
     *
     * @param allowedSpecialCharactersParam
     *            the allowed special characters
     * @return the string
     * @throws PasswordValidationException
     *             the password validation exception
     */
    private String checkAllowedSpecialCharacters(String allowedSpecialCharactersParam) throws PasswordValidationException {
        if (!StringUtils.containsOnly(allowedSpecialCharactersParam, Configuration.getString(ConfigurationKey.um_pwValidator_allowedSpecialChars))) {
            throw new PasswordValidationException("The given string contains not allowed chars");
        }
        return allowedSpecialCharactersParam;
    }

    private void checkCombinedMinAmounts(int maxLengthParam, int minAmountNumbersParam, int minAmountLowerCaseLettersParam, int minAmountUpperCaseLettersParam, int minAmountSpecialCharactersParam) throws PasswordValidationException {
        if (maxLengthParam < (minAmountNumbersParam + minAmountLowerCaseLettersParam + minAmountUpperCaseLettersParam + minAmountSpecialCharactersParam)) {
            throw new PasswordValidationException("The given configuration can not validate any password, because the minimal amount of numbers, upper case letters, lower case letters and special characters is bigger than the max length of the password!");
        }
    }

    /**
     * Check max length.
     *
     * @param maxLengthParam
     *            the max length
     * @return the int
     * @throws PasswordValidationException
     *             the password validation exception
     */
    private int checkMaxLength(int maxLengthParam) throws PasswordValidationException {
        if (minLength > maxLengthParam) {
            throw new PasswordValidationException("The maximal length of the password (" + maxLengthParam + ") can not be smaller than minimal length (" + minLength + ")");
        }
        return maxLengthParam;
    }

    /**
     * Check min amount lower case letters.
     *
     * @param minAmountLowerCaseLettersParam
     *            the min amount lower case letters
     * @return the int
     * @throws PasswordValidationException
     *             the password validation exception
     */
    private int checkMinAmountLowerCaseLetters(int minAmountLowerCaseLettersParam) throws PasswordValidationException {
        if (minAmountLowerCaseLettersParam < 0) {
            throw new PasswordValidationException("The minimal amount of lower case characters must be >= 0");
        }
        return minAmountLowerCaseLettersParam;
    }

    /**
     * Check min amount numbers.
     *
     * @param minAmountNumbersParam
     *            the min amount numbers
     * @return the int
     * @throws PasswordValidationException
     *             the password validation exception
     */
    private int checkMinAmountNumbers(int minAmountNumbersParam) throws PasswordValidationException {
        if (minAmountNumbersParam < 0) {
            throw new PasswordValidationException("The minimal amount of numbers must be >= 0");
        }
        return minAmountNumbersParam;
    }

    /**
     * Check min amount special characters.
     *
     * @param minAmountSpecialCharactersParam
     *            the min amount special characters
     * @return the int
     * @throws PasswordValidationException
     *             the password validation exception
     */
    private int checkMinAmountSpecialCharacters(int minAmountSpecialCharactersParam) throws PasswordValidationException {
        if (minAmountSpecialCharactersParam < 0) {
            throw new PasswordValidationException("The minimal amount of special characters must be >= 0");
        }
        return minAmountSpecialCharactersParam;
    }

    /**
     * Check min amount upper case letters.
     *
     * @param minAmountUpperCaseLettersParam
     *            the min amount upper case letters
     * @return the int
     * @throws PasswordValidationException
     *             the password validation exception
     */
    private int checkMinAmountUpperCaseLetters(int minAmountUpperCaseLettersParam) throws PasswordValidationException {
        if (minAmountUpperCaseLettersParam < 0) {
            throw new PasswordValidationException("The minimal amount of lower case characters must be >= 0");
        }
        return minAmountUpperCaseLettersParam;
    }

    /**
     * Check min length.
     *
     * @param minLengthParam
     *            the min length
     * @return the int
     * @throws PasswordValidationException
     *             the password validation exception
     */
    private int checkMinLength(int minLengthParam) throws PasswordValidationException {
        if (minLengthParam > maxLength) {
            throw new PasswordValidationException("The minimal length of the password (" + minLengthParam + ") can not be bigger than maximal length (" + maxLength + ")");
        }
        if (minLengthParam < 0) {
            throw new PasswordValidationException("The minimal length of the password must be >= 0");
        }
        return minLengthParam;
    }

    /**
     * Gets the allowed special characters.
     *
     * @return the allowedSpecialCharacters
     */
    public String getAllowedSpecialCharacters() {
        return allowedSpecialCharacters;
    }

    /**
     * Gets the max length.
     *
     * @return the maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Gets the min amount lower case letters.
     *
     * @return the minAmountLowerCaseLetters
     */
    public int getMinAmountLowerCaseLetters() {
        return minAmountLowerCaseLetters;
    }

    /**
     * Gets the min amount numbers.
     *
     * @return the minAmountNumbers
     */
    public int getMinAmountNumbers() {
        return minAmountNumbers;
    }

    /**
     * Gets the min amount special characters.
     *
     * @return the minAmountSpecialCharacters
     */
    public int getMinAmountSpecialCharacters() {
        return minAmountSpecialCharacters;
    }

    /**
     * Gets the min amount upper case letters.
     *
     * @return the minAmountUpperCaseLetters
     */
    public int getMinAmountUpperCaseLetters() {
        return minAmountUpperCaseLetters;
    }

    /**
     * Gets the min length.
     *
     * @return the minLength
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Validate password with regular expression.
     *
     * @param password
     *            password for validation
     * @return true valid password, false invalid password
     */
    public boolean validate(final String password) {

        int lowerCaselettersCount = 0;
        int upperCaselettersCount = 0;
        int numbersCount = 0;
        int specialCharsCount = 0;

        if (password == null || password.length() < minLength || password.length() > maxLength) {
            return false;
        }

        String allowedChars = Configuration.getString(ConfigurationKey.um_pwValidator_allowedSpecialChars);

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                ++lowerCaselettersCount;
            } else if (Character.isUpperCase(c)) {
                ++upperCaselettersCount;
            } else if (Character.isDigit(c)) {
                ++numbersCount;
            } else if (StringUtils.contains(allowedChars, c)) {
                ++specialCharsCount;
            } else {
                // c is a not allowed character
                return false;
            }

        }
        if (lowerCaselettersCount < minAmountLowerCaseLetters || //
                upperCaselettersCount < minAmountUpperCaseLetters || //
                numbersCount < minAmountNumbers || //
                specialCharsCount < minAmountSpecialCharacters) {
            return false;
        }
        return true;
    }
}
