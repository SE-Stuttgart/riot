package de.uni_stuttgart.riot.android.management;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyEditNumber;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyEditText;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyEnumDropDown;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyFractionalSlider;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyIntegralSlider;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyPercentageSlider;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyToggleButton;
import de.uni_stuttgart.riot.android.things.ThingManager;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Activity that displays all details of a thing.
 * It also provides to edit this information.
 *
 * @author Benny
 */
public class ManagementThingDetailFragment extends ManagementDetailFragment {

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
        if (findViewById(R.id.thing_linear_layout) == null) {
            return;
        }

        // Clear all children
        ((LinearLayout) findViewById(R.id.thing_linear_layout)).removeAllViews();

        // Add items to the main layout
        for (Property<?> property : ((Thing) data).getProperties()) {
            View preparedItem = prepareItem(property);
            if (preparedItem != null) {
                ((LinearLayout) findViewById(R.id.thing_linear_layout)).addView(preparedItem);
            }
        }
    }

    @Override
    protected Object getData() {
        try {
            // Get the thing with the given id
            return ThingManager.getInstance().getThing(this, this.itemId);
        } catch (Exception e) {
            // FIXME output message!!
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: " + e.getMessage());
        }
        return null;
    }

    /**
     * Prepare an item for the appropriate property type.
     *
     * @param property for that item the view will be prepared
     * @return the prepared view
     */
    private View prepareItem(Property<?> property) {
        // Create new layout element
        LinearLayout linearLayoutItem = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams linearLayoutItemParams;

        // Add name of the property
        if (!(property.getUiHint() instanceof UIHint.ToggleButton)) {
            View preparedTextView = prepareTextView(property.getName());
            if (preparedTextView != null) {
                linearLayoutItem.addView(preparedTextView);
            }
            //Get layout params
            linearLayoutItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Set linearLayoutItem attributes
            linearLayoutItem.setOrientation(LinearLayout.VERTICAL);

            // Set a padding on the top and the bottom
            linearLayoutItem.setPadding(0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) * 3, 0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) / 2);
        } else {
            //Get layout params
            linearLayoutItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Set a padding on the top and the bottom
            linearLayoutItem.setPadding(0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) / 2, 0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) / 2);
        }

        // Set layout params
        linearLayoutItem.setLayoutParams(linearLayoutItemParams);

        // Prepare the property items
        preparePropertyItems(property, linearLayoutItem);

//        // Change top padding of the first element in the group TODO add group property
//        if (isPropertyType(property, PropertyType.GROUP)) {
//            ((ViewGroup) linearLayoutItem.getChildAt(1)).getChildAt(0).setPadding(0, (int) getDimension(R.dimen.fragment_margin_between_elements) / 2, 0, (int) getDimension(R.dimen.fragment_margin_between_elements) / 2);
//        }

        // Return prepared item
        return linearLayoutItem;
    }

    /**
     * Prepare the property items.
     *
     * @param property         includes the value
     * @param linearLayoutItem the layout around the property
     */
    @SuppressWarnings("unchecked")
    private void preparePropertyItems(Property<?> property, LinearLayout linearLayoutItem) {
        /**
         * Note for UIHint - value types:
         *
         * EditText = String
         * EditNumber = Double
         * ToggleButton = Boolean
         * IntegralSlider = Integer
         * PercentageSlider = Double
         * FractionalSlider = Double
         * DropDown = Enum<?>
         **/

//        // If property is a group element get the sub list and prepare this items TODO add group property
//        if (property.getUiHint() instanceof UIHint.GROUP)) {
//            // Prepare and add property group
//            View preparedView = preparePropertyGroup(property);
//            if (preparedView != null) {
//                linearLayoutItem.addView(preparedView);
//            }
//        } else
        if (property.getUiHint() instanceof UIHint.EditText) {
            // Prepare and add edit text property
            addThingProperty(new ThingPropertyEditText((Property<String>) property, getApplicationContext()), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.EditNumber) {
            // Prepare and add edit number property
            addThingProperty(new ThingPropertyEditNumber((Property<Number>) property, getApplicationContext()), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.ToggleButton) {
            // Prepare and add toggle button property
            addThingProperty(new ThingPropertyToggleButton((Property<Boolean>) property, getApplicationContext()), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.FractionalSlider) {
            // Prepare and add scale value property
            UIHint.FractionalSlider uiHint = (UIHint.FractionalSlider) property.getUiHint();
            addThingProperty(new ThingPropertyFractionalSlider((Property<Double>) property, getApplicationContext(), (float) uiHint.min, (float) uiHint.max), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.IntegralSlider) {
            // Prepare and add scale value property
            UIHint.IntegralSlider uiHint = (UIHint.IntegralSlider) property.getUiHint();
            addThingProperty(new ThingPropertyIntegralSlider((Property<Integer>) property, getApplicationContext(), (int) uiHint.min, (int) uiHint.max), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.PercentageSlider) {
            // Prepare and add scale percent property
            addThingProperty(new ThingPropertyPercentageSlider((Property<Double>) property, getApplicationContext()), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.EnumDropDown) {
            // Prepare and add scale percent property
            UIHint.EnumDropDown uiHint = (UIHint.EnumDropDown) property.getUiHint();
            ArrayList<Enum<?>> possibleValues = new ArrayList<Enum<?>>();
            Collections.addAll(possibleValues, uiHint.possibleValues);
            addThingProperty(new ThingPropertyEnumDropDown((Property<Enum<?>>) property, getApplicationContext(), possibleValues), linearLayoutItem);
        } else {
            // Show a quick message that this type is unknown (ToDo log this?)
            IM.INSTANCES.getMH().showQuickMessage("Unknown type in preparePropertyItems(): " + property.getValueType().toString());
        }
    }

    /**
     * Prepare a text view for the property name.
     *
     * @param text is the text that will be displayed
     * @return the prepared view
     */
    private View prepareTextView(String text) {
        // Create new layout elements
        TextView textView = new TextView(getApplicationContext());

        // Get layout params
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        textView.setLayoutParams(textViewParams);

        // Set the current value
        if (text != null) {
            textView.setText(text);
        } else {
            // Set default values
            textView.setText(getText(R.string.default_item_title));
        }

        return textView;
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
                View preparedItem = prepareItem(subItem);

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
