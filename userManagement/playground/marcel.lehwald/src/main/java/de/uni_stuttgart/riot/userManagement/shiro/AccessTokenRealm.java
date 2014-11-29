package de.uni_stuttgart.riot.userManagement.shiro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.JdbcUtils;

/**
 * The AccessTokenRealm provides authentication and authorization capabilities for using an access token with a 
 * REST API. The realm will use the JDBC interface for connecting to a database as a storing meachnism for the
 * tokens and user data.
 * 
 * TODO authorization is not implemented yet
 * 
 * @author Marcel Lehwald
 *
 */
public class AccessTokenRealm extends AuthorizingRealm {

	protected DataSource dataSource;
	private String authenticationQuery;
	private String authorizationQuery;

	public AccessTokenRealm() {
		setAuthenticationTokenClass(AccessTokenAuthentication.class);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		AccessTokenAuthentication tokenImpl = (AccessTokenAuthentication)token;
		String accessToken = tokenImpl.getToken();

		Connection connection = null;
		SimpleAuthenticationInfo info = null;
        try {
        	//establish database connection
            connection = dataSource.getConnection();

            info = findSubjectInfo(connection, accessToken);

            if (info == null) {
                throw new UnknownAccountException("No account found for provided token");
            }
        } catch (SQLException e) {
            throw new AuthenticationException("There was a SQL error while authenticating the user", e);
        } finally {
            JdbcUtils.closeConnection(connection);
        }

		return info;
	}

	/**
	 * Find the AuthenticationInfo of a user based on an access token.
	 * 
	 * @param connection
	 * @param accessToken
	 * @return
	 * @throws SQLException
	 */
	private SimpleAuthenticationInfo findSubjectInfo(Connection connection, String accessToken) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleAuthenticationInfo info = null;

        try {
        	//establish database connection
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

				String principal = rs.getString(1);
				info = new SimpleAuthenticationInfo(principal, accessToken, getName());

				foundResult = true;
			}
        } finally {
			JdbcUtils.closeResultSet(rs);
			JdbcUtils.closeStatement(ps);
        }
        
        return info;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setAuthenticationQuery(String authenticationQuery) {
		this.authenticationQuery = authenticationQuery;
	}

	public void setAuthorizationQuery(String authorizationQuery) {
		this.authorizationQuery = authorizationQuery;
	}
}
