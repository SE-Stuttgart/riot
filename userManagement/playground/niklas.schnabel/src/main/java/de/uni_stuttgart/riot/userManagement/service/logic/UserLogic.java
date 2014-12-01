package de.uni_stuttgart.riot.userManagement.service.logic;

import java.util.List;

import de.uni_stuttgart.riot.userManagement.dao.UserDao;
import de.uni_stuttgart.riot.userManagement.dao.inMemory.UserDaoInMemory;
import de.uni_stuttgart.riot.userManagement.resource.User;
import de.uni_stuttgart.riot.userManagement.service.exception.user.AddUserException;
import de.uni_stuttgart.riot.userManagement.service.exception.user.DeleteUserException;
import de.uni_stuttgart.riot.userManagement.service.exception.user.GetAllUsersException;
import de.uni_stuttgart.riot.userManagement.service.exception.user.GetUserException;
import de.uni_stuttgart.riot.userManagement.service.exception.user.UpdateUserException;

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

    public void updateUser(int id, User user) throws UpdateUserException {
        try {
            user.setId(id);
            dao.updateUser(user);
        } catch (Exception e) {
            throw new UpdateUserException();
        }
    }

    public User getUser(int id) throws GetUserException {
        User user = null;

        try {
            user = dao.getUserById(id);
        } catch (Exception e) {
            throw new GetUserException();
        }

        return user;
    }

    public List<User> getAllUsers() throws GetAllUsersException {
    	List<User> users = null;

        try {
            users = dao.getUsers();
        } catch (Exception e) {
            throw new GetAllUsersException();
        }

        return users;
    }

    public void addPermission() {

    }
}
