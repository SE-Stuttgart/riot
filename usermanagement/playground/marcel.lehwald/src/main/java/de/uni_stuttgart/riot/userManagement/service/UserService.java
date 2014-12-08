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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;

import de.uni_stuttgart.riot.userManagement.dao.UserDao;
import de.uni_stuttgart.riot.userManagement.dao.inMemory.UserDaoInMemory;
import de.uni_stuttgart.riot.userManagement.resource.User;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiError;
import de.uni_stuttgart.riot.userManagement.service.exception.ApiErrorResponse;

/**
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/users/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserService {

	public static class UserServiceError
	{
		public static final ApiError SOME_ERROR = new ApiError(1, "SOME_ERROR message..");
		public static final ApiError SOME_OTHER_ERROR = new ApiError(2, "SOME_OTHER_ERROR message..");
		public static final ApiError FOO_ERROR = new ApiError(3, "FOO_ERROR message..");
		public static final ApiError BAR_ERROR = new ApiError(4, "BAR_ERROR message..");
	}

	private UserDao dao = new UserDaoInMemory();

	@GET
	public List<User> getUsers() {
		return dao.getUsers();
	}

	@GET
	@Path("/{id}/")
	public User getUser(@PathParam("id") int id) {
		User user = null;

		try {
			user = dao.getUserById(id);
		}
		catch (Exception e) {
			throw new ApiErrorResponse(Response.Status.BAD_REQUEST, UserServiceError.SOME_ERROR);
		}

		return user;
	}

	@PUT
	@RequiresRoles("Master") //static authorization check
	public Response putUser(User user) {
		SecurityUtils.getSubject().checkRole("foo:" + user.getId()); //dynamic authorization check
		try {
			dao.insertUser(user);
		}
		catch (Exception e) {
			throw new ApiErrorResponse(Response.Status.BAD_REQUEST, UserServiceError.SOME_OTHER_ERROR);
		}

		return Response.ok().build();
	}

	@PUT
	@Path("/{id}/")
	public Response putUser(@PathParam("id") int id, User user) {
		try {
			user.setId(id);
			dao.updateUser(user);
		}
		catch (Exception e) {
			throw new ApiErrorResponse(Response.Status.BAD_REQUEST, UserServiceError.FOO_ERROR);
		}

		return Response.ok().build();
	}

	@DELETE
	@Path("/{id}/")
	public Response deleteUser(@PathParam("id") int id) {
		try {
			dao.deleteUser(dao.getUserById(id));
		}
		catch (Exception e) {
			throw new ApiErrorResponse(Response.Status.BAD_REQUEST, UserServiceError.BAR_ERROR);
		}

		return Response.ok().build();
	}

}
