package de.uni_stuttgart.riot.usermanagement.service.facade;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.logic.AuthenticationLogic;
import de.uni_stuttgart.riot.usermanagement.logic.PermissionLogic;
import de.uni_stuttgart.riot.usermanagement.logic.RoleLogic;
import de.uni_stuttgart.riot.usermanagement.logic.TokenLogic;
import de.uni_stuttgart.riot.usermanagement.logic.UserLogic;

/**
 * This class is the only point which should be used to access the user management.
 * 
 * @author Niklas Schnabel
 *
 */
public class UserManagementFacade {

    private static UserManagementFacade instance = null;

    private UserLogic userLogic;
    private TokenLogic tokenLogic;
    private RoleLogic roleLogic;
    private PermissionLogic permissionLogic;
    private AuthenticationLogic authLogic;

    private UserManagementFacade() {
        userLogic = new UserLogic();
        tokenLogic = new TokenLogic();
        roleLogic = new RoleLogic();
        permissionLogic = new PermissionLogic();
        authLogic = new AuthenticationLogic();
    }

    /**
     * Get the instance of the facade.
     * 
     * @return Instance of the facade.
     */
    public static UserManagementFacade getInstance() {
        if (instance == null) {
            instance = new UserManagementFacade();
        }
        return instance;
    }

    /**
     * Generate one bearer and one refresh token.
     * 
     * @param username
     *            User name of the user. Is used for authentication.
     * @param password
     *            Password of the user. Is used for authentication.
     * @return Contains the generated bearer and refresh token
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public Token login(String username, String password) throws UserManagementException {
        return authLogic.login(username, password);
    }

    /**
     * Generate a new set of bearer and refresh token from a given refresh token.
     * 
     * @param refreshToken
     *            The refresh token used for generating the new tokens
     * @return Contains the generated bearer and refresh token
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public Token refreshToken(String refreshToken) throws UserManagementException {
        return authLogic.refreshToken(refreshToken);
    }

    /**
     * Log an user out be invalidating the bearer and refresh token.
     * 
     * @param currentBearerToken
     *            The current bearer token used to authenticate the user. Will no longer be valid after calling this method.
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void logout(String currentBearerToken) throws UserManagementException {
        authLogic.logout(currentBearerToken);
    }

    /**
     * Add a new user.
     * 
     * @param username
     *            The user name of the user,
     * @param email
     *            The email of the user,
     * @param clearTextPassword
     *            The password of the user as clear text,
     * @return The added user,
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public User addUser(String username, String email, String clearTextPassword) throws UserManagementException {
        return (User) userLogic.addUser(username, email, clearTextPassword);
    }

    /**
     * Delete an existing user. Whoever calls this method from an actual server service should remember to remove the user from all
     * ServerThingBehaviors as well!
     * 
     * @param id
     *            Id of the user
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void deleteUser(Long id) throws UserManagementException {
        userLogic.deleteUser(id);
    }

    /**
     * Update the data of an existing user.
     * 
     * @param user
     *            New user data.
     * @param clearTextPassword
     *            The password of the user as clear text. If the password should not be updated use null.
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void updateUser(User user, String clearTextPassword) throws UserManagementException {
        if (user instanceof UMUser) {
            userLogic.updateUser((UMUser) user, clearTextPassword);
        } else {
            throw new UserManagementException("The user is not an instance of a UMUser");
        }
    }

    /**
     * Get a single user.
     * 
     * @param id
     *            Id of the user to get.
     * @return User with the given id.
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public User getUser(Long id) throws UserManagementException {
        return (User) userLogic.getUser(id);
    }

    /**
     * Get a single user.
     *
     * @param username
     *            the username
     * @return User with the given id.
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public User getUser(String username) throws UserManagementException {
        return (User) userLogic.getUser(username);
    }

    /**
     * Get user by token.
     * 
     * @param token
     *            The token of the user.
     * @return Returns user.
     * @throws UserManagementException
     *             When getting the user fails.
     */
    public User getUser(Token token) throws UserManagementException {
        return (User) userLogic.getUser(token);
    }

