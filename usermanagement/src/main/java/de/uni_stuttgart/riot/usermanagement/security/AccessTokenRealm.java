package de.uni_stuttgart.riot.usermanagement.security;

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
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * The AccessTokenRealm provides authentication and authorization capabilities for using an access token with a REST API. The realm will use
 * the JDBC interface for connecting to a database as a storing mechanism for the tokens, user data, roles and permissions.
 * 
 * @author Marcel Lehwald
 *
 */
public class AccessTokenRealm extends AuthorizingRealm {

    protected DataSource dataSource;
    private String authenticationQuery;
    private String rolesQuery;
    private String permissionsQuery;

    /**
     * Create a access token realm for authentication and authorization using shiro.
     */
    public AccessTokenRealm() {
        setAuthenticationTokenClass(AccessToken.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        AccessToken tokenImpl = (AccessToken) token;
        String accessToken = tokenImpl.getToken();

        try (Connection connection = dataSource.getConnection()) {

            String principal = getPrincipal(connection, accessToken);
            if (principal == null) {
                return null;
            }

            return new SimpleAuthenticationInfo(accessToken, accessToken, getName());
        } catch (SQLException e) {
            throw new AuthenticationException("There was a SQL error while authenticating the user", e);
        }

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
        try (PreparedStatement ps = connection.prepareStatement(authenticationQuery)) {
            ps.setString(1, accessToken);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null");
        }

        String token = (String) getAvailablePrincipal(principals);

        Set<String> roleNames = null;
        Set<String> permissions = null;

        try (Connection connection = dataSource.getConnection()) {
            roleNames = getRoles(connection, token);
            permissions = getPermissions(connection, roleNames);
        } catch (SQLException e) {
            throw new AuthorizationException("There was a SQL error while authorizing the user", e);
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissions);

        return info;
    }

    /**
     * Get all roles associated with an access token.
     * 
     * @param connection
     *            An open database connection.
     * @param token
     *            The token which will be used to lookup the associated roles.
     * @return Returns a list of roles associated with the token.
     * @throws SQLException
     *             When an SQL error occurs.
     */
    protected Set<String> getRoles(Connection connection, String token) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(rolesQuery)) {
            ps.setString(1, token);

            try (ResultSet rs = ps.executeQuery()) {

                // loop over results and add each returned role to a set
                Set<String> roles = new LinkedHashSet<String>();
                while (rs.next()) {
                    String role = rs.getString(1);
                    if (role != null) {
                        roles.add(role);
                    }
                }
                return roles;
            }
        }
    }

    /**
     * Get all permissions of a list of roles.
     * 
     * @param connection
     *            An open database connection.
     * @param roles
     *            The roles which will be used to lookup the associated permissions.
     * @return Returns a list of permission associated with the roles.
     * @throws SQLException
     *             When an SQL error occurs.
     */
    protected Set<String> getPermissions(Connection connection, Collection<String> roles) throws SQLException {
        Set<String> permissions = new LinkedHashSet<String>();
        try (PreparedStatement ps = connection.prepareStatement(permissionsQuery)) {
            for (String role : roles) {
                ps.setString(1, role);

                try (ResultSet rs = ps.executeQuery()) {
                    // loop over results and add each returned role to a set
                    while (rs.next()) {
                        String permission = rs.getString(1);
                        permissions.add(permission);
                    }
                }
            }
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
