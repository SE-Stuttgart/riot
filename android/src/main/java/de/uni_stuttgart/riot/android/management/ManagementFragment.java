package de.uni_stuttgart.riot.android.management;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

<<<<<<< HEAD
import de.uni_stuttgart.riot.android.R;
=======
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.commons.model.OnlineState;
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> RIOT-87:Android:All changes of the last commits
import de.uni_stuttgart.riot.commons.rest.data.Storable;
=======
>>>>>>> RIOT-87:Android:Get things from the server
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.thing.ui.UIHint;
=======
>>>>>>> RIOT-87:Android:Exchange data with server and try to update data

/**
 * Is the main abstract fragment for all management classes.
 *
 * @author Benny
 */
public abstract class ManagementFragment extends Activity {

// FIXME reorder methods in this class and all its subclasses!
    // FIXME rename all fragment things to activity

    protected final String BUNDLE_OBJECT_ID = "SELECTED_OBJECT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getLayoutResource() == 0) {
            // ToDo output message!
            IM.INSTANCES.getMH().writeErrorMessage("No layout resource was defined!");
            return;
        }

        setContentView(getLayoutResource());

        // Get extras from intent if saved instance state is empty
        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }
        // Handle arguments if not null
        if (savedInstanceState != null) {
            handleArguments(savedInstanceState);
        }

        // Prepare the refresh button
        prepareRefreshButton();

        // Set the title of the frame
        updateTitle();

        // Refresh data (first time is needed to show data)
        doRefresh();

        // Helps to update the menu
        // TODO setHasOptionsMenu(true);
    }

    /**
     * Save the id of the object.
     *
     * @param id the id
     */
    protected void saveObjectId(long id) {
        getSharedPreferences("RIOT", 0).edit().putLong(BUNDLE_OBJECT_ID, id).commit();
    }

    /**
     * Get the id of the object.
     *
     * @return the id of the object
     */
    protected long getObjectId() {
        return getSharedPreferences("RIOT", 0).getLong(BUNDLE_OBJECT_ID, 0);
    }

    /**
     * Prepare the refresh button.
     */
    private void prepareRefreshButton() {
        ImageButton buttonBack = (ImageButton) findViewById(R.id.detail_button_back);
        if (buttonBack == null) {
            // ToDo output message!
            IM.INSTANCES.getMH().writeErrorMessage("No back button was found!");
            return;
        }
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                finish();
            }
        });
        buttonBack.setVisibility(View.VISIBLE);

        ImageButton buttonRefresh = (ImageButton) findViewById(R.id.detail_button_refresh);
        if (buttonRefresh == null) {
            // ToDo output message!
            IM.INSTANCES.getMH().writeErrorMessage("No refresh button was found!");
            return;
        }
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                doRefresh();
            }
        });
        buttonRefresh.setVisibility(View.VISIBLE);
    }

    /**
     * Refresh the displayed data.
     */
    private void doRefresh() {
        new Thread() {

            @Override
            public void run() {
                // TODO use a timeout and show processing dialog
                // Prepare and show a progress dialog
//                ProgressDialog progressDialog = new ProgressDialog(getCurrentContext());
//                progressDialog.setTitle(getString(R.string.loading));
//                progressDialog.setMessage(getString(R.string.prepareData));
//                progressDialog.show();

                try {
                    // Get all known things (like a refresh action)
                    // ToDo use that on app start and with a refresh button
                    RIOTApiClient.getInstance().getDeviceBehavior().updateThings();

                    // Display the data in the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Display data
                            displayData();

                            // Update title if necessary
                            updateTitle();
                        }
                    });
                } catch (Exception e) {
                    // FIXME output message!!
                    IM.INSTANCES.getMH().writeErrorMessage("Problems by creating view: " + e.getMessage());
                }

                // Dismiss the progress dialog
