package de.uni_stuttgart.riot.android.management;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.UsermanagementClient;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Created by Benny on 09.01.2015
 */
public abstract class ManagementListFragment extends ManagementFragment {

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_management_list;
    }

    @Override
    protected void handleArguments(Bundle args) {
    }

    @Override
    protected void displayData() {

        UsermanagementClient usermanagementClient = RIOTApiClient.getInstance().getUserManagementClient();

        // Collection<User> userCollection = androidUser.getUmClient().getUsers(); // TODO server-connection
        // ArrayList<User> userList = new ArrayList<User>(userCollection);

        List<Storable> data = getData();
        if (data == null) {
            // ToDo message or notice on screen that there are no data..
            return;
        }

        ManagementListAdapter managementListAdapter = new ManagementListAdapter(getFragment(), getActivity(), R.layout.list_item_managment_list, data);
        ListView listView = (ListView) this.view.findViewById(R.id.management_listView);
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

    /**
     * Returns a list of all data that will be displayed.
     *
     * @return the list of the specified data type
     */
    protected abstract List<Storable> getData();

    /**
     * Returns the current frame.
     *
     * @return normally just 'this'
     */
    protected abstract ManagementListFragment getFragment();

    /**
     * Returns the fragment that is called by clicking on an icon
     *
     * @return the called fragment (null if there should be no onClick action)
     */
    protected abstract Fragment getOnItemClickFragment();

    /**
     * Returns the default subject for the list item.
     *
     * @return is null if the element should not be displayed
     */
    protected abstract String getDefaultSubject();

    /**
     * Returns the subject for the list item.
     *
     * @return the subject for the list item
     */
    protected abstract String getSubject(Storable item);

    /**
     * Returns the default description of the list item.
     *
     * @return is null if the element should not be displayed
     */
    protected abstract String getDefaultDescription();

    /**
     * Returns the description of the list item.
     *
     * @return the description of the list item
     */
    protected abstract String getDescription(Storable item);

    /**
     * Returns the default image uri of the list item.
     *
     * @return is null if the element should not be displayed
     */
    protected abstract String getDefaultImageUri();

    /**
     * Returns the image uri of the list item.
     *
     * @return the image uri of the list item
     */
    protected abstract String getImageUri(Storable item);

    /**
     * Returns the default image resource id of the list item.
     *
     * @return is 0 if the element should not be displayed
     */
    protected abstract int getDefaultImageId();

    /**
     * Returns the image resource id of the list item.
     *
     * @return the image resource id of the list item
     */
    protected abstract int getImageId(Storable item);

    /**
     * Returns the default online state of the list item.
     *
     * @return is null if the element should not be displayed
     */
    protected abstract OnlineState getDefaultOnlineState();

    /**
     * Returns the online state of the list item.
     *
     * @return the online state of the list item
     */
    protected abstract OnlineState getOnlineState(Storable item);


    public void doGetView(View view, Storable item) {
        // Save the default values
        String subject, defaultSubject = getDefaultSubject();
        String description, defaultDescription = getDefaultDescription();
        int imageId, defaultImageId = getDefaultImageId();
        String imageUri, defaultImageUri = getDefaultImageUri();
        OnlineState onlineState, defaultOnlineState = getDefaultOnlineState();

        // Get values of the item when the item is an instance of the expected class
        if (isInstanceOf(item)) {
            subject = getSubject(item);
            description = getDescription(item);
            imageId = getImageId(item);
            imageUri = getImageUri(item);
            onlineState = getOnlineState(item);

            // Save the subject
            if (subject != null && !subject.isEmpty()) {
                defaultSubject = subject;
            }

            // Save the description
            if (description != null && !description.isEmpty()) {
                defaultDescription = description;
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

        // Set the subject value
        if (defaultSubject != null && !defaultSubject.isEmpty()) {
            ((TextView) view.findViewById(R.id.listItem_management_subject)).setText(defaultSubject);
        }

        // Set the description value
        if (defaultDescription != null && !defaultDescription.isEmpty()) {
            ((TextView) view.findViewById(R.id.listItem_management_description)).setText(defaultDescription);
        }

        // Set the image value
        if (defaultImageId != 0) {
            ((ImageView) view.findViewById(R.id.listItem_management_picture)).setImageResource(defaultImageId);
        } else if (defaultImageUri != null && !defaultImageUri.isEmpty()) {
            ((ImageView) view.findViewById(R.id.listItem_management_picture)).setImageURI(Uri.parse(defaultImageUri));
            // ToDo maybe load asynchronous??
        }

        // Set the online state value
        if (defaultOnlineState != null) {
            ((ImageView) view.findViewById(R.id.listItem_management_onlineState)).setImageResource(defaultOnlineState.getR());
        }
    }

    /**
     * Is the overwrite of the onItemClick method (it's possible to abstract this method for subclasses).
     *
     * @param parent   the adapter view where the click came from
     * @param view     the view where the element is displayed
     * @param position the position in the displayed list
     * @param id       the id of the element?
     */
    private void doOnItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getOnItemClickFragment() != null) {
            Storable item = (Storable) parent.getItemAtPosition(position);

            // Save information for the calling fragment
            Bundle args = new Bundle();
            args.putLong(BUNDLE_OBJECT_ID, item.getId());
            callOtherFragment(getOnItemClickFragment(), args);
        }
    }
}
