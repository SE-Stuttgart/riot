package de.uni_stuttgart.riot.userManagement.service;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresRoles;

import de.uni_stuttgart.riot.userManagement.resource.Permission;
import de.uni_stuttgart.riot.userManagement.resource.Role;
import de.uni_stuttgart.riot.userManagement.resource.User;
import de.uni_stuttgart.riot.userManagement.service.exception.permission.AddPermissionException;
import de.uni_stuttgart.riot.userManagement.service.exception.permission.DeletePermissionException;
import de.uni_stuttgart.riot.userManagement.service.exception.permission.GetAllPermissionsException;
import de.uni_stuttgart.riot.userManagement.service.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.userManagement.service.exception.permission.UpdatePermissionException;
import de.uni_stuttgart.riot.userManagement.service.exception.role.AddRoleException;
import de.uni_stuttgart.riot.userManagement.service.exception.role.DeleteRoleException;
import de.uni_stuttgart.riot.userManagement.service.exception.role.GetAllRolesException;
import de.uni_stuttgart.riot.userManagement.service.exception.role.GetRoleException;
import de.uni_stuttgart.riot.userManagement.service.exception.role.UpdateRoleException;
import de.uni_stuttgart.riot.userManagement.service.exception.user.AddUserException;
import de.uni_stuttgart.riot.userManagement.service.exception.user.DeleteUserException;
import de.uni_stuttgart.riot.userManagement.service.exception.user.GetAllUsersException;
import de.uni_stuttgart.riot.userManagement.service.exception.user.GetUserException;
import de.uni_stuttgart.riot.userManagement.service.exception.user.UpdateUserException;
import de.uni_stuttgart.riot.userManagement.service.logic.PermissionLogic;
import de.uni_stuttgart.riot.userManagement.service.logic.RoleLogic;
import de.uni_stuttgart.riot.userManagement.service.logic.UserLogic;

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
     * Add a new user
     * 
     * @param user
     *            The new user
     * @throws AddUserException
     */
    // static authorization check
    @RequiresRoles("Master")
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
    public List<User> getAllUsers() throws GetAllUsersException {
        return userLogic.getAllUsers();
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
     * Get all existing permissions
     * 
     * @return All permissions in a list
     * @throws GetAllPermissionsException
     */
    public List<Permission> getAllPermissionsFromUser(int id) throws GetPermissionException {
        return permissionLogic.getAllPermissionsFromUser(id);
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
}
