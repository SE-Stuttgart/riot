package de.uni_stuttgart.riot.android.management;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.thingproperty.ThingProperty;
import de.uni_stuttgart.riot.android.things.ThingManager;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;

/**
 * Activity that displays all details of a thing.
 * It also provides to edit this information.
 *
 * @author Benny
 */
public class ThingDetailActivity extends ManagementDetailActivity {

    private Thing thing;

    @Override
    protected void onStop() {
        super.onStop();
        unbindAllThingProperties();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.management_detail;
    }

    @Override
    protected Drawable getHomeIcon() {
        return null; // ToDo
    }

    @Override
    protected int getHomeLogo() {
        return R.drawable.ic_drawer; // ToDo
    }

    @Override
    protected String getDefaultPageTitle() {
        return getString(R.string.thing_detail);
    }

    @Override
    protected boolean isInstanceOf(Object item) {
        return (item instanceof Thing);
    }

    @Override
    protected void displayDetailData(Object data) {
        // Check if there is the needed main linear layout
        if (findViewById(R.id.management_linear_layout) == null) {
            return;
        }

        // Unbind all thing properties if they are bind
        unbindAllThingProperties();

        // Clear all children
        ((LinearLayout) findViewById(R.id.management_linear_layout)).removeAllViews();

        // Add items to the main layout
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.management_linear_layout);
        for (Property<?> property : ((Thing) data).getProperties()) {
            View preparedItem = prepareItemBy(property);
            if (preparedItem != null) {
                mainLayout.addView(preparedItem);
            }
        }

        // Bind all thing properties to constantly update the data
        bindAllThingProperties();
    }

    @Override
    protected void getAndDisplayData() {
        try {
            // Get the thing with the given id
            this.thing = ThingManager.getInstance().getThing(this, this.itemId);

            // Display data
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Update the home logo or icon
                    updateHomeLogo();

                    // Display the data
                    displayData(thing);

                    // End processing animation
                    startProcessingAnimation(false);
                }
            });
        } catch (Exception e) {
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: ", e);
            IM.INSTANCES.getMH().showQuickMessage("Problems by getting data!");
        }
    }

    @Override
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
                if (thingProperties != null && thingProperties.size() > 0) {
                    for (ThingProperty thingProperty : thingProperties) {
                        thingProperty.bind();
                    }

                    // Start monitoring to receive change events
                    ThingManager.getInstance().startMonitoring(thing, this);
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
                if (thingProperties != null && thingProperties.size() > 0) {
                    // Stop monitoring for receiving change events
                    ThingManager.getInstance().stopMonitoring(this);

                    for (ThingProperty thingProperty : thingProperties) {
                        thingProperty.unbind();
                    }
                }
            }
        }).start();
    }

    /**
     * Prepare the property group.
     *
     * @param property the root element of the group
     * @return the prepared property group
     */
    private View preparePropertyGroup(Property<?> property) {
        // Save sub list
        ArrayList<Property> subList = null; //property.getSubList();
        // TODO Handle propertyType group!!

        // Create new layout element
        final RelativeLayout relativeLayoutGroup = new RelativeLayout(getApplicationContext());

        // Set an listener to the layout to order the items inside after they know their size
        relativeLayoutGroup.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // Sort the items in the group
                        orderGroupedItems(relativeLayoutGroup);

                        // Unregister the listener
                        relativeLayoutGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        // Get layout params
        RelativeLayout.LayoutParams relativeLayoutGroupParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        relativeLayoutGroup.setLayoutParams(relativeLayoutGroupParams);

        // Set linearLayoutGroup attributes
        relativeLayoutGroup.setBackground(getResources().getDrawable(R.drawable.group_border));
        int padding = (int) getResources().getDimension(R.dimen.group_padding);
        relativeLayoutGroup.setPadding(padding, padding, padding, padding);


        // Add sub items to group
        if (subList != null) {
            for (Property subItem : subList) {
                View preparedItem = prepareItemBy(subItem);

                // Add an id to the view
                preparedItem.setId(generateViewId());

                // Add item to the layout
                relativeLayoutGroup.addView(preparedItem);
            }
        }
        return relativeLayoutGroup;
    }

    /**
     * Order the items inside the group layout.
     *
     * @param relativeLayoutGroup the group
     */
    private void orderGroupedItems(RelativeLayout relativeLayoutGroup) {

        // Get available width and decrement with two times left and right padding of the main layout and two times of the group layout
        int availableWidth = relativeLayoutGroup.getMeasuredWidth(); // - (2 * (int) getDimension(R.dimen.fragment_main_margin));
        int usedWidth = 0;

        int idOfFirstElementInLastRow = 0;
        int idOfFirstElementInRow = 0;
        int idOfPreviousElement = 0;

        for (int i = 0; i < relativeLayoutGroup.getChildCount(); i++) {
            View preparedItem = relativeLayoutGroup.getChildAt(i);

            if (preparedItem != null) {
                // Set layout params
                RelativeLayout.LayoutParams relativeLayoutItemParams = (RelativeLayout.LayoutParams) preparedItem.getLayoutParams();
                preparedItem.setLayoutParams(relativeLayoutItemParams);

                // Check if the element would have enough space
                int itemWidth = preparedItem.getMeasuredWidth();
                if ((usedWidth + itemWidth) < availableWidth) {
                    if (idOfFirstElementInRow == 0) {
                        // This is the very first element that will be added
                        idOfFirstElementInRow = preparedItem.getId();
                        idOfPreviousElement = preparedItem.getId();
                    } else {
                        // All following elements in the same row
                        relativeLayoutItemParams.addRule(RelativeLayout.RIGHT_OF, idOfPreviousElement);
                        if (idOfFirstElementInLastRow != 0) {
                            relativeLayoutItemParams.addRule(RelativeLayout.BELOW, idOfFirstElementInLastRow);
                        }
                        idOfPreviousElement = preparedItem.getId();
                    }
                    usedWidth += itemWidth;
                } else {
                    if (idOfPreviousElement == 0) {
                        // This could be the first element if it is to large for one row
                        idOfFirstElementInRow = preparedItem.getId();
                        idOfPreviousElement = preparedItem.getId();
                    } else {
                        // All first elements in a row
                        relativeLayoutItemParams.addRule(RelativeLayout.BELOW, idOfFirstElementInRow);
                        idOfFirstElementInLastRow = idOfFirstElementInRow;
                        idOfFirstElementInRow = preparedItem.getId();
                        idOfPreviousElement = preparedItem.getId();
                    }
                    usedWidth = itemWidth;
                }
            }
        }
    }
}
