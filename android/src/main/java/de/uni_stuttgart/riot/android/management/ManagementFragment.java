package de.uni_stuttgart.riot.android.management;


import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.atomic.AtomicInteger;

import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.commons.model.OnlineState;

/**
 * Is the main abstract activity for all management classes.
 *
 * @author Benny
 */
public abstract class ManagementFragment extends Activity {

    // FIXME TODOs for this and its subclasses:
    // reorder methods
    // rename all fragment things to activity
    // check output messages
    // do all fixes and to-do-s
    // optimize the call of the "RIOTApiClient.getInstance()." -> maybe optimize that

    // FIXME Changes will be saved instant? (detailView)

    // Is needed to set ids for grouped views
    private static final AtomicInteger GENERATED_ID = new AtomicInteger(1);

    protected final String BUNDLE_OBJECT_ID = "SELECTED_OBJECT_ID";
    protected MenuItem refreshItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getLayoutResource() == 0) {
            // ToDo output message!
            IM.INSTANCES.getMH().writeErrorMessage("No layout resource was defined!");
            return;
        }

        setContentView(getLayoutResource());

        // Get extras from intent if saved instance state is empty
        if (savedInstanceState == null) {
            savedInstanceState = getIntent().getExtras();
        }
        // Handle arguments if not null
        if (savedInstanceState != null) {
            handleArguments(savedInstanceState);
        }

        // Set action bar main icon as back menu item
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set the (default) title of the frame
        updateTitle();

        // Refresh data (first time is needed to show data)
        doRefresh();

        // Helps to update the (action) menu
        // TODO setHasOptionsMenu(true);
        // for using onCreateOptionsMenu(), onPrepareOptionsMenu(),...
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add item to menu
        refreshItem = menu.add("refresh");
//        refreshItem = menu.add(getString(R.string.refresh)); // FIXME getString throws a Resources$NotFoundException
        refreshItem.setIcon(android.R.drawable.stat_notify_sync).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                doRefresh();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Refresh the displayed data.
     */
    private void doRefresh() {
        new Thread() {

            @Override
            public void run() {
                // TODO use a timeout
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Prepare a progress animation
                            if (refreshItem != null) {
                                refreshItem.setActionView(R.layout.management_refresh_progress);
                            }
                        }
                    });

                    // Get all known things (like a refresh action)
                    // ToDo use that on app start and with a refresh button
                    RIOTApiClient.getInstance().getDeviceBehavior().updateThings();

                    // Display the data in the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Display data
                            displayData();

                            // Update the home logo
                            updateHomeLogo();

                            // Update title if necessary
                            updateTitle();

                            // Stop progress animation
                            if (refreshItem != null) {
                                refreshItem.setActionView(null);
                            }
                        }
                    });
                } catch (Exception e) {
                    // Stop progress animation
                    if (refreshItem != null) {
                        refreshItem.setActionView(null);
                    }

                    // Show a message that there was something wrong (FIXME output message!!)
                    IM.INSTANCES.getMH().writeErrorMessage("Problems by creating view: " + e.getMessage());
                }
            }
        }.start();
    }

    /**
     * Generate a value suitable for use in View.setId(int).
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    protected static int generateViewId() {
        for (; ; ) {
            final int result = GENERATED_ID.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            final int maxValue = 0x00FFFFFF;
            int newValue = result + 1;
            if (newValue > maxValue) {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (GENERATED_ID.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * Update the home logo.
     */
    private void updateHomeLogo() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (getHomeIcon() != null) {
                actionBar.setIcon(getHomeIcon());
                return;
            }
            if (getHomeLogo() != 0) {
                actionBar.setLogo(getHomeLogo());
                return;
            }
        }
    }

    /**
     * Update the activity title.
     */
    private void updateTitle() {
        setTitle(getPageTitle());
    }

    /**
     * Handle the arguments on creating this view.
     *
     * @param bundle includes the arguments
     */
    protected abstract void handleArguments(Bundle bundle);

    /**
     * Returns the resource id of the view layout.
     *
     * @return the id of the resource layout
     */
    protected abstract int getLayoutResource();

    /**
     * Returns the drawable item of the home logo.
     *
     * @return the drawable item
     */
    protected abstract Drawable getHomeIcon();

    /**
     * Returns the resource id of the home logo.
     *
     * @return the resource id
     */
    protected abstract int getHomeLogo();

    /**
     * Returns the string of the view title.
     *
     * @return the title
     */
    protected abstract String getPageTitle();

    /**
     * Checks if the item is an instance of this class.
     *
     * @param item the item that will be checked
     * @return true if the item is an instance of the class
     */
    protected abstract boolean isInstanceOf(Object item);

    /**
     * Build the elements that will be displayed.
     */
    protected abstract void displayData();

    /**
     * Returns the android resource id of the specific online state.
     *
     * @param onlineState that needs the resource id
     * @return the id of the online state resource
     */
    protected int getOnlineStateResourceId(OnlineState onlineState) {
        // FIXME -> push this method to another class??
        if (onlineState.equals(OnlineState.STATUS_ONLINE)) {
            return android.R.drawable.presence_online;
        } else if (onlineState.equals(OnlineState.STATUS_OFFLINE)) {
            return android.R.drawable.presence_offline;
        } else if (onlineState.equals(OnlineState.STATUS_AWAY)) {
            return android.R.drawable.presence_away;
        } else {
            return android.R.drawable.presence_busy;
        }
    }
}
