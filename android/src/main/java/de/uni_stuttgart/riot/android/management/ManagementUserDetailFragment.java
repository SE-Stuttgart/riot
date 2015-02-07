package de.uni_stuttgart.riot.android.management;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.LinkedHashSet;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Created by Benny on 25.01.2015.
 * Fragment that displays all details of an user.
 * It also provides to edit this information.
 */
public class ManagementUserDetailFragment extends ManagementDetailFragment {

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_user_edit;
    }

    @Override
    protected int getTitleId() {
        return R.string.user_detail;
    }

    @Override
    protected boolean isInstanceOf(Storable item) {
        return (item instanceof DummyUser);
    }

    @Override
    protected void displayDetailData() {
        // Save the default values
        Long defaultId = getDefaultId();
        String defaultUsername = getDefaultUsername();
        int defaultImageId = getDefaultImageId();
        String defaultImageUri = getDefaultImageUri();
        OnlineState defaultOnlineState = getDefaultOnlineState();
        LinkedHashSet<String> roleNames = null;
        LinkedHashSet<String> allRoleNames = ManagementFragment.getAllRoleNames();
        LinkedHashSet<String> permissionNames = null;
        LinkedHashSet<String> allPermissionNames = ManagementFragment.getAllPermissionNames();

        // Get values of the item when the item is an instance of the expected class
        if (isInstanceOf(data)) {
            DummyUser item = (DummyUser) data;

            defaultId = doGetId(item, defaultId);
            defaultUsername = doGetUsername(item, defaultUsername);
            defaultImageId = doGetImageId(item, defaultImageId);
            defaultImageUri = doGetImageUri(item, defaultImageUri);
            defaultOnlineState = doGetOnlineState(item, defaultOnlineState);

            roleNames = item.getRoleNames();
            permissionNames = item.getPermissionNames();
        }

        // Set the id value
        if (defaultId != null && defaultId != 0) {
            ((EditText) view.findViewById(R.id.userIdEdit)).setText(defaultId.toString());
            view.findViewById(R.id.userIdEdit).setEnabled(this.enableElements);
        }

        // Set the username value
        if (defaultUsername != null && !defaultUsername.isEmpty()) {
            ((EditText) view.findViewById(R.id.userNameEdit)).setText(defaultUsername);
            view.findViewById(R.id.userNameEdit).setEnabled(this.enableElements);
        }

        // Set the image value
        if (defaultImageId != 0) {
            ((ImageView) view.findViewById(R.id.userImage)).setImageResource(defaultImageId);
            view.findViewById(R.id.userImage).setEnabled(this.enableElements);
        } else if (defaultImageUri != null && !defaultImageUri.isEmpty()) {
            ((ImageView) view.findViewById(R.id.userImage)).setImageURI(Uri.parse(defaultImageUri));
            view.findViewById(R.id.userImage).setEnabled(this.enableElements);
            // ToDo maybe load asynchronous??
        }

        // Set the online state value
        if (defaultOnlineState != null) {
            ((ImageView) view.findViewById(R.id.userOnlineState)).setImageResource(defaultOnlineState.getR());
            view.findViewById(R.id.userOnlineState).setEnabled(this.enableElements);
        }

        // Set all role names
        doSetRoles(roleNames, allRoleNames);

        // Set all permission names
        doSetPermissions(permissionNames, allPermissionNames);
    }

    @Override
    protected Storable getData() {
        return getUser(this.itemId);
    }

    @Override
    protected void setOnAbortClick(View view) {
        if (this.enableElements) {
            Bundle args = new Bundle();
            args.putLong(ManagementFragment.BUNDLE_OBJECT_ID, this.itemId);
            args.putBoolean(BUNDLE_ENABLE_ELEMENTS, false);
            callOtherFragment(new ManagementUserDetailFragment(), args);
        } else {
            callOtherFragment(new ManagementUserListFragment());
        }
    }

    @Override
    protected void setOnEditClick(View view) {
        Bundle args = new Bundle();
        args.putLong(ManagementFragment.BUNDLE_OBJECT_ID, this.itemId);

        if (this.enableElements) {
            args.putBoolean(BUNDLE_ENABLE_ELEMENTS, false);
            callOtherFragment(new ManagementUserDetailFragment(), args);
        } else {
            // ToDo save changed object
            args.putBoolean(BUNDLE_ENABLE_ELEMENTS, true);
            callOtherFragment(new ManagementUserDetailFragment(), args);
        }
    }

    @Override
    protected int getAbortText() {
        return this.enableElements ? R.string.user_abort : R.string.user_back;
    }

    @Override
    protected int getEditText() {
        return this.enableElements ? R.string.user_save : R.string.user_edit;
    }

    /**
     * Returns the default id of the object.
     *
     * @return is null if the element should not be displayed
     */
    protected Long getDefaultId() {
        return null;
    }

    /**
     * Returns the default username of the object.
     *
     * @return is null if the element should not be displayed
     */
    protected String getDefaultUsername() {
        return null;
    }

    /**
     * Returns the default image uri of the object.
     *
     * @return is null if the element should not be displayed
     */
    protected String getDefaultImageUri() {
        return null;
    }

    /**
     * Returns the default image resource id of the object.
     *
     * @return is 0 if the element should not be displayed
     */
    protected int getDefaultImageId() {
        return 0;
    }

    /**
     * Returns the default online state of the object.
     *
     * @return is null if the element should not be displayed
     */
    protected OnlineState getDefaultOnlineState() {
        return null;
    }


    /**
     * Get the id from the item.
     *
     * @param item      includes the values
     * @param defaultId is the default value if the item does not have the wanted value
     * @return the value from the item or the default value
     */
    private Long doGetId(DummyUser item, Long defaultId) {
        Long id = item.getId();
        if (id != null && id != 0) {
            return id;
        }
        return defaultId;
    }

    /**
     * Get the username from the item.
     *
     * @param item            includes the values
     * @param defaultUserName is the default value if the item does not have the wanted value
     * @return the value from the item or the default value
     */
    private String doGetUsername(DummyUser item, String defaultUserName) {
        String username = item.getUsername();
        if (username != null && !username.isEmpty()) {
            return username;
        }
        return defaultUserName;
    }

    /**
     * Get the image id from the item.
     *
     * @param item           includes the values
     * @param defaultImageId is the default value if the item does not have the wanted value
     * @return the value from the item or the default value
     */
    private int doGetImageId(DummyUser item, int defaultImageId) {
        int imageId = item.getImageId();
        if (imageId != 0) {
            return imageId;
        }
        return defaultImageId;
    }

    /**
     * Get the image uri from the item.
     *
     * @param item            includes the values
     * @param defaultImageUri is the default value if the item does not have the wanted value
     * @return the value from the item or the default value
     */
    private String doGetImageUri(DummyUser item, String defaultImageUri) {
        String imageUri = item.getImageUri();
        if (imageUri != null && !imageUri.isEmpty()) {
            return imageUri;
        }
        return defaultImageUri;
    }

    /**
     * Get the online state from the item.
     *
     * @param item               includes the values
     * @param defaultOnlineState is the default value if the item does not have the wanted value
     * @return the value from the item or the default value
     */
    private OnlineState doGetOnlineState(DummyUser item, OnlineState defaultOnlineState) {
        OnlineState onlineState = item.getOnlineState();
        if (onlineState != null) {
            return onlineState;
        }
        return defaultOnlineState;
    }

    /**
     * Set the multi selection spinner for the roles.
     *
     * @param roleNames    the selected role names
     * @param allRoleNames all available role names
     */
    private void doSetRoles(LinkedHashSet<String> roleNames, LinkedHashSet<String> allRoleNames) {
        if (allRoleNames != null && !allRoleNames.isEmpty()) {
            MultiSelectionSpinner roleSpinner = (MultiSelectionSpinner) view.findViewById(R.id.user_role_spinner);
            roleSpinner.setItems(allRoleNames);

            // Select already dedicated roles
            if (roleNames != null && !roleNames.isEmpty()) {
                roleSpinner.setSelection(roleNames);
            }

            // ToDo - just disable the list
//            roleSpinner.setEnabled(this.enableElements);
            roleSpinner.setSelectionEnabled(this.enableElements);
            // roleSpinner.getSelectedItemsAsArrayList();
        }
    }

    /**
     * Set the multi selection spinner for the permissions.
     *
     * @param permissionNames    the selected permission names
     * @param allPermissionNames all available permission names
     */
    private void doSetPermissions(LinkedHashSet<String> permissionNames, LinkedHashSet<String> allPermissionNames) {

        if (allPermissionNames != null && !allPermissionNames.isEmpty()) {
            MultiSelectionSpinner roleSpinner = (MultiSelectionSpinner) view.findViewById(R.id.userPermissionSpinner);
            roleSpinner.setItems(allPermissionNames);

            // Select already detected permissions
            if (permissionNames != null && !permissionNames.isEmpty()) {
                roleSpinner.setSelection(permissionNames);
            }

            // ToDo - just disable the list
//            roleSpinner.setEnabled(this.enableElements);
            roleSpinner.setSelectionEnabled(this.enableElements);
            // roleSpinner.getSelectedItemsAsArrayList();
        }
    }
}
