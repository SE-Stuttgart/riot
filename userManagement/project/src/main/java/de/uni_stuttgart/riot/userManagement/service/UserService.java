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

import org.apache.shiro.authz.annotation.RequiresRoles;

import de.uni_stuttgart.riot.userManagement.data.storable.User;
import de.uni_stuttgart.riot.userManagement.logic.exception.LogicException;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiErrorResponse;
import de.uni_stuttgart.riot.userManagement.service.request.LoginRequest;

/**
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/users/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserService {

    @GET
    @RequiresRoles("Master")
    public List<User> getUsers() {
        try {
            return (List<User>) UserManagementFacade.getInstance().getAllUsers();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    @GET
    @Path("/{id}/")
    public User getUser(@PathParam("id") int id) {
        try {
            return UserManagementFacade.getInstance().getUser(id);
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    @PUT
    public Response putUser(LoginRequest user) {
        try {
            UserManagementFacade.getInstance().addUser(new User(42L, user.getUsername(), user.getPassword(), "gibtsnet"));
            return Response.ok().build();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    @PUT
    @Path("/{id}/")
    public Response putUser(@PathParam("id") int id, User user) {
        try {
            UserManagementFacade.getInstance().updateUser(id, user);
            return Response.ok().build();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }

    @DELETE
    @Path("/{id}/")
    public Response deleteUser(@PathParam("id") int id) {
        try {
            UserManagementFacade.getInstance().deleteUser(id);
            return Response.ok().build();
        } catch (LogicException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, e);
        }
    }
}
