package de.uni_stuttgart.riot.usermanagement.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationKey;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.config.Configuration;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.UserRoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.UserSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
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
import de.uni_stuttgart.riot.usermanagement.security.AuthenticationUtil;
import de.uni_stuttgart.riot.usermanagement.security.PasswordValidator;

/**
 * Contains all logic regarding an user.
 * 
 * @author Niklas Schnabel
 *
 */
public class UserLogic {

    private DAO<UMUser> dao = new UserSqlQueryDAO();

    /**
     * Add a new user to the system.
     * 
     * @param username
     *            The new user.
     * @param clearTextPassword
     *            The password of the user as clear text.
     * @return The added user.
     * @throws AddUserException
     *             When adding the user failed.
     * 
     * 
     */
    public UMUser addUser(String username, String email,String clearTextPassword) throws AddUserException {
        try {
            Validate.notEmpty(username, "username must not be empty");
            Validate.notEmpty(clearTextPassword, "clearTextPassword must not be empty");

            PasswordValidator pv = new PasswordValidator();
            if (pv.validate(clearTextPassword)) {
                UMUser user = new UMUser(username, email);
                hashPassword(user, clearTextPassword);

                dao.insert(user);

                return user;
            } else {
                throw new AddUserException("The password does not meet the requirements");
            }

        } catch (Exception e) {
            throw new AddUserException(e);
        }
    }

    /**
     * Delete an existing user.
     * 
     * @param id
     *            The id of the user.
     * @throws DeleteUserException
     *             Thrown if any error occurs.
     */
    public void deleteUser(Long id) throws DeleteUserException {
        try {
            dao.delete(dao.findBy(id));
        } catch (Exception e) {
            throw new DeleteUserException(e);
        }
    }

    /**
     * Update an existing user.
     * 
     * @param user
     *            The new content of the user.
     * @param clearTextPassword
     *            The password of the user as clear text. If the password should not be updated use null.
     * @throws UpdateUserException
     *             When updating the user failed.
     */
    public void updateUser(UMUser user, String clearTextPassword) throws UpdateUserException {
        try {
            if (!isUserValid(user)) {
                throw new UpdateUserException("Username, password and password salt must not be empty or null");
            }

            if (StringUtils.isNotEmpty(clearTextPassword)) {
                PasswordValidator pv = new PasswordValidator();
                if (pv.validate(clearTextPassword)) {
                    user.setLoginAttemptCount(0);
                    hashPassword(user, clearTextPassword);
                } else {
                    throw new AddUserException("The password does not meet the requirements");
                }
            }

            dao.update(user);
        } catch (Exception e) {
            throw new UpdateUserException(e);
        }
    }

    /**
     * Retrieve an existing user.
     * 
     * @param id
     *            The user's id.
     * @return The user.
     * @throws GetUserException
     *             When getting the user failed.
     */
    public UMUser getUser(Long id) throws GetUserException {
        try {
            return dao.findBy(id);
        } catch (Exception e) {
            throw new GetUserException(e);
        }
    }

    /**
     * Retrive an existing user by his name.
     * 
     * @param username
     *            Username of the user.
     * @return The user.
     * @throws GetUserException
     *             When getting the user failed.
     */
    public UMUser getUser(String username) throws GetUserException {

        try {
            // search user by user name
            return dao.findByUniqueField(new SearchParameter(SearchFields.USERNAME, username));
        } catch (Exception e) {
            throw new GetUserException(e);
        }
    }

    /**
     * Get user by token by one of his tokens.
     * 
     * @param token
     *            The token of the user.
     * @return Returns user.
     * @throws GetUserException
     *             When getting the user failed.
     */
    public UMUser getUser(Token token) throws GetUserException {
        try {
            return dao.findByUniqueField(new SearchParameter(SearchFields.TABLEPK, token.getUserID()));
        } catch (Exception e) {
            throw new GetUserException(e);
        }
    }

