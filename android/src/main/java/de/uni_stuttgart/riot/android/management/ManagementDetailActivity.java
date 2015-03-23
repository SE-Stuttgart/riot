package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.android.thingproperty.ThingProperty;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyEditNumber;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyEditText;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyEnumDropDown;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyFractionalSlider;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyIntegralSlider;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyPercentageSlider;
import de.uni_stuttgart.riot.android.thingproperty.ThingPropertyToggleButton;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * This is an abstract activity for displaying more information about a list item from the ManagementListFragment.
 *
 * @author Benny
 */
public abstract class ManagementDetailActivity extends ManagementActivity {

    protected long itemId;
    protected String pageTitle;
    protected Collection<ThingProperty> thingProperties;

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
//        mainLayout.addView(new ThingPropertyEditText("Example Text", this).getUiElement());
//        mainLayout.addView(new ThingPropertyEditNumber(42, this).getUiElement());
//        mainLayout.addView(new ThingPropertyToggleButton(true, this).getUiElement());
//        mainLayout.addView(new ThingPropertyToggleButton(false, this).getUiElement());
//        mainLayout.addView(new ThingPropertyIntegralSlider(25, this, 0, 100).getUiElement());
//        mainLayout.addView(new ThingPropertyIntegralSlider(0, this, -20, 20).getUiElement());
//        mainLayout.addView(new ThingPropertyPercentageSlider(0.5d, this).getUiElement());
//        mainLayout.addView(new ThingPropertyFractionalSlider(0.5d, this, 0.0d, 1.0d).getUiElement());
//        mainLayout.addView(new ThingPropertyFractionalSlider(3.0d, this, 2.25d, 5.0d).getUiElement());
//        mainLayout.addView(new ThingPropertyFractionalSlider(0.05d, this, -0.05d, 0.05d).getUiElement());
//        ArrayList<Enum<?>> possibleValues = new ArrayList<Enum<?>>();
//        Collections.addAll(possibleValues, OnlineState.values());
//        mainLayout.addView(new ThingPropertyEnumDropDown(OnlineState.STATUS_ONLINE, this, possibleValues).getUiElement());
        // <--- TEST

        // Check if there is some data to display
        if (data == null) {
            IM.INSTANCES.getMH().writeErrorMessage("There are no data available!");
            IM.INSTANCES.getMH().showQuickMessage("There are no data available!");
            return;
        }

        // Check if the data is instance of the needed class
        if (!isInstanceOf(data)) {
            IM.INSTANCES.getMH().writeErrorMessage("Data is not an instance of the right class!");
            IM.INSTANCES.getMH().showQuickMessage("Data is not an instance of the right class!");
            return;
        }

