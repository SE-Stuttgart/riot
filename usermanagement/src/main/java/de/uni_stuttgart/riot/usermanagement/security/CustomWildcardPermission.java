package de.uni_stuttgart.riot.usermanagement.security;

import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.subject.Subject;

/**
 * This class extends the permissions wildcard ability of Shiro so that wildcards can also be used in the
 * {@link Subject#isPermitted(String)} method.
 */
public class CustomWildcardPermission extends WildcardPermission {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new custom wildcard permission.
     *
     * @param wildcardString
     *            the wildcard string
     */
    public CustomWildcardPermission(String wildcardString) {
        super(wildcardString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.shiro.authz.permission.WildcardPermission#implies(org.apache.shiro.authz.Permission)
     */
    @Override
    public boolean implies(Permission p) {
        // By default only supports comparisons with other WildcardPermissions
        if (!(p instanceof CustomWildcardPermission)) {
            return false;
        }

        CustomWildcardPermission wp = (CustomWildcardPermission) p;

        List<Set<String>> otherParts = wp.getParts();

        int i = 0;
        for (Set<String> otherPart : otherParts) {
            // If this permission has less parts than the other permission, everything after the number of parts contained
            // in this permission is automatically implied, so return true
            if (getParts().size() - 1 < i) {
                return true;
            } else {
                Set<String> part = getParts().get(i);
                if (!part.contains(WILDCARD_TOKEN) && !otherPart.contains(WILDCARD_TOKEN) && !part.containsAll(otherPart)) {
                    return false;
                }
                i++;
            }
        }

        // If this permission has more parts than the other parts, only imply it if all of the other parts are wildcards
        for (; i < getParts().size(); i++) {
            Set<String> part = getParts().get(i);
            if (!part.contains(WILDCARD_TOKEN)) {
                return false;
            }
        }

        return true;
    }
}
