package de.uni_stuttgart.riot.userManagement.service;

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

import de.uni_stuttgart.riot.userManagement.resource.Permission;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiError;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiErrorResponse;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/permissions/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PermissionService {

    @GET
    public List<Permission> getPermissions() {
        try {
            return UserManagementFacade.getInstance().getAllPermissions();
        } catch (UserManagementException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }

    @PUT
    public Response putPermission(Permission permission) {
        try {
            UserManagementFacade.getInstance().addPermission(permission);
            return Response.ok().build();
        } catch (UserManagementException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }

    @GET
    @Path("/{id}/")
    public List<Permission> getPermission(@PathParam("id") int id) {
        try {
            return UserManagementFacade.getInstance().getAllPermissionsFromUser(id);
        } catch (UserManagementException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }

    @PUT
    @Path("/{id}/")
    public Response putPermission(@PathParam("id") int id, Permission permission) {
        try {
            UserManagementFacade.getInstance().updatePermission(id, permission);
            return Response.ok().build();
        } catch (UserManagementException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }

    @DELETE
    @Path("/{id}/")
    public Response deletePermission(@PathParam("id") int id) {
        try {
            UserManagementFacade.getInstance().deletePermission(id);
            return Response.ok().build();
        } catch (UserManagementException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }
}
