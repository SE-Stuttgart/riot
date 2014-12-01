package de.uni_stuttgart.riot.userManagement.service;

import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresRoles;

import de.uni_stuttgart.riot.userManagement.resource.User;
import de.uni_stuttgart.riot.userManagement.service.exception.AddUserException;
import de.uni_stuttgart.riot.userManagement.service.exception.DeleteUserException;
import de.uni_stuttgart.riot.userManagement.service.logic.UserLogic;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class UserManagementFacade {

    private static UserManagementFacade instance = null;

    private UserLogic userLogic;

    private UserManagementFacade() {
        userLogic = new UserLogic();
    }

    public static UserManagementFacade getInstance() {
        if (instance == null) {
            instance = new UserManagementFacade();
        }
        return instance;
    }

    // static authorization check
    @RequiresRoles("Master")
    public void addUser(User user) throws AddUserException {
        userLogic.addUser(user);
    }

    public void deleteUser(int id) throws DeleteUserException {
        userLogic.deleteUser(id);
    }

    public Response updateUser(int id, User user) {
        return userLogic.updateUser(id, user);
    }

    public User getUser(int id) {
        return userLogic.getUser(id);
    }

    public List<User> getAllUsers() {
        return userLogic.getAllUsers();
    }

    public void addPermission() {

    }

    public void deletePermission() {

    }

    public void getPermission() {

    }

    public void getAllPermissions() {

    }

    public void addRole() {

    }

    public void deleteRole() {

    }

    public void updateRole() {

    }

    public void getRole() {

    }

    public void getAllRoles() {

    }

    public void getAllRolesFromUser() {

    }
}
