package de.uni_stuttgart.riot.usermanagement.logic;

import java.util.Collection;

import javax.naming.NamingException;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.DatasourceUtil;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.AddUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.DeleteUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetAllUsersException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.UpdateUserException;

/**
 * Contains all logic regarding an user.
 * 
 * @author Niklas Schnabel
 *
 */
public class UserLogic {

    private DAO<User> dao;

    public UserLogic() {
        try {
            dao = new UserSqlQueryDao(DatasourceUtil.getDataSource());
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void addUser(User user) throws AddUserException {
        try {
            dao.insert(user);
        } catch (Exception e) {
            throw new AddUserException(e);
        }
    }

    public void deleteUser(int id) throws DeleteUserException {
        try {
            dao.delete(dao.findBy(id));
        } catch (Exception e) {
            throw new DeleteUserException(e);
        }
    }

    public void updateUser(int id, User user) throws UpdateUserException {
        try {
            dao.update(user);
        } catch (Exception e) {
            throw new UpdateUserException(e);
        }
    }

    public User getUser(int id) throws GetUserException {
        User user = null;

        try {
            user = dao.findBy(id);
        } catch (Exception e) {
            throw new GetUserException(e);
        }

        return user;
    }

    public Collection<User> getAllUsers() throws GetAllUsersException {
        Collection<User> users = null;
        try {
            users = dao.findAll();
        } catch (Exception e) {
            throw new GetAllUsersException(e);
        }

        return users;
    }

    public void addPermission() {

    }
}
