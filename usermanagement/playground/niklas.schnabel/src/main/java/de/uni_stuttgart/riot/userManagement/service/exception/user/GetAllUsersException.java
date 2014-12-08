package de.uni_stuttgart.riot.userManagement.service.exception.user;

import de.uni_stuttgart.riot.userManagement.service.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class GetAllUsersException extends UserManagementException {


    public int getErrorCode() {
        return ErrorCodes.GET_USER;
    }

    @Override
    public String getMessage() {
        return "unable to get the user";
    }

}
