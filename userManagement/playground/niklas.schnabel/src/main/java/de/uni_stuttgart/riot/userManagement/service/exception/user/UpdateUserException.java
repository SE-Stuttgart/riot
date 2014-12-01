package de.uni_stuttgart.riot.userManagement.service.exception.user;

import de.uni_stuttgart.riot.userManagement.service.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class UpdateUserException extends UserManagementException {


    public int getErrorCode() {
        return ErrorCodes.GET_ALL_USERS;
    }

    @Override
    public String getMessage() {
        return "unable to get all users";
    }

}
