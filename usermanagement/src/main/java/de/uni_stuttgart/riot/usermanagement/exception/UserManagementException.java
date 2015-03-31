package de.uni_stuttgart.riot.usermanagement.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Superclass for all exceptions regarding the user management.
 * 
 * @author Niklas Schnabel
 *
 */
@JsonIgnoreProperties({ "cause", "stackTrace", "suppressed" })
public class UserManagementException extends Exception {

    private static final long serialVersionUID = 1182375069868647185L;

    /**
     * Constructor for UserManagementException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public UserManagementException(String message) {
        super(message);
    }

    /**
     * Constructor for UserManagementException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public UserManagementException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for UserManagementException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public UserManagementException(Throwable cause) {
        super(cause);
    }

    @JsonProperty("errorMessage")
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @JsonProperty("localizedErrorMessage")
    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }

}
