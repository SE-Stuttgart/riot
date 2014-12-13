package de.uni_stuttgart.riot.usermanagement.data.exception;

/**
 * Exception for errors during the update of Objects in the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public class DatasourceUpdateException extends DatasourceException {
    
    /**
     * Default-Constructor.
     * 
     * @param massage
     *            Error text
     */
    public DatasourceUpdateException(String massage) {
        super(massage);
    }

    @Override
    public int getErrorCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
