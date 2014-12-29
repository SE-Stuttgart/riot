package de.uni_stuttgart.riot.usermanagement.data.exception;

/**
 * Exception for errors during the insertion of Objects in the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public class DatasourceInsertException extends DatasourceException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for DatasourceInsertException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public DatasourceInsertException(String message) {
        super(message);
    }

    /**
     * Constructor for DatasourceInsertException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public DatasourceInsertException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for DatasourceInsertException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public DatasourceInsertException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getErrorCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
