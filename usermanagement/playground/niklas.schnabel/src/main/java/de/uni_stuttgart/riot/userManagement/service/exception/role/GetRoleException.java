package de.uni_stuttgart.riot.userManagement.service.exception.role;

import de.uni_stuttgart.riot.userManagement.service.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class GetRoleException extends UserManagementException {

    /**
     * 
     */
    private static final long serialVersionUID = 1810429172580316871L;

    public int getErrorCode() {
        return ErrorCodes.GET_ROLE;
    }

    @Override
    public String getMessage() {
        return "Unable to get the role";
    }

}
