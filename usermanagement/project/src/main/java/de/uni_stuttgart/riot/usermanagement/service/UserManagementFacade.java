package de.uni_stuttgart.riot.usermanagement.service;

import java.util.Collection;
import java.util.List;

import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.logic.PermissionLogic;
import de.uni_stuttgart.riot.usermanagement.logic.RoleLogic;
import de.uni_stuttgart.riot.usermanagement.logic.UserLogic;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.AddPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.DeletePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetAllPermissionsException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.UpdatePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.AddRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.DeleteRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetAllRolesException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.UpdateRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.AddUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.DeleteUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetAllUsersException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetUserException;
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
    private RoleLogic roleLogic;
    private PermissionLogic permissionLogic;

    private UserManagementFacade() {
        userLogic = new UserLogic();
        roleLogic = new RoleLogic();
        permissionLogic = new PermissionLogic();
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
     */
    public void generateTokens(String username, String password) {
        // TODO
    }

    /**
     * Generate a new set of bearer and refresh token from a given refresh token.
     * 
     * @param refreshToken
     *            The refresh token used for generating the new tokens
     */
    public void refreshToken(String refreshToken) {
        // TODO
    }

    /**
     * Log an user out be invalidating the bearer and refresh token
     * 
     * @param currentBearerToken
     *            The current bearer token used to authenticate the user. Will no longer be valid after calling this method.
     */
    public void logout(String currentBearerToken) {
        // TODO
    }

    /**
     * Add a new user
     * 
     * @param user
     *            The new user
     * @throws AddUserException
     */
    public void addUser(User user) throws AddUserException {
        userLogic.addUser(user);
    }

    /**
     * Delete an existing user
     * 
     * @param id
     *            Id of the user
     * @throws DeleteUserException
     */
    public void deleteUser(int id) throws DeleteUserException {
        userLogic.deleteUser(id);
    }

    /**
     * Update the data of an existing user
     * 
     * @param id
     *            Id of the user to modify
     * @param user
     *            New user data
     * @throws UpdateUserException
     */
    public void updateUser(int id, User user) throws UpdateUserException {
        userLogic.updateUser(id, user);
    }

    /**
     * Get a single user
     * 
     * @param id
     *            Id of the user to get
     * @return User with the given id
     * @throws GetUserException
     */
    public User getUser(int id) throws GetUserException {
        return userLogic.getUser(id);
    }

    /**
     * Get all existing users
     * 
     * @return All users in a List
     * @throws GetAllUsersException
     */
    public Collection<User> getAllUsers() throws GetAllUsersException {
        return userLogic.getAllUsers();
    }

    /**
     * Add a new role to an existing user
     * 
     * @param id
     *            The id of the user
     * @param role
     *            The role to add
     * @throws AddRoleException
     */
    public void addRoleToUser(int id, Role role) throws AddRoleException {
        roleLogic.addRoleToUser(id, role);
    }

    /**
     * Get all roles from a specific user
     * 
     * @param id
     *            The id of the user to get roles from
     * @return A list with all roles of the given user
     * @throws GetRoleException
     */
    public List<Role> getAllRolesFromUser(int id) throws GetRoleException {
        return roleLogic.getAllRolesFromUser(id);
    }

    /**
     * Remove a role from a specific user
     * 
     * @param idUser
     *            The id of the user
     * @param idRole
     *            The id of the role to remove
     */
    public void removeRoleFromUser(int idUser, int idRole) {
        // TODO
    }

    /**
     * Get all active tokens of a specific user
     * 
     * @param idUser
     *            The id of the user
     */
    public void getActiveTokensFromUser(int idUser) {
        // TODO
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
    public void deletePermission(int id) throws DeletePermissionException {
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
    public void updatePermission(int id, Permission permission) throws UpdatePermissionException {
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
    public Permission getPermission(int id) throws GetPermissionException {
        return permissionLogic.getPermission(id);
    }

    /**
     * Get all existing permissions
     * 
     * @return All permissions in a list
     * @throws GetAllPermissionsException
     */
    public List<Permission> getAllPermissions() throws GetAllPermissionsException {
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
    public void deleteRole(int id) throws DeleteRoleException {
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
    public void updateRole(int id, Role role) throws UpdateRoleException {
        roleLogic.updateRole(id, role);
    }

    /**
     * Get a single role
     * 
     * @param id
     *            The id of the role to get
     * @return The role
     * @throws GetRoleException
     */
    public Role getRole(int id) throws GetRoleException {
        return roleLogic.getRole(id);
    }

    /**
     * Get all existing roles
     * 
     * @return All existing roles in a list
     * @throws GetAllRolesException
     */
    public List<Role> getAllRoles() throws GetAllRolesException {
        return roleLogic.getAllRoles();
    }

    /**
     * Get all permissions belonging to a role
     * 
     * @param roleId
     *            The id of the role
     */
    public void getAllPermissionsOfRole(int roleId) {
        // TODO
    }

    /**
     * Add an existing permission to an existing role
     * 
     * @param roleId
     *            The id of the role
     * @param permission
     *            The id of the permission, which should be added
     */
    public void addPermissionToRole(int roleId, int permissionId) {
        // TODO
    }

    /**
     * Remove a permission from a role
     * 
     * @param rolId
     *            The id of the role
     * @param permissionId
     *            The id of the permission, which should be removed
     */
    public void deletePermissionFromRole(int rolId, int permissionId) {
        // TODO
    }
}
