package de.uni_stuttgart.riot.usermanagement.logic;

import java.util.Collection;

import javax.naming.NamingException;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.DatasourceUtil;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.RolePermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.AddPermissionToRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.AddRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.DeleteRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetAllRolesException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetPermissionsFromRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.RemovePermissionFromRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.UpdateRoleException;

/**
 * Contains all logic regarding the roles.
 * 
 * @author Niklas Schnabel
 *
 */
public class RoleLogic {

    private DAO<Role> dao;

    /**
     * Constructor
     */
    public RoleLogic() {
        try {
            dao = new RoleSqlQueryDAO(DatasourceUtil.getDataSource());
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new role to the system.
     * 
     * @param role
     *            The role to add
     * @throws AddRoleException
     */
    public void addRole(Role role) throws AddRoleException {
        try {
            dao.insert(role);
        } catch (Exception e) {
            throw new AddRoleException(e);
        }
    }

    /**
     * Delete an existing role.
     * 
     * @param id
     *            The id of the role to delete
     * @throws DeleteRoleException
     */
    public void deleteRole(Long id) throws DeleteRoleException {
        try {
            // FIXME dao.deleteRole(id); //delete a role by id
            dao.delete(dao.findBy(new Long(id)));
        } catch (Exception e) {
            throw new DeleteRoleException(e);
        }
    }

    /**
     * Update an existing role.
     * 
     * @param id
     *            The id of the role to update
     * @param role
     *            The content of the role to update
     * @throws UpdateRoleException
     */
    public void updateRole(Long id, Role role) throws UpdateRoleException {
        try {
            dao.update(new Role(id, role.getRoleName()));
        } catch (Exception e) {
            throw new UpdateRoleException(e);
        }
    }

    /**
     * Retrieve an existing role.
     * 
     * @param id
     *            The id of the role to retrieve
     * @return Retrieved role
     * @throws GetRoleException
     */
    public Role getRole(Long id) throws GetRoleException {
        try {
            return dao.findBy(new Long(id));
        } catch (Exception e) {
            throw new GetRoleException(e);
        }
    }

    /**
     * Retrieve all existing roles.
     * 
     * @return Collection with all roles
     * @throws GetAllRolesException
     */
    public Collection<Role> getAllRoles() throws GetAllRolesException {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new GetAllRolesException(e);
        }
    }

    /**
     * Get all permissions, that are associated with a role.
     * 
     * @param roleId
     *            The id of the role
     * @return Collection with permissions
     * @throws GetPermissionsFromRoleException
     */
    public Collection<Permission> getAllPermissionsFromRole(Long roleId) throws GetPermissionsFromRoleException {
        // FIXME dao.getPermissionsFromRole(roleId);
        return null;
    }

    /**
     * Add an existing permission to an existing role.
     * 
     * @param roleId
     *            The id of the role
     * @param permissionId
     *            The id of the permission
     * @throws AddPermissionToRoleException
     */
    public void addPermissiontToRole(Long roleId, Long permissionId) throws AddPermissionToRoleException {
        try {
            // FIXME Question: Is there any check if the role and permission ids already exist?
            DAO<RolePermission> rolePermissionDao = new RolePermissionSqlQueryDAO(DatasourceUtil.getDataSource());
            RolePermission rp = new RolePermission(1L, roleId, permissionId); // FIXME id
            rolePermissionDao.insert(rp);
        } catch (Exception e) {
            throw new AddPermissionToRoleException(e);
        }
    }

    /**
     * Remove a permission from a role.
     * 
     * @param roleId
     *            The id of the role
     * @param permissionId
     *            The id of the permission
     * @throws RemovePermissionFromRoleException
     */
    public void removePermissionFromRole(Long roleId, Long permissionId) throws RemovePermissionFromRoleException {
        try {
            // FIXME Question: Is there any check if the role and permission ids already exist?
            DAO<RolePermission> rolePermissionDao = new RolePermissionSqlQueryDAO(DatasourceUtil.getDataSource());
            RolePermission rp = new RolePermission(1L, roleId, permissionId); // FIXME id
            rolePermissionDao.delete(rp);
        } catch (Exception e) {
            throw new RemovePermissionFromRoleException(e);
        }
    }
}
