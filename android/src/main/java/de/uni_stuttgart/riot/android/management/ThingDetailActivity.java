package de.uni_stuttgart.riot.android.management;

import android.view.View;
import android.widget.LinearLayout;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.managementproperty.ManagementProperty;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.things.ThingManager;
import de.uni_stuttgart.riot.thing.Property;
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
    protected void displayDetailData(LinearLayout mainLayout) {
        // Unbind all thing properties if they are bind
        unbindAllThingProperties();

        // Clear all children
        mainLayout.removeAllViews();

        // Add items to the main layout
        for (Property<?> property : super.item.getProperties()) {
            View preparedItem = prepareItemBy(property);
            if (preparedItem != null) {
                mainLayout.addView(preparedItem);
            }
        }

        // Update the home icon and title
        setHomeIcon(getDrawableLetter(super.item.getName(), String.valueOf(super.item.getId())));
        setTitle(super.item.getName());

        // Bind all thing properties to constantly update the data
        bindAllThingProperties();
    }

    @Override
    protected void setDefaultData() {
        setHomeLogo(android.R.drawable.ic_menu_gallery);
        setTitle(getString(R.string.default_thing_subject));
    }

    @Override
    protected void loadData() {
        try {
            // Get the thing with the given id
            super.item = ThingManager.getInstance().getThing(this, super.itemId);
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
    }

    /**
     * Bind all thing properties with their property listener.
     */
    private void bindAllThingProperties() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (managementProperties != null && managementProperties.size() > 0) {
                    for (ManagementProperty managementProperty : managementProperties) {
                        managementProperty.bind();
                    }

                    // Start monitoring to receive change events
                    ThingManager.getInstance().startMonitoring(item, this);
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
                }
            }
        }).start();
    }
}
