package de.uni_stuttgart.riot.usermanagement.security;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * The UsernamePasswordRealm is simply a JdbcRealm where authorization is disabled. This realm will only be used 
 * when authenticating the user based on a username + password.
 * 
 * @author Marcel Lehwald
 *
 */
public class UsernamePasswordRealm extends JdbcRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

}
