package de.uni_stuttgart.riot.data.exc;

/**
 * Superclass for all errors according the datasource.
 * 
 * @author Jonas Tangermann
 *
 */
public class DatasourceException extends Exception {

    private static final long serialVersionUID = -509609274709680393L;

    /**
     * Default-Constructor.
     * 
     * @param massage
     *            Error text
     */
    public DatasourceException(String massage) {
        super(massage);
    }

}
