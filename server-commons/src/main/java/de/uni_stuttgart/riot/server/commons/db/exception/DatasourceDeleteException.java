package de.uni_stuttgart.riot.server.commons.db.exception;


/**
 * Exception for errors during the deletion of Objects in the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public class DatasourceDeleteException extends DatasourceException {

    /**
     * Error message.
     */
    public static final String OBJECT_DOES_NOT_EXIST_IN_DATASOURCE = "Object does not exist in datasource";
    private static final long serialVersionUID = 5218235116831140076L;

    /**
     * Constructor for DatasourceDeleteException.
     * 
     * @param message
     *            The message that should be shown to the user.
     */
    public DatasourceDeleteException(String message) {
        super(message);
    }

    /**
     * Constructor for DatasourceDeleteException.
     * 
     * @param message
     *            The message that should be shown to the user.
     * @param cause
     *            The root cause
     */
    public DatasourceDeleteException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructor for DatasourceDeleteException.
     * 
     * @param cause
     *            The root cause of the error.
     */
    public DatasourceDeleteException(Throwable cause) {
        super(cause);
    }

}