//                progressDialog.dismiss();
            }
        }.start();
    }

    /**
     * Update the fragment title.
     */
    private void updateTitle() {
        setTitle(getPageTitle());
    }

    /**
     * Calls an other fragment and sends some data.
     *
     * @param fragment the called fragment
     * @param args     some data for the new fragment
     */
    @Deprecated
    protected void callOtherPage(Fragment fragment, Bundle args) {


        // TODO
//        Intent settingsIntent = new Intent(homeScreen, SettingScreen.class);
//        settingsIntent.putExtra("pressedButton", homeScreen.getString(R.string.settings));
//        homeScreen.startActivity(settingsIntent);

//        if (args != null) {
//            fragment.setArguments(args);
//        }
//        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    /**
     * Calls the callOtherPage method without arguments
     *
     * @param fragment the called fragment
     */
    protected void callOtherFragment(Fragment fragment) {
        callOtherPage(fragment, null);
    }

    /**
     * Handle the arguments on creating this view.
     *
     * @param bundle includes the arguments
     */
    protected abstract void handleArguments(Bundle bundle);

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
    protected abstract String getPageTitle();

    /**
     * Checks if the item is an instance of this class.
     *
     * @param item the item that will be checked
     * @return true if the item is an instance of the class
     */
    protected abstract boolean isInstanceOf(Object item);

    /**
     * Build the elements that will be displayed.
     */
    protected abstract void displayData();



    /* ***************
     *****************
     * Dummy classes *
     *****************
     *****************/


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
    // TODO TODO TODO TODO !!!!!!!!!!!!!!!!!!!!!!!!!!!! anstelle von Dummy richtig verwenden!!


    // ToDo ToDO -> Undo "static" -- dummy methods for testing reason

//    /**
//     * Dummy class for preparing some things.
//     *
//     * @return .
//     */
//    public static ArrayList<DummyThing> getThings() {
//        if (myThings.isEmpty()) {
//
//            final int number = 5;
//
//            ArrayList<DummyThing> things = new ArrayList<DummyThing>();
//            DummyThing thing;
//            DummyProperty property;
//            final long integralMax = 20;
//            final long integralMin = -20;
//            final long integralValue = 0;
//            final double fractionalMax = 15.987;
//            final double fractionalMin = -0.1;
//            final double fractionalValue = 0.23;
//            final int percentageValue = 20;
//
//            for (long i = 1; i < number; i++) {
//                thing = new DummyThing("My Thing " + String.valueOf(i));
//
//                UIHint.IntegralSlider integralSlider = new UIHint.IntegralSlider();
//                integralSlider.max = integralMax;
//                integralSlider.min = integralMin;
//                thing.addProperty(new DummyProperty(integralSlider, "integralSlider", PropertyType.IntegralSlider, integralValue));
//
//                UIHint.FractionalSlider fractionalSlider = new UIHint.FractionalSlider();
//                fractionalSlider.max = fractionalMax;
//                fractionalSlider.min = fractionalMin;
//                thing.addProperty(new DummyProperty(fractionalSlider, "fractionalSlider", PropertyType.FractionalSlider, fractionalValue));
//
//                UIHint.PercentageSlider percentageSlider = new UIHint.PercentageSlider();
//                thing.addProperty(new DummyProperty(percentageSlider, "percentageSlider", PropertyType.PercentageSlider, percentageValue));
//
//                UIHint.ToggleButton toggleButton = new UIHint.ToggleButton();
//                thing.addProperty(new DummyProperty(toggleButton, "toggleButton", PropertyType.ToggleButton, true));
//
//                UIHint.EditText editText = new UIHint.EditText();
//                thing.addProperty(new DummyProperty(editText, "editText", PropertyType.EditText, "Example Text"));
//
//                UIHint.EditNumber editNumber = new UIHint.EditNumber();
//                property = new DummyProperty(editNumber, "editNumber", PropertyType.EditNumber, "0123456789");
//                thing.addProperty(property);
//
//                UIHint.DropDown dropDown = new UIHint.DropDown();
//                PropertyType[] propertyTypes = {PropertyType.EditNumber, PropertyType.EditText, PropertyType.FractionalSlider, PropertyType.ToggleButton};
//                dropDown.possibleValues = propertyTypes;
//                thing.addProperty(new DummyProperty(dropDown, "dropDown", PropertyType.DropDown, PropertyType.FractionalSlider));
//
//                things.add(thing);
//
//                // TODO INFO GROUP:
//                // In DB werden die benoetigten Elemente angelegt und die jeweiligen IDs zum GruppenElement als value hinzugefuegt?
//            }
//            myThings = things;
//        }
//
//        return myThings;
//    }
//
//    /**
//     * Dummy class for getting the thing with the given id.
//     *
//     * @param id .
//     * @return .
//     */
//    public static DummyThing getThing(Long id) {
//        if (id != null && id != 0) {
//            for (DummyThing thing : getThings()) {
//                if (thing.getId().equals(id)) {
//                    return thing;
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Dummy class for preparing some devices.
//     *
//     * @return .
//     */
//    public static ArrayList<DummyDevice> getDevices() {
//        final int number = 20;
//        ArrayList<DummyDevice> devices = new ArrayList<DummyDevice>();
//        for (long i = 0; i < number; i++) {
//            DummyDevice device = new DummyDevice(i, "Device_" + String.valueOf(i));
//            devices.add(device);
//        }
//        return devices;
//    }
//
//    /**
//     * Dummy class for getting the device with the given id.
//     *
//     * @param id .
//     * @return .
//     */
//    public static DummyDevice getDevice(Long id) {
//        if (id != null && id != 0) {
//            for (DummyDevice device : getDevices()) {
//                if (device.getId().equals(id)) {
//                    return device;
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Dummy class for preparing some users.
//     *
//     * @return .
//     */
//    public static ArrayList<DummyUser> getUsers() {
//        final int numberPermissions = 3;
//        ArrayList<Permission> permissionList = new ArrayList<Permission>();
//        for (long i = 1; i < numberPermissions; i++) {
//            Permission permission = new Permission(i, "Permission_" + String.valueOf(i));
//            permissionList.add(permission);
//        }
//
//        final int numberRoles = 5;
//        ArrayList<Role> roleList = new ArrayList<Role>();
//        for (long i = 1; i < numberRoles; i++) {
//            Role role = new Role(i, "Role_" + String.valueOf(i));
//            role.setPermissions(permissionList);
//            roleList.add(role);
//        }
//
//        final int numberUsers = 10;
//        ArrayList<DummyUser> userList = new ArrayList<DummyUser>();
//        for (long i = 1; i < numberUsers; i++) {
//            DummyUser user = new DummyUser(i, "User_" + String.valueOf(i));
//            user.setRoles(roleList);
//            userList.add(user);
//        }
//
//        return userList;
//    }
//
//    /**
//     * Dummy class for getting the user with the given id.
//     *
//     * @param id .
//     * @return .
//     */
//    public static DummyUser getUser(Long id) {
//        if (id != null && id != 0) {
//            for (DummyUser user : getUsers()) {
//                if (user.getId().equals(id)) {
//                    return user;
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Dummy class for preparing some user roles.
//     *
//     * @return .
//     */
//    public static ArrayList<Role> getRoles() {
//        final int number = 10;
//        ArrayList<Role> roles = new ArrayList<Role>();
//        for (long i = 1; i < number; i++) {
//            Role role = new Role(i, "Role_" + String.valueOf(i));
//            roles.add(role);
//        }
//
//        return roles;
//    }
//
//    /**
//     * Dummy class for getting the names of all user roles.
//     *
//     * @return .
//     */
//    public static LinkedHashSet<String> getAllRoleNames() {
//        return getRoleNames(getRoles());
//    }
//
//    /**
//     * Dummy class for getting the names of the given user roles.
//     *
//     * @param roles .
//     * @return .
//     */
//    public static LinkedHashSet<String> getRoleNames(Collection<Role> roles) {
//        return getRoleNames(new ArrayList<Role>(roles));
//    }
//
//    /**
//     * Dummy class for getting the names of the given user roles.
//     *
//     * @param roles .
//     * @return .
//     */
//    public static LinkedHashSet<String> getRoleNames(ArrayList<Role> roles) {
//        LinkedHashSet<String> names = new LinkedHashSet<String>();
//        if (roles != null && !roles.isEmpty()) {
//            for (Role role : roles) {
//                names.add(role.getRoleName());
//            }
//        }
//        return names;
//    }
//
//    /**
//     * Dummy class for preparing some user permissions.
//     *
//     * @return .
//     */
//    public static ArrayList<Permission> getPermissions() {
//        final int number = 10;
//        ArrayList<Permission> permissions = new ArrayList<Permission>();
//        for (long i = 1; i < number; i++) {
//            Permission permission = new Permission(i, "Permission_" + String.valueOf(i));
//            permissions.add(permission);
//        }
//
//        return permissions;
//    }
//
//    /**
//     * Dummy class for getting the names of all user permissions.
//     *
//     * @return .
//     */
//    public static LinkedHashSet<String> getAllPermissionNames() {
//        return getPermissionNames(getPermissions());
//    }
//
//    /**
//     * Dummy class for getting the names of the given user permissions.
//     *
//     * @param permissions .
//     * @return .
//     */
//    public static LinkedHashSet<String> getPermissionNames(Collection<Permission> permissions) {
//        return getPermissionNames(new ArrayList<Permission>(permissions));
//    }
//
//    /**
//     * Dummy class for getting the names fo the given user permissions.
//     *
//     * @param permissions .
//     * @return .
//     */
//    public static LinkedHashSet<String> getPermissionNames(ArrayList<Permission> permissions) {
//        LinkedHashSet<String> names = new LinkedHashSet<String>();
//        if (permissions != null && !permissions.isEmpty()) {
//            for (Permission permission : permissions) {
//                names.add(permission.getPermissionValue());
//            }
//        }
//        return names;
//    }
}
