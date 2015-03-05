package de.uni_stuttgart.riot.android.management;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

<<<<<<< HEAD
import de.uni_stuttgart.riot.android.R;
=======
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.commons.model.OnlineState;
>>>>>>> RIOT-87:Android:All changes of the last commits
import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Is the main abstract fragment for all management classes.
 *
 * @author Benny
 */
public abstract class ManagementFragment extends Fragment {

    protected static final String BUNDLE_OBJECT_ID = "bundle_object_id";

    // ToDo just for testing reasons
    private static ArrayList<DummyThing> myThings = new ArrayList<DummyThing>();


    // Attributes
    protected View view; // ToDo is it also possible to use public "getView()" method?? [[or do I need the view later??]]

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get and save view
        this.view = inflater.inflate(getLayoutResource(), container, false);

        // Handle arguments
        handleArguments(getArguments());

        // Display data
        displayData();

        // Set the title of the frame
        getActivity().setTitle(getTitle());

        setHasOptionsMenu(true);

        return this.view;
    }

    /**
     * Returns the resource id of the view layout.
     *
     * @return the id of the resource layout
     */
    protected abstract int getLayoutResource();

    /**
     * Returns the string of the view title.
     *
     * @return the title
     */
    protected abstract String getTitle();

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


    // ToDo ToDO -> Undo "static" -- dummy methods for testing reason

    /**
     * Dummy class for preparing some things.
     *
     * @return .
     */
    public static ArrayList<DummyThing> getThings() {
        if (myThings.isEmpty()) {

            final int number = 5;
//            final int tTMPtemp = 5;
//            final int tTMPfill = 80;

            /** TODO info
             Thing myThing = new Thing();
             ThingDescription myThingDescription = ThingDescription.create(myThing);
             Collection<PropertyDescription> myProperties = myThingDescription.getProperties();
             for(PropertyDescription myPropertyDescription : myProperties) {
             myPropertyDescription.getName(); // name der property
             myPropertyDescription.getUiHint(); // hilft beim aufbau des layout items
             myPropertyDescription.getValueType(); // Wert der spaeter vom property kommt
             // myPropertyDescription.isWritable()
             }

             ThingClient myThingClient = new ThingClient(RIOTApiClient.getInstance().getLoginClient());
             MirroringThingBehavior myMirroring = new MirroringThingBehavior(myThingClient);
             // myMirroring.fetch();
             **/


/**
 UIHint myHint = myPropertyDescription.getUiHint();
 if(myHint instanceof UIHint.IntegralSlider) {
 } else if(myHint instanceof UIHint.FractionalSlider) {
 } else if(myHint instanceof UIHint.PercentageSlider) {
 } else if(myHint instanceof UIHint.ToggleButton) {
 } else if(myHint instanceof UIHint.EditText) {
 } else if(myHint instanceof UIHint.EditNumber) {
 } else if(myHint instanceof UIHint.DropDown) {
 }
 **/

//        ArrayList<String> dropDownItems = new ArrayList<String>();
//        dropDownItems.add("Selection 1");
//        dropDownItems.add("Selection 2");
//        dropDownItems.add("Selection 3");
//        dropDownItems.add("Selection 4");
//        HashMap<String, Object> dropDownValues = new HashMap<String, Object>();
//        dropDownValues.put("items", dropDownItems);
//        dropDownValues.put("selectedId", 3);

            ArrayList<DummyThing> things = new ArrayList<DummyThing>();
            DummyThing thing;
            DummyProperty property;
            final long integralMax = 20;
            final long integralMin = -20;
            final long integralValue = 0;
            final double fractionalMax = 0.987;
            final double fractionalMin = -0.1;
            final double fractionalValue = 0.23;
            final int percentageValue = 20;

            for (long i = 1; i < number; i++) {
                thing = new DummyThing("My Thing " + String.valueOf(i));

                UIHint.IntegralSlider integralSlider = new UIHint.IntegralSlider();
                integralSlider.max = integralMax;
                integralSlider.min = integralMin;
                thing.addProperty(new DummyProperty(integralSlider, "integralSlider", PropertyType.IntegralSlider, integralValue));

                UIHint.FractionalSlider fractionalSlider = new UIHint.FractionalSlider();
                fractionalSlider.max = fractionalMax;
                fractionalSlider.min = fractionalMin;
                thing.addProperty(new DummyProperty(fractionalSlider, "fractionalSlider", PropertyType.FractionalSlider, fractionalValue));

                UIHint.PercentageSlider percentageSlider = new UIHint.PercentageSlider();
                thing.addProperty(new DummyProperty(percentageSlider, "percentageSlider", PropertyType.PercentageSlider, percentageValue));

                UIHint.ToggleButton toggleButton = new UIHint.ToggleButton();
                thing.addProperty(new DummyProperty(toggleButton, "toggleButton", PropertyType.ToggleButton, true));

                UIHint.EditText editText = new UIHint.EditText();
                thing.addProperty(new DummyProperty(editText, "editText", PropertyType.EditText, "Example Text"));

                UIHint.EditNumber editNumber = new UIHint.EditNumber();
                property = new DummyProperty(editNumber, "editNumber", PropertyType.EditNumber, "0123456789");
                thing.addProperty(property);

                UIHint.DropDown dropDown = new UIHint.DropDown();
                PropertyType[] propertyTypes = {PropertyType.EditNumber, PropertyType.EditText, PropertyType.FractionalSlider, PropertyType.ToggleButton};
                dropDown.possibleValues = propertyTypes;
                thing.addProperty(new DummyProperty(dropDown, "dropDown", PropertyType.DropDown, PropertyType.FractionalSlider));

                things.add(thing);

                // TODO INFO GROUP:
                // In DB werden die benoetigten Elemente angelegt und die jeweiligen IDs zum GruppenElement als value hinzugefuegt?

//            properties.add(new DummyProperty("Drop the List", PropertyType.DROP_DOWN, dropDownValues));
//
//            properties.add(new DummyProperty("Temperature", PropertyType.SCALE_VALUE, tTMPtemp));
//            properties.add(new DummyProperty("Filling", PropertyType.SCALE_PERCENT, tTMPfill));
//            properties.add(new DummyProperty("Filling with wrong start", PropertyType.SCALE_PERCENT, false)); // Test with wrong value
//            properties.add(new DummyProperty("Alarm", PropertyType.TOGGLE_BUTTON, true));
//            properties.add(new DummyProperty("Power", PropertyType.TOGGLE_BUTTON, false));
//
//            ArrayList<DummyProperty> subList = new ArrayList<DummyProperty>();
//            subList.add(new DummyProperty("Kitchen", PropertyType.TOGGLE_BUTTON, true));
//            subList.add(new DummyProperty("Living Room", PropertyType.TOGGLE_BUTTON, false));
//            subList.add(new DummyProperty("Floor", PropertyType.TOGGLE_BUTTON, false));
//            subList.add(new DummyProperty("Outdoor", PropertyType.TOGGLE_BUTTON, true));
//            properties.add(new DummyProperty("Light", PropertyType.PROPERTY_GROUP, subList));
//
//            properties.add(new DummyProperty("Text Edit Field", PropertyType.EDIT_TEXT, "Sample Text"));
//            properties.add(new DummyProperty("Number Edit Field", PropertyType.EDIT_NUMBER, "123456789"));
//
//            ArrayList<DummyProperty> combinedList = new ArrayList<DummyProperty>();
//            combinedList.add(new DummyProperty("Scale Value", PropertyType.SCALE_VALUE, 0));
//            combinedList.add(new DummyProperty("Button 1", PropertyType.TOGGLE_BUTTON, true));
//            combinedList.add(new DummyProperty("Button 2", PropertyType.TOGGLE_BUTTON, false));
//            combinedList.add(new DummyProperty("Button 3", PropertyType.TOGGLE_BUTTON, false));
//            combinedList.add(new DummyProperty("Button 4", PropertyType.TOGGLE_BUTTON, false));
//            combinedList.add(new DummyProperty("Edit this text:", PropertyType.EDIT_TEXT, "..."));
//            combinedList.add(new DummyProperty("Text Edit Field", PropertyType.EDIT_TEXT, "Sample Text"));
//            combinedList.add(new DummyProperty("Button 2", PropertyType.TOGGLE_BUTTON, false));
//            properties.add(new DummyProperty("Combined Group", PropertyType.PROPERTY_GROUP, combinedList));
//
//            DummyThing thing = new DummyThing(i, "Thing_" + String.valueOf(i));
//            thing.setProperties(properties);
//            things.add(thing);
            }
            myThings = things;
        }

        return myThings;
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

    /**
     * Returns the android resource id of the specific online state.
     *
     * @param onlineState that needs the resource id
     * @return the id of the online state resource
     */
    protected int getOnlineStateResourceId(OnlineState onlineState) {
        if (onlineState.equals(OnlineState.STATUS_ONLINE)) {
            return android.R.drawable.presence_online;
        } else if (onlineState.equals(OnlineState.STATUS_OFFLINE)) {
            return android.R.drawable.presence_offline;
        } else if (onlineState.equals(OnlineState.STATUS_AWAY)) {
            return android.R.drawable.presence_away;
        } else {
            return android.R.drawable.presence_busy;
        }
    }
}
