package de.uni_stuttgart.riot.data.shiro_realm;

import javax.sql.DataSource;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlQueryRealm extends JdbcRealm {
	
    private static final Logger log = LoggerFactory.getLogger(SqlQueryRealm.class);
	
	private static final String AUTHEN_QUERY = "SELECT pword FROM users WHERE username = ?";
	private static final String PERMISSION_QUERY = "SELECT permissionvalue FROM permissions INNER JOIN roles_permissions ON roles_permissions.permissionID = permissions.permissionID INNER JOIN roles ON roles.roleID = roles_permissions.roleID where roles.rolename = ?";
	/**
	 * Over TOKEN!
	 */
	private static final String ROLE_QUERY = "SELECT roles.rolename FROM roles INNER JOIN tokens_roles ON tokens_roles.roleID = roles.roleID INNER JOIN tokens ON tokens.tokenID = tokens_roles.tokenID WHERE tokens.tokenvalue = ?";
	//FIXME time check into ROLE_QUERY
	
	public SqlQueryRealm(DataSource ds){
		this.setDataSource(ds);
		this.setAuthenticationQuery(AUTHEN_QUERY);
		this.setPermissionsQuery(PERMISSION_QUERY);
		this.setUserRolesQuery(ROLE_QUERY);
		this.setPermissionsLookupEnabled(true);
		this.setAuthorizationCachingEnabled(false); // Do not cache because of token validity check.
		// FIXME Change Authorizer to check token validity on cache use and enable caching 
	}
	
}
