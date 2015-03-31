package de.uni_stuttgart.riot.usermanagement.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.RolePermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;

/**
 * Contains all logic regarding the roles.
 * 
 * @author Niklas Schnabel
 *
 */
public class RoleLogic {

    private DAO<Role> dao = new RoleSqlQueryDAO();

    /**
     * Add a new role to the system.
     * 
     * @param role
     *            The role to add
     * @throws UserManagementException
     *             When adding the role fails.
     */
    public void addRole(Role role) throws UserManagementException {
        try {
            if (StringUtils.isEmpty(role.getRoleName())) {
                throw new UserManagementException("You have to specify a role name");
            }

            dao.insert(role);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't add role", e);
        }
    }

    /**
     * Delete an existing role.
     * 
     * @param id
     *            The id of the role to delete
     * @throws UserManagementException
     *             When deleting the role fails.
     */
    public void deleteRole(Long id) throws UserManagementException {
        try {
            dao.delete(dao.findBy(id));
        } catch (Exception e) {
            throw new UserManagementException("Couldn't delete role", e);
        }
    }

    /**
     * Update an existing role.
     * 
     * @param role
     *            The content of the role to update
     * @throws UserManagementException
     *             When updating the role fails.
     */
    public void updateRole(Role role) throws UserManagementException {
        try {
            if (StringUtils.isEmpty(role.getRoleName())) {
                throw new UserManagementException("You have to specify a role name");
            }

            dao.update(role);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't update role", e);
        }
    }

    /**
     * Retrieve an existing role.
     * 
     * @param id
     *            The id of the role to retrieve
     * @return Retrieved role
     * @throws UserManagementException
     *             When getting the role fails.
     */
    public Role getRole(Long id) throws UserManagementException {
        try {
            return dao.findBy(id);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get role", e);
        }
    }

    /**
     * Retrieve all existing roles.
     * 
     * @return Collection with all roles
     * @throws UserManagementException
     *             When getting the roles fails.
     */
    public Collection<Role> getAllRoles() throws UserManagementException {
        try {
            return dao.findAll();
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get all roles", e);
        }
    }

    /**
     * Get all permissions, that are associated with a role.
     * 
     * @param roleId
     *            The id of the role.
     * @return Collection with permissions.
     * @throws UserManagementException
     *             When getting the permissions fails.
     */
    public Collection<Permission> getAllPermissionsFromRole(Long roleId) throws UserManagementException {
        if (roleId == null) {
            throw new UserManagementException("roleId must not be null!");
        }
        try {
            DAO<RolePermission> rolePermissionDao = new RolePermissionSqlQueryDAO();
            DAO<Permission> permissionDao = new PermissionSqlQueryDAO();

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
            throw new UserManagementException("Couldn't get all permissions from role", e);
        }
    }

    /**
     * Add an existing permission to an existing role.
     * 
     * @param roleId
     *            The id of the role
     * @param permissionId
     *            The id of the permission
     * @throws UserManagementException
     *             When adding the permissions fails.
     */
    public void addPermissionToRole(Long roleId, Long permissionId) throws UserManagementException {
        try {
            DAO<RolePermission> rolePermissionDao = new RolePermissionSqlQueryDAO();
            RolePermission rp = new RolePermission(roleId, permissionId);
            rolePermissionDao.insert(rp);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't add permission to role", e);
        }
    }

    /**
     * Remove a permission from a role.
     * 
     * @param roleId
     *            The id of the role.
     * @param permissionId
     *            The id of the permission.
     * @throws UserManagementException
     *             When removing the permission fails.
     */
    public void removePermissionFromRole(Long roleId, Long permissionId) throws UserManagementException {
        try {
            if (roleId == null || permissionId == null) {
                throw new UserManagementException("Role id and permission id can not be null");
            }

            DAO<RolePermission> rolePermissionDao = new RolePermissionSqlQueryDAO();

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
                throw new UserManagementException("The user with the id " + roleId + " does not have the permission with the id " + permissionId);
            }

            // remove the permission
            rolePermissionDao.delete(rp);
        } catch (Exception e) {
            throw new UserManagementException("Couldn't remove permission from role", e);
        }
    }
}
