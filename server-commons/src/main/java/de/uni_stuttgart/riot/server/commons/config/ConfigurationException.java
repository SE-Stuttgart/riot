package de.uni_stuttgart.riot.server.commons.config;

/**
 * The Class ConfigurationException.
 * 
 * @author Niklas Schnabel
 */
public class ConfigurationException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new configuration exception.
     */
    public ConfigurationException() {
        super();
    }

    /**
     * Instantiates a new configuration exception.
     *
     * @param msg
     *            the msg
     */
    public ConfigurationException(String msg) {
        super(msg);
    }

    /**
     * Instantiates a new configuration exception.
     *
     * @param cause
     *            the cause
     */
    public ConfigurationException(Exception cause) {
        super(cause);
    }
}
