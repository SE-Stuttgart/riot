package de.uni_stuttgart.riot.android.management;


import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.atomic.AtomicInteger;

import de.uni_stuttgart.riot.android.R;
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

    protected static final String BUNDLE_OBJECT_ID = "SELECTED_OBJECT_ID";
    protected static final String BUNDLE_PAGE_TITLE = "SELECTED_PAGE_TITLE";

    // Is needed to set ids for grouped views
    private static final AtomicInteger GENERATED_ID = new AtomicInteger(1);

    private MenuItem refreshButton;

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
        Bundle tmpSavedInstanceState = savedInstanceState;
        if (tmpSavedInstanceState == null) {
            tmpSavedInstanceState = getIntent().getExtras();
        }
        // Handle arguments if not null
        if (tmpSavedInstanceState != null) {
            handleArguments(tmpSavedInstanceState);
        }

        // Set action bar main icon as back menu item
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set the title of the page
        setTitle(getPageTitle());

        // Display data the first time
        refreshAndDisplayData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add item to menu
        refreshButton = menu.add("refresh");
//        refreshButton = menu.add(getString(R.string.refresh)); // FIXME getString throws a Resources$NotFoundException
        refreshButton.setIcon(android.R.drawable.stat_notify_sync).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        refreshButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Update displayed data
                refreshAndDisplayData();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//        }

        // Refresh the displayed data
        refreshAndDisplayData();
    }

    /**
     * Refresh and display data.
     */
    private void refreshAndDisplayData() {
        new Thread() {

            @Override
            public void run() {
                // TODO use a timeout
                try {
                    startProcessingAnimation(true);

                    // Get data
                    final Object data = getData();

                    // Display data
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update the home logo or icon
                            updateHomeLogo();

                            // Display the data
                            displayData(data);

                            // End processing animation
                            startProcessingAnimation(false);
                        }
                    });
                } catch (Exception e) {
                    // End processing animation
                    startProcessingAnimation(false);

                    // Show a message that there was something wrong (FIXME output message!!)
                    IM.INSTANCES.getMH().writeErrorMessage("Problems by creating view: " + e.getMessage());
                }
            }
        }.start();
    }

    /**
     * Starts the processing animation (runs in a separate thread).
     *
     * @param value true starts it - false stops it
     */
    private void startProcessingAnimation(final boolean value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (value) {
                    // Prepare a progress animation
                    if (refreshButton != null) {
                        refreshButton.setActionView(R.layout.management_refresh_progress);
                    }
                    return;
                }
                // Stop progress animation
                if (refreshButton != null) {
                    refreshButton.setActionView(null);
                }
            }
        });
    }

    /**
     * Update the home logo asynchronous (runs in a separate thread).
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
            }
        }
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

    /**
     * Returns the string of the view title.
     *
     * @return the title
     */
    protected abstract String getPageTitle();

    /**
     * Returns a list of all data that will be displayed (runs in a separate thread).
     *
     * @return the list of the specified data type
     */
    protected abstract Object getData();

    /**
     * Build the elements that will be displayed (runs in a separate thread).
     *
     * @param data includes the data that where delivered from the server
     */
    protected abstract void displayData(Object data);

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
     * Checks if the item is an instance of this class.
     *
     * @param item the item that will be checked
     * @return true if the item is an instance of the class
     */
    protected abstract boolean isInstanceOf(Object item);
}
