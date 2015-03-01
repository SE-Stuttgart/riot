package de.uni_stuttgart.riot.clientlibrary.usermanagement.client;

import java.io.IOException;
import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import de.uni_stuttgart.riot.clientlibrary.LoginClient;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.UserRequest;

/**
 * Rest client for the Usermanagement. // FIXME handle baseresource changes
 */
public class UsermanagementClient {

    private static final String PREFIX = "/api/v1/";

    private static final String GET_USERS = PREFIX + "users";
    private static final String GET_ROLES = PREFIX + "roles";
    private static final String GET_PERMISSIONS = PREFIX + "permissions";
    private static final String GET_USER_ROLES = "/roles";

    private static final String GET_USER = PREFIX + "users/";
    private static final String GET_ROLE = PREFIX + "roles/";
    private static final String GET_PERMISSION = PREFIX + "permissions/";

    private static final String POST_ADD_USER = PREFIX + "users/sec/";
    private static final String POST_ADD_ROLE = PREFIX + "roles";
    private static final String POST_ADD_PERMISSION = PREFIX + "permissions";
    private static final String POST_ADD_USER_ROLE = "/roles/";

    private static final String PUT_UPDATE_USER = PREFIX + "users/sec/";
    private static final String PUT_UPDATE_ROLE = PREFIX + "roles/";
    private static final String PUT_UPDATE_PERMISSION = PREFIX + "permissions/";

    private static final String DELETE_USER = PREFIX + "users/";
    private static final String DELETE_ROLE = PREFIX + "roles/";
    private static final String DELETE_PERMISSION = PREFIX + "permissions/";
    private static final String DELETE_USER_ROLE = "/roles/";

    private final LoginClient loginClient;

    /**
     * Constructor.
     * 
     * @param loginClient
     *            the {@link LoginClient} to be used
     */
    public UsermanagementClient(LoginClient loginClient) {
        this.loginClient = loginClient;
    }

    /**
     * Updates the user with id userID with the given values of userRequest.
     * 
     * @param userID
     *            id of user to be updated
     * @param userRequest
     *            new values for the user
     * @throws RequestException .
     */
    public void updateUser(long userID, UserRequest userRequest) throws RequestException {
        HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl() + PUT_UPDATE_USER + userID, userRequest);
        try {
            if (response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Updates the role with id roleID with the given values of role.
     * 
     * @param roleID
     *            id of role to be updated
     * @param role
     *            new values for the role
     * @throws RequestException .
     */
    public void updateRole(long roleID, Role role) throws RequestException {
        this.loginClient.put(this.loginClient.getServerUrl() + PUT_UPDATE_ROLE + roleID, role);
    }

    /**
     * Updates the permission with id permissionID with the given values of permission.
     * 
     * @param permissionID
     *            if of the permission to be updated
     * @param permission
     *            new values
     * @throws RequestException .
     */
    public void updatePermission(long permissionID, Permission permission) throws RequestException {
        this.loginClient.put(this.loginClient.getServerUrl() + PUT_UPDATE_PERMISSION + permissionID, permission);
    }

    /**
     * Deletes the role with id roleID for the roles of the user with id userID.
     * 
     * @param userID
     *            user id
     * @param roleID
     *            role id
     * @return http code (200 ok)
     * @throws RequestException .
     */
    public int removeUserRole(long userID, long roleID) throws RequestException {
        HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl() + PREFIX + "users/" + userID + DELETE_USER_ROLE + roleID);
        int result = response.getStatusLine().getStatusCode();
        try {
            if (response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        } catch (IOException e) {
            throw new RequestException(e);
        }
        return result;
    }

    /**
     * Deletes the user with id userID.
     * 
     * @param userID
     *            id of user t be deleted
     * @return http code (200 ok)
     * @throws RequestException .
     */
    public int removeUser(long userID) throws RequestException {
        HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl() + DELETE_USER + userID);
        int result = response.getStatusLine().getStatusCode();
        return result;
    }

    /**
     * Deletes the role with id roleID.
     * 
     * @param roleID
     *            id of role to be deleted
     * @return http code (200 ok)
     * @throws RequestException .
     */
    public int removeRole(long roleID) throws RequestException {
        HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl() + DELETE_ROLE + roleID);
        int result = response.getStatusLine().getStatusCode();
        return result;
    }

    /**
     * Deletes the permission with id permissionID.
     * 
     * @param permissionID
     *            id of permission to be deleted
     * @return http code (200 ok)
     * @throws RequestException .
     */
    public int removePermission(long permissionID) throws RequestException {
        HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl() + DELETE_PERMISSION + permissionID);
        int result = response.getStatusLine().getStatusCode();
        return result;
    }

    /**
     * Adds a User.
     * 
     * @param userRequest
     *            data of user to be added.
     * @return the added user
     * @throws RequestException .
     */
    public User addUser(UserRequest userRequest) throws RequestException {
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + POST_ADD_USER, userRequest);
        try {
            User result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), User.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Adds a {@link Role}.
     * 
     * @param role
     *            role to be added
     * @return the added role
     * @throws RequestException .
     */
    public Role addRole(Role role) throws RequestException {
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + POST_ADD_ROLE, role);
        try {
            Role result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), Role.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Adds the role with id roleID to the roles of user with id userID.
     * 
     * @param userID
     *            user id
     * @param roleID
     *            role id
     * @return http code (200 ok)
     * @throws RequestException .
     */
    public int addUserRole(long userID, long roleID) throws RequestException {
        HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl() + PREFIX + "users/" + userID + POST_ADD_USER_ROLE + roleID, "");
        try {
            EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            throw new RequestException(e);
        }
        return response.getStatusLine().getStatusCode();
    }

    /**
     * Adds a Permission.
     * 
     * @param permission
     *            to be added
     * @return the new permission
     * @throws RequestException .
     */
    public Permission addPermission(Permission permission) throws RequestException {
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + POST_ADD_PERMISSION, permission);
        try {
            Permission result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), Permission.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Retrieves all roles of a given user.
     * 
     * @param userid
     *            user id
     * @return Collection of roles associated with the given user.
     * @throws RequestException .
     */
    public Collection<Role> getUserRoles(long userid) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_USER + userid + GET_USER_ROLES);
        try {
            Collection<Role> result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), new TypeReference<Collection<Role>>() {
            });
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns the user with the given id.
     * 
     * @param id
     *            user id
     * @return the user
     * @throws RequestException .
     */
    public User getUser(long id) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_USER + id);
        try {
            User result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), User.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns the role with the given id.
     * 
     * @param id
     *            role id
     * @return the role found
     * @throws RequestException .
     */
    public Role getRole(long id) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_ROLE + id);
        try {
            Role result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), Role.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns the permission with the given id.
     * 
     * @param id
     *            permission id
     * @return the permission found
     * @throws RequestException .
     */
    public Permission getPermission(long id) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_PERMISSION + id);
        try {
            Permission result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), Permission.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns all users.
     * 
     * @return collection of all users.
     * @throws RequestException .
     */
    public Collection<User> getUsers() throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_USERS);
        try {
            Collection<User> result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), new TypeReference<Collection<User>>() {
            });
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns all roles.
     * 
     * @return collection of all roles
     * @throws RequestException .
     */
    public Collection<Role> getRoles() throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_ROLES);
        try {
            Collection<Role> result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), new TypeReference<Collection<Role>>() {
            });
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns all permissions.
     * 
     * @return collection of all permissions
     * @throws RequestException .
     */
    public Collection<Permission> getPermissions() throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_PERMISSIONS);
        try {
            Collection<Permission> result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), new TypeReference<Collection<Permission>>() {
            });
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

}
