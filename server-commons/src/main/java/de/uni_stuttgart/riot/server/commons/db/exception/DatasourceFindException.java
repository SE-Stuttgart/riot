package de.uni_stuttgart.riot.server.commons.db.exception;

/**
 * Exception for errors during the retrieval of Objects in the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public class DatasourceFindException extends DatasourceException {

 
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for DatasourceFindException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public DatasourceFindException(String message) {
        super(message);
    }

    /**
     * Constructor for DatasourceFindException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public DatasourceFindException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for DatasourceFindException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public DatasourceFindException(Throwable cause) {
        super(cause);
    }

}
