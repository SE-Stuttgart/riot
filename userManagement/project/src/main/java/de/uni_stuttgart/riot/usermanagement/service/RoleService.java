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

import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;
import de.uni_stuttgart.riot.usermanagement.service.exception.ApiErrorResponse;

/**
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/roles/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoleService {

    @GET
    public List<Role> getRoles() {
        try {
            return UserManagementFacade.getInstance().getAllRoles();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    @PUT
    public Response putRole(Role role) {
        try {
            UserManagementFacade.getInstance().addRole(role);
            return Response.ok().build();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    @GET
    @Path("/{id}/")
    public List<Role> getRoles(@PathParam("id") int id) {
        try {
            return UserManagementFacade.getInstance().getAllRolesFromUser(id);
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    @PUT
    @Path("/{id}/")
    public Response putRole(@PathParam("id") int id, Role role) {
        try {
            UserManagementFacade.getInstance().addRoleToUser(id, role);
            return Response.ok().build();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    @DELETE
    @Path("/{id}/")
    public Response deleteRole(@PathParam("id") int id) {
        try {
            UserManagementFacade.getInstance().deleteRole(id);
            return Response.ok().build();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

}
