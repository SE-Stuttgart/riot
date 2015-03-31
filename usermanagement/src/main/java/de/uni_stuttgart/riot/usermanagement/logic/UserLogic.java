package de.uni_stuttgart.riot.usermanagement.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationKey;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.config.Configuration;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.UserPermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.UserRoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.UserSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserPermission;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserRole;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
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
     * @param email
     *            The mail of the user
     * @return The added user.
     * @throws UserManagementException
     *             When adding the user failed.
     */
    public UMUser addUser(String username, String email, String clearTextPassword) throws UserManagementException {
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
                throw new UserManagementException("The password does not meet the requirements");
            }

        } catch (Exception e) {
            throw new UserManagementException("Couldn't add a new user", e);
        }
    }

    /**
     * Delete an existing user.
     * 
     * @param id
     *            The id of the user.
     * @throws UserManagementException
     *             Thrown if any error occurs.
     */
    public void deleteUser(Long id) throws UserManagementException {
        try {
            dao.delete(dao.findBy(id));
        } catch (Exception e) {
            throw new UserManagementException("Couldn't delete user", e);
        }
    }

    /**
     * Update an existing user.
     * 
     * @param user
     *            The new content of the user.
     * @param clearTextPassword
     *            The password of the user as clear text. If the password should not be updated use null.
     * @throws UserManagementException
     *             When updating the user failed.
     */
    public void updateUser(UMUser user, String clearTextPassword) throws UserManagementException {
        try {
            if (!isUserValid(user)) {
                throw new UserManagementException("Username, password and password salt must not be empty or null");
            }

            if (StringUtils.isNotEmpty(clearTextPassword)) {
                PasswordValidator pv = new PasswordValidator();
                if (pv.validate(clearTextPassword)) {
                    user.setLoginAttemptCount(0);
                    hashPassword(user, clearTextPassword);
                } else {
                    throw new UserManagementException("The password does not meet the requirements");
                }
            }

            dao.update(user);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't update user", e);
        }
    }

    /**
     * Retrieve an existing user.
     * 
     * @param id
     *            The user's id.
     * @return The user.
     * @throws UserManagementException
     *             When getting the user failed.
     */
    public UMUser getUser(Long id) throws UserManagementException {
        try {
            return dao.findBy(id);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get user", e);
        }
    }

    /**
     * Retrive an existing user by his name.
     * 
     * @param username
     *            Username of the user.
     * @return The user.
     * @throws UserManagementException
     *             When getting the user failed.
     */
    public UMUser getUser(String username) throws UserManagementException {

        try {
            // search user by user name
            return dao.findByUniqueField(new SearchParameter(SearchFields.USERNAME, username));
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get user", e);
        }
    }

    /**
     * Get user by token by one of his tokens.
     * 
     * @param token
     *            The token of the user.
     * @return Returns user.
     * @throws UserManagementException
     *             When getting the user failed.
     */
    public UMUser getUser(Token token) throws UserManagementException {
        try {
            return dao.findByUniqueField(new SearchParameter(SearchFields.TABLEPK, token.getUserID()));
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get user", e);
        }
    }

    /**
     * Retrieve all existing users.
     * 
     * @return Collection containing all users.
     * @throws UserManagementException
     *             When getting the users failed.
     */
    public Collection<UMUser> getAllUsers() throws UserManagementException {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get all user", e);
        }
    }

    /**
     * Retrieve all roles associated with a user.
     * 
     * @param id
     *            The id of the user
     * @return Collection with roles
     * @throws UserManagementException
     *             When getting the roles failed.
     */
    public Collection<Role> getAllRolesFromUser(Long id) throws UserManagementException {
        if (id == null) {
            throw new UserManagementException("The id must not be null");
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
            throw new UserManagementException("Couldn't get all roles from the user", e);
        }
    }

    /**
     * Add an existing role to an existing user.
     * 
     * @param userId
     *            The id of the user.
     * @param roleId
     *            The id of the role.
     * @throws UserManagementException
     *             When adding the user role failed.
     */
    public void addRoleToUser(Long userId, Long roleId) throws UserManagementException {
        try {
            DAO<UserRole> roleDao = new UserRoleSqlQueryDAO();
            UserRole ur = new UserRole(userId, roleId);
            roleDao.insert(ur);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't add role to user", e);
        }
    }

    /**
     * Remove a role from a user.
     * 
     * @param userId
     *            The id of a user
     * @param roleId
     *            The id of the role
     * @throws UserManagementException
     *             When getting the roles failed.
     */
    public void removeRoleFromUser(Long userId, Long roleId) throws UserManagementException {
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
                throw new UserManagementException("User with the id " + userId + " has not the role with the id " + roleId);
            }
        } catch (Exception e) {
            throw new UserManagementException("Couldn't remove role from user", e);
        }
    }

    /**
     * Retrieve all directly assigned permissions associated with a user.
     * 
     * @param id
     *            The id of the user
     * @return Collection with permissions
     * @throws UserManagementException
     *             When getting the permissions failed.
     */
    public Collection<Permission> getAllPermissionsFromUser(Long id) throws UserManagementException {
        if (id == null) {
            throw new UserManagementException("The id must not be null");
        }

        try {
            DAO<UserPermission> userPermissionDao = new UserPermissionSqlQueryDAO();
            DAO<Permission> permissionDao = new PermissionSqlQueryDAO();

            // get all permissions with the given user id
            Collection<SearchParameter> searchParameter = new ArrayList<SearchParameter>();
            searchParameter.add(new SearchParameter(SearchFields.USERID, id));
            Collection<UserPermission> userPermissions = userPermissionDao.findBy(searchParameter, false);

            // contains all associated permissions
            Collection<Permission> permissions = new ArrayList<Permission>();

            // get all associated permissions
            for (UserPermission userPermission : userPermissions) {
                permissions.add(permissionDao.findBy(userPermission.getPermissionID()));
            }

            return permissions;
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get all permissions from user", e);
        }
    }

    /**
     * Add an existing permission to an existing user.
     * 
     * @param userId
     *            The id of the user.
     * @param permissionId
     *            The id of the permission.
     * @throws UserManagementException
     *             When adding the user permission failed.
     */
    public void addPermissionToUser(Long userId, Long permissionId) throws UserManagementException {
        try {
            DAO<UserPermission> userPermissionDao = new UserPermissionSqlQueryDAO();
            UserPermission userPermission = new UserPermission(userId, permissionId);
            userPermissionDao.insert(userPermission);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't add permission to user", e);
        }
    }

    /**
     * Remove a permission from a user.
     * 
     * @param userId
     *            The id of a user
     * @param permissionId
     *            The id of the permission
     * @throws UserManagementException
     *             When removing the permission failed.
     */
    public void removePermissionFromUser(Long userId, Long permissionId) throws UserManagementException {
        try {
            DAO<UserPermission> userPermissionDao = new UserPermissionSqlQueryDAO();

            Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
            searchParams.add(new SearchParameter(SearchFields.USERID, userId));
            searchParams.add(new SearchParameter(SearchFields.PERMISSIONID, permissionId));
            Collection<UserPermission> userPermissions = userPermissionDao.findBy(searchParams, false);

            Iterator<UserPermission> i = userPermissions.iterator();
            if (i.hasNext()) {
                userPermissionDao.delete(i.next());
            } else {
                throw new UserManagementException("User with the id " + userId + " has not the permission with the id " + permissionId);
            }
        } catch (Exception e) {
            throw new UserManagementException("Couldn't remove permission from user", e);
        }
    }

    /**
     * Get all active Tokens of a user. If no active token exists, a GetActiveTokenException is thrown.
     * 
     * @param userId
     *            The id of a user.
     * @return Collection with tokens.
     * @throws UserManagementException
     *             When getting the token failed.
     */
    public Collection<Token> getActiveTokensFromUser(Long userId) throws UserManagementException {
        if (userId == null) {
            throw new UserManagementException("The id must not be null");
        }

        try {
            DAO<Token> tokenDao = new TokenSqlQueryDAO();

            // get all active tokens with the given user id
            Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
            searchParams.add(new SearchParameter(SearchFields.USERID, userId));
            searchParams.add(new SearchParameter(SearchFields.TOKENVALID, true));

            return tokenDao.findBy(searchParams, false);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get tokens from user", e);
        }
    }

    /**
     * Create a new permission if it is not already created and add it to the user.
     *
     * @param userId
     *            the user id
     * @param permission
     *            the permission
     * @throws UserManagementException
     *             When inserting the new permission-user-relation failed or retrieving the permission failed.
     */
    public void addNewPermissionToUser(Long userId, Permission permission) throws UserManagementException {
        PermissionLogic pl = new PermissionLogic();
        try {
            pl.addPermission(permission);
        } catch (UserManagementException e) {
            // permission is already created.
            if (permission.getId() < 0) {
                permission.setId(pl.getPermission(permission.getPermissionValue()).getId());
            }
        }

        DAO<UserPermission> upDao = new UserPermissionSqlQueryDAO();
        try {
            upDao.insert(new UserPermission(userId, permission.getId()));
        } catch (DatasourceInsertException e) {
            throw new UserManagementException(e);
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
