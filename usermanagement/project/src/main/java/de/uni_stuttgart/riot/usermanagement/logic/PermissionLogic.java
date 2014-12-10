package de.uni_stuttgart.riot.usermanagement.logic;

import java.util.Collection;

import javax.naming.NamingException;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.DatasourceUtil;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
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

    private DAO<Permission> dao;

    /**
     * Constructor.
     */
    public PermissionLogic() {
        try {
            dao = new PermissionSqlQueryDAO(DatasourceUtil.getDataSource());
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

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
     */
    public void deletePermission(Long id) throws DeletePermissionException {
        try {
            dao.delete(dao.findBy(id));
        } catch (Exception e) {
            throw new DeletePermissionException(e);
        }
    }

    /**
     * Change an existing permission
     * 
     * @param id
     *            The id of the permission to change
     * @param permission
     *            The new content of the permission
     * @throws UpdatePermissionException
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
     */
    public Collection<Permission> getAllPermissions() throws GetAllPermissionsException {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new GetAllPermissionsException(e);
        }
    }
}
