package de.uni_stuttgart.riot.rest;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.uni_stuttgart.riot.data.exc.DatasourceInsertException;
import de.uni_stuttgart.riot.data.sqlQueryDao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.data.storable.User;


@Path("test")
public class TestService implements ServerService{

	private static final String DATASOURCE_JDNI_PATH = "jdbc/postgres_umdb";
	private ServerService serverService = new DefaultServerService();

	@GET
	@Produces( MediaType.TEXT_PLAIN )
	@Path("start")
	public String startUserManagement(){
		try {
			Manager.getUsermanagementManager().init(DATASOURCE_JDNI_PATH);
		} catch (NamingException e) {
			e.printStackTrace();
			return "Init error: "+e.getMessage();
		}
		return "Service init!";
	}

	@GET
	@Produces( MediaType.TEXT_PLAIN )
	@Path("isPermitted/permission/{permission}/token/{token}")
	@Override
	public boolean isPermitted(@PathParam("permission") String permission,@PathParam("token") String token) {
		return this.serverService.isPermitted(permission, token);
	}
	
	@GET
	@Produces( MediaType.TEXT_PLAIN )
	@Path("newUser/id/{id}/name/{name}")
	public String newUser(@PathParam("id") String id,@PathParam("name") String name) {
		UserSqlQueryDao dao = new UserSqlQueryDao(Manager.getUsermanagementManager().getDataSource());
		try {
			dao.insert(new User(new Long(id), name));
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "User added";
	}
}
