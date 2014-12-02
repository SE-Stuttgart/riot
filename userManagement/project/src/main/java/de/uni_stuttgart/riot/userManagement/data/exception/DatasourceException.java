package de.uni_stuttgart.riot.userManagement.data.exception;

import de.uni_stuttgart.riot.userManagement.exception.UserManagementException;

/**
 * Superclass for all errors according the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public abstract class DatasourceException extends UserManagementException {

    public DatasourceException(String message, Exception cause) {
		super(message, cause);
	}

    /**
     * Default-Constructor.
     * 
     * @param massage
     *            Error text
     */
    public DatasourceException(String massage) {
        super(massage);
    }
    
	private static final long serialVersionUID = -509609274709680393L;


}
