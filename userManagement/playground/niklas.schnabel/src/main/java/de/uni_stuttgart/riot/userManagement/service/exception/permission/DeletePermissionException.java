package de.uni_stuttgart.riot.userManagement.service.exception.permission;

import de.uni_stuttgart.riot.userManagement.service.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class DeletePermissionException extends UserManagementException {

    /**
     * 
     */
    private static final long serialVersionUID = 1810899395980316871L;

    public int getErrorCode() {
        return ErrorCodes.DELETE_PERMISSION;
    }

    @Override
    public String getMessage() {
        return "Unable to delete the permission";
    }

}
