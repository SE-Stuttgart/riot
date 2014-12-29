package de.uni_stuttgart.riot.usermanagement.data.exception;

import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;

/**
 * Superclass for all errors according the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public abstract class DatasourceException extends UserManagementException {

    private static final long serialVersionUID = -509609274709680393L;

    /**
     * Constructor for DatasourceException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public DatasourceException(String message) {
        super(message);
    }

    /**
     * Constructor for DatasourceException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public DatasourceException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for DatasourceException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public DatasourceException(Throwable cause) {
        super(cause);
    }

}
