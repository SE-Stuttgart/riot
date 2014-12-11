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
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.service.exception.UserManagementExceptionMapper;
import de.uni_stuttgart.riot.usermanagement.service.response.PermissionResponse;

/**
 * The permissions service will handle any access (create, read, update, delete) to the permissions.
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/permissions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PermissionService {

    UserManagementFacade facade = UserManagementFacade.getInstance();

    /**
     * Get all permissions.
     * 
     * @return Returns a list of all permissions.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    public Collection<PermissionResponse> getPermissions() throws UserManagementException {
        // TODO limit returned permissions
        Collection<Permission> permissions = facade.getAllPermissions();

        Collection<PermissionResponse> permissionResponse = new ArrayList<PermissionResponse>();
        for (Iterator<Permission> it = permissions.iterator(); it.hasNext();) {
            permissionResponse.add(new PermissionResponse(it.next()));
        }

        return permissionResponse;
    }

    /**
     * Get a permission.
     * 
     * @param permissionID
     *            The permission ID.
     * @return Returns a permission with the permission ID.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    @Path("/{permissionID}")
    public Permission getPermission(@PathParam("permissionID") Long permissionID) throws UserManagementException {
        return facade.getPermission(permissionID);
    }

    /**
     * Add new permission.
     * 
     * @param permission
     *            The permission.
     * @return Returns the added permission.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @PUT
    public PermissionResponse addPermission(Permission permission) throws UserManagementException {
        facade.addPermission(permission);

        return new PermissionResponse(facade.getPermission(permission.getId()));
    }

    /**
     * Update permission.
     * 
     * @param permissionID
     *            The permission ID.
     * @param permission
     *            The permission.
     * @return Returns the updated permission.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @PUT
    @Path("/{permissionID}")
    public PermissionResponse updatePermission(@PathParam("permissionID") Long permissionID, Permission permission) throws UserManagementException {
        facade.updatePermission(permissionID, permission);

        return new PermissionResponse(facade.getPermission(permission.getId()));
    }

    /**
     * Remove permission.
     * 
     * @param permissionID
     *            The permission ID.
     * @return Returns empty response (with status code 200) on success.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @DELETE
    @Path("/{permissionID}")
    public Response removePermission(@PathParam("permissionID") Long permissionID) throws UserManagementException {
        facade.deletePermission(permissionID);

        return Response.ok().build();
    }

}
