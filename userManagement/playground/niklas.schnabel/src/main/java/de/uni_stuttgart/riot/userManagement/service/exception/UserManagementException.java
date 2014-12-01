package de.uni_stuttgart.riot.userManagement.service.exception;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public abstract class UserManagementException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 3792732636934644900L;

    public abstract int getErrorCode();

    @Override
    public abstract String getMessage();
}
