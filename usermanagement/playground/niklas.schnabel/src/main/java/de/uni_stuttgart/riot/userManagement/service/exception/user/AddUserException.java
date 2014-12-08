package de.uni_stuttgart.riot.userManagement.service.exception.user;

import de.uni_stuttgart.riot.userManagement.service.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class AddUserException extends UserManagementException {

    /**
     * 
     */
    private static final long serialVersionUID = 1810899195980316871L;

    public int getErrorCode() {
        return ErrorCodes.ADD_USER;
    }

    @Override
    public String getMessage() {
        return "Unable to add the new user";
    }

}