    /**
     * Get all existing users.
     * 
     * @return All users in a List.
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public Collection<User> getAllUsers() throws UserManagementException {
        ArrayList<User> users = new ArrayList<User>();
        users.addAll(userLogic.getAllUsers());
        return users;
    }

    /**
     * Get all roles from a specific user.
     * 
     * @param id
     *            The id of the user to get roles from
     * @return A list with all roles of the given user
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public Collection<Role> getAllRolesFromUser(Long id) throws UserManagementException {
        return userLogic.getAllRolesFromUser(id);
    }

    /**
     * Add a new role to an existing user.
     * 
     * @param userId
     *            The id of the user.
     * @param roleId
     *            The id of the role.
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void addRoleToUser(Long userId, Long roleId) throws UserManagementException {
        userLogic.addRoleToUser(userId, roleId);
    }

    /**
     * Remove a role from a specific user.
     * 
     * @param idUser
     *            The id of the user
     * @param idRole
     *            The id of the role to remove
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void removeRoleFromUser(Long idUser, Long idRole) throws UserManagementException {
        userLogic.removeRoleFromUser(idUser, idRole);
    }

    /**
     * Get all directly assigned permissions from a specific user.
     * 
     * @param id
     *            The id of the user to get permissions from
     * @return A list with all permissions of the given user
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public Collection<Permission> getAllPermissionsFromUser(Long id) throws UserManagementException {
        return userLogic.getAllPermissionsFromUser(id);
    }

    /**
     * Add a new directly assigned permission to an existing user.
     * 
     * @param userId
     *            The id of the user.
     * @param permissionId
     *            The id of the permission.
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void addPermissionToUser(Long userId, Long permissionId) throws UserManagementException {
        userLogic.addPermissionToUser(userId, permissionId);
    }

    /**
     * Remove a directly assigned permission from a specific user.
     * 
     * @param idUser
     *            The id of the user
     * @param permissionId
     *            The id of the permission to remove
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void removePermissionFromUser(Long idUser, Long permissionId) throws UserManagementException {
        userLogic.removePermissionFromUser(idUser, permissionId);
    }

    /**
     * Get all active tokens of a specific user.
     * 
     * @param idUser
     *            The id of the user
     * @return All active tokens of the user.
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public Collection<Token> getActiveTokensFromUser(Long idUser) throws UserManagementException {
        return userLogic.getActiveTokensFromUser(idUser);
    }

    /**
     * Get token from token value.
     * 
     * @param token
     *            The token value.
     * @return Returns token.
     * @throws UserManagementException
     *             When getting the token fails.
     */
    public Token getToken(String token) throws UserManagementException {
        return tokenLogic.getToken(token);
    }

    /**
     * Add a new permission.
     * 
     * @param permission
     *            The new permission
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void addPermission(Permission permission) throws UserManagementException {
        permissionLogic.addPermission(permission);
    }

    /**
     * Delete an existing permission.
     * 
     * @param id
     *            The id of the permission to delete
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void deletePermission(Long id) throws UserManagementException {
        permissionLogic.deletePermission(id);
    }

    /**
     * Update an existing permission.
     * 
     * @param id
     *            The id of the permission to update
     * @param permission
     *            The data of the new permission
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void updatePermission(Long id, Permission permission) throws UserManagementException {
        permissionLogic.updatePermission(id, permission);
    }

    /**
     * Get an existing permission.
     * 
     * @param id
     *            The id of the permission to get
     * @return The permission.
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public Permission getPermission(Long id) throws UserManagementException {
        return permissionLogic.getPermission(id);
    }

    /**
     * Get all existing permissions.
     * 
     * @return All permissions in a list
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public Collection<Permission> getAllPermissions() throws UserManagementException {
        return permissionLogic.getAllPermissions();
    }

    /**
     * Add a new role.
     * 
     * @param role
     *            The role to add
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void addRole(Role role) throws UserManagementException {
        roleLogic.addRole(role);
    }

    /**
     * Delete an existing role.
     * 
     * @param id
     *            The id of the role to delete
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void deleteRole(Long id) throws UserManagementException {
        roleLogic.deleteRole(id);
    }

    /**
     * Update an existing role.
     * 
     * @param role
     *            The new data for the role
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void updateRole(Role role) throws UserManagementException {
        roleLogic.updateRole(role);
    }

    /**
     * Get a single role.
     * 
     * @param id
     *            The id of the role to get
     * @return The role
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public Role getRole(Long id) throws UserManagementException {
        return roleLogic.getRole(id);
    }

    /**
     * Get all existing roles.
     * 
     * @return All existing roles in a list
     * @throws UserManagementException
     *             Thrown if any errors occur.
     */
    public Collection<Role> getAllRoles() throws UserManagementException {
        return roleLogic.getAllRoles();
    }

