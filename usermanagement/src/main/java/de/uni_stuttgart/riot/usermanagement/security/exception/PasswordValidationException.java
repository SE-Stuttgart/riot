package de.uni_stuttgart.riot.usermanagement.security.exception;

import de.uni_stuttgart.riot.usermanagement.exception.ErrorCodes;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;

/**
 * The Class PasswordValidationException.
 *
 * @author Niklas Schnabel
 */
public class PasswordValidationException extends UserManagementException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new password validation exception.
     *
     * @param message
     *            the message
     */
    public PasswordValidationException(String message) {
        super(message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.usermanagement.exception.UserManagementException#getErrorCode()
     */
    @Override
    public int getErrorCode() {
        return ErrorCodes.PASSWORD_VALIDATION;
    }

}
