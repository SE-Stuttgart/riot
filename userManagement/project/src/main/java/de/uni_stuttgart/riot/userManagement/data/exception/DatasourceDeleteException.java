package de.uni_stuttgart.riot.userManagement.data.exception;

import de.uni_stuttgart.riot.userManagement.exception.ErrorCodes;

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
     * Default-Constructor.
     * 
     * @param massage
     *            Error text
     */
    public DatasourceDeleteException(String massage) {
        super(massage);
    }

	@Override
	public int getErrorCode() {
		return ErrorCodes.DATASOURCE_DELETE;
	}

}
