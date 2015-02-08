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
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Abstract fragment that provides a list to show all elements of a storable item typ.
 *
 * @author Benny
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

        // UsermanagementClient usermanagementClient = RIOTApiClient.getInstance().getUserManagementClient();

        // Collection<User> userCollection = androidUser.getUmClient().getUsers(); // TODO server-connection
        // ArrayList<User> userList = new ArrayList<User>(userCollection);

        List<Storable> data = getData();
        if (data == null) {
            // ToDo message or notice on screen that there are no data..
            return;
        }

        ManagementListAdapter managementListAdapter = new ManagementListAdapter(getFragment(), getActivity(), R.layout.list_item_managment_list, data);
        ListView listView = (ListView) this.view.findViewById(R.id.managementListView);
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
     * @param item the item
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
     * @param item the item
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
     * @param item the item
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
     * @param item the item
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
     * @param item the item
     * @return the online state of the list item
     */
    protected abstract OnlineState getOnlineState(Storable item);


    /**
     * Set values of the given item to the view elements.
     *
     * @param view includes the elements of the current view
     * @param item includes the data
     */
    public void doGetView(View view, Storable item) {
        // Save the default values
        String defaultSubject = getDefaultSubject();
        String defaultDescription = getDefaultDescription();
        int defaultImageId = getDefaultImageId();
        String defaultImageUri = getDefaultImageUri();
        OnlineState defaultOnlineState = getDefaultOnlineState();

        // Get values of the item when the item is an instance of the expected class
        if (isInstanceOf(item)) {
            defaultSubject = doGetSubject(item, defaultSubject);
            defaultDescription = doGetDescription(item, defaultDescription);
            defaultImageId = doGetImageId(item, defaultImageId);
            defaultImageUri = doGetImageUri(item, defaultImageUri);
            defaultOnlineState = doGetOnlineState(item, defaultOnlineState);
        }

        // Set the subject value
        if (defaultSubject != null && !defaultSubject.isEmpty()) {
            ((TextView) view.findViewById(R.id.listItemManagementSubject)).setText(defaultSubject);
        }

        // Set the description value
        if (defaultDescription != null && !defaultDescription.isEmpty()) {
            ((TextView) view.findViewById(R.id.listItemManagementDescription)).setText(defaultDescription);
        }

        // Set the image value
        if (defaultImageId != 0) {
            ((ImageView) view.findViewById(R.id.listItemManagementPicture)).setImageResource(defaultImageId);
        } else if (defaultImageUri != null && !defaultImageUri.isEmpty()) {
            ((ImageView) view.findViewById(R.id.listItemManagementPicture)).setImageURI(Uri.parse(defaultImageUri));
            // ToDo maybe load asynchronous??
        }

        // Set the online state value
        if (defaultOnlineState != null) {
            ((ImageView) view.findViewById(R.id.listItemManagementOnlineState)).setImageResource(defaultOnlineState.getR());
        }
    }

    /**
     * Get the subject from the item.
     *
     * @param item           includes the values
     * @param defaultSubject is the default value if the item does not have the wanted value
     * @return the value from the item or the default value
     */
    private String doGetSubject(Storable item, String defaultSubject) {
        // Get subject from item
        String subject = getSubject(item);

        // Check if item had a subject
        if (subject != null && !subject.isEmpty()) {
            return subject;
        }
        return defaultSubject;
    }

    /**
     * Get the description from the item.
     *
     * @param item               includes the values
     * @param defaultDescription is the default value if the item does not have the wanted value
     * @return the value from the item or the default value
     */
    private String doGetDescription(Storable item, String defaultDescription) {
        // Get description from item
        String description = getDescription(item);

        // Check if item had a description
        if (description != null && !description.isEmpty()) {
            return description;
        }
        return defaultDescription;
    }

    /**
     * Get the image id from the item.
     *
     * @param item           includes the values
     * @param defaultImageId is the default value if the item does not have the wanted value
     * @return the value from the item or the default value
     */
    private int doGetImageId(Storable item, int defaultImageId) {
        // Get subject from image id
        int imageId = getImageId(item);

        // Check if item had a image id
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
    private String doGetImageUri(Storable item, String defaultImageUri) {
        // Get image uri from item
        String imageUri = getImageUri(item);

        // Check if item had a image uri
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
    private OnlineState doGetOnlineState(Storable item, OnlineState defaultOnlineState) {
        // Get online state from item
        OnlineState onlineState = getOnlineState(item);

        // Check if item had a online state
        if (onlineState != null) {
            return onlineState;
        }
        return defaultOnlineState;
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
