package de.uni_stuttgart.riot.userManagement.logic;

import java.util.Collection;
import java.util.List;

import de.uni_stuttgart.riot.userManagement.data.DAO;
import de.uni_stuttgart.riot.userManagement.data.memorydao.MemoryDAO;
import de.uni_stuttgart.riot.userManagement.data.storable.User;
import de.uni_stuttgart.riot.userManagement.logic.exception.user.AddUserException;
import de.uni_stuttgart.riot.userManagement.logic.exception.user.DeleteUserException;
import de.uni_stuttgart.riot.userManagement.logic.exception.user.GetAllUsersException;
import de.uni_stuttgart.riot.userManagement.logic.exception.user.GetUserException;
import de.uni_stuttgart.riot.userManagement.logic.exception.user.UpdateUserException;

/**
 * 
 * @author Niklas Schnabel
 *
 */
public class UserLogic {

    private DAO<User> dao = new MemoryDAO<User>();

    public void addUser(User user) throws AddUserException {
        try {
            dao.insert(user);
        } catch (Exception e) {
            throw new AddUserException(e.getMessage(),e);
        }
    }

    public void deleteUser(int id) throws DeleteUserException {
        try {
            dao.delete(dao.findBy(id));
        } catch (Exception e) {
            throw new DeleteUserException(e.getMessage(),e);
        }
    }

    public void updateUser(int id, User user) throws UpdateUserException {
        try {
            dao.update(user);
        } catch (Exception e) {
            throw new UpdateUserException(e.getMessage(),e);
        }
    }

    public User getUser(int id) throws GetUserException {
        User user = null;

        try {
            user = dao.findBy(id);
        } catch (Exception e) {
            throw new GetUserException(e.getMessage(),e);
        }

        return user;
    }

    public Collection<User> getAllUsers() throws GetAllUsersException {
    	Collection<User> users = null;
        try {
            users = dao.findAll();
        } catch (Exception e) {
            throw new GetAllUsersException(e.getMessage(),e);
        }

        return users;
    }

    public void addPermission() {

    }
}
