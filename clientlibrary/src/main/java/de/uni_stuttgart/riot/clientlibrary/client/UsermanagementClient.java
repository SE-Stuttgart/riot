package de.uni_stuttgart.riot.clientlibrary.client;

import java.io.IOException;
import java.util.Collection;

import de.uni_stuttgart.riot.clientlibrary.BaseClient;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.UserRequest;

/**
 * Rest client for the Usermanagement. // FIXME handle baseresource changes
 */
public class UsermanagementClient extends BaseClient {

    private static final String GET_USERS = "users";
    private static final String GET_CURRENT_USER = "users/self";
    private static final String GET_ROLES = "roles";
    private static final String GET_PERMISSIONS = "permissions";
    private static final String GET_USER_ROLES = "/roles";
    private static final String GET_USER_STATE = "/state";

    private static final String GET_USER = "users/";
    private static final String GET_ROLE = "roles/";
    private static final String GET_PERMISSION = "permissions/";

    private static final String POST_ADD_USER = "users/sec/";
    private static final String POST_ADD_ROLE = "roles";
    private static final String POST_ADD_PERMISSION = "permissions";
    private static final String POST_ADD_USER_ROLE = "/roles/";

    private static final String PUT_UPDATE_USER = "users/sec/";
    private static final String PUT_UPDATE_ROLE = "roles/";
    private static final String PUT_UPDATE_PERMISSION = "permissions/";

    private static final String DELETE_USER = "users/";
    private static final String DELETE_ROLE = "roles/";
    private static final String DELETE_PERMISSION = "permissions/";
    private static final String DELETE_USER_ROLE = "/roles/";

    /**
     * Constructor.
     *
     * @param serverConnector
     *            the {@link ServerConnector} to be used
     */
    public UsermanagementClient(ServerConnector serverConnector) {
        super(serverConnector);
    }

    /**
     * Updates the user with id userID with the given values of userRequest.
     *
     * @param userID
     *            id of user to be updated
     * @param userRequest
     *            new values for the user
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public void updateUser(long userID, UserRequest userRequest) throws RequestException, IOException {
        getConnector().doPUT(PUT_UPDATE_USER + userID, userRequest);
    }

    /**
     * Updates the role with id roleID with the given values of role.
     *
     * @param roleID
     *            id of role to be updated
     * @param role
     *            new values for the role
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public void updateRole(long roleID, Role role) throws RequestException, IOException {
        getConnector().doPUT(PUT_UPDATE_ROLE + roleID, role);
    }

    /**
     * Updates the permission with id permissionID with the given values of permission.
     *
     * @param permissionID
     *            if of the permission to be updated
     * @param permission
     *            new values
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public void updatePermission(long permissionID, Permission permission) throws RequestException, IOException {
        getConnector().doPUT(PUT_UPDATE_PERMISSION + permissionID, permission);
    }

    /**
     * Deletes the role with id roleID for the roles of the user with id userID.
     *
     * @param userID
     *            user id
     * @param roleID
     *            role id
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public void removeUserRole(long userID, long roleID) throws RequestException, IOException {
        getConnector().doDELETE("users/" + userID + DELETE_USER_ROLE + roleID);
    }

    /**
     * Deletes the user with id userID.
     *
     * @param userID
     *            id of user t be deleted
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public void removeUser(long userID) throws RequestException, IOException {
        getConnector().doDELETE(DELETE_USER + userID);
    }

    /**
     * Deletes the role with id roleID.
     *
     * @param roleID
     *            id of role to be deleted
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public void removeRole(long roleID) throws RequestException, IOException {
        getConnector().doDELETE(DELETE_ROLE + roleID);
    }

    /**
     * Deletes the permission with id permissionID.
     *
     * @param permissionID
     *            id of permission to be deleted
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public void removePermission(long permissionID) throws RequestException, IOException {
        getConnector().doDELETE(DELETE_PERMISSION + permissionID);
    }

    /**
     * Adds a User.
     *
     * @param userRequest
     *            data of user to be added.
     * @return the added user
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public User addUser(UserRequest userRequest) throws RequestException, IOException {
        return getConnector().doPOST(POST_ADD_USER, userRequest, User.class);
    }

    /**
     * Adds a {@link Role}.
     *
     * @param role
     *            role to be added
     * @return the added role
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public Role addRole(Role role) throws RequestException, IOException {
        return getConnector().doPOST(POST_ADD_ROLE, role, Role.class);
    }

    /**
     * Adds the role with id roleID to the roles of user with id userID.
     *
     * @param userID
     *            user id
     * @param roleID
     *            role id
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public void addUserRole(long userID, long roleID) throws RequestException, IOException {
        getConnector().doPUT("users/" + userID + POST_ADD_USER_ROLE + roleID, "");
    }

    /**
     * Adds a Permission.
     *
     * @param permission
     *            to be added
     * @return the new permission
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public Permission addPermission(Permission permission) throws RequestException, IOException {
        return getConnector().doPOST(POST_ADD_PERMISSION, permission, Permission.class);
    }

    /**
     * Retrieves all roles of a given user.
     *
     * @param userid
     *            user id
     * @return Collection of roles associated with the given user.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public Collection<Role> getUserRoles(long userid) throws RequestException, IOException {
        try {
            return getConnector().doGETCollection(GET_USER + userid + GET_USER_ROLES, Role.class);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns the user with the given id.
     *
     * @param id
     *            user id
     * @return the user
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     * @throws NotFoundException
     *             If the user does not exist.
     */
    public User getUser(long id) throws RequestException, IOException, NotFoundException {
        return getConnector().doGET(GET_USER + id, User.class);
    }

    /**
     * Returns the current logged in user.
     *
     * @return the user
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     * @throws NotFoundException
     *             If the user does not exist.
     */
    public User getCurrentUser() throws RequestException, IOException, NotFoundException {
        return getConnector().doGET(GET_CURRENT_USER, User.class);
    }

    /**
     * Returns the role with the given id.
     *
     * @param id
     *            role id
     * @return the role found
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     * @throws NotFoundException
     *             If the role does not exist.
     */
    public Role getRole(long id) throws RequestException, IOException, NotFoundException {
        return getConnector().doGET(GET_ROLE + id, Role.class);
    }

    /**
     * Returns the online state of user with given id.
     *
     * @param userId
     *            users id
     * @return the onlinestate
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     * @throws NotFoundException
     *             If the user does not exist.
     */
    public OnlineState getOnlineState(long userId) throws RequestException, IOException, NotFoundException {
        return OnlineState.getEnumById(getConnector().doGET(GET_USER + userId + GET_USER_STATE, Integer.class));
    }

    /**
     * Returns the permission with the given id.
     *
     * @param id
     *            permission id
     * @return the permission found
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     * @throws NotFoundException
     *             If the permission does not exist.
     */
    public Permission getPermission(long id) throws RequestException, IOException, NotFoundException {
        return getConnector().doGET(GET_PERMISSION + id, Permission.class);
    }

    /**
     * Returns all users.
     *
     * @return collection of all users.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public Collection<User> getUsers() throws RequestException, IOException {
        try {
            return getConnector().doGETCollection(GET_USERS, User.class);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns all roles.
     *
     * @return collection of all roles
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public Collection<Role> getRoles() throws RequestException, IOException {
        try {
            return getConnector().doGETCollection(GET_ROLES, Role.class);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns all permissions.
     *
     * @return collection of all permissions
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When executing the request failed.
     */
    public Collection<Permission> getPermissions() throws RequestException, IOException {
        try {
            return getConnector().doGETCollection(GET_PERMISSIONS, Permission.class);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

}
