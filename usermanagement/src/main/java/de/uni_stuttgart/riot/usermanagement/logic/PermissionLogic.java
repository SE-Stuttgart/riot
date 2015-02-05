package de.uni_stuttgart.riot.usermanagement.logic;

import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.AddPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.DeletePermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetAllPermissionsException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.UpdatePermissionException;

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
     * @throws AddPermissionException
     *             When adding the permission failed.
     */
    public void addPermission(Permission permission) throws AddPermissionException {
        try {
            dao.insert(permission);
        } catch (Exception e) {
            throw new AddPermissionException(e);
        }
    }

    /**
     * Remove a permission from the system.
     * 
     * @param id
     *            The id of the permission to remove
     * @throws DeletePermissionException
     *             When deleting the permission failed.
     */
    public void deletePermission(Long id) throws DeletePermissionException {
        try {
            dao.delete(dao.findBy(id));
        } catch (Exception e) {
            throw new DeletePermissionException(e);
        }
    }

    /**
     * Change an existing permission.
     * 
     * @param id
     *            The id of the permission to change
     * @param permission
     *            The new content of the permission
     * @throws UpdatePermissionException
     *             When updating the permission failed.
     */
    public void updatePermission(Long id, Permission permission) throws UpdatePermissionException {
        try {
            dao.update(new Permission(id, permission.getPermissionValue()));
        } catch (Exception e) {
            throw new UpdatePermissionException(e);
        }
    }

    /**
     * Get a specific permission.
     * 
     * @param id
     *            The id of the permission to get
     * @return The retrieved permission
     * @throws GetPermissionException
     *             When the permission could not be retrieved.
     */
    public Permission getPermission(Long id) throws GetPermissionException {
        try {
            return dao.findBy(id);
        } catch (Exception e) {
            throw new GetPermissionException(e);
        }
    }

    /**
     * Get all existing permissions.
     * 
     * @return All existing permissions in a collection
     * @throws GetAllPermissionsException
     *             When retrieving all per missions failed.
     */
    public Collection<Permission> getAllPermissions() throws GetAllPermissionsException {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new GetAllPermissionsException(e);
        }
    }
}
