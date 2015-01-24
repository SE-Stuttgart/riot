package de.uni_stuttgart.riot.android.management;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;

/**
 * Created by Benny on 10.01.2015
 */
public abstract class ManagementFragment extends Fragment {

    // Attributes
    protected View view; // ToDo is it also possible to use public "getView()" method??
    protected static String BUNDLE_OBJECT_ID = "bundle_object_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get and save view
        this.view = inflater.inflate(getLayoutResource(), container, false);

        // Set the title of the frame
        getActivity().setTitle(getTitleId());

        // Display data
        displayData();

        // Login with user?
        // androidUser.logIn("R2D2", "R2D2PW");
        // androidUser.logIn("Yoda", "YodaPW");
        // MainActivity.au.logIn("Vader", "VaderPW"); // TODO server-connection (also use an extra thread)
        // displayData(MainActivity.au); // TODO server-connection

        // Logout the user
        // MainActivity.au.logOut(); // TODO server-connection

        return this.view;
    }

    /**
     * Returns the resource id of the view layout.
     *
     * @return the id of the resource layout
     */
    protected abstract int getLayoutResource();

    /**
     * Returns the resource id of the view title.
     *
     * @return the id of the resource string
     */
    protected abstract int getTitleId();

    /**
     * Set values to the view elements.
     */
    protected abstract void displayData();

    /**
     * Calls an other fragment and sends some data.
     *
     * @param fragment the called fragment
     * @param args     some data for the new fragment
     */
    protected void callOtherFragment(Fragment fragment, Bundle args) {
        if (args != null) {
            fragment.setArguments(args);
        }
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    /* ***************
     *****************
     * Dummy classes *
     *****************
     *****************/

    /**
     * Dummy class for preparing some users.
     *
     * @return .
     */
    public static ArrayList<DummyUser> getUsers() {
        ArrayList<Permission> permissionList = new ArrayList<Permission>();
        for (long i = 0; i < 3; i++) {
            Permission permission = new Permission(i, "Permission_" + String.valueOf(i));
            permissionList.add(permission);
        }

        ArrayList<Role> roleList = new ArrayList<Role>();
        for (long i = 0; i < 5; i++) {
            Role role = new Role(i, "Role_" + String.valueOf(i));
            role.setPermissions(permissionList);
            roleList.add(role);
        }

        ArrayList<DummyUser> userList = new ArrayList<DummyUser>();
        for (long i = 0; i < 10; i++) {
            DummyUser user = new DummyUser(i, "User_" + String.valueOf(i));
            user.setRoles(roleList);
            userList.add(user);
        }

        return userList;
    }

    /**
     * Dummy class for getting the user with the given id.
     *
     * @param id .
     * @return .
     */
    public static DummyUser getUser(Long id) {
        for (DummyUser user : getUsers()) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Dummy class for preparing some user roles.
     *
     * @return .
     */
    public static ArrayList<Role> getRoles() {
        ArrayList<Role> roles = new ArrayList<Role>();
        for (long i = 0; i < 10; i++) {
            Role role = new Role(i, "Role_" + String.valueOf(i));
            roles.add(role);
        }

        return roles;
    }

    /**
     * Dummy class for getting the names of all user roles.
     *
     * @return .
     */
    public static LinkedHashSet<String> getAllRoleNames() {
        return getRoleNames(getRoles());
    }

    /**
     * Dummy class for getting the names of the given user roles.
     *
     * @param roles .
     * @return .
     */
    public static LinkedHashSet<String> getRoleNames(Collection<Role> roles) {
        return getRoleNames(new ArrayList<Role>(roles));
    }

    /**
     * Dummy class for getting the names of the given user roles.
     *
     * @param roles .
     * @return .
     */
    public static LinkedHashSet<String> getRoleNames(ArrayList<Role> roles) {
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        if (roles != null && !roles.isEmpty()) {
            for (Role role : roles) {
                names.add(role.getRoleName());
            }
        }
        return names;
    }

    /**
     * Dummy class for preparing some user permissions.
     *
     * @return .
     */
    public static ArrayList<Permission> getPermissions() {
        ArrayList<Permission> permissions = new ArrayList<Permission>();
        for (long i = 0; i < 10; i++) {
            Permission permission = new Permission(i, "Permission_" + String.valueOf(i));
            permissions.add(permission);
        }

        return permissions;
    }

    /**
     * Dummy class for getting the names of all user permissions.
     *
     * @return .
     */
    public static LinkedHashSet<String> getAllPermissionNames() {
        return getPermissionNames(getPermissions());
    }

    /**
     * Dummy class for getting the names of the given user permissions.
     *
     * @param permissions .
     * @return .
     */
    public static LinkedHashSet<String> getPermissionNames(Collection<Permission> permissions) {
        return getPermissionNames(new ArrayList<Permission>(permissions));
    }

    /**
     * Dummy class for getting the names fo the given user permissions.
     *
     * @param permissions .
     * @return .
     */
    public static LinkedHashSet<String> getPermissionNames(ArrayList<Permission> permissions) {
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
                names.add(permission.getPermissionValue());
            }
        }
        return names;
    }
}
