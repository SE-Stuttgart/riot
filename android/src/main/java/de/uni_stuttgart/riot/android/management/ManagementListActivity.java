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

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.commons.model.OnlineState;

/**
 * Abstract activity that provides a list to show all elements of a Object item typ.
 *
 * @author Benny
 */
public abstract class ManagementListActivity extends ManagementActivity {

    @Override
    protected int getLayoutResource() {
        return R.layout.management_list;
    }

    @Override
    protected void handleArguments(Bundle bundle) {
    }

    @Override
    protected void displayData(Object data) {
        // Check if there is some data to display
        if (data == null) {
            IM.INSTANCES.getMH().writeErrorMessage("There are no data available!");
            IM.INSTANCES.getMH().showQuickMessage("There are no data available!");
            return;
        }


        // Check if the given data is a list
        if (!(data instanceof List)) {
            IM.INSTANCES.getMH().writeErrorMessage("Data is not a list!");
            IM.INSTANCES.getMH().showQuickMessage("Data is not a list!");
            return;
        }

        // Add data list to the list adapter
        ManagementListAdapter managementListAdapter = new ManagementListAdapter(getActivity(), this, R.layout.managment_list_item, (List<Object>) data);
        ListView listView = (ListView) findViewById(R.id.management_list_view);
        if (listView == null) {
            IM.INSTANCES.getMH().writeErrorMessage("No list view was found!");
            IM.INSTANCES.getMH().showQuickMessage("No list view was found!");
            return;
        }
        listView.setAdapter(managementListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doOnItemClick(parent, position);
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

    @Override
    protected void getAndDisplayData() {
        getAndDisplayListData();
    }

    /**
     * Is the overwrite of the onItemClick method (it's possible to abstract this method for subclasses).
     *
     * @param parent   the adapter view where the click came from
     * @param position the position in the displayed list
     */
    private void doOnItemClick(AdapterView<?> parent, int position) {
        if (getOnItemClickActivity() != null) {
            Object item = parent.getItemAtPosition(position);

            // Call detail view with the id as argument
            Intent detailView = new Intent(getApplicationContext(), getOnItemClickActivity());
            if (isInstanceOf(item)) {
                detailView.putExtra(BUNDLE_OBJECT_ID, getId(item));
                detailView.putExtra(BUNDLE_PAGE_TITLE, getDetailPageTitle(item));
            }
            startActivity(detailView);
        }
    }

    /**
     * Tries to get the online state from the server and update it.
     *
     * @param item is the item that has the online state
     * @param view of the list item
     */
    private void getAndUpdateOnlineState(Object item, final View view) {
        if (isInstanceOf(item)) {
            final OnlineState onlineState = getOnlineState(item);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateOnlineState(view, onlineState);
                }
            });
        }
    }

    /**
     * Get the subject from the item.
     *
     * @param item includes the values
     * @return the value from the item or the default value
     */
    private String doGetSubject(Object item) {
        if (isInstanceOf(item)) {
            // Get subject from item
            String subject = getSubject(item);

            // Check if item had a subject
            if (subject != null && !subject.isEmpty()) {
                return subject;
            }
        }
        return getDefaultSubject();
    }

    /**
     * Get the description from the item.
     *
     * @param item includes the values
     * @return the value from the item or the default value
     */
    private String doGetDescription(Object item) {
        if (isInstanceOf(item)) {
            // Get description from item
            String description = getDescription(item);

            // Check if item had a description
            if (description != null && !description.isEmpty()) {
                return description;
            }
        }
        return getDefaultDescription();
    }

    /**
     * Get the image id from the item.
     *
     * @param item includes the values
     * @return the value from the item or the default value
     */
    private int doGetImageId(Object item) {
        if (isInstanceOf(item)) {
            // Get subject from image id
            int imageId = getImageId(item);

            // Check if item had a image id
            if (imageId != 0) {
                return imageId;
            }
        }
        return getDefaultImageId();
    }

    /**
     * Get the image uri from the item.
     *
     * @param item includes the values
     * @return the value from the item or the default value
     */
    private String doGetImageUri(Object item) {
        if (isInstanceOf(item)) {
            // Get image uri from item
            String imageUri = getImageUri(item);

            // Check if item had a image uri
            if (imageUri != null && !imageUri.isEmpty()) {
                return imageUri;
            }
        }
        return getDefaultImageUri();
    }

    /**
     * Set the text of the text view with the given id.
     *
     * @param view the main view
     * @param id   the id of the text view
     * @param text the text that will be set
     */
    private void setTextViewText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        if (textView == null) {
            IM.INSTANCES.getMH().writeErrorMessage("No text view was found!");
            IM.INSTANCES.getMH().showQuickMessage("No text view was found!");
            return;
        }
        textView.setText(text);
    }

    /**
     * Set the image of the image view with the given id.
     *
     * @param view    the main view
     * @param id      the id of the image view
     * @param imageId the id for the image
     */
    private void setImageViewImage(View view, int id, int imageId) {
        ImageView imageView = (ImageView) view.findViewById(id);
        if (imageView == null) {
            IM.INSTANCES.getMH().writeErrorMessage("No image view was found!");
            IM.INSTANCES.getMH().showQuickMessage("No image view was found!");
            return;
        }
        imageView.setImageResource(imageId);
    }

    /**
     * Set the image of the image view with the given id.
     *
     * @param view the main view
     * @param id   the id of the image view
     * @param uri  the uri for the image
     */
    private void setImageViewImage(View view, int id, String uri) {
        ImageView imageView = (ImageView) view.findViewById(id);
        if (imageView == null) {
            IM.INSTANCES.getMH().writeErrorMessage("No image view was found!");
            IM.INSTANCES.getMH().showQuickMessage("No image view was found!");
            return;
        }
        imageView.setImageURI(Uri.parse(uri));
        // ToDo maybe load asynchronous??
    }

    /**
     * Update the online state of the list item.
     *
     * @param view        the view that will be updated
     * @param onlineState is the online state of one item
     */
    private void updateOnlineState(final View view, OnlineState onlineState) {
        final OnlineState defaultOnlineState;
        if (onlineState == null) {
            // Take default online state if there is no one
            defaultOnlineState = getDefaultOnlineState();
        } else {
            defaultOnlineState = onlineState;
        }

        // Set the online state value
        if (defaultOnlineState != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.list_item_management_online_state);
            if (imageView == null) {
                IM.INSTANCES.getMH().writeErrorMessage("No image view was found!");
                IM.INSTANCES.getMH().showQuickMessage("No image view was found!");
                return;
            }
            imageView.setImageResource(getOnlineStateResourceId(defaultOnlineState));
        }
    }

    /**
     * Set values of the given item to the view elements.
     *
     * @param view includes the elements of the current view
     * @param item includes the data
     */
    public void doGetView(final View view, final Object item) {
        // Get values of the item
        String subject = doGetSubject(item);
        String description = doGetDescription(item);
        int imageId = doGetImageId(item);
        String imageUri = doGetImageUri(item);

        // Set the subject value
        if (subject != null && !subject.isEmpty()) {
            setTextViewText(view, R.id.list_item_management_subject, subject);
        }

        // Set the description value
        if (description != null && !description.isEmpty()) {
            setTextViewText(view, R.id.list_item_management_description, description);
        }

        // Set the image value
        if (imageId != 0) {
            setImageViewImage(view, R.id.list_item_management_picture, imageId);
        } else if (imageUri != null && !imageUri.isEmpty()) {
            setImageViewImage(view, R.id.list_item_management_picture, imageUri);
        }

        // Load data asynchronous
        new Thread() {

            @Override
            public void run() {
                // Load the online state
                getAndUpdateOnlineState(item, view);
            }
        }.start();
    }

    /**
     * Returns a list of all data that will be displayed.
     */
    protected abstract void getAndDisplayListData();

    /**
     * Returns the current frame.
     *
     * @return normally just 'this'
     */
    protected abstract ManagementListActivity getActivity();

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
     * @return the online state of the item
     */
    protected abstract OnlineState getOnlineState(Object item);

    /**
     * Returns the page title for the detail view of the given item.
     *
     * @param item is the item that will be displayed in the detail view
     * @return the page title for the detail view
     */
    protected abstract String getDetailPageTitle(Object item);
}
