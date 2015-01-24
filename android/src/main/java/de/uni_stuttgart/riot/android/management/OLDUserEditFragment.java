package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.enpro.android.riot.R;

/**
 * Created by Benny on 10.01.2015.
 */
public class OLDUserEditFragment extends OLDManagementDetailEditFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((Button) view.findViewById(R.id.user_abort_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt(OLDUserEditFragment.BUNDLE_OBJECT_ID, objectId);
                callOtherFragment(new OLDUserDetailFragment(), args);
            }
        });
        ((Button) view.findViewById(R.id.user_save_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ToDo: Save changes!!
                Bundle args = new Bundle();
                args.putInt(OLDUserEditFragment.BUNDLE_OBJECT_ID, objectId);
                callOtherFragment(new OLDUserDetailFragment(), args);
            }
        });
        return view;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_user_edit;
    }

    @Override
    protected String setTagArray() {
        return null;
    }

    @Override
    protected int getTitleId() {
        return R.string.user_edit;
    }

    @Override
    protected OLDManagementFragment getFragment() {
        return this;
    }

    @Override
    protected String getUrl() {
        return "{\"users\":[{\"id\":1,\"name\":\"Benny\",\"roles\":[{\"rId\":\"1\",\"rName\":\"Admin\"},{\"rId\":\"2\",\"rName\":\"User\"},{\"rId\":\"3\",\"rName\":\"Guest\"}],\"permissions\":[{\"pId\":\"1\",\"pName\":\"Read\"},{\"pId\":\"2\",\"pName\":\"Write\"},{\"pId\":\"3\",\"pName\":\"Delete\"}],\"onlineState\":\"online\"},{\"id\":2,\"name\":\"Benutzer\",\"roles\":[{\"rId\":\"2\",\"rName\":\"User\"},{\"rId\":\"3\",\"rName\":\"Guest\"}],\"permissions\":[{\"pId\":\"1\",\"pName\":\"Read\"},{\"pId\":\"2\",\"pName\":\"Write\"}],\"onlineState\":\"offline\"},{\"id\":3,\"name\":\"Admin\",\"roles\":[{\"rId\":\"1\",\"rName\":\"Admin\"}],\"permissions\":[{\"pId\":\"2\",\"pName\":\"Write\"},{\"pId\":\"3\",\"pName\":\"Delete\"}],\"onlineState\":\"away\"}]}";
    }

    @Override
    protected void displayData(List<HashMap<String, Object>> itemsList) {
        // Read the items and put the values to the matching components (normally there should be just one element)
        for (HashMap<String, Object> items : itemsList) {
            // Check the id if that entry is the right one
            if (items.containsKey(TAG_ID) && items.get(TAG_ID).getClass().equals(Integer.class) && ((Integer) items.get(TAG_ID)).equals(objectId)) {

                // Check the item names and set the value on the correct display object
                if (items.containsKey(TAG_NAME) && items.get(TAG_NAME).getClass().equals(String.class)) {
                    ((TextView) view.findViewById(R.id.user_name_edit)).setText((String) items.get(TAG_NAME));
                }
                if (items.containsKey(TAG_ROLES) && items.get(TAG_ROLES).getClass().equals(ArrayList.class)) {
                    // Get sub list elements
                    List<String> stringList = new ArrayList<String>();
                    for (HashMap<String, Object> subItems : (List<HashMap<String, Object>>) items.get(TAG_ROLES)) {
                        if (subItems.containsKey(TAG_ROLES_NAME) && subItems.get(TAG_ROLES_NAME).getClass().equals(String.class)) {
                            stringList.add((String) subItems.get(TAG_ROLES_NAME));
                        }
                    }
                    String[] roleArray = {"Admin", "User", "Guest"};
                    MultiSelectionSpinner roleSpinner = (MultiSelectionSpinner) view.findViewById(R.id.user_role_spinner);
                    roleSpinner.setItems(roleArray);
                    roleSpinner.setSelection(stringList);
                    // roleSpinner.getSelectedItemsAsArrayList();
                }
                if (items.containsKey(TAG_PERMISSIONS) && items.get(TAG_PERMISSIONS).getClass().equals(ArrayList.class)) {
                    // Get sub list elements
                    List<String> stringList = new ArrayList<String>();
                    for (HashMap<String, Object> subItems : (List<HashMap<String, Object>>) items.get(TAG_PERMISSIONS)) {
                        if (subItems.containsKey(TAG_PERMISSIONS_NAME) && subItems.get(TAG_PERMISSIONS_NAME).getClass().equals(String.class)) {
                            stringList.add((String) subItems.get(TAG_PERMISSIONS_NAME));
                        }
                    }

                    String[] permissionArray = {"Read", "Write", "Delte"};
                    MultiSelectionSpinner permissionSpinner = (MultiSelectionSpinner) view.findViewById(R.id.user_permission_spinner);
                    permissionSpinner.setItems(permissionArray);
                    permissionSpinner.setSelection(stringList);
                    // permissionSpinner.getSelectedItemsAsArrayList();
                }
                break;
            }
            // ToDo Default values setzten fuer die listenelemente..
        }
    }
}
