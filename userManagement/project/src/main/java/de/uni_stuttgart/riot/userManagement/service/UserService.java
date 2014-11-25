package de.uni_stuttgart.riot.userManagement.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.uni_stuttgart.riot.userManagement.dao.UserDao;
import de.uni_stuttgart.riot.userManagement.dao.UserDaoInMemory;
import de.uni_stuttgart.riot.userManagement.resource.User;

@Path("/users/")
@Consumes("application/json")
@Produces("application/json")
public class UserService {

	private UserDao dao = new UserDaoInMemory();

	public UserDao getDao() {
		return dao;
	}

	public void setDao(UserDao dao) {
		this.dao = dao;
	}

	@GET
	@Path("/")
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
			throw new UserServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
		}

		return user;
	}

	@PUT
	@Path("/")
	public Response putUser(User user) {
		try {
			dao.insertUser(user);
		}
		catch (Exception e) {
			throw new UserServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
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
			throw new UserServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
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
			throw new UserServiceException(e.getMessage(), Response.Status.BAD_REQUEST);
		}

		return Response.ok().build();
	}

}
