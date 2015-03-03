package de.uni_stuttgart.riot.usermanagement.security;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;

/**
 * The Class CustomWildcardPermissionResolver.
 */
public class CustomWildcardPermissionResolver implements PermissionResolver {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.shiro.authz.permission.PermissionResolver#resolvePermission(java.lang.String)
     */
    @Override
    public Permission resolvePermission(String permissionString) {
        return new CustomWildcardPermission(permissionString);
    }
}
