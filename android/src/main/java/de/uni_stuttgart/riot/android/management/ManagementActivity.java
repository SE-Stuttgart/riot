package de.uni_stuttgart.riot.android.management;


import android.app.ActionBar;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.commons.model.OnlineState;

/**
 * Is the main abstract activity for all management classes.
 *
 * @author Benny
 */
public abstract class ManagementActivity extends Activity {

    // FIXME TODOs for this and its subclasses:
    // reorder methods
    // do all fixes and to-do-s

    /**
     * Helps to save the selected item of the list to display the details of it.
     */
    protected static final String BUNDLE_OBJECT_ID = "SELECTED_OBJECT_ID";

    /**
     * Is needed to set ids for grouped views.
     */
    private static final AtomicInteger GENERATED_ID = new AtomicInteger(1);

    /**
     * The refresh item in the action bar.
     */
    private MenuItem refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If no layout resource was defined throw an exception
        if (getLayoutResource() == 0) {
            throw new IllegalArgumentException();
        }
        setContentView(getLayoutResource());
        // TODO do this generic!!!!!!!!!!!!!!!!!!
        // add new view in display data -> save as local attribute and check if not null -> create new one


        // Get extras from intent and handle them
        if (getIntent().getExtras() != null) {
            handleArguments(getIntent().getExtras());
        }

        // Set action bar main icon as back menu item
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set the default data (like home icon, title, ...)
        setDefaultData();

        // Load the data to display them
        loadAndDisplayData();
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
                loadAndDisplayData();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Load and display the loaded data afterwards.
     */
    private void loadAndDisplayData() {
        // Start processing animation
        startProcessingAnimation();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // If loading the data was successful display them
                try {
                    loadData();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Display loaded data
                            displayData();

                            // End processing animation
                            stopProcessingAnimation();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // End processing animation
                            stopProcessingAnimation();
                            IM.INSTANCES.getMH().writeErrorMessage("Problems by creating the view: ", e);
                            IM.INSTANCES.getMH().showQuickMessage("Problems by creating the view!");
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Start the processing animation.
     */
    private void startProcessingAnimation() {
        // Prepare a progress animation
        if (refreshButton != null) {
            refreshButton.setActionView(R.layout.management_refresh_progress);
        }
    }

    /**
     * Stop the processing animation.
     */
    private void stopProcessingAnimation() {
        if (refreshButton != null) {
            refreshButton.setActionView(null);
        }
    }

    /**
     * Set the home icon by a drawable item.
     *
     * @param icon the drawable item
     */
    protected void setHomeIcon(Drawable icon) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (icon != null) {
                actionBar.setIcon(icon);
            }
        }
    }

    /**
     * Set the home logo by a resource id.
     *
     * @param logo the resource id
     */
    protected void setHomeLogo(int logo) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            if (logo != 0) {
                actionBar.setLogo(logo);
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
     * Return the drawable image for the given uri.
     *
     * @param uri the uri for the image
     * @return the drawable image for the given uri
     */
    protected Drawable getDrawableByUri(final String uri) {
        // TODO pruefen ob das so ok ist oder ob der thread benoetigt wird!!!!!!!
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        try {
            return new BitmapDrawable(getResources(), BitmapFactory.decodeStream(new URL(uri).openConnection().getInputStream()));
        } catch (MalformedURLException e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by creating an url from the given uri!", e);
        } catch (IOException e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by streaming the image (\"" + uri + "\")!", e);
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting an image by an uri!", e);
        }
        return null;
//            }
//        }).start();
    }

    /**
     * Prepare and return a drawable colored letter.
     *
     * @param displayName The name used to create the letter for the tile
     * @param key         The key used to generate the background color for the tile
     * @return the drawable colored letter
     */
    protected Drawable getDrawableLetter(String displayName, String key) {
        return new BitmapDrawable(getResources(), new LetterTileProvider(this).getLetterTile(displayName, key));
    }

    /**
     * Returns the android resource id of the specific online state.
     *
     * @param onlineState that needs the resource id
     * @return the id of the online state resource
     */
    protected int getOnlineStateResourceId(OnlineState onlineState) {
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
     * Set the default data (like home icon, title, ...).
     */
    protected abstract void setDefaultData();

    /**
     * Load the data for displaying.
     */
    protected abstract void loadData();

    /**
     * Display the loaded data.
     */
    protected abstract void displayData();
}
