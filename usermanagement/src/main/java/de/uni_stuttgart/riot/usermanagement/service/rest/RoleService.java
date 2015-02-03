package de.uni_stuttgart.riot.usermanagement.service.rest;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetPermissionsFromRoleException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;
import de.uni_stuttgart.riot.usermanagement.service.rest.exception.UserManagementExceptionMapper;

/**
 * The roles service will handle any access (create, read, update, delete) to the roles.
 *
 * @author Marcel Lehwald
 *
 */
@Path("roles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoleService extends BaseResource<Role> {

    UserManagementFacade facade = UserManagementFacade.getInstance();

    /**
     * Constructor.
     */
    public RoleService() {
        super(new RoleSqlQueryDAO());
    }

    /**
     * Get permissions of a role. FIXME This method seems to have the wrong name (has nothing to do with users?).
     *
     * @param roleID
     *            The role ID.
     * @return Returns a list of all permissions of a role.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    @Path("/{roleID}/permissions")
    @RequiresAuthentication
    public Collection<Permission> getUserRoles(@PathParam("roleID") Long roleID) throws UserManagementException {
        // TODO limit returned permissions
        return facade.getAllPermissionsOfRole(roleID).stream().collect(Collectors.toList());
    }

    /**
     * Add permission to a role.
     *
     * @param roleID
     *            The role ID.
     * @param permissionID
     *            The permission ID.
     * @return Returns empty response (with status code 200) on success.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @PUT
    @Path("/{roleID}/permissions/{permissionID}")
    @RequiresAuthentication
    public Response addUserRole(@PathParam("roleID") Long roleID, @PathParam("permissionID") Long permissionID) throws UserManagementException {
        facade.addPermissionToRole(roleID, permissionID);

        return Response.ok().build();
    }

    /**
     * Remove permission of a role.
     *
     * @param roleID
     *            The role ID.
     * @param permissionID
     *            The permission ID.
     * @return Returns empty response (with status code 200) on success.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @DELETE
    @Path("/{roleID}/permissions/{permissionID}")
    @RequiresAuthentication
    public Response removeUserRole(@PathParam("roleID") Long roleID, @PathParam("permissionID") Long permissionID) throws UserManagementException {
        facade.deletePermissionFromRole(roleID, permissionID);

        return Response.ok().build();
    }

    private Collection<Permission> getRolePermissions(Role role) throws GetPermissionsFromRoleException {
        Collection<Permission> permissions = UserManagementFacade.getInstance().getAllPermissionsOfRole(role.getId());
        Collection<Permission> permissionsResult = new LinkedList<Permission>();
        for (Permission permission : permissions) {
            permissionsResult.add(permission);
        }
        return permissionsResult;
    }

    @Override
    public void init(Role storable) throws Exception {
        storable.setPermissions(this.getRolePermissions(storable));
    }

}
