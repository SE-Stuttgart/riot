package de.uni_stuttgart.riot.android.management;


import java.util.LinkedHashSet;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * Dummy class - is an extension of the current user class.
 *
 * @author Benny
 */
public class DummyUser extends User {

    /**
     * Constructor.
     *
     * @param id       .
     * @param userName .
     */
    public DummyUser(long id, String userName) {
        super(id, "dummy@mail.de", userName);
    }

    /**
     * Returns the image resource id to display the online state.
     *
     * @return .
     */
    public OnlineState getOnlineState() {
        final int number = 5;
        return OnlineState.getEnumById(this.getId() % number);
    }

    /**
     * Returns the uri for the image that depends to an user.
     *
     * @return .
     */
    public String getImageUri() {
        // ToDo maybe use item for things (like light,...) or use pictures that are saved in the server (with url) or both..
//        return "https://lh4.googleusercontent.com/-mRLn8mYbJw0/AAAAAAAAAAI/AAAAAAAAAAA/tVxGLHv78hg/photo.jpg";
        return null;
    }

    /**
     * Returns the id for the image that depends to an user.
     *
     * @return .
     */
    public int getImageId() {
        if (getId() == 3) {
            return R.drawable.ic_launcher;
        }
        return 0;
    }

    /**
     * .
     *
     * @return .
     */
    public LinkedHashSet<String> getRoleNames() {
        return ManagementFragment.getRoleNames(getRoles());
    }

    /**
     * .
     *
     * @return .
     */
    public LinkedHashSet<String> getPermissionNames() {
        return ManagementFragment.getPermissionNames(getPermissions());
    }

    /**
     * .
     *
     * @return .
     */
    public LinkedHashSet<Permission> getPermissions() {
        LinkedHashSet<Permission> permissions = new LinkedHashSet<Permission>();
        if (getRoles() != null && !getRoles().isEmpty()) {
            for (Role role : getRoles()) {
                if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
                    for (Permission permission : role.getPermissions()) {
                        permissions.add(permission);
                    }
                }
            }
        }
        return permissions;
    }
}
