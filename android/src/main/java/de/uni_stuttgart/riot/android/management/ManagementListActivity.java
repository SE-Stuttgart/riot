package de.uni_stuttgart.riot.android.management;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
 * @param <T> type of the handled object
 * @param <D> type of the detailed view (that will be displayed after clicking on a item)
 * @author Benny
 */
public abstract class ManagementListActivity<T, D> extends ManagementActivity<List<T>> {

    protected List<T> items;
    private ListView listView;

    @Override
    protected int getLayoutResource() {
        return R.layout.management_list;
    }

    @Override
    protected void handleArguments(Bundle bundle) {
    }

    @Override
    protected void displayManagementData() {
        // Check if there is an item to display
        if (this.items == null) {
            IM.INSTANCES.getMH().writeErrorMessage("There is no data for displaying!");
            IM.INSTANCES.getMH().showQuickMessage("There is no data for displaying!");
            return;
        }

        // Check if the list view is available
        if (this.listView == null) {
            this.listView = (ListView) findViewById(R.id.management_list_view);
            if (this.listView == null) {
                IM.INSTANCES.getMH().writeErrorMessage("No list view was found!");
                IM.INSTANCES.getMH().showQuickMessage("No list view was found!");
                return;
            }
        }

        // Add data list to the list adapter
        ManagementListAdapter<T> managementListAdapter = new ManagementListAdapter<T>(this, R.layout.managment_list_item, this.items);
        this.listView.setAdapter(managementListAdapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    T item = (T) parent.getItemAtPosition(position);
                    doOnItemClick(item);
                } catch (Exception e) {
                    IM.INSTANCES.getMH().writeErrorMessage("Could not cast list item to the needed type!", e);
                    IM.INSTANCES.getMH().showQuickMessage("Could not cast list item to the needed type!");
                }

            }
        });
        // To change items afterwards (when list adapter is already installed) use:
        // managementListAdapter.notifyDataSetChanged();
    }

    /**
     * Get values of the item and save them.
     *
     * @param item is the clicked list item
     * @param view of the list item
     */
    public void buildListItem(T item, View view) {
        setSubject(item, view);
        setDescription(item, view);
        setImage(item, view);
        setOnlineState(item, view);
    }

    /**
     * Is the overwrite of the onItemClick method (it's possible to abstract this method for subclasses).
     *
     * @param item is the clicked list item
     */
    protected void doOnItemClick(T item) {
        if (getOnItemClickActivity() != null) {
            // Call detail view with the id as argument
            Intent detailView = new Intent(getApplicationContext(), getOnItemClickActivity());
            detailView.putExtra(BUNDLE_OBJECT_ID, getId(item));
            startActivity(detailView);
        }
    }

    /**
     * Set the subject of the list item.
     *
     * @param item includes the values
     * @param view of the list item
     */
    private void setSubject(final T item, final View view) {
        String subject = getSubject(item);
        if (subject == null || subject.isEmpty()) {
            subject = getDefaultSubject();
        }

        if (subject != null && !subject.isEmpty()) {
            setTextViewText(view, R.id.list_item_management_subject, subject);
        }
    }

    /**
     * Set the description of the list item.
     *
     * @param item includes the values
     * @param view of the list item
     */
    private void setDescription(final T item, final View view) {
        String description = getDescription(item);
        if (description == null || description.isEmpty()) {
            description = getDefaultDescription();
        }

        if (description != null && !description.isEmpty()) {
            setTextViewText(view, R.id.list_item_management_description, description);
        }
    }

    /**
     * Set the image of the list item.
     *
     * @param item includes the values
     * @param view of the list item
     */
    private void setImage(final T item, final View view) {
        new AsyncTask<Void, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(Void... voids) {
                try {
                    return getImage(item);
                } catch (Exception e) {
                    IM.INSTANCES.getMH().writeErrorMessage("An error occurred during loading the data: ", e);
                    IM.INSTANCES.getMH().showQuickMessage("An error occurred during loading the data!");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);

                Drawable newDrawable;
                if (drawable == null) {
                    newDrawable = getDefaultImage();
                } else {
                    newDrawable = drawable;
                }
                setImageViewImage(view, R.id.list_item_management_picture, newDrawable);
            }
        }.execute();
    }

    /**
     * Try to get the online state from the server and update it.
     *
     * @param item is the item that has the online state
     * @param view of the list item
     */
    private void setOnlineState(final T item, final View view) {

        new AsyncTask<Void, Void, OnlineState>() {

            @Override
            protected OnlineState doInBackground(Void... voids) {
                try {
                    return getOnlineState(item);
                } catch (Exception e) {
                    IM.INSTANCES.getMH().writeErrorMessage("An error occurred during loading the data: ", e);
                    IM.INSTANCES.getMH().showQuickMessage("An error occurred during loading the data!");
                }
                return null;
            }

            @Override
            protected void onPostExecute(OnlineState onlineState) {
                super.onPostExecute(onlineState);

                OnlineState newOnlineState;
                if (onlineState == null) {
                    newOnlineState = getDefaultOnlineState();
                } else {
                    newOnlineState = onlineState;
                }
                setImageViewImage(view, R.id.list_item_management_online_state, getDrawableByResource(getOnlineStateResourceId(newOnlineState)));
            }
        }.execute();
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
     * Set the image of the image view with the given drawable.
     *
     * @param view  the main view
     * @param id    the id of the image view
     * @param image the drawable for the image
     */
    private void setImageViewImage(View view, int id, Drawable image) {
        if (image == null) {
            return;
        }
        ImageView imageView = (ImageView) view.findViewById(id);
        if (imageView == null) {
            IM.INSTANCES.getMH().writeErrorMessage("No image view was found!");
            IM.INSTANCES.getMH().showQuickMessage("No image view was found!");
            return;
        }
        imageView.setImageDrawable(image);
    }

    /**
     * Returns the fragment that is called by clicking on an icon
     *
     * @return the called fragment (null if there should be no onClick action)
     */
    protected abstract Class<D> getOnItemClickActivity();

    /**
     * Returns the id of the list item.
     *
     * @param item the item
     * @return the id of the list item
     */
    protected abstract long getId(T item);

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
    protected abstract String getSubject(T item);

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
    protected abstract String getDescription(T item);

    /**
     * Returns the default drawable image of the list item.
     *
     * @return is null if the element should not be displayed
     */
    protected abstract Drawable getDefaultImage();

    /**
     * Returns the drawable image of the list item.
     *
     * @param item the item
     * @return the drawable image of the list item
     */
    protected abstract Drawable getImage(T item);

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
    protected abstract OnlineState getOnlineState(T item);
}
