package de.uni_stuttgart.riot.usermanagement.data.exception;

/**
 * Exception for errors during the update of Objects in the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public class DatasourceUpdateException extends DatasourceException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for DatasourceUpdateException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public DatasourceUpdateException(String message) {
        super(message);
    }

    /**
     * Constructor for DatasourceUpdateException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public DatasourceUpdateException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for DatasourceUpdateException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public DatasourceUpdateException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getErrorCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