    /**
     * Retrieve all existing users.
     * 
     * @return Collection containing all users.
     * @throws GetAllUsersException
     *             When getting the users failed.
     */
    public Collection<UMUser> getAllUsers() throws GetAllUsersException {
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
     *            The id of the user.
     * @param roleId
     *            The id of the role.
     * @throws AddRoleToUserException
     *             When adding the user role failed.
     */
    public void addRoleToUser(Long userId, Long roleId) throws AddRoleToUserException {
        try {
            DAO<UserRole> roleDao = new UserRoleSqlQueryDAO();
            UserRole ur = new UserRole(userId, roleId);
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
     *             When getting the roles failed.
     */
    public Collection<Role> getAllRolesFromUser(Long id) throws GetRolesFromUserException {
        if (id == null) {
            throw new GetRolesFromUserException("The id must not be null");
        }

        try {
            DAO<UserRole> userRoleDao = new UserRoleSqlQueryDAO();
            DAO<Role> roleDao = new RoleSqlQueryDAO();

            // get all roles with the given user id
            Collection<SearchParameter> searchParameter = new ArrayList<SearchParameter>();
            searchParameter.add(new SearchParameter(SearchFields.USERID, id));
            Collection<UserRole> userRoles = userRoleDao.findBy(searchParameter, false);

            // contains all associated roles
            Collection<Role> roles = new ArrayList<Role>();

            // get all associated roles
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
     * @throws RemoveRoleFromUserException
     *             When getting the roles failed.
     */
    public void removeRoleFromUser(Long userId, Long roleId) throws RemoveRoleFromUserException {
        try {
            DAO<UserRole> userRoleDao = new UserRoleSqlQueryDAO();

            Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
            searchParams.add(new SearchParameter(SearchFields.USERID, userId));
            searchParams.add(new SearchParameter(SearchFields.ROLEID, roleId));
            Collection<UserRole> userRoles = userRoleDao.findBy(searchParams, false);

            Iterator<UserRole> i = userRoles.iterator();
            if (i.hasNext()) {
                userRoleDao.delete(i.next());
            } else {
                throw new RemoveRoleFromUserException("User with the id " + userId + " has not the role with the id " + roleId);
            }
        } catch (Exception e) {
            throw new RemoveRoleFromUserException(e);
        }
    }

    /**
     * Get all active Tokens of a user. If no active token exists, a GetActiveTokenException is thrown.
     * 
     * @param userId
     *            The id of a user.
     * @return Collection with tokens.
     * @throws GetActiveTokenException
     *             When getting the token failed.
     */
    public Collection<Token> getActiveTokensFromUser(Long userId) throws GetActiveTokenException {
        if (userId == null) {
            throw new GetActiveTokenException("The id must not be null");
        }

        try {
            DAO<Token> tokenDao = new TokenSqlQueryDAO();

            // get all active tokens with the given user id
            Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
            searchParams.add(new SearchParameter(SearchFields.USERID, userId));
            searchParams.add(new SearchParameter(SearchFields.TOKENVALID, true));

            return tokenDao.findBy(searchParams, false);
        } catch (Exception e) {
            throw new GetActiveTokenException(e);
        }
    }

    /**
     * Test if the user is valid.
     * 
     * @param user
     *            The user to test.
     * @return true if valid, else false.
     */
    private boolean isUserValid(UMUser user) {

        return StringUtils.isNotEmpty(user.getHashedPassword()) && StringUtils.isNotEmpty(user.getPasswordSalt()) //
                && StringUtils.isNotEmpty(user.getUsername()) && user.getHashIterations() > 0 && user.getId() >= 0;
    }

    /**
     * Hash the password of a user.
     * 
     * @param user
     *            The user of whom the password shall be hashed.
     */
    private void hashPassword(UMUser user, String clearTextPassword) {
        user.setHashIterations(Configuration.getInt(ConfigurationKey.um_hashIterations));

        user.setPasswordSalt(AuthenticationUtil.generateSalt());

        String hashedPassword = AuthenticationUtil.getHashedString(clearTextPassword, user.getPasswordSalt(), user.getHashIterations());
        user.setHashedPassword(hashedPassword);
    }
}
