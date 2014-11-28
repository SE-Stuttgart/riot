package de.uni_stuttgart.riot.rest;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

/**
 * An exemplary REST resource class.
 */
@Path("example")
public class ExampleResource {

	/**
	 * @return a plain string message
	 * @throws NamingException 
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getHello(){
		try {
			InitialContext context = new InitialContext();
			DataSource ds = (DataSource)context.lookup("jdbc/postgres_umdb");
			System.err.println("Recived Datasource from JNDI: "+ds.getClass());
			
			JdbcRealm realm = new JdbcRealm();
			realm.setDataSource(ds);
			DefaultSecurityManager securityManager = new DefaultSecurityManager(realm);
			SecurityUtils.setSecurityManager(securityManager);
			
			try{
				UsernamePasswordToken token = new UsernamePasswordToken("Yoda", "42");
				Subject yoda = SecurityUtils.getSecurityManager().login(SecurityUtils.getSubject(), token);
				yoda.isPermitted("doThat");
			
				System.err.println("Is user Yoda auth? = "+yoda.isAuthenticated());
			} catch (AuthenticationException e){
				System.err.println("Login failed");
			}			
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return "End of DB Test";
	}
}
