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

import de.uni_stuttgart.riot.userManagement.resource.User;
import de.uni_stuttgart.riot.userManagement.service.exception.AddUserException;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiError;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiErrorResponse;
import de.uni_stuttgart.riot.userManagement.service.exception.DeleteUserException;

/**
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/users/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserService {

    public static class UserServiceError {
        public static final ApiError SOME_ERROR = new ApiError(1, "SOME_ERROR message..");
        public static final ApiError SOME_OTHER_ERROR = new ApiError(2, "SOME_OTHER_ERROR message..");
        public static final ApiError FOO_ERROR = new ApiError(3, "FOO_ERROR message..");
        public static final ApiError BAR_ERROR = new ApiError(4, "BAR_ERROR message..");
    }

    @GET
    public List<User> getUsers() {
        return UserManagementFacade.getInstance().getAllUsers();
    }

    @GET
    @Path("/{id}/")
    public User getUser(@PathParam("id") int id) {
        return UserManagementFacade.getInstance().getUser(id);
    }

    @PUT
    public Response putUser(User user) {
        try {
            UserManagementFacade.getInstance().addUser(user);
            return Response.ok().build();
        } catch (AddUserException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }

    @PUT
    @Path("/{id}/")
    public Response putUser(@PathParam("id") int id, User user) {
        return UserManagementFacade.getInstance().updateUser(id, user);
    }

    @DELETE
    @Path("/{id}/")
    public Response deleteUser(@PathParam("id") int id) {
        try {
            UserManagementFacade.getInstance().deleteUser(id);
            return Response.ok().build();
        } catch (DeleteUserException e) {
            throw new ApiErrorResponse(Response.Status.BAD_REQUEST, new ApiError(e.getErrorCode(), e.getMessage()));
        }
    }
}
