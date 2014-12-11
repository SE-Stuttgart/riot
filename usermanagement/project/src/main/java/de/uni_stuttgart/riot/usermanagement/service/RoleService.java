package de.uni_stuttgart.riot.usermanagement.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.service.exception.UserManagementExceptionMapper;
import de.uni_stuttgart.riot.usermanagement.service.response.PermissionResponse;
import de.uni_stuttgart.riot.usermanagement.service.response.RoleResponse;

/**
 * The roles service will handle any access (create, read, update, delete) to the roles.
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/roles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoleService {

    UserManagementFacade facade = UserManagementFacade.getInstance();

    /**
     * Get all roles.
     * 
     * @return Returns a list of all roles.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    public Collection<RoleResponse> getRoles() throws UserManagementException {
        // TODO limit returned roles
        Collection<Role> roles = facade.getAllRoles();

        Collection<RoleResponse> roleResponse = new ArrayList<RoleResponse>();
        for (Iterator<Role> it = roles.iterator(); it.hasNext();) {
            roleResponse.add(new RoleResponse(it.next()));
        }

        return roleResponse;
    }

    /**
     * Get a role.
     * 
     * @param roleID
     *            The role ID.
     * @return Returns the role with the role ID.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    @Path("/{roleID}")
    public RoleResponse getRole(@PathParam("roleID") Long roleID) throws UserManagementException {
        return new RoleResponse(facade.getRole(roleID));
    }

    /**
     * Add new role.
     * 
     * @param role
     *            The role ID.
     * @return Returns the added role.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @PUT
    public RoleResponse addRole(Role role) throws UserManagementException {
        facade.addRole(role);

        return new RoleResponse(facade.getRole(role.getId()));
    }

    /**
     * Update role.
     * 
     * @param roleID
     *            The role ID.
     * @param role
     *            The role.
     * @return Returns the updated role.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @PUT
    @Path("/{roleID}")
    public RoleResponse updateRole(@PathParam("roleID") Long roleID, Role role) throws UserManagementException {
        facade.updateRole(roleID, role);

        return new RoleResponse(facade.getRole(role.getId()));
    }

    /**
     * Remove role.
     * 
     * @param roleID
     *            The role ID.
     * @return Returns empty response (with status code 200) on success.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @DELETE
    @Path("/{roleID}")
    public Response removeRole(@PathParam("roleID") Long roleID) throws UserManagementException {
        facade.deleteRole(roleID);

        return Response.ok().build();
    }

    /**
     * Get permissions of a role.
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
    public Collection<PermissionResponse> getUserRoles(@PathParam("roleID") Long roleID) throws UserManagementException {
        // TODO limit returned permissions
        Collection<Permission> permissions = facade.getAllPermissionsOfRole(roleID);

        Collection<PermissionResponse> permissionResponse = new ArrayList<PermissionResponse>();
        for (Iterator<Permission> it = permissions.iterator(); it.hasNext();) {
            permissionResponse.add(new PermissionResponse(it.next()));
        }

        return permissionResponse;
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
    public Response removeUserRole(@PathParam("roleID") Long roleID, @PathParam("permissionID") Long permissionID) throws UserManagementException {
        facade.deletePermissionFromRole(roleID, permissionID);

        return Response.ok().build();
    }

}
