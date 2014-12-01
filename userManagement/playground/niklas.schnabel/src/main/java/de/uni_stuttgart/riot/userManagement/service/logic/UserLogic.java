package de.uni_stuttgart.riot.userManagement.service.logic;

import java.util.List;

import javax.ws.rs.core.Response;

import de.uni_stuttgart.riot.userManagement.dao.UserDao;
import de.uni_stuttgart.riot.userManagement.dao.inMemory.UserDaoInMemory;
import de.uni_stuttgart.riot.userManagement.resource.User;
import de.uni_stuttgart.riot.userManagement.service.UserService.UserServiceError;
import de.uni_stuttgart.riot.userManagement.service.exception.AddUserException;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiErrorResponse;
import de.uni_stuttgart.riot.userManagement.service.exception.DeleteUserException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class UserLogic {

    private UserDao dao = new UserDaoInMemory();

    public void addUser(User user) throws AddUserException {
        // SecurityUtils.getSubject().checkRole("foo:" + user.getId()); //dynamic authorization check
        try {
            dao.insertUser(user);
        } catch (Exception e) {
            throw new AddUserException();
        }
    }

    public void deleteUser(int id) throws DeleteUserException {
        try {
            dao.deleteUser(dao.getUserById(id));
        } catch (Exception e) {
            throw new DeleteUserException();
        }
    }

    public Response updateUser(int id, User user) {
        try {
            user.setId(id);
            dao.updateUser(user);
        } catch (Exception e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, UserServiceError.FOO_ERROR);
        }

        return Response.ok().build();
    }

    public User getUser(int id) {
        User user = null;

        try {
            user = dao.getUserById(id);
        } catch (Exception e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, UserServiceError.SOME_ERROR);
        }

        return user;
    }

    public List<User> getAllUsers() {
        return dao.getUsers();
    }

    public void addPermission() {

    }
}
