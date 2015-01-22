package de.uni_stuttgart.riot.android.management;


import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.account.AndroidUser;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * Created by Benny on 10.01.2015.
 */
public abstract class ManagementFragment extends Fragment {

    // Attributes
    protected View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(getLayoutResource(), container, false);

        // Set the title of the frame
        getActivity().setTitle(getTitleId());

        // Add a new android user
        AndroidUser androidUser = new AndroidUser(getActivity());
        // androidUser.logIn("R2D2", "R2D2PW");
        // androidUser.logIn("Yoda", "YodaPW");
        androidUser.logIn("Vader", "VaderPW");

        // Display data
        try {
            displayData(androidUser);
        } catch (RequestException e) {
            // ToDo output!
            e.printStackTrace();
        }

        // Logout the user
        androidUser.logOut();

        return this.view;
    }


    private void displayData(AndroidUser androidUser) throws RequestException {
        ManagementListAdapter managementListAdapter = new ManagementListAdapter(this, getActivity(), R.layout.list_item_managment_list, new ArrayList<User>(androidUser.getUmClient().getUsers()));
        ListView listView = (ListView) getActivity().findViewById(R.id.management_listView);
        listView.setAdapter(managementListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doOnItemClick(parent, view, position, id);
            }
        });
        // If you want to change items afterwards (when list adapter is already installed) use:
        // managementListAdapter.notifyDataSetChanged();

    }


    public void doGetView(View view, DummyUser item) {

        // Save values
        ((TextView) view.findViewById(R.id.listItem_management_subject)).setText(item.getUsername());
        ((TextView) view.findViewById(R.id.listItem_management_description)).setText(item.getId().toString());
        ((ImageView) view.findViewById(R.id.listItem_management_onlineState)).setImageResource(item.getOnlineState().getR());

        // ToDo maybe load asynchronous??
        ((ImageView) view.findViewById(R.id.listItem_management_picture)).setImageURI(Uri.parse(item.getImageUri()));

        // ToDo Default values for list elements..
    }


    protected void doOnItemClick(AdapterView<?> parent, View view, int position, long id) {
        DummyUser item = (DummyUser) parent.getItemAtPosition(position);

        // Save information for the calling fragment
        Bundle args = new Bundle();
        // args.putInt(OLDUserDetailFragment.BUNDLE_OBJECT_ID, (Integer) item.get(TAG_ID));
        // callOtherFragment(new OLDUserDetailFragment(), args);
    }


    protected int getLayoutResource() {
        return R.layout.fragment_management_list;
    }

    protected int getTitleId() {
        return R.string.user_list;
    }
}
