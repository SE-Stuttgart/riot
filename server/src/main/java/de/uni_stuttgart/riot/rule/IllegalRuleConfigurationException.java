package de.uni_stuttgart.riot.rule;

/**
 * This exception is thrown when trying to launch or shutdown a rule with an invalid configuration.
 * 
 * @author Philipp Keck
 */
public class IllegalRuleConfigurationException extends Exception {

    private static final long serialVersionUID = 1182375069868647185L;

    /**
     * Constructor for IllegalRuleConfigurationException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public IllegalRuleConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructor for IllegalRuleConfigurationException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public IllegalRuleConfigurationException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for IllegalRuleConfigurationException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public IllegalRuleConfigurationException(Throwable cause) {
        super(cause);
    }

}
