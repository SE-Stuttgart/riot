package de.uni_stuttgart.riot.userManagement.service.exception.user;

import de.uni_stuttgart.riot.userManagement.service.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class GetUserException extends UserManagementException {


    public int getErrorCode() {
        return ErrorCodes.UPDATE_USER;
    }

    @Override
    public String getMessage() {
        return "unable to update the user";
    }

}
