package de.uni_stuttgart.riot.userManagement.service.exception.role;

import de.uni_stuttgart.riot.userManagement.service.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class UpdateRoleException extends UserManagementException {

    /**
     * 
     */
    private static final long serialVersionUID = 1810429192580316871L;

    public int getErrorCode() {
        return ErrorCodes.DELETE_ROLE;
    }

    @Override
    public String getMessage() {
        return "Unable to update the role";
    }

}
