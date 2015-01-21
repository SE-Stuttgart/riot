package de.uni_stuttgart.riot.usermanagement.logic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.RolePermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.AddPermissionToRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.AddRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.DeleteRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetAllRolesException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetPermissionsFromRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.RemovePermissionFromRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.UpdateRoleException;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
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
            dao = new RoleSqlQueryDAO(ConnectionMgr.openConnection(), false);
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
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
            if (StringUtils.isEmpty(role.getRoleName())) {
                throw new AddRoleException("You have to specify a role name");
            }

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
            dao.delete(dao.findBy(id));
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
    public void updateRole(Role role) throws UpdateRoleException {
        try {
            if (StringUtils.isEmpty(role.getRoleName())) {
                throw new UpdateRoleException("You have to specify a role name");
            }

            dao.update(role);
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
            return dao.findBy(id);
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

        try {
            DAO<RolePermission> rolePermissionDao = new RolePermissionSqlQueryDAO(ConnectionMgr.openConnection(), false);
            DAO<Permission> permissionDao = new PermissionSqlQueryDAO(ConnectionMgr.openConnection(), false);

            // find all permissions belonging to a role
            Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
            searchParams.add(new SearchParameter(SearchFields.ROLEID, roleId));
            Collection<RolePermission> rolePermissions = rolePermissionDao.findBy(searchParams, false);

            // collection with the associated permissions
            Collection<Permission> permissions = new ArrayList<Permission>();

            // get all associated permissions
            for (RolePermission rolePermission : rolePermissions) {
                permissions.add(permissionDao.findBy(rolePermission.getPermissionID()));
            }
            return permissions;
        } catch (Exception e) {
            throw new GetPermissionsFromRoleException(e);
        }
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
    public void addPermissionToRole(Long roleId, Long permissionId) throws AddPermissionToRoleException {
        try {
            DAO<RolePermission> rolePermissionDao = new RolePermissionSqlQueryDAO(ConnectionMgr.openConnection(), false);
            RolePermission rp = new RolePermission(roleId, permissionId);
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
            if (roleId == null || permissionId == null) {
                throw new RemovePermissionFromRoleException("Role id and permission id can not be null");
            }

            DAO<RolePermission> rolePermissionDao = new RolePermissionSqlQueryDAO(ConnectionMgr.openConnection(), false);

            // get the permission to remove
            Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
            searchParams.add(new SearchParameter(SearchFields.ROLEID, roleId));
            searchParams.add(new SearchParameter(SearchFields.PERMISSIONID, permissionId));
            Collection<RolePermission> rolePermission = rolePermissionDao.findBy(searchParams, false);

            Iterator<RolePermission> i = rolePermission.iterator();
            RolePermission rp = null;

            if (i.hasNext()) {
                rp = i.next();
            } else {
                throw new RemovePermissionFromRoleException("The user with the id " + roleId + " does not have the permission with the id " + permissionId);
            }

            // remove the permission
            rolePermissionDao.delete(rp);
        } catch (Exception e) {
            throw new RemovePermissionFromRoleException(e);
        }
    }
}
