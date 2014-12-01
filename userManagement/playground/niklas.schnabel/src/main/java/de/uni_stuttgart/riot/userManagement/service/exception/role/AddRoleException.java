package de.uni_stuttgart.riot.userManagement.service.exception.role;

import de.uni_stuttgart.riot.userManagement.service.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class AddRoleException extends UserManagementException {

    /**
     * 
     */
    private static final long serialVersionUID = 1810829195980316871L;

    public int getErrorCode() {
        return ErrorCodes.ADD_ROLE;
    }

    @Override
    public String getMessage() {
        return "Unable to add the new role";
    }

}
