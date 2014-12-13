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

    public DatasourceException(String message, Exception cause) {
        super(message, cause);
    }

    public DatasourceException(String massage) {
        super(massage);
    }

    public DatasourceException(Throwable cause) {
        super(cause);
    }

}
