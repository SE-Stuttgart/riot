package de.uni_stuttgart.riot.android.management;

import android.graphics.drawable.Drawable;
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

    private Thing thing;
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
    protected void displayDetailData(final Thing data) {
        // If the refresh button was pressed firstly stop monitoring current change events
        stopMonitoring();
        unbindAllManagementProperties();

        // Update the home icon and title
        new AsyncHelper<Drawable>() {

            @Override
            protected Drawable loadData() {
                return getDrawableLetter(data.getName(), String.valueOf(data.getId()));
            }

            @Override
            protected void processData(Drawable data) {
                if (data != null) {
                    setHomeIcon(data);
                }
            }
        };
        setTitle(data.getName());

        // Prepare the items and save them in the right group
        prepareAndGroupItems(data.getProperties());

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
    protected Thing loadManagementData() {
        try {
            // Get the thing with the given id
            this.thing = ThingManager.getInstance().getThing(this, super.itemId);
            return this.thing;
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
        return null;
    }

    /**
     * Start monitoring to receive change events.
     */
    private void startMonitoring() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (thing != null && !monitoring) {
                    ThingManager.getInstance().startMonitoring(thing, this);
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
                if (thing != null && monitoring) {
                    ThingManager.getInstance().stopMonitoring(thing, this);
                    monitoring = false;
                }
            }
        }).start();
    }
}
