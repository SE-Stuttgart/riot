package de.uni_stuttgart.riot.commons.rest.data.config;

/**
 * Enum which contains all keys regarding the central configuration of the server. <br>
 * <br>
 * <b>Every key should use a prefix, which identifies to which module the key belongs. All used prefixes should be listed in the
 * comment!</b> <br>
 * <br>
 * Prefixes:
 * <ul>
 * <li>um: UserManagement</li>
 * <li>cal: Calendar</li>
 * <li>con: Contacts</li>
 * <li>test: Only for testing purposes</li>
 * </ul>
 * 
 * @author Niklas Schnabel
 */
public enum ConfigurationKey {

    /** The Hash iterations. */
    um_hashIterations(Integer.class),

    /** The minimal length of a password. */
    um_pwValidator_minLength(Integer.class),

    /** The maximal length of a password. */
    um_pwValidator_maxLength(Integer.class),

    /** The minimal count a password must contain. */
    um_pwValidator_numberCount(Integer.class),

    /** The minimal amount special characters a password must contain. */
    um_pwValidator_specialCharsCount(Integer.class),

    /** The minimal amount lower case characters a password must contain. */
    um_pwValidator_lowerCaseCharsCount(Integer.class),

    /** The minimal amount upper case characters a password must contain. */
    um_pwValidator_upperCaseCharCount(Integer.class),

    /** The allowed special characters. */
    um_pwValidator_allowedSpecialChars(String.class),

    /** The maximum amount of failed logins, before a user gets locked. */
    um_maxLoginRetries(Integer.class),

    /** The time in seconds the authentication token is valid. */
    um_authTokenValidTime(Integer.class),

    /** Integer key. Used only for testing. */
    test_int(Integer.class),

    /** Long key. Used only for testing. */
    test_long(Long.class),

    /** Boolean key. Used only for testing. */
    test_boolean(Boolean.class),

    /** String key. Used only for testing. */
    test_string(String.class),

    /** Float key. Used only for testing. */
    test_float(Float.class),

    /** Double key. Used only for testing. */
    test_double(Double.class),

    /** Double key that does not exist. Used only for testing. */
    test_nonexistingDouble(Double.class);

    /** The value type. */
    private final Class<?> valueType;

    /**
     * Instantiates a new configuration key.
     *
     * @param valueType
     *            the value type
     */
    private ConfigurationKey(Class<?> valueType) {
        this.valueType = valueType;
    }

    /**
     * Gets the value type.
     *
     * @return the value type
     */
    public Class<?> getValueType() {
        return valueType;
    }
}
