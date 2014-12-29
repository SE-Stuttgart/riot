package de.uni_stuttgart.riot.usermanagement.exception;

/**
 * Contains the error codes for all user management exceptions.<br>
 * FIXME Replace this by a proper enum, or stop using error codes in the first place (they are unnecessary, just send the name of the
 * exception, it will be just as unique).
 * 
 * @author Niklas Schnabel
 * 
 */
// CHECKSTYLE:OFF
public class ErrorCodes {

    public static final int DATASOURCE_DELETE = 0;

    public static final int ADD_USER = 1;
    public static final int DELETE_USER = 2;
    public static final int UPDATE_USER = 3;
    public static final int GET_USER = 4;
    public static final int GET_ALL_USERS = 5;
    public static final int ADD_ROLE_TO_USER = 6;
    public static final int REMOVE_ROLE_FROM_USER = 7;
    public static final int GET_ROLES_FROM_USER = 8;

    public static final int ADD_PERMISSION = 11;
    public static final int DELETE_PERMISSION = 12;
    public static final int UPDATE_PERMISSION = 13;
    public static final int GET_PERMISSION = 14;
    public static final int GET_ALL_PERMISSIONS = 15;

    public static final int ADD_ROLE = 21;
    public static final int DELETE_ROLE = 22;
    public static final int UPDATE_ROLE = 23;
    public static final int GET_ROLE = 24;
    public static final int GET_ALL_ROLES = 25;
    public static final int ADD_PERMISSION_TO_ROLE = 26;
    public static final int REMOVE_PERMISSION_FROM_ROLE = 27;
    public static final int GET_PERMISSIONS_FROM_ROLE = 28;

    public static final int GET_TOKEN = 31;
    public static final int LOGOUT = 32;
}
