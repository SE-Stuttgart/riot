package de.uni_stuttgart.riot.usermanagement.service;

import java.util.List;

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
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;
import de.uni_stuttgart.riot.usermanagement.service.exception.ApiErrorResponse;

/**
 * The permissions service will handle any access (read, write, delete, update) to the permissions.
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/permissions/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PermissionService {

    /**
     * Get a list of all permissions.
     * 
     * @return Returns the permission response containing a list of all permissions.
     */
    @GET
    public List<Permission> getPermissions() {
        try {
            return (List<Permission>) UserManagementFacade.getInstance().getAllPermissions();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    /**
     * Update or create a permission.
     * 
     * @param permission
     *            The permission request containing the permission.
     * @return Returns an empty response.
     */
    @PUT
    public Response putPermission(Permission permission) {
        try {
            UserManagementFacade.getInstance().addPermission(permission);
            return Response.ok().build();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    /**
     * Get all permission of a user.
     * 
     * @param id
     *            The ID of the user.
     * @return Returns a list of the users permissions.
     */
    @GET
    @Path("/{id}/")
    public List<Permission> getPermission(@PathParam("id") int id) {
        return null;
    }

    /**
     * Set the permission of a user.
     * 
     * @param id
     * @param permission
     * @return
     */
    @PUT
    @Path("/{id}/")
    public Response putPermission(@PathParam("id") Long id, Permission permission) {
        try {
            UserManagementFacade.getInstance().updatePermission(id, permission);
            return Response.ok().build();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    /**
     * Delete the permission of a user.
     * 
     * @param id
     * @return
     */
    @DELETE
    @Path("/{id}/")
    public Response deletePermission(@PathParam("id") Long id) {
        try {
            UserManagementFacade.getInstance().deletePermission(id);
            return Response.ok().build();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }
}
