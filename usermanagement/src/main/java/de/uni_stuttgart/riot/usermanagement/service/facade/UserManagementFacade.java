package de.uni_stuttgart.riot.usermanagement.service.facade;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.logic.AuthenticationLogic;
import de.uni_stuttgart.riot.usermanagement.logic.PermissionLogic;
import de.uni_stuttgart.riot.usermanagement.logic.RoleLogic;
import de.uni_stuttgart.riot.usermanagement.logic.TokenLogic;
import de.uni_stuttgart.riot.usermanagement.logic.UserLogic;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.InvalidTokenException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LoginException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LogoutException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.RefreshException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.AddPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.DeletePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetAllPermissionsException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.UpdatePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.AddPermissionToRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.AddRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.DeleteRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetAllRolesException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetPermissionsFromRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.RemovePermissionFromRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.UpdateRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.token.GetTokenException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.AddRoleToUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.AddUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.DeleteUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetActiveTokenException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetAllUsersException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetRolesFromUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.RemoveRoleFromUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.UpdateUserException;

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
     * @throws LoginException
     *             Thrown if any errors occur
     */
    public Token login(String username, String password) throws LoginException {
        return authLogic.login(username, password);
    }

    /**
     * Generate a new set of bearer and refresh token from a given refresh token.
     * 
     * @param refreshToken
     *            The refresh token used for generating the new tokens
     * @return Contains the generated bearer and refresh token
     * @throws InvalidTokenException
     *             When the provided token is invalid.
     * @throws RefreshException
     *             When another, rather unexpected error occurs.
     */
    public Token refreshToken(String refreshToken) throws RefreshException, InvalidTokenException {
        return authLogic.refreshToken(refreshToken);
    }

    /**
     * Log an user out be invalidating the bearer and refresh token.
     * 
     * @param currentBearerToken
     *            The current bearer token used to authenticate the user. Will no longer be valid after calling this method.
     * @throws LogoutException
     *             Thrown if any errors occur
     */
    public void logout(String currentBearerToken) throws LogoutException {
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
     * @throws AddUserException
     *             Thrown if any errors occur
     */
    public User addUser(String username, String email, String clearTextPassword) throws AddUserException {
        return (User) userLogic.addUser(username, email, clearTextPassword);
    }

    /**
     * Delete an existing user. Whoever calls this method from an actual server service should remember to remove the user from all
     * ServerThingBehaviors as well!
     * 
     * @param id
     *            Id of the user
     * @throws DeleteUserException
     *             Thrown if any errors occur
     */
    public void deleteUser(Long id) throws DeleteUserException {
        userLogic.deleteUser(id);
    }

    /**
     * Update the data of an existing user.
     * 
     * @param user
     *            New user data.
     * @param clearTextPassword
     *            The password of the user as clear text. If the password should not be updated use null.
     * @throws UpdateUserException
     *             Thrown if any errors occur
     */
    public void updateUser(User user, String clearTextPassword) throws UpdateUserException {
        if (user instanceof UMUser) {
            userLogic.updateUser((UMUser) user, clearTextPassword);
        } else {
            throw new UpdateUserException("The user is not an instance of a UMUser");
        }
    }

    /**
     * Get a single user.
     * 
     * @param id
     *            Id of the user to get.
     * @return User with the given id.
     * @throws GetUserException
     *             Thrown if any errors occur
     */
    public User getUser(Long id) throws GetUserException {
        return (User) userLogic.getUser(id);
    }

    /**
     * Get a single user.
     *
     * @param username
     *            the username
     * @return User with the given id.
     * @throws GetUserException
     *             Thrown if any errors occur
     */
    public User getUser(String username) throws GetUserException {
        return (User) userLogic.getUser(username);
    }

    /**
     * Get user by token.
     * 
     * @param token
     *            The token of the user.
     * @return Returns user.
     * @throws GetUserException
     *             When getting the user fails.
     */
    public User getUser(Token token) throws GetUserException {
        return (User) userLogic.getUser(token);
    }

    /**
     * Get all existing users.
     * 
     * @return All users in a List.
     * @throws GetAllUsersException
     *             Thrown if any errors occur
     */
    public Collection<User> getAllUsers() throws GetAllUsersException {
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
     * @throws GetRolesFromUserException
     *             Thrown if any errors occur
     */
    public Collection<Role> getAllRolesFromUser(Long id) throws GetRolesFromUserException {
        return userLogic.getAllRolesFromUser(id);
    }

    /**
     * Add a new role to an existing user.
     * 
     * @param userId
     *            The id of the user.
     * @param roleId
     *            The id of the role.
     * @throws AddRoleToUserException
     *             Thrown if any errors occur
     */
    public void addRoleToUser(Long userId, Long roleId) throws AddRoleToUserException {
        userLogic.addRoleToUser(userId, roleId);
    }

    /**
     * Remove a role from a specific user.
     * 
     * @param idUser
     *            The id of the user
     * @param idRole
     *            The id of the role to remove
     * @throws RemoveRoleFromUserException
     *             Thrown if any errors occur
     */
    public void removeRoleFromUser(Long idUser, Long idRole) throws RemoveRoleFromUserException {
        userLogic.removeRoleFromUser(idUser, idRole);
    }

    /**
     * Get all directly assigned permissions from a specific user.
     * 
     * @param id
     *            The id of the user to get permissions from
     * @return A list with all permissions of the given user
     * @throws GetRolesFromUserException
     *             Thrown if any errors occur
     */
    public Collection<Permission> getAllPermissionsFromUser(Long id) throws GetRolesFromUserException {
        return userLogic.getAllPermissionsFromUser(id);
    }

    /**
     * Add a new directly assigned permission to an existing user.
     * 
     * @param userId
     *            The id of the user.
     * @param permissionId
     *            The id of the permission.
     * @throws AddRoleToUserException
     *             Thrown if any errors occur
     */
    public void addPermissionToUser(Long userId, Long permissionId) throws AddRoleToUserException {
        userLogic.addPermissionToUser(userId, permissionId);
    }

    /**
     * Remove a directly assigned permission from a specific user.
     * 
     * @param idUser
     *            The id of the user
     * @param permissionId
     *            The id of the permission to remove
     * @throws RemoveRoleFromUserException
     *             Thrown if any errors occur
     */
    public void removePermissionFromUser(Long idUser, Long permissionId) throws RemoveRoleFromUserException {
        userLogic.removePermissionFromUser(idUser, permissionId);
    }

    /**
     * Get all active tokens of a specific user.
     * 
     * @param idUser
     *            The id of the user
     * @return All active tokens of the user.
     * @throws GetActiveTokenException
     *             Thrown if any errors occur
     */
    public Collection<Token> getActiveTokensFromUser(Long idUser) throws GetActiveTokenException {
        return userLogic.getActiveTokensFromUser(idUser);
    }

    /**
     * Get token from token value.
     * 
     * @param token
     *            The token value.
     * @return Returns token.
     * @throws GetTokenException
     *             When getting the token fails.
     */
    public Token getToken(String token) throws GetTokenException {
        return tokenLogic.getToken(token);
    }

    /**
     * Add a new permission.
     * 
     * @param permission
     *            The new permission
     * @throws AddPermissionException
     *             Thrown if any errors occur
     */
    public void addPermission(Permission permission) throws AddPermissionException {
        permissionLogic.addPermission(permission);
    }

    /**
     * Delete an existing permission.
     * 
     * @param id
     *            The id of the permission to delete
     * @throws DeletePermissionException
     *             Thrown if any errors occur
     */
    public void deletePermission(Long id) throws DeletePermissionException {
        permissionLogic.deletePermission(id);
    }

    /**
     * Update an existing permission.
     * 
     * @param id
     *            The id of the permission to update
     * @param permission
     *            The data of the new permission
     * @throws UpdatePermissionException
     *             Thrown if any errors occur
     */
    public void updatePermission(Long id, Permission permission) throws UpdatePermissionException {
        permissionLogic.updatePermission(id, permission);
    }

    /**
     * Get an existing permission.
     * 
     * @param id
     *            The id of the permission to get
     * @return The permission.
     * @throws GetPermissionException
     *             Thrown if any errors occur
     */
    public Permission getPermission(Long id) throws GetPermissionException {
        return permissionLogic.getPermission(id);
    }

    /**
     * Get all existing permissions.
     * 
     * @return All permissions in a list
     * @throws GetAllPermissionsException
     *             Thrown if any errors occur
     */
    public Collection<Permission> getAllPermissions() throws GetAllPermissionsException {
        return permissionLogic.getAllPermissions();
    }

    /**
     * Add a new role.
     * 
     * @param role
     *            The role to add
     * @throws AddRoleException
     *             Thrown if any errors occur
     */
    public void addRole(Role role) throws AddRoleException {
        roleLogic.addRole(role);
    }

    /**
     * Delete an existing role.
     * 
     * @param id
     *            The id of the role to delete
     * @throws DeleteRoleException
     *             Thrown if any errors occur
     */
    public void deleteRole(Long id) throws DeleteRoleException {
        roleLogic.deleteRole(id);
    }

    /**
     * Update an existing role.
     * 
     * @param role
     *            The new data for the role
     * @throws UpdateRoleException
     *             Thrown if any errors occur
     */
    public void updateRole(Role role) throws UpdateRoleException {
        roleLogic.updateRole(role);
    }

    /**
     * Get a single role.
     * 
     * @param id
     *            The id of the role to get
     * @return The role
     * @throws GetRoleException
     *             Thrown if any errors occur
     */
    public Role getRole(Long id) throws GetRoleException {
        return roleLogic.getRole(id);
    }

    /**
     * Get all existing roles.
     * 
     * @return All existing roles in a list
     * @throws GetAllRolesException
     *             Thrown if any errors occur.
     */
    public Collection<Role> getAllRoles() throws GetAllRolesException {
        return roleLogic.getAllRoles();
    }

    /**
     * Get all permissions belonging to a role.
     * 
     * @param roleId
     *            The id of the role
     * @return All permissions of the role.
     * @throws GetPermissionsFromRoleException
     *             Thrown if any errors occur
     */
    public Collection<Permission> getAllPermissionsOfRole(Long roleId) throws GetPermissionsFromRoleException {
        return roleLogic.getAllPermissionsFromRole(roleId);
    }

    /**
     * Add an existing permission to an existing role.
     * 
     * @param roleId
     *            The id of the role
     * @param permissionId
     *            The id of the permission, which should be added
     * @throws AddPermissionToRoleException
     *             Thrown if any errors occur
     */
    public void addPermissionToRole(Long roleId, Long permissionId) throws AddPermissionToRoleException {
        roleLogic.addPermissionToRole(roleId, permissionId);
    }

    /**
     * Adds the new permission to user.
     *
     * @param userId
     *            the user id
     * @param permission
     *            the permission
     * @throws GetPermissionException
     *             the get permission exception
     * @throws DatasourceInsertException
     *             the datasource insert exception
     */
    public void addNewPermissionToUser(Long userId, Permission permission) throws GetPermissionException, DatasourceInsertException {
        userLogic.addNewPermissionToUser(userId, permission);
    }

    /**
     * Remove a permission from a role.
     * 
     * @param roleId
     *            The id of the role
     * @param permissionId
     *            The id of the permission, which should be removed
     * @throws RemovePermissionFromRoleException
     *             Thrown if any errors occur
     */
    public void deletePermissionFromRole(Long roleId, Long permissionId) throws RemovePermissionFromRoleException {
        roleLogic.removePermissionFromRole(roleId, permissionId);
    }

    /**
     * Checks if the given role is held by the current user, and fails if not.
     * 
     * @param role
     *            The role.
     * @throws AuthorizationException
     *             If the role is not held.
     */
    public void requiresRole(String role) throws AuthorizationException {
        SecurityUtils.getSubject().checkRole(role);
    }

    /**
     * Checks if the given permission is held by the current user, and fails if not.
     * 
     * @param permission
     *            The permission.
     * @throws AuthorizationException
     *             If the permission is not held.
     */
    public void requiresPermission(String permission) throws AuthorizationException {
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
        } catch (GetUserException e) {
            throw new UnauthenticatedException("Invalid token " + token);
        }
    }

    /**
     * Determines the currently logged-in user. This method will throw a {@link UnauthenticatedException} if the user is currently not
     * logged in.
     * 
     * @return The user who is currently logged in.
     * @throws GetUserException
     *             When the user logged in does not exist anymore in the database.
     */
    public User getCurrentUser() throws GetUserException {
        return getUser(getCurrentUserId());
    }

}
