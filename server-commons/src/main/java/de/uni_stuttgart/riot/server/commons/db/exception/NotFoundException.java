package de.uni_stuttgart.riot.server.commons.db.exception;

/**
 * Raised when a queried element is not found.
 *
 */
public class NotFoundException extends DatasourceFindException {

    /**
     * Error message.
     */
    public static final String OBJECT_DOES_NOT_EXIST_IN_DATASOURCE = "There is no object stored in the datasource assosiated with the given ID or parameter";

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public NotFoundException() {
        super(OBJECT_DOES_NOT_EXIST_IN_DATASOURCE);
    }

}
