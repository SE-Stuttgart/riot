package de.uni_stuttgart.riot.usermanagement.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.NamingException;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.DatasourceUtil;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.UserRoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserRole;
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
 * Contains all logic regarding an user.
 * 
 * @author Niklas Schnabel
 *
 */
public class UserLogic {

    private DAO<User> dao;

    /**
     * Constructor.
     */
    public UserLogic() {
        try {
            dao = new UserSqlQueryDao(DatasourceUtil.getDataSource());
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new user to the system.
     * 
     * @param user
     *            The new user
     * @throws AddUserException
     */
    public void addUser(User user) throws AddUserException {
        try {
            dao.insert(user);
        } catch (Exception e) {
            throw new AddUserException(e);
        }
    }

    /**
     * Delete an existing user.
     * 
     * @param id
     *            The id of the user
     * @throws DeleteUserException
     */
    public void deleteUser(Long id) throws DeleteUserException {
        try {
            dao.delete(dao.findBy(id)); // TODO ugly
        } catch (Exception e) {
            throw new DeleteUserException(e);
        }
    }

    /**
     * Update an existing user.
     * 
     * @param id
     *            The id of the user
     * @param user
     *            The new content of the user
     * @throws UpdateUserException
     */
    public void updateUser(Long id, User user) throws UpdateUserException {
        try {
            // FIXME given id is ignored
            dao.update(user);
        } catch (Exception e) {
            throw new UpdateUserException(e);
        }
    }

    /**
     * Retrieve an existing user.
     * 
     * @param id
     *            The id of the user
     * @return The user
     * @throws GetUserException
     */
    public User getUser(Long id) throws GetUserException {
        try {
            return dao.findBy(id);
        } catch (Exception e) {
            throw new GetUserException(e);
        }
    }

    public User getUser(String username) throws GetUserException {

        try {
            Collection<SearchParameter> searchParameter = new ArrayList<SearchParameter>();
            searchParameter.add(new SearchParameter(SearchFields.USERNAME, username));

            Collection<User> userRoles = dao.findBy(searchParameter, false);

            Iterator<User> i = userRoles.iterator();

            if (i.hasNext()) {
                return i.next();
            } else {
                throw new GetUserException("User does not exist");
            }
        } catch (Exception e) {
            throw new GetUserException(e);
        }
    }

    /**
     * Retrieve all existing users.
     * 
     * @return Collection containing all users
     * @throws GetAllUsersException
     */
    public Collection<User> getAllUsers() throws GetAllUsersException {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new GetAllUsersException(e);
        }
    }

    /**
     * Add an existing role to an existing user.
     * 
     * @param userId
     *            The id of the user
     * @param roleId
     *            The id of the role
     * @throws AddRoleToUserException
     */
    public void addRoleToUser(Long userId, Long roleId) throws AddRoleToUserException {
        try {
            // FIXME Question: Is there any check if the user and role ids already exist?
            DAO<UserRole> roleDao = new UserRoleSqlQueryDAO(DatasourceUtil.getDataSource());
            UserRole ur = new UserRole(1L, userId, roleId); // FIXME id
            roleDao.insert(ur);
        } catch (Exception e) {
            throw new AddRoleToUserException(e);
        }
    }

    /**
     * Retrieve all roles associated with a user.
     * 
     * @param id
     *            The id of the user
     * @return Collection with roles
     * @throws GetRolesFromUserException
     */
    public Collection<Role> getAllRolesFromUser(Long id) throws GetRolesFromUserException {
        try {
            DAO<UserRole> userRoleDao = new UserRoleSqlQueryDAO(DatasourceUtil.getDataSource());
            Collection<SearchParameter> searchParameter = new ArrayList<SearchParameter>();
            searchParameter.add(new SearchParameter(SearchFields.USERID, id));

            Collection<UserRole> userRoles = userRoleDao.findBy(searchParameter, false);

            DAO<Role> roleDao = new RoleSqlQueryDAO(DatasourceUtil.getDataSource());

            Collection<Role> roles = new ArrayList<Role>();
            for (UserRole userRole : userRoles) {
                roles.add(roleDao.findBy(userRole.getRoleID()));
            }

            return roles;
        } catch (Exception e) {
            throw new GetRolesFromUserException(e);
        }
    }

    /**
     * Remove a role from a user.
     * 
     * @param userId
     *            The id of a user
     * @param roleId
     *            The id of the role
     * @throws GetRolesFromUserException
     */
    public void removeRoleFromUser(Long userId, Long roleId) throws RemoveRoleFromUserException {
        try {
            DAO<UserRole> userRoleDao = new UserRoleSqlQueryDAO(DatasourceUtil.getDataSource());
            UserRole ur = new UserRole(1L, userId, roleId); // FIXME id
            userRoleDao.delete(ur);
        } catch (Exception e) {
            throw new RemoveRoleFromUserException(e);
        }
    }

    /**
     * Get all active Tokens of a user
     * 
     * @param idUser
     *            The id of a user
     * @return Collection with tokens
     * @throws GetActiveTokenException
     */
    public Collection<Token> getActiveTokensFromUser(Long idUser) throws GetActiveTokenException {
        // FIXME return dao.getActiveTokens(idUser);
        return null;
    }
}
