package de.uni_stuttgart.riot.usermanagement.data.exception;

/**
 * Exception for errors during the retrieval of Objects in the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public class DatasourceFindException extends DatasourceException {

    /**
     * Error message.
     */
    public static final String OBJECT_DOES_NOT_EXIST_IN_DATASOURCE = 
            "There is no object stored in the datasource assosiated with the given ID or parameter";

    /**
     * Default-Constructor.
     * 
     * @param massage
     *            Error text
     */
    public DatasourceFindException(String massage) {
        super(massage);
    }

    @Override
    public int getErrorCode() {
        return 0;
    }
}
