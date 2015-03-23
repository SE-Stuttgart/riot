package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collection;

import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.thingproperty.ThingProperty;

/**
 * This is an abstract activity for displaying more information about a list item from the ManagementListFragment.
 *
 * @author Benny
 */
public abstract class ManagementDetailFragment extends ManagementFragment {

    protected long itemId;
    protected String pageTitle;
    protected Collection<ThingProperty> thingProperties;

    @Override
    protected void onStop() {
        super.onStop();
        unbindAllThingProperties();
    }

    @Override
    protected void handleArguments(Bundle bundle) {
        this.itemId = 0;
        this.pageTitle = null;
        this.thingProperties = new ArrayList<ThingProperty>();

        if (bundle != null) {
            if (bundle.containsKey(BUNDLE_OBJECT_ID)) {
                this.itemId = bundle.getLong(BUNDLE_OBJECT_ID);
            }
            if (bundle.containsKey(BUNDLE_PAGE_TITLE)) {
                this.pageTitle = bundle.getString(BUNDLE_PAGE_TITLE);
            }
        }
    }

    @Override
    protected void displayData(Object data) {
        // TEST --->
       /*
        * EditText = String
        * EditNumber = Double
        * ToggleButton = Boolean
        * IntegralSlider = Integer
        * PercentageSlider = Double
        * FractionalSlider = Double
        * DropDown = Enum<?>
        */
//        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.thing_linear_layout);
//        mainLayout.addView(new ThingPropertyEditText("Example Text", getApplicationContext()).getUiElement());
//        mainLayout.addView(new ThingPropertyEditNumber(42, getApplicationContext()).getUiElement());
//        mainLayout.addView(new ThingPropertyToggleButton(true, getApplicationContext()).getUiElement());
//        mainLayout.addView(new ThingPropertyToggleButton(false, getApplicationContext()).getUiElement());
//        mainLayout.addView(new ThingPropertyIntegralSlider(25, getApplicationContext(), 0, 100).getUiElement());
//        mainLayout.addView(new ThingPropertyIntegralSlider(0, getApplicationContext(), -20, 20).getUiElement());
//        mainLayout.addView(new ThingPropertyPercentageSlider(0.5d, getApplicationContext()).getUiElement());
//        mainLayout.addView(new ThingPropertyFractionalSlider(0.5d, getApplicationContext(), 0.0d, 1.0d).getUiElement());
//        mainLayout.addView(new ThingPropertyFractionalSlider(3.0d, getApplicationContext(), 2.25d, 5.0d).getUiElement());
//        mainLayout.addView(new ThingPropertyFractionalSlider(0.05d, getApplicationContext(), -0.05d, 0.05d).getUiElement());
//        ArrayList<Enum<?>> possibleValues = new ArrayList<Enum<?>>();
//        Collections.addAll(possibleValues, OnlineState.values());
//        mainLayout.addView(new ThingPropertyEnumDropDown(OnlineState.STATUS_ONLINE, getApplicationContext(), possibleValues).getUiElement());
        // <--- TEST

        // Check if there is some data to display
        if (data == null) {
            // Show a message that there was something wrong (FIXME output message!!)
            IM.INSTANCES.getMH().writeErrorMessage("There are no data available!");
            return;
        }

        // Check if the data is instance of the needed class
        if (!isInstanceOf(data)) {
            // Show a message that there was something wrong (FIXME output message!!)
            IM.INSTANCES.getMH().writeErrorMessage("Data is not an instance of the right class!");
            return;
        }

        // Display the detailed data of the object
        displayDetailData(data);

        // Bind all thing properties to constantly update the data
        bindAllThingProperties();
    }

    @Override
    protected String getPageTitle() {
        if (this.pageTitle == null || this.pageTitle.isEmpty()) {
            // Use default page title if there is not a saved one yet
            return getDefaultPageTitle();
        }
        return this.pageTitle;
    }

    /**
     * Add a thing property to the list.
     *
     * @param thingProperty    the new created thing property
     * @param linearLayoutItem is the layout where the prepared ui element will be added
     */
    protected void addThingProperty(ThingProperty thingProperty, LinearLayout linearLayoutItem) {
        this.thingProperties.add(thingProperty);
        linearLayoutItem.addView(thingProperty.getView());
    }

    /**
     * Bind all thing properties with their property listener.
     */
    protected void bindAllThingProperties() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (ThingProperty thingProperty : thingProperties) {
                    thingProperty.bind();
                }
            }
        }).start();

    }

    /**
     * Unbind all thing properties with their property listener.
     */
    protected void unbindAllThingProperties() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (ThingProperty thingProperty : thingProperties) {
                    thingProperty.unbind();
                }
            }
        }).start();
    }

    /**
     * Return the default page title for this view.
     *
     * @return the default page title
     */
    protected abstract String getDefaultPageTitle();

    /**
     * Set values to the view elements.
     *
     * @param data includes the data that will be displayed
     */
    protected abstract void displayDetailData(Object data);
}
