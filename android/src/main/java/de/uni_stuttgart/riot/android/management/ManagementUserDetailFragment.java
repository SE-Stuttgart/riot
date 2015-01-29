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
 * Created by Benny on 25.01.2015
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
        Long id, defaultId = getDefaultId();
        String username, defaultUsername = getDefaultUsername();
        int imageId, defaultImageId = getDefaultImageId();
        String imageUri, defaultImageUri = getDefaultImageUri();
        OnlineState onlineState, defaultOnlineState = getDefaultOnlineState();
        LinkedHashSet<String> roleNames = null, allRoleNames = ManagementFragment.getAllRoleNames();
        LinkedHashSet<String> permissionNames = null, allPermissionNames = ManagementFragment.getAllPermissionNames();

        // Get values of the item when the item is an instance of the expected class
        if (isInstanceOf(data)) {
            DummyUser item = (DummyUser) data;

            id = item.getId();
            username = item.getUsername();
            imageId = item.getImageId();
            imageUri = item.getImageUri();
            onlineState = item.getOnlineState();
            roleNames = item.getRoleNames();
            permissionNames = item.getPermissionNames();

            // Save the id
            if (id != null && id != 0) {
                defaultId = id;
            }

            // Save the username
            if (username != null && !username.isEmpty()) {
                defaultUsername = username;
            }
            // Save the image id
            if (imageId != 0) {
                defaultImageId = imageId;
            }

            // Save the image uri
            if (imageUri != null && !imageUri.isEmpty()) {
                defaultImageUri = imageUri;
            }

            // Save the online state
            if (onlineState != null) {
                defaultOnlineState = onlineState;
            }
        }

        // Set the id value
        if (defaultId != null && defaultId != 0) {
            ((EditText) view.findViewById(R.id.user_id_edit)).setText(defaultId.toString());
            view.findViewById(R.id.user_id_edit).setEnabled(this.enableElements);
        }

        // Set the username value
        if (defaultUsername != null && !defaultUsername.isEmpty()) {
            ((EditText) view.findViewById(R.id.user_name_edit)).setText(defaultUsername);
            view.findViewById(R.id.user_name_edit).setEnabled(this.enableElements);
        }

        // Set the image value
        if (defaultImageId != 0) {
            ((ImageView) view.findViewById(R.id.user_image)).setImageResource(defaultImageId);
            view.findViewById(R.id.user_image).setEnabled(this.enableElements);
        } else if (defaultImageUri != null && !defaultImageUri.isEmpty()) {
            ((ImageView) view.findViewById(R.id.user_image)).setImageURI(Uri.parse(defaultImageUri));
            view.findViewById(R.id.user_image).setEnabled(this.enableElements);
            // ToDo maybe load asynchronous??
        }

        // Set the online state value
        if (defaultOnlineState != null) {
            ((ImageView) view.findViewById(R.id.user_online_state)).setImageResource(defaultOnlineState.getR());
            view.findViewById(R.id.user_online_state).setEnabled(this.enableElements);
        }

        // Set all role names
        if (allRoleNames != null && !allRoleNames.isEmpty()) {
            MultiSelectionSpinner roleSpinner = (MultiSelectionSpinner) view.findViewById(R.id.user_role_spinner);
            roleSpinner.setItems(allRoleNames);

            // Select already dedicated roles
            if (roleNames != null && !roleNames.isEmpty()) {
                roleSpinner.setSelection(roleNames);
            }

            roleSpinner.setEnabled(this.enableElements);
            // roleSpinner.getSelectedItemsAsArrayList();
        }

        // Set all permission names
        if (allPermissionNames != null && !allPermissionNames.isEmpty()) {
            MultiSelectionSpinner roleSpinner = (MultiSelectionSpinner) view.findViewById(R.id.user_permission_spinner);
            roleSpinner.setItems(allPermissionNames);

            // Select already detected permissions
            if (permissionNames != null && !permissionNames.isEmpty()) {
                roleSpinner.setSelection(permissionNames);
            }

            roleSpinner.setEnabled(this.enableElements);
            // roleSpinner.getSelectedItemsAsArrayList();
        }
    }

    @Override
    protected Storable getData() {
        return getUser(this.itemId);
    }

    @Override
    protected void setOnAbortClick(View view) {
        callOtherFragment(new ManagementUserDetailFragment());
    }

    @Override
    protected void setOnEditClick(View view) {
        // ToDo save changed object

        Bundle args = new Bundle();
        args.putLong(ManagementFragment.BUNDLE_OBJECT_ID, this.itemId);
        args.putBoolean(BUNDLE_ENABLE_ELEMENTS, false);
        callOtherFragment(new ManagementUserDetailFragment(), args);
    }

    @Override
    protected int getAbortText() {
        return R.string.user_abort;
    }

    @Override
    protected int getEditText() {
        return R.string.user_save;
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
}
