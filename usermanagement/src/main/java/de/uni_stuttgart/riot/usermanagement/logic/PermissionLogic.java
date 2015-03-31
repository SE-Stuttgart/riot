package de.uni_stuttgart.riot.usermanagement.logic;

import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;

/**
 * Contains all logic regarding the permissions.
 * 
 * @author Niklas Schnabel
 *
 */
public class PermissionLogic {

    private DAO<Permission> dao = new PermissionSqlQueryDAO();

    /**
     * Adds a new permission entry.
     * 
     * @param permission
     *            The permission to be added.
     * @throws UserManagementException
     *             When adding the permission failed.
     */
    public void addPermission(Permission permission) throws UserManagementException {
        try {
            dao.insert(permission);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't add permission", e);
        }
    }

    /**
     * Remove a permission from the system.
     * 
     * @param id
     *            The id of the permission to remove
     * @throws UserManagementException
     *             When deleting the permission failed.
     */
    public void deletePermission(Long id) throws UserManagementException {
        try {
            dao.delete(dao.findBy(id));
        } catch (Exception e) {
            throw new UserManagementException("Couldn't delete permission", e);
        }
    }

    /**
     * Change an existing permission.
     * 
     * @param id
     *            The id of the permission to change
     * @param permission
     *            The new content of the permission
     * @throws UserManagementException
     *             When updating the permission failed.
     */
    public void updatePermission(Long id, Permission permission) throws UserManagementException {
        try {
            dao.update(new Permission(id, permission.getPermissionValue()));
        } catch (Exception e) {
            throw new UserManagementException("Couldn't update permission", e);
        }
    }

    /**
     * Get a specific permission.
     * 
     * @param id
     *            The id of the permission to get
     * @return The retrieved permission
     * @throws UserManagementException
     *             When the permission could not be retrieved.
     */
    public Permission getPermission(Long id) throws UserManagementException {
        try {
            return dao.findBy(id);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get permission", e);
        }
    }

    /**
     * Get a specific permission.
     *
     * @param permissionValue
     *            the permission value
     * @return The retrieved permission
     * @throws UserManagementException
     *             When the permission could not be retrieved.
     */
    public Permission getPermission(String permissionValue) throws UserManagementException {
        try {
            return dao.findByUniqueField(new SearchParameter(SearchFields.PERMISSIONVALUE, permissionValue));
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get permission", e);
        }
    }

    /**
     * Get all existing permissions.
     * 
     * @return All existing permissions in a collection
     * @throws UserManagementException
     *             When retrieving all per missions failed.
     */
    public Collection<Permission> getAllPermissions() throws UserManagementException {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get all permissions", e);
        }
    }
}
