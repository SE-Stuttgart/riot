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
 * @param <T> type of the handled object
 * @author Benny
 */
public abstract class ManagementActivity<T> extends Activity {

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
            IM.INSTANCES.getMH().writeErrorMessage("Missing the management layout!");
            IM.INSTANCES.getMH().showQuickMessage("Missing the management layout!");
            throw new IllegalArgumentException();
        }
        setContentView(getLayoutResource());

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
        loadAndDisplayManagementData();
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
                loadAndDisplayManagementData();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Load and display the loaded data afterwards.
     */
    private void loadAndDisplayManagementData() {
        // Start processing animation
        startProcessingAnimation();

        new AsyncHelper<T>() {

            @Override
            protected T loadData() {
                return loadManagementData();
            }

            @Override
            protected void processData(T data) {
                if (data == null) {
                    IM.INSTANCES.getMH().writeErrorMessage("There are no data available!");
                    IM.INSTANCES.getMH().showQuickMessage("There are no data available!");
                    return;
                }
                displayManagementData(data);

                // End processing animation
                stopProcessingAnimation();
            }

            @Override
            protected void doAfterErrorOccurred() {
                // End processing animation
                stopProcessingAnimation();
            }
        };
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
        if (logo != 0) {
            setHomeIcon(getDrawableByResource(logo));
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
     * Return the drawable of the given resource id.
     *
     * @param id of the resource
     * @return the drawable item
     */
    protected Drawable getDrawableByResource(int id) {
        return getResources().getDrawable(id);
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
     *
     * @return the loaded data
     */
    protected abstract T loadManagementData();

    /**
     * Display the loaded data.
     *
     * @param data the loaded data
     */
    protected abstract void displayManagementData(T data);
}
