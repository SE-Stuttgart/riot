package de.uni_stuttgart.riot.usermanagement.service.facade;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;

import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.logic.AuthenticationLogic;
import de.uni_stuttgart.riot.usermanagement.logic.PermissionLogic;
import de.uni_stuttgart.riot.usermanagement.logic.RoleLogic;
import de.uni_stuttgart.riot.usermanagement.logic.TokenLogic;
import de.uni_stuttgart.riot.usermanagement.logic.UserLogic;
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
     * Get the instance of the facade
     * 
     * @return Instance of the facade
     */
    public static UserManagementFacade getInstance() {
        if (instance == null) {
            instance = new UserManagementFacade();
        }
        return instance;
    }

    /**
     * Generate one bearer and one refresh token
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
     * @throws LoginException
     *             Thrown if any errors occur
     */
    public Token refreshToken(String refreshToken) throws RefreshException {
        return authLogic.refreshToken(refreshToken);
    }

    /**
     * Log an user out be invalidating the bearer and refresh token
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
     *            The user name of the user
     * @param clearTextPassword
     *            The password of the user as clear text
     * @return The added user
     * @throws AddUserException
     */
    public User addUser(String username, String clearTextPassword) throws AddUserException {
        return (User) userLogic.addUser(username, clearTextPassword);
    }

    /**
     * Delete an existing user
     * 
     * @param id
     *            Id of the user
     * @throws DeleteUserException
     */
    public void deleteUser(Long id) throws DeleteUserException {
        userLogic.deleteUser(id);
    }

    /**
     * Update the data of an existing user
     * 
     * @param user
     *            New user data
     * @param clearTextPassword
     *            The password of the user as clear text. If the password should not be updated use null.
     * @throws UpdateUserException
     */
    public void updateUser(User user, String clearTextPassword) throws UpdateUserException {
        if (user instanceof UMUser) {
            userLogic.updateUser((UMUser) user, clearTextPassword);
        } else {
            throw new UpdateUserException("The user is not an instance of a UMUser");
        }
    }

    /**
     * Get a single user
     * 
     * @param id
     *            Id of the user to get
     * @return User with the given id
     * @throws GetUserException
     */
    public User getUser(Long id) throws GetUserException {
        return (User) userLogic.getUser(id);
    }

    /**
     * Get user by token.
     * 
     * @param token The token of the user.
     * @return Returns user.
     * @throws GetUserException
     */
	public User getUser(Token token) throws GetUserException {
		return (User) userLogic.getUser(token);
	}

    /**
     * Get all existing users
     * 
     * @return All users in a List
     * @throws GetAllUsersException
     */
    public Collection<User> getAllUsers() throws GetAllUsersException {
        ArrayList<User> users = new ArrayList<User>();
        users.addAll(userLogic.getAllUsers());
        return users;
    }

    /**
     * Add a new role to an existing user
     * 
     * @param userId
     *            The id of the user
     * @param roleId
     *            The id of the role
     * @throws AddRoleToUserException
     */
    public void addRoleToUser(Long userId, Long roleId) throws AddRoleToUserException {
        userLogic.addRoleToUser(userId, roleId);
    }

    /**
     * Get all roles from a specific user
     * 
     * @param id
     *            The id of the user to get roles from
     * @return A list with all roles of the given user
     * @throws GetRolesFromUserException
     */
    public Collection<Role> getAllRolesFromUser(Long id) throws GetRolesFromUserException {
        return userLogic.getAllRolesFromUser(id);
    }

    /**
     * Remove a role from a specific user
     * 
     * @param idUser
     *            The id of the user
     * @param idRole
     *            The id of the role to remove
     * @throws RemoveRoleFromUserException
     */
    public void removeRoleFromUser(Long idUser, Long idRole) throws RemoveRoleFromUserException {
        userLogic.removeRoleFromUser(idUser, idRole);
    }

    /**
     * Get all active tokens of a specific user
     * 
     * @param idUser
     *            The id of the user
     * @throws GetActiveTokenException
     */
    public Collection<Token> getActiveTokensFromUser(Long idUser) throws GetActiveTokenException {
        return userLogic.getActiveTokensFromUser(idUser);
    }

    /**
     * Get token from token value.
     * 
     * @param token The token value.
     * @return Returns token.
     * @throws GetTokenException 
     */
	public Token getToken(String token) throws GetTokenException {
		return tokenLogic.getToken(token);
	}

    /**
     * Add a new permission
     * 
     * @param permission
     *            The new permission
     * @throws AddPermissionException
     */
    public void addPermission(Permission permission) throws AddPermissionException {
        permissionLogic.addPermission(permission);
    }

    /**
     * Delete an existing permission
     * 
     * @param id
     *            The id of the permission to delete
     * @throws DeletePermissionException
     */
    public void deletePermission(Long id) throws DeletePermissionException {
        permissionLogic.deletePermission(id);
    }

    /**
     * Update an existing permission
     * 
     * @param id
     *            The id of the permission to update
     * @param permission
     *            The data of the new permission
     * @throws UpdatePermissionException
     */
    public void updatePermission(Long id, Permission permission) throws UpdatePermissionException {
        permissionLogic.updatePermission(id, permission);
    }

    /**
     * Get an existing permission
     * 
     * @param id
     *            The id of the permission to get
     * @return
     * @throws GetPermissionException
     */
    public Permission getPermission(Long id) throws GetPermissionException {
        return permissionLogic.getPermission(id);
    }

    /**
     * Get all existing permissions
     * 
     * @return All permissions in a list
     * @throws GetAllPermissionsException
     */
    public Collection<Permission> getAllPermissions() throws GetAllPermissionsException {
        return permissionLogic.getAllPermissions();
    }

    /**
     * Add a new role
     * 
     * @param role
     *            The role to add
     * @throws AddRoleException
     */
    public void addRole(Role role) throws AddRoleException {
        roleLogic.addRole(role);
    }

    /**
     * Delete an existing role
     * 
     * @param id
     *            The id of the role to delete
     * @throws DeleteRoleException
     */
    public void deleteRole(Long id) throws DeleteRoleException {
        roleLogic.deleteRole(id);
    }

    /**
     * Update an existing role
     * 
     * @param id
     *            The id of the role to update
     * @param role
     *            The new data for the role
     * @throws UpdateRoleException
     */
    public void updateRole(Role role) throws UpdateRoleException {
        roleLogic.updateRole(role);
    }

    /**
     * Get a single role
     * 
     * @param id
     *            The id of the role to get
     * @return The role
     * @throws GetRoleException
     */
    public Role getRole(Long id) throws GetRoleException {
        return roleLogic.getRole(id);
    }

    /**
     * Get all existing roles
     * 
     * @return All existing roles in a list
     * @throws GetAllRolesException
     */
    public Collection<Role> getAllRoles() throws GetAllRolesException {
        return roleLogic.getAllRoles();
    }

    /**
     * Get all permissions belonging to a role
     * 
     * @param roleId
     *            The id of the role
     * @throws GetPermissionsFromRoleException
     */
    public Collection<Permission> getAllPermissionsOfRole(Long roleId) throws GetPermissionsFromRoleException {
        return roleLogic.getAllPermissionsFromRole(roleId);
    }

    /**
     * Add an existing permission to an existing role
     * 
     * @param roleId
     *            The id of the role
     * @param permission
     *            The id of the permission, which should be added
     * @throws AddPermissionToRoleException
     */
    public void addPermissionToRole(Long roleId, Long permissionId) throws AddPermissionToRoleException {
        roleLogic.addPermissionToRole(roleId, permissionId);
    }

    /**
     * Remove a permission from a role
     * 
     * @param rolId
     *            The id of the role
     * @param permissionId
     *            The id of the permission, which should be removed
     * @throws RemovePermissionFromRoleException
     */
    public void deletePermissionFromRole(Long roleId, Long permissionId) throws RemovePermissionFromRoleException {
        roleLogic.removePermissionFromRole(roleId, permissionId);
    }

    public void requiresRole(String role) throws AuthorizationException {
        SecurityUtils.getSubject().checkRole(role);
    }

    public void requiresPermission(String permission) throws AuthorizationException {
        SecurityUtils.getSubject().checkPermission(permission);
    }

}
