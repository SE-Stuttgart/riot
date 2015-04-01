package de.uni_stuttgart.riot.android.management;

import android.graphics.drawable.Drawable;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.managementproperty.ManagementProperty;
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

    @Override
    protected void onStop() {
        super.onStop();
        unbindAllThingProperties();
    }

    @Override
    protected void displayDetailData(final Thing data) {
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

        // Unbind all thing properties if they are bind
        unbindAllThingProperties();

        // Prepare the items and save them in the right group
        prepareAndGroupItems(data.getProperties());

        // Add prepared and grouped items to the main layout
        addGroupedItemsToMainLayout();

        // Bind all thing properties to constantly update the data
        bindAllThingProperties(data);
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
            return ThingManager.getInstance().getThing(this, super.itemId);
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
        return null;
    }

    /**
     * Bind all thing properties with their property listener.
     */
    private void bindAllThingProperties(final Thing data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (managementProperties != null && managementProperties.size() > 0) {
                    for (ManagementProperty managementProperty : managementProperties) {
                        managementProperty.bind();
                    }

                    // Start monitoring to receive change events
                    ThingManager.getInstance().startMonitoring(data, this);
                }
            }
        }).start();
    }

    /**
     * Unbind all thing properties with their property listener.
     */
    private void unbindAllThingProperties() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (managementProperties != null && managementProperties.size() > 0) {
                    // Stop monitoring for receiving change events
                    ThingManager.getInstance().stopMonitoring(this);

                    for (ManagementProperty managementProperty : managementProperties) {
                        managementProperty.unbind();
                    }
                    managementProperties.clear();
                }
            }
        }).start();
    }
}
