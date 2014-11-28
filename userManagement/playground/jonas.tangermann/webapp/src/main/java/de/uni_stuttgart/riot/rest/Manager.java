package de.uni_stuttgart.riot.rest;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.jdbc.JdbcRealm;

public class Manager {
	
	private static final String AUTHEN_QUERY = "SELECT pword FROM users WHERE username = ?";
	private static final String PERMISSION_QUERY = "SELECT permissionvalue FROM permissions INNER JOIN roles_permissions ON roles_permissions.permissionID = permissions.permissionID INNER JOIN roles ON roles.roleID = roles_permissions.roleID where roles.rolename = ?";
	private static final String ROLE_QUERY = "SELECT roles.rolename FROM roles INNER JOIN tokens_roles ON tokens_roles.roleID = roles.roleID INNER JOIN tokens ON tokens.tokenID = tokens_roles.tokenID WHERE tokens.tokenvalue = ?";
	
	private static Manager instance;
	private DataSource dataSource;
	private String realmName="";
	
	public static Manager getUsermanagementManager(){
		if(instance == null){
			instance = new Manager();
		}
		return instance;
	}
	
	public ServerService getNewServerService(){
		return new DefaultServerService();
	}
	
	public void init(String datasourceJndiPath) throws NamingException{
		this.initDataSource(datasourceJndiPath);
		this.initSecurityManager();
	}
	
	private void initDataSource(String datasourceJndiPath) throws NamingException{
		this.dataSource = this.obtainDatasourceFromJNDI(datasourceJndiPath);
	}

	private void initSecurityManager() throws NamingException{
		Realm realm = this.buildRealm();
		DefaultSecurityManager securityManager = new DefaultSecurityManager(realm);
		SecurityUtils.setSecurityManager(securityManager);
	}
	
	private Realm buildRealm() throws NamingException{
		JdbcRealm realm = new JdbcRealm();
		realm.setDataSource(this.getDataSource());
		realm.setAuthenticationQuery(AUTHEN_QUERY);
		realm.setPermissionsQuery(PERMISSION_QUERY);
		realm.setUserRolesQuery(ROLE_QUERY);
		realm.setPermissionsLookupEnabled(true);
		this.realmName = realm.getName();
		return realm;
	}
	
	private DataSource obtainDatasourceFromJNDI(String datasourceJndiPath) throws NamingException{
		InitialContext context = new InitialContext();
		return (DataSource)context.lookup(datasourceJndiPath);			
	}

	public String getrealmName() {
		return this.realmName;
	} 
	
	public DataSource getDataSource(){
		return this.dataSource;
	}
}
