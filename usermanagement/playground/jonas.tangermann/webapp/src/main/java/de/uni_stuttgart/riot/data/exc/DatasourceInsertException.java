package de.uni_stuttgart.riot.data.exc;

/**
 * Exception for errors during the insertion of Objects in the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public class DatasourceInsertException extends DatasourceException {

    /**
     * Default-Constructor.
     * 
     * @param massage
     *            Error text
     */
    public DatasourceInsertException(String massage) {
        super(massage);
    }
}
