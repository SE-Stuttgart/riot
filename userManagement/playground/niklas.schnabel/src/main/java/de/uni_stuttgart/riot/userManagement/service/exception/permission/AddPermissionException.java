package de.uni_stuttgart.riot.userManagement.service.exception.permission;

import de.uni_stuttgart.riot.userManagement.service.exception.ErrorCodes;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class AddPermissionException extends UserManagementException {

    /**
     * 
     */
    private static final long serialVersionUID = 1810899195980316871L;

    public int getErrorCode() {
        return ErrorCodes.ADD_USER;
    }

    @Override
    public String getMessage() {
        return "Unable to add the new permission";
    }

}
