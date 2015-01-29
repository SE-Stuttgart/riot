package de.uni_stuttgart.riot.android.users;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.enpro.android.riot.R;

/**
 * Created by Benny on 16.12.2014.
 */
public class UserEditFragment extends Fragment {

    private MultiSelectionSpinner roleSpinner, permissionSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_edit, container, false);

//        String[] roleArray = {"Admin", "User", "Guest"};
//        roleSpinner = (MultiSelectionSpinner) view.findViewById(R.id.user_role_spinner);
//        roleSpinner.setItems(roleArray);
//
//        String[] permissionArray = {"Edit User", "View User", "Everything"};
//        permissionSpinner = (MultiSelectionSpinner) view.findViewById(R.id.user_permission_spinner);
//        permissionSpinner.setItems(permissionArray);
//
//
//        final EditText values = (EditText) view.findViewById(R.id.user_name_edit);
//
//        ((Button) view.findViewById(R.id.user_edit_button)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String s = "Roles: ";
//                s += roleSpinner.getSelectedItemsAsString();
//                s += " -- Permissions: ";
//                s += permissionSpinner.getSelectedItemsAsString();
//                values.setText(s);
//            }
//        });

        return view;
    }
}
