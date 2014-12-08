package de.uni_stuttgart.riot.userManagement.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.JdbcUtils;

/**
 * The AccessTokenRealm provides authentication and authorization capabilities for using an access token with a 
 * REST API. The realm will use the JDBC interface for connecting to a database as a storing mechanism for the
 * tokens, user data, roles and permissions.
 * 
 * @author Marcel Lehwald
 *
 */
public class AccessTokenRealm extends AuthorizingRealm {

	protected DataSource dataSource;
	private String authenticationQuery;
	private String rolesQuery;
	private String permissionsQuery;

	public AccessTokenRealm() {
		setAuthenticationTokenClass(AccessTokenAuthentication.class);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		System.out.println("token: " + token.getPrincipal());
		AccessTokenAuthentication tokenImpl = (AccessTokenAuthentication)token;
		String accessToken = tokenImpl.getToken();

		Connection connection = null;
		SimpleAuthenticationInfo info = null;
        try {
            connection = dataSource.getConnection();

            String principal = getPrincipal(connection, accessToken);

            if (principal == null) {
                throw new UnknownAccountException("No account found for provided token " + accessToken);
            }

            info = new SimpleAuthenticationInfo(accessToken, accessToken, getName());
        } catch (SQLException e) {
            throw new AuthenticationException("There was a SQL error while authenticating the user", e);
        } finally {
            JdbcUtils.closeConnection(connection);
        }

		return info;
	}

	/**
	 * Get the principal (username) of a subject (user) based on an access token.
	 * 
	 * @param connection
	 * @param accessToken
	 * @return
	 * @throws SQLException
	 */
	private String getPrincipal(Connection connection, String accessToken) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String principal = null;

        try {
            connection = dataSource.getConnection();

            //prepare query
			ps = connection.prepareStatement(authenticationQuery);
			ps.setString(1, accessToken);

			//execute query
			rs = ps.executeQuery();

			// Loop over results - although we are only expecting one result, since usernames should be unique
			boolean foundResult = false;
			while (rs.next()) {
				// Check to ensure only one row is processed
				if (foundResult) {
					throw new AuthenticationException("More than one user row found for provided token");
				}

				principal = rs.getString(1);

				foundResult = true;
			}
        } finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
        }

        return principal;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null");
        }

        String token = (String) getAvailablePrincipal(principals);

        Connection connection = null;
        Set<String> roleNames = null;
        Set<String> permissions = null;

        try {
            connection = dataSource.getConnection();

            roleNames = getRoles(connection, token);
            permissions = getPermissions(connection, roleNames);
        } catch (SQLException e) {
        	throw new AuthorizationException("There was a SQL error while authorizing the user", e);
        } finally {
            JdbcUtils.closeConnection(connection);
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissions);

        return info;
	}

	/**
	 * Get all roles associated with an access token.
	 * 
	 * @param connection
	 * @param token
	 * @return
	 * @throws SQLException
	 */
	protected Set<String> getRoles(Connection connection, String token) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Set<String> roles = new LinkedHashSet<String>();

		try {
			ps = connection.prepareStatement(rolesQuery);
			ps.setString(1, token);

			// execute query
			rs = ps.executeQuery();

			// loop over results and add each returned role to a set
			while (rs.next()) {
				String role = rs.getString(1);
				if (role != null) {
					roles.add(role);
				}
			}
		} finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
		}

		return roles;
	}

	/**
	 * Get all permissions of a list of roles.
	 * 
	 * @param connection
	 * @param roles
	 * @return
	 * @throws SQLException
	 */
	protected Set<String> getPermissions(Connection connection, Collection<String> roles) throws SQLException {
		PreparedStatement ps = null;
		Set<String> permissions = new LinkedHashSet<String>();

		try {
			ps = connection.prepareStatement(permissionsQuery);

			for (String role : roles) {
				ps.setString(1, role);

				ResultSet rs = null;

				try {
					// execute query
					rs = ps.executeQuery();

					// loop over results and add each returned role to a set
					while (rs.next()) {
						String permission = rs.getString(1);
						permissions.add(permission);
					}
				} finally {
					JdbcUtils.closeResultSet(rs);
				}
			}
		} finally {
			JdbcUtils.closeStatement(ps);
		}

		return permissions;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setAuthenticationQuery(String authenticationQuery) {
		this.authenticationQuery = authenticationQuery;
	}

	public void setRolesQuery(String rolesQuery) {
		this.rolesQuery = rolesQuery;
	}

	public void setPermissionsQuery(String permissionsQuery) {
		this.permissionsQuery = permissionsQuery;
	}

}
