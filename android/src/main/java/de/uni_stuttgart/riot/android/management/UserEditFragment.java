package de.uni_stuttgart.riot.android.management;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedHashSet;

import de.enpro.android.riot.R;

/**
 * Created by Benny on 10.01.2015.
 */
public class UserEditFragment extends Fragment {

    // Attributes
    private View view;
    private long objectId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(getLayoutResource(), container, false);

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ManagementFragment.BUNDLE_OBJECT_ID)) {
                this.objectId = args.getLong(ManagementFragment.BUNDLE_OBJECT_ID);
            }
        }

        // Set the title of the frame
        getActivity().setTitle(getTitleId());

        ((Button) view.findViewById(R.id.user_abort_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putLong(ManagementFragment.BUNDLE_OBJECT_ID, objectId); // ToDo why not this.objectId
                callOtherFragment(new UserDetailFragment(), args);
            }
        });

        ((Button) view.findViewById(R.id.user_save_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ToDo save changes!!
                Bundle args = new Bundle();
                args.putLong(ManagementFragment.BUNDLE_OBJECT_ID, objectId); // ToDo why not this.objectId
                callOtherFragment(new UserDetailFragment(), args);
            }
        });

        displayData();

        return view;
    }

    private int getLayoutResource() {
        return R.layout.fragment_user_edit;
    }

    private int getTitleId() {
        return R.string.user_edit;
    }

    private void displayData() {
        DummyUser item = ManagementFragment.getUser(objectId);
        if (item == null) {
            // ToDo message or notice on screen that there are no data..
            return;
        }

        if (item.getId() != null && item.getId() != -1) { // ToDo -> default value -1?
            ((TextView) view.findViewById(R.id.user_id_edit)).setText(item.getId().toString());
        }

        if (item.getUsername() != null && !item.getUsername().isEmpty()) {
            ((TextView) view.findViewById(R.id.user_name_edit)).setText(item.getUsername());
        }

        LinkedHashSet<String> allRoleNames = ManagementFragment.getAllRoleNames();
        LinkedHashSet<String> roleNames = item.getRoleNames();
        if (allRoleNames != null && !allRoleNames.isEmpty() && roleNames != null && !roleNames.isEmpty()) {
            MultiSelectionSpinner roleSpinner = (MultiSelectionSpinner) view.findViewById(R.id.user_role_spinner);
            roleSpinner.setItems(allRoleNames);
            roleSpinner.setSelection(roleNames);
            // roleSpinner.getSelectedItemsAsArrayList();
        }

        LinkedHashSet<String> allPermissionNames = ManagementFragment.getAllPermissionNames();
        LinkedHashSet<String> permissionNames = item.getPermissionNames();
        if (allPermissionNames != null && !allPermissionNames.isEmpty() && permissionNames != null && !permissionNames.isEmpty()) {
            MultiSelectionSpinner roleSpinner = (MultiSelectionSpinner) view.findViewById(R.id.user_permission_spinner);
            roleSpinner.setItems(allPermissionNames);
            roleSpinner.setSelection(permissionNames);
            // roleSpinner.getSelectedItemsAsArrayList();
        }
        // ToDo set default values for fields...
    }

    /**
     * Calls an other fragment
     *
     * @param fragment the called fragment
     */
    private void callOtherFragment(Fragment fragment) {
        callOtherFragment(fragment, null);
    }

    /**
     * Calls an other fragment and sends some data
     *
     * @param fragment the called fragment
     * @param args     some data for the new fragment
     */
    private void callOtherFragment(Fragment fragment, Bundle args) {
        if (args != null) {
            fragment.setArguments(args);
        }
        ((FragmentManager) getFragmentManager()).beginTransaction().replace(R.id.content_frame, fragment).commit();
    }


}
