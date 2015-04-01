package de.uni_stuttgart.riot.android.management;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.things.ThingManager;
import de.uni_stuttgart.riot.thing.Thing;

/**
 * Activity that displays all details of a thing.
 * It also provides to edit this information.
 *
 * @author Benny
 */
public class ThingDetailActivity extends ManagementDetailActivity<Thing> {

    private boolean monitoring;

    @Override
    protected void onStop() {
        super.onStop();
        stopMonitoring();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMonitoring();
        unbindAllManagementProperties();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startMonitoring();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.monitoring = false;
    }

    @Override
    protected void displayDetailData() {
        // If the refresh button was pressed firstly stop monitoring current change events
        stopMonitoring();
        unbindAllManagementProperties();

        // Update the home icon and title
        new AsyncTask<Void, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(Void... voids) {
                try {
                    return getDrawableLetter(item.getName(), String.valueOf(item.getId()));
                } catch (Exception e) {
                    IM.INSTANCES.getMH().writeErrorMessage("An error occurred during loading the data: ", e);
                    IM.INSTANCES.getMH().showQuickMessage("An error occurred during loading the data!");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);

                if (drawable != null) {
                    setHomeIcon(drawable);
                }
            }
        };
// ToDo DELTE!
//        new AsyncHelper<Drawable>() {
//
//            @Override
//            protected Drawable loadData() {
//                return getDrawableLetter(data.getName(), String.valueOf(data.getId()));
//            }
//
//            @Override
//            protected void processData(Drawable data) {
//                if (data != null) {
//                    setHomeIcon(data);
//                }
//            }
//        };
        setTitle(super.item.getName());

        // Prepare the items and save them in the right group
        prepareAndGroupItems(super.item.getProperties());

        // Add prepared and grouped items to the main layout
        addGroupedItemsToMainLayout();

        // Bind all properties and start monitoring the change events
        bindAllManagementProperties();
        startMonitoring();
    }

    @Override
    protected void setDefaultData() {
        setHomeLogo(android.R.drawable.ic_menu_gallery);
        setTitle(getString(R.string.default_thing_subject));
    }

    @Override
    protected void loadManagementData() {
        try {
            // Get the thing with the given id
            super.item = ThingManager.getInstance().getThing(this, super.itemId);
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
    }

    /**
     * Start monitoring to receive change events.
     */
    private void startMonitoring() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (item != null && !monitoring) {
                    ThingManager.getInstance().startMonitoring(item, this);
                    monitoring = true;
                }
            }
        }).start();
    }

    /**
     * Stop monitoring receiving change events.
     */
    private void stopMonitoring() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (item != null && monitoring) {
                    ThingManager.getInstance().stopMonitoring(item, this);
                    monitoring = false;
                }
            }
        }).start();
    }
}
