package de.uni_stuttgart.riot.usermanagement.data.exception;

/**
 * Exception for errors during the insertion of Objects in the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public class DatasourceInsertException extends DatasourceException {
    
    private static final long serialVersionUID = 1L;

    public DatasourceInsertException(String message, Exception cause) {
        super(message, cause);
    }

    public DatasourceInsertException(String message) {
        super(message);
    }

    public DatasourceInsertException(Throwable cause) {
        super(cause);
    }

    @Override
    public int getErrorCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
