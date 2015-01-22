package de.uni_stuttgart.riot.server.commons.db.exception;

/**
 *  //TODO. 
 *
 */
public class NotFoundException extends DatasourceFindException {

    /**
     * Error message.
     */
    public static final String OBJECT_DOES_NOT_EXIST_IN_DATASOURCE = "There is no object stored in the datasource assosiated with the given ID or parameter";

    /**
     * Const.
     */
    public NotFoundException() {
        super(OBJECT_DOES_NOT_EXIST_IN_DATASOURCE);
    }

}