        // Display the detailed data of the object
        displayDetailData(data);
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
     * Prepare an item - add a surrounding view with the name of the item.
     *
     * @param itemName is the name that will be displayed above the item
     * @param uiHint   is needed for decide which ui element will be built
     * @return the prepared view
     */
    private LinearLayout prepareItem(String itemName, UIHint uiHint) {
        // Create new layout element
        LinearLayout linearLayoutItem = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams linearLayoutItemParams;

        // Add name of the property
        if (!(uiHint instanceof UIHint.ToggleButton)) {
            View preparedTextView = prepareTextView(itemName);
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

//        // Change top padding of the first element in the group TODO add group property
//        if (isPropertyType(property, PropertyType.GROUP)) {
//            ((ViewGroup) linearLayoutItem.getChildAt(1)).getChildAt(0).setPadding(0, (int) getDimension(R.dimen.fragment_margin_between_elements) / 2, 0, (int) getDimension(R.dimen.fragment_margin_between_elements) / 2);
//        }

        // Return prepared item
        return linearLayoutItem;
    }

    /**
     * Prepare an item by name and ui hint.
     *
     * @param itemName is the name that will be displayed above the item
     * @param value    includes the value that will be displayed with this item
     * @param uiHint   is needed for decide which ui element will be built
     * @return the prepared view
     */
    protected View prepareItemBy(String itemName, Object value, UIHint uiHint) {
        // Prepare the item
        LinearLayout linearLayoutItem = prepareItem(itemName, uiHint);
        preparePropertyItems(value, uiHint, linearLayoutItem);

        // Return prepared item
        return linearLayoutItem;
    }

    /**
     * Prepare an item for the appropriate property type.
     *
     * @param property for that item the view will be prepared
     * @return the prepared view
     */
    protected View prepareItemBy(Property<?> property) {
        // Prepare the property item
        LinearLayout linearLayoutItem = prepareItem(property.getName(), property.getUiHint());
        preparePropertyItems(property, linearLayoutItem);

        // Return prepared item
        return linearLayoutItem;
    }

    /**
     * Prepare the property items.
     *
     * @param value            includes the value that will be displayed with this item
     * @param uiHint           is needed for decide which ui element will be built
     * @param linearLayoutItem the layout around the item
     */
    private void preparePropertyItems(Object value, UIHint uiHint, LinearLayout linearLayoutItem) {
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
        if (uiHint instanceof UIHint.EditText) {
            // Prepare and add edit text property
            linearLayoutItem.addView(new ThingPropertyEditText((String) value, this).getUiElement());
        } else if (uiHint instanceof UIHint.EditNumber) {
            // Prepare and add edit number property
            linearLayoutItem.addView(new ThingPropertyEditNumber((Number) value, this).getUiElement());
        } else if (uiHint instanceof UIHint.ToggleButton) {
            // Prepare and add toggle button property
            UIHint.ToggleButton uiHintToggleButton = (UIHint.ToggleButton) uiHint;
            linearLayoutItem.addView(new ThingPropertyToggleButton((Boolean) value, this, uiHintToggleButton.valueOff, uiHintToggleButton.valueOn).getUiElement());
        } else if (uiHint instanceof UIHint.FractionalSlider) {
            // Prepare and add scale value property
            UIHint.FractionalSlider uiHintFractionalSlider = (UIHint.FractionalSlider) uiHint;
            linearLayoutItem.addView(new ThingPropertyFractionalSlider((Double) value, this, uiHintFractionalSlider.min, uiHintFractionalSlider.max).getUiElement());
        } else if (uiHint instanceof UIHint.IntegralSlider) {
            // Prepare and add scale value property
            UIHint.IntegralSlider uiHintIntegralSlider = (UIHint.IntegralSlider) uiHint;
            linearLayoutItem.addView(new ThingPropertyIntegralSlider((Integer) value, this, (int) uiHintIntegralSlider.min, (int) uiHintIntegralSlider.max).getUiElement());
        } else if (uiHint instanceof UIHint.PercentageSlider) {
            // Prepare and add scale percent property
            linearLayoutItem.addView(new ThingPropertyPercentageSlider((Double) value, this).getUiElement());
        } else if (uiHint instanceof UIHint.EnumDropDown) {
            // Prepare and add scale percent property
            UIHint.EnumDropDown uiHintEnumDropDown = (UIHint.EnumDropDown) uiHint;
            ArrayList<Enum<?>> possibleValues = new ArrayList<Enum<?>>();
            Collections.addAll(possibleValues, uiHintEnumDropDown.possibleValues);
            linearLayoutItem.addView(new ThingPropertyEnumDropDown((Enum<?>) value, this, possibleValues).getUiElement());
        } else {
            IM.INSTANCES.getMH().writeErrorMessage("Unknown type in preparePropertyItems()!");
            IM.INSTANCES.getMH().showQuickMessage("Unknown type in preparePropertyItems()!");
        }
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
            addThingProperty(new ThingPropertyEditText((Property<String>) property, this), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.EditNumber) {
            // Prepare and add edit number property
            addThingProperty(new ThingPropertyEditNumber((Property<Number>) property, this), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.ToggleButton) {
            // Prepare and add toggle button property
            UIHint.ToggleButton uiHint = (UIHint.ToggleButton) property.getUiHint();
            // ToDo save this values at the server
            uiHint.valueOff = property.getName() + " " + getString(R.string.off);
            uiHint.valueOn = property.getName() + " " + getString(R.string.on);
            addThingProperty(new ThingPropertyToggleButton((Property<Boolean>) property, this, uiHint.valueOff, uiHint.valueOn), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.FractionalSlider) {
            // Prepare and add scale value property
            UIHint.FractionalSlider uiHint = (UIHint.FractionalSlider) property.getUiHint();
            addThingProperty(new ThingPropertyFractionalSlider((Property<Double>) property, this, uiHint.min, uiHint.max), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.IntegralSlider) {
            // Prepare and add scale value property
            UIHint.IntegralSlider uiHint = (UIHint.IntegralSlider) property.getUiHint();
            addThingProperty(new ThingPropertyIntegralSlider((Property<Integer>) property, this, (int) uiHint.min, (int) uiHint.max), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.PercentageSlider) {
            // Prepare and add scale percent property
            addThingProperty(new ThingPropertyPercentageSlider((Property<Double>) property, this), linearLayoutItem);
        } else if (property.getUiHint() instanceof UIHint.EnumDropDown) {
            // Prepare and add scale percent property
            UIHint.EnumDropDown uiHint = (UIHint.EnumDropDown) property.getUiHint();
            ArrayList<Enum<?>> possibleValues = new ArrayList<Enum<?>>();
            Collections.addAll(possibleValues, uiHint.possibleValues);
            addThingProperty(new ThingPropertyEnumDropDown((Property<Enum<?>>) property, this, possibleValues), linearLayoutItem);
        } else {
            IM.INSTANCES.getMH().writeErrorMessage("Unknown type in preparePropertyItems(): " + property.getValueType().toString());
            IM.INSTANCES.getMH().showQuickMessage("Unknown type in preparePropertyItems()!");
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

    /**
     * Add a thing property to the list.
     *
     * @param thingProperty    the new created thing property
     * @param linearLayoutItem is the layout where the prepared ui element will be added
     */
    protected abstract void addThingProperty(ThingProperty thingProperty, LinearLayout linearLayoutItem);
}
