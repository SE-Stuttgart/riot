package de.uni_stuttgart.riot.android.management;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.commons.model.OnlineState;

/**
 * Abstract activity that provides a list to show all elements of a Object item typ.
 *
 * @author Benny
 */
public abstract class ManagementListFragment extends ManagementFragment {

    @Override
    protected int getLayoutResource() {
        return R.layout.management_list;
    }

    @Override
    protected void handleArguments(Bundle bundle) {
    }

    @Override
    protected void displayData() {
        List<Object> data = getData();
        if (data == null) {
            // Show a message that no data is available (ToDo log?)
            IM.INSTANCES.getMH().showQuickMessage("There are no data available!");
            return;
        }

        ManagementListAdapter managementListAdapter = new ManagementListAdapter(getFragment(), this, R.layout.managment_list_item, data);
        ListView listView = (ListView) findViewById(R.id.management_list_view);
        if (listView == null) {
            // Show a message that no list view was found (ToDo output message!)
            IM.INSTANCES.getMH().writeErrorMessage("No list view was found!");
            return;
        }
        listView.setAdapter(managementListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doOnItemClick(parent, view, position, id);
            }
        });
        // To change items afterwards (when list adapter is already installed) use:
        // managementListAdapter.notifyDataSetChanged();
    }


    @Override
    protected Drawable getHomeIcon() {
        return null;
    }

    @Override
    protected int getHomeLogo() {
        return 0;
    }

    /**
     * Returns a list of all data that will be displayed.
     *
     * @return the list of the specified data type
     */
    protected abstract List<Object> getData();

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
    protected abstract Class getOnItemClickActivity();

    /**
     * Returns the id of the list item.
     *
     * @param item the item
     * @return the id of the list item
     */
    protected abstract long getId(Object item);

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
    protected abstract String getSubject(Object item);

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
    protected abstract String getDescription(Object item);

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
    protected abstract String getImageUri(Object item);

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
    protected abstract int getImageId(Object item);

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
     * @param view the list item
     * @return the online state of the list item
     */
    protected abstract void getOnlineState(Object item, View view);

    /**
     * Set values of the given item to the view elements.
     *
     * @param view includes the elements of the current view
     * @param item includes the data
     */
    public void doGetView(View view, Object item) {
        // Save the default values
        String defaultSubject = getDefaultSubject();
        String defaultDescription = getDefaultDescription();
        int defaultImageId = getDefaultImageId();
        String defaultImageUri = getDefaultImageUri();

        // Get values of the item when the item is an instance of the expected class
        if (isInstanceOf(item)) {
            defaultSubject = doGetSubject(item, defaultSubject);
            defaultDescription = doGetDescription(item, defaultDescription);
            defaultImageId = doGetImageId(item, defaultImageId);
            defaultImageUri = doGetImageUri(item, defaultImageUri);
        }

        // Set the subject value
        if (defaultSubject != null && !defaultSubject.isEmpty()) {
            TextView textView = (TextView) view.findViewById(R.id.list_item_management_subject);
            if (textView == null) {
                // ToDo output message!
                IM.INSTANCES.getMH().writeErrorMessage("No text view was found!");
                return;
            }
            textView.setText(defaultSubject);
        }

        // Set the description value
        if (defaultDescription != null && !defaultDescription.isEmpty()) {
            TextView textView = (TextView) view.findViewById(R.id.list_item_management_description);
            if (textView == null) {
                // ToDo output message!
                IM.INSTANCES.getMH().writeErrorMessage("No text view was found!");
                return;
            }
            textView.setText(defaultDescription);
        }

        // Set the image value
        if (defaultImageId != 0) {
            ImageView imageView = (ImageView) view.findViewById(R.id.list_item_management_picture);
            if (imageView == null) {
                // ToDo output message!
                IM.INSTANCES.getMH().writeErrorMessage("No image view was found!");
                return;
            }
            imageView.setImageResource(defaultImageId);
        } else if (defaultImageUri != null && !defaultImageUri.isEmpty()) {
            ImageView imageView = (ImageView) view.findViewById(R.id.list_item_management_picture);
            if (imageView == null) {
                // ToDo output message!
                IM.INSTANCES.getMH().writeErrorMessage("No image view was found!");
                return;
            }
            imageView.setImageURI(Uri.parse(defaultImageUri));
            // ToDo maybe load asynchronous??
        }

        // Load data asynchronous
        getOnlineState(item, view);
    }

    /**
     * Update the online state of the list item.
     *
     * @param onlineState is the online state of one item
     */
    protected void doUpdateOnlineState(final View view, OnlineState onlineState) {
        // FIXME always offline (from server side)
        final OnlineState defaultOnlineState;
        if (onlineState == null) {
            // Take default online state if there is no one
            defaultOnlineState = getDefaultOnlineState();
        } else {
            defaultOnlineState = onlineState;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Set the online state value
                if (defaultOnlineState != null) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.list_item_management_online_state);
                    if (imageView == null) {
                        // ToDo output message!
                        IM.INSTANCES.getMH().writeErrorMessage("No image view was found!");
                        return;
                    }
                    imageView.setImageResource(getOnlineStateResourceId(defaultOnlineState));
                }
            }
        });
    }

    /**
     * Get the subject from the item.
     *
     * @param item           includes the values
     * @param defaultSubject is the default value if the item does not have the wanted value
     * @return the value from the item or the default value
     */
    private String doGetSubject(Object item, String defaultSubject) {
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
    private String doGetDescription(Object item, String defaultDescription) {
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
    private int doGetImageId(Object item, int defaultImageId) {
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
    private String doGetImageUri(Object item, String defaultImageUri) {
        // Get image uri from item
        String imageUri = getImageUri(item);

        // Check if item had a image uri
        if (imageUri != null && !imageUri.isEmpty()) {
            return imageUri;
        }
        return defaultImageUri;
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
        if (getOnItemClickActivity() != null) {
            Object item = parent.getItemAtPosition(position);

            // Call detail view with the id as argument
            Intent detailView = new Intent(getApplicationContext(), getOnItemClickActivity());
            detailView.putExtra(BUNDLE_OBJECT_ID, getId(item));
            startActivity(detailView);
        }
    }
}
