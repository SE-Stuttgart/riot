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
import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;

/**
 * Created by Benny on 10.01.2015.
 * Is the main abstract fragment for all management classes.
 */
public abstract class ManagementFragment extends Fragment {

    protected static final String BUNDLE_OBJECT_ID = "bundle_object_id";

    // Attributes
    protected View view; // ToDo is it also possible to use public "getView()" method?? [[or do I need the view later??]]

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get and save view
        this.view = inflater.inflate(getLayoutResource(), container, false);

        // Set the title of the frame
        getActivity().setTitle(getTitleId());

        // Handle arguments
        handleArguments(getArguments());

        // Display data
        displayData();

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
     * Handle (/save) the arguments, that were set by calling the fragment
     *
     * @param args the arguments that were sent
     */
    protected abstract void handleArguments(Bundle args);

    /**
     * Checks if the item is an instance of this class.
     *
     * @param item the item that will be checked
     * @return true if the item is an instance of the class
     */
    protected abstract boolean isInstanceOf(Storable item);

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

    /**
     * Calls the callOtherFragment method without arguments
     *
     * @param fragment the called fragment
     */
    protected void callOtherFragment(Fragment fragment) {
        callOtherFragment(fragment, null);
    }

    /* ***************
     *****************
     * Dummy classes *
     *****************
     *****************/

    // ToDo ToDO -> Undo "static"

    /**
     * Dummy class for preparing some things.
     *
     * @return .
     */
    public static ArrayList<DummyThing> getThings() {
        final int number = 20;
        ArrayList<DummyThing> things = new ArrayList<DummyThing>();
        for (long i = 1; i < number; i++) {
            DummyThing thing = new DummyThing(i, "Thing_" + String.valueOf(i));
            things.add(thing);
        }
        return things;
    }

    /**
     * Dummy class for getting the thing with the given id.
     *
     * @param id .
     * @return .
     */
    public static DummyThing getThing(Long id) {
        if (id != null && id != 0) {
            for (DummyThing thing : getThings()) {
                if (thing.getId().equals(id)) {
                    return thing;
                }
            }
        }
        return null;
    }

    /**
     * Dummy class for preparing some devices.
     *
     * @return .
     */
    public static ArrayList<DummyDevice> getDevices() {
        final int number = 20;
        ArrayList<DummyDevice> devices = new ArrayList<DummyDevice>();
        for (long i = 0; i < number; i++) {
            DummyDevice device = new DummyDevice(i, "Device_" + String.valueOf(i));
            devices.add(device);
        }
        return devices;
    }

    /**
     * Dummy class for getting the device with the given id.
     *
     * @param id .
     * @return .
     */
    public static DummyDevice getDevice(Long id) {
        if (id != null && id != 0) {
            for (DummyDevice device : getDevices()) {
                if (device.getId().equals(id)) {
                    return device;
                }
            }
        }
        return null;
    }

    /**
     * Dummy class for preparing some users.
     *
     * @return .
     */
    public static ArrayList<DummyUser> getUsers() {
        final int numberPermissions = 3;
        ArrayList<Permission> permissionList = new ArrayList<Permission>();
        for (long i = 1; i < numberPermissions; i++) {
            Permission permission = new Permission(i, "Permission_" + String.valueOf(i));
            permissionList.add(permission);
        }

        final int numberRoles = 5;
        ArrayList<Role> roleList = new ArrayList<Role>();
        for (long i = 1; i < numberRoles; i++) {
            Role role = new Role(i, "Role_" + String.valueOf(i));
            role.setPermissions(permissionList);
            roleList.add(role);
        }

        final int numberUsers = 10;
        ArrayList<DummyUser> userList = new ArrayList<DummyUser>();
        for (long i = 1; i < numberUsers; i++) {
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
        if (id != null && id != 0) {
            for (DummyUser user : getUsers()) {
                if (user.getId().equals(id)) {
                    return user;
                }
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
        final int number = 10;
        ArrayList<Role> roles = new ArrayList<Role>();
        for (long i = 1; i < number; i++) {
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
        final int number = 10;
        ArrayList<Permission> permissions = new ArrayList<Permission>();
        for (long i = 1; i < number; i++) {
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
