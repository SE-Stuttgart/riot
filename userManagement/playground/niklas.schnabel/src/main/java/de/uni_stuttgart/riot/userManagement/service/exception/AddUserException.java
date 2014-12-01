package de.uni_stuttgart.riot.userManagement.service.exception;

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
        return "unable to add the new user";
    }

}
