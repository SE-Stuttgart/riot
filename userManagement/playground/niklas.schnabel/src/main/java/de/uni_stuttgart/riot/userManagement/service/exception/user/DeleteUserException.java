package de.uni_stuttgart.riot.userManagement.service.exception.user;

import de.uni_stuttgart.riot.userManagement.service.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class DeleteUserException extends UserManagementException {

    /**
     * 
     */
    private static final long serialVersionUID = 3251547602783328996L;

    public int getErrorCode() {
        return ErrorCodes.DELETE_USER;
    }

    @Override
    public String getMessage() {
        return "unable to delete the user";
    }

}
