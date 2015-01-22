package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Created by Benny on 09.01.2015.
 */
public abstract class OLDManagementDetailEditFragment extends OLDManagementFragment {

    public static final String BUNDLE_OBJECT_ID = "OBJECT_ID";
    protected int objectId;

    protected final String TAG_ID = "id";
    protected final String TAG_NAME = "name";
    protected final String TAG_ONLINE_STATE = "onlineState";
    protected final String TAG_PERMISSIONS = "permissions";
    protected final String TAG_PERMISSIONS_ID = "pId";
    protected final String TAG_PERMISSIONS_NAME = "pName";
    protected final String TAG_ROLES = "roles";
    protected final String TAG_ROLES_ID = "rId";
    protected final String TAG_ROLES_NAME = "rName";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(BUNDLE_OBJECT_ID)) {
                this.objectId = args.getInt(BUNDLE_OBJECT_ID);
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected HashMap<String, Object> setManagementItems() {
        HashMap<String, Object> items = new HashMap<String, Object>();

        // Add single elements to the items list
        items.put(TAG_ID, Integer.class);
        items.put(TAG_NAME, String.class);
        items.put(TAG_ONLINE_STATE, String.class);

        // Sub list for permissions
        HashMap<String, Object> permissionsItems = new HashMap<String, Object>();
        permissionsItems.put(TAG_PERMISSIONS_ID, Integer.class);
        permissionsItems.put(TAG_PERMISSIONS_NAME, String.class);
        items.put(TAG_PERMISSIONS, permissionsItems);

        // Sub list for roles
        HashMap<String, Object> rolesItems = new HashMap<String, Object>();
        rolesItems.put(TAG_ROLES_ID, Integer.class);
        rolesItems.put(TAG_ROLES_NAME, String.class);
        items.put(TAG_ROLES, rolesItems);

        return items;
    }
}