    /**
     * Get all permissions belonging to a role.
     * 
     * @param roleId
     *            The id of the role
     * @return All permissions of the role.
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public Collection<Permission> getAllPermissionsOfRole(Long roleId) throws UserManagementException {
        return roleLogic.getAllPermissionsFromRole(roleId);
    }

    /**
     * Add an existing permission to an existing role.
     * 
     * @param roleId
     *            The id of the role
     * @param permissionId
     *            The id of the permission, which should be added
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void addPermissionToRole(Long roleId, Long permissionId) throws UserManagementException {
        roleLogic.addPermissionToRole(roleId, permissionId);
    }

    /**
     * Adds the new permission to user.
     *
     * @param userId
     *            the user id
     * @param permission
     *            the permission
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void addNewPermissionToUser(Long userId, Permission permission) throws UserManagementException {
        userLogic.addNewPermissionToUser(userId, permission);
    }

    /**
     * Remove a permission from a role.
     * 
     * @param roleId
     *            The id of the role
     * @param permissionId
     *            The id of the permission, which should be removed
     * @throws UserManagementException
     *             Thrown if any errors occur
     */
    public void deletePermissionFromRole(Long roleId, Long permissionId) throws UserManagementException {
        roleLogic.removePermissionFromRole(roleId, permissionId);
    }

    /**
     * Checks if the given role is held by the current user, and fails if not.
     * 
     * @param role
     *            The role.
     * @throws UserManagementException
     *             If the role is not held.
     */
    public void requiresRole(String role) throws UserManagementException {
        SecurityUtils.getSubject().checkRole(role);
    }

    /**
     * Checks if the given permission is held by the current user, and fails if not.
     * 
     * @param permission
     *            The permission.
     * @throws UserManagementException
     *             If the permission is not held.
     */
    public void requiresPermission(String permission) throws UserManagementException {
        SecurityUtils.getSubject().checkPermission(permission);
    }

    /**
     * Determines the currently logged-in user. This method will throw a {@link UnauthenticatedException} if the user is currently not
     * logged in.
     * 
     * @return The ID of the user who is currently logged in.
     */
    public long getCurrentUserId() {
        Object token = SecurityUtils.getSubject().getPrincipal();
        if (token == null) {
            throw new UnauthenticatedException("Not logged in!");
        }

        try {
            return tokenLogic.getUserIdFromToken(token.toString());
        } catch (UserManagementException e) {
            throw new UnauthenticatedException("Invalid token " + token);
        }
    }

    /**
     * Determines the currently logged-in user. This method will throw a {@link UnauthenticatedException} if the user is currently not
     * logged in.
     * 
     * @return The user who is currently logged in.
     * @throws UserManagementException
     *             When the user logged in does not exist anymore in the database.
     */
    public User getCurrentUser() throws UserManagementException {
        return getUser(getCurrentUserId());
    }

}
