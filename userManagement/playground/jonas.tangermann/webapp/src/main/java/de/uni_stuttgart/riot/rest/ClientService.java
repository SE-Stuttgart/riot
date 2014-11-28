package de.uni_stuttgart.riot.rest;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;

@Path("test")
public class ClientService {

	@GET 
	@Produces( MediaType.TEXT_PLAIN )
	@Path("User/{user}/pw/{pw}")
	public String login( @PathParam("user") String user , @PathParam("pw") String password, @Context HttpServletRequest request){
		try{
			UsernamePasswordToken token = new UsernamePasswordToken(user, password);
			Subject yoda = SecurityUtils.getSecurityManager().login(SecurityUtils.getSubject(), token);
			yoda.getPrincipals();
			System.out.println(request.getRemoteAddr());
		} catch (Exception e){
			e.printStackTrace();
			return "Login faild";
		}			
		return "I'am the token ;)";
	}
}
