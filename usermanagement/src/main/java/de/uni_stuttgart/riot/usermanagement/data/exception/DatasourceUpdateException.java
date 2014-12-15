package de.uni_stuttgart.riot.usermanagement.data.exception;

/**
 * Exception for errors during the update of Objects in the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public class DatasourceUpdateException extends DatasourceException {

    private static final long serialVersionUID = 1L;

    public DatasourceUpdateException(String message, Exception cause) {
        super(message, cause);
    }

    public DatasourceUpdateException(String message) {
        super(message);
    }

    public DatasourceUpdateException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getErrorCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
