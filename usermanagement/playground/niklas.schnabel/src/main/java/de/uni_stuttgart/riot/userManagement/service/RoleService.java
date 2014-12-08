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

import de.uni_stuttgart.riot.userManagement.dao.RoleDao;
import de.uni_stuttgart.riot.userManagement.resource.Role;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiError;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiErrorResponse;
import de.uni_stuttgart.riot.userManagement.service.exception.UserManagementException;

/**
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/roles/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoleService {

    public static class RoleServiceError {
        public static final ApiError UNIMPLEMENTED = new ApiError(1, "unimplemented..");
    }

    private RoleDao dao;

    @GET
    public List<Role> getRoles() {
        try {
            return UserManagementFacade.getInstance().getAllRoles();
        } catch (UserManagementException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }

    @PUT
    public Response putRole(Role role) {
        try {
            UserManagementFacade.getInstance().addRole(role);
            return Response.ok().build();
        } catch (UserManagementException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }

    @GET
    @Path("/{id}/")
    public List<Role> getRoles(@PathParam("id") int id) {
        try {
            return UserManagementFacade.getInstance().getAllRolesFromUser(id);
        } catch (UserManagementException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }

    @PUT
    @Path("/{id}/")
    public Response putRole(@PathParam("id") int id, Role role) {
        try {
            UserManagementFacade.getInstance().addRoleToUser(id, role);
            return Response.ok().build();
        } catch (UserManagementException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }

    @DELETE
    @Path("/{id}/")
    public Response deleteRole(@PathParam("id") int id) {
        try {
            UserManagementFacade.getInstance().deleteRole(id);
            return Response.ok().build();
        } catch (UserManagementException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }

}
