package de.uni_stuttgart.riot.usermanagement.data.exception;

/**
 * Exception for errors during the retrieval of Objects in the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public class DatasourceFindException extends DatasourceException {

    private static final long serialVersionUID = 1L;

    /**
     * Error message.
     */
    public static final String OBJECT_DOES_NOT_EXIST_IN_DATASOURCE = "There is no object stored in the datasource assosiated with the given ID or parameter";

    public DatasourceFindException(String message) {
        super(message);
    }

    public DatasourceFindException(Throwable cause) {
        super(cause);
    }

    public DatasourceFindException(String message, Exception cause) {
        super(message, cause);
    }

    @Override
    public int getErrorCode() {
        return 0;
    }
}
