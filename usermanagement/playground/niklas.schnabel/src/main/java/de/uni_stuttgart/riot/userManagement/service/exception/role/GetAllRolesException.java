package de.uni_stuttgart.riot.userManagement.service.exception.role;

import de.uni_stuttgart.riot.userManagement.service.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class GetAllRolesException extends UserManagementException {

    /**
     * 
     */
    private static final long serialVersionUID = 181042917258036871L;

    public int getErrorCode() {
        return ErrorCodes.GET_ALL_ROLES;
    }

    @Override
    public String getMessage() {
        return "Unable to get the roles";
    }

}
