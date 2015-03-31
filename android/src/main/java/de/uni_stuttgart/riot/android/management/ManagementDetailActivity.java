package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.managementproperty.ManagementProperty;
import de.uni_stuttgart.riot.android.managementproperty.ManagementPropertyEditNumber;
import de.uni_stuttgart.riot.android.managementproperty.ManagementPropertyEditText;
import de.uni_stuttgart.riot.android.managementproperty.ManagementPropertyEnumDropDown;
import de.uni_stuttgart.riot.android.managementproperty.ManagementPropertyFractionalSlider;
import de.uni_stuttgart.riot.android.managementproperty.ManagementPropertyIntegralSlider;
import de.uni_stuttgart.riot.android.managementproperty.ManagementPropertyPercentageSlider;
import de.uni_stuttgart.riot.android.managementproperty.ManagementPropertyToggleButton;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * This is an abstract activity for displaying more information about a list item from the ManagementListFragment.
 *
 * @param <T> type of the handled object
 * @author Benny
 */
public abstract class ManagementDetailActivity<T> extends ManagementActivity {

    protected long itemId;
    protected T item;
    protected Collection<ManagementProperty> managementProperties;

    @Override
    protected void handleArguments(Bundle bundle) {
        this.itemId = 0;
        this.managementProperties = new ArrayList<ManagementProperty>();

        if (bundle != null) {
            if (bundle.containsKey(BUNDLE_OBJECT_ID)) {
                this.itemId = bundle.getLong(BUNDLE_OBJECT_ID);
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.management_detail;
    }

    @Override
    protected void displayData() {
        // Check if there is a item for displaying
        if (this.item == null) {
            IM.INSTANCES.getMH().writeErrorMessage("There are no data available!");
            IM.INSTANCES.getMH().showQuickMessage("There are no data available!");
            return;
        }

        // Check if the list view is available
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.management_linear_layout);
        if (mainLayout == null) {
            IM.INSTANCES.getMH().writeErrorMessage("No main layout was found!");
            IM.INSTANCES.getMH().showQuickMessage("No main layout was found!");
            return;
        }

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
//        mainLayout.addView(new ManagementPropertyEditText("Example Text", this).getUiElement());
//        mainLayout.addView(new ManagementPropertyEditNumber(42, this).getUiElement());
//        mainLayout.addView(new ManagementPropertyToggleButton(true, this).getUiElement());
//        mainLayout.addView(new ManagementPropertyToggleButton(false, this).getUiElement());
//        mainLayout.addView(new ManagementPropertyIntegralSlider(25, this, 0, 100).getUiElement());
//        mainLayout.addView(new ManagementPropertyIntegralSlider(0, this, -20, 20).getUiElement());
//        mainLayout.addView(new ManagementPropertyPercentageSlider(0.5d, this).getUiElement());
//        mainLayout.addView(new ManagementPropertyFractionalSlider(0.5d, this, 0.0d, 1.0d).getUiElement());
//        mainLayout.addView(new ManagementPropertyFractionalSlider(3.0d, this, 2.25d, 5.0d).getUiElement());
//        mainLayout.addView(new ManagementPropertyFractionalSlider(0.05d, this, -0.05d, 0.05d).getUiElement());
//        ArrayList<Enum<?>> possibleValues = new ArrayList<Enum<?>>();
//        Collections.addAll(possibleValues, OnlineState.values());
//        mainLayout.addView(new ManagementPropertyEnumDropDown(OnlineState.STATUS_ONLINE, this, possibleValues).getUiElement());
        // <--- TEST

        displayDetailData(mainLayout);
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
        LinearLayout linearLayoutItem = prepareItemName(itemName);
        ManagementProperty managementProperty = prepareItem(value, uiHint);

        if (managementProperty != null) {
            // Add prepared item
            linearLayoutItem.addView(managementProperty.getView());
        }

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
        LinearLayout linearLayoutItem = prepareItemName(property.getName());
        ManagementProperty managementProperty = preparePropertyItem(property);

        if (managementProperty != null) {
            // Add prepared item
            this.managementProperties.add(managementProperty);
            linearLayoutItem.addView(managementProperty.getView());
        }

        // Return prepared item
        return linearLayoutItem;
    }

    /**
     * Prepare the item group.
     *
     * @param groupName the name of the group
     * @param items     the items of the group
     * @return the prepared item group
     */
    protected View prepareGroup(String groupName, List<View> items) {

        LinearLayout linearLayout = prepareItemName(groupName);

        // Create new layout element
        final RelativeLayout relativeLayoutGroup = new RelativeLayout(getApplicationContext());

        // Add group layout to the prepared layout with the group name
        linearLayout.addView(relativeLayoutGroup);

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

        // Set linearLayoutGroup attributes
        relativeLayoutGroup.setBackground(getResources().getDrawable(R.drawable.group_border));
        int padding = (int) getResources().getDimension(R.dimen.group_padding);
        relativeLayoutGroup.setPadding(padding, padding, padding, padding);


        // Add sub items to group
        if (items != null) {
            for (View view : items) {
                //        // Change top padding of the first element in the group TODO add group property
//            ((ViewGroup) linearLayoutItem.getChildAt(1)).getChildAt(0).setPadding(0, (int) getDimension(R.dimen.fragment_margin_between_elements) / 2, 0, (int) getDimension(R.dimen.fragment_margin_between_elements) / 2);

                // Add an id to the view
                view.setId(generateViewId());

                // Add item to the layout
                relativeLayoutGroup.addView(view);
            }
        }

        return linearLayout;
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

    /**
     * Prepare an item - add a surrounding view with the name of the item.
     *
     * @param itemName is the name that will be displayed above the item
     * @return a layout with the name of the item and space for the item itself
     */
    private LinearLayout prepareItemName(String itemName) {
        // Create new layout element
        LinearLayout linearLayoutItem = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams linearLayoutItemParams;

        // Add name of the item
        if (itemName != null) {
            // Create a full line item
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
            //Get layout params - just wrapping the item
            linearLayoutItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Set a padding on the top and the bottom
            linearLayoutItem.setPadding(0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) / 2, 0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) / 2);
        }

        // Set layout params
        linearLayoutItem.setLayoutParams(linearLayoutItemParams);

        // Return prepared item
        return linearLayoutItem;
    }

    /**
     * Prepare the ui item.
     *
     * @param value  includes the value that will be displayed with this item
     * @param uiHint is needed for decide which ui element will be built
     */
    private ManagementProperty prepareItem(Object value, UIHint uiHint) {
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
        if (uiHint instanceof UIHint.EditText) {
            // Prepare and add edit text property
            return new ManagementPropertyEditText((String) value, this);
        } else if (uiHint instanceof UIHint.EditNumber) {
            // Prepare and add edit number property
            return new ManagementPropertyEditNumber((Number) value, this);
        } else if (uiHint instanceof UIHint.ToggleButton) {
            // Prepare and add toggle button property
            UIHint.ToggleButton uiHintToggleButton = (UIHint.ToggleButton) uiHint;
            return new ManagementPropertyToggleButton((Boolean) value, this, uiHintToggleButton.valueOff, uiHintToggleButton.valueOn);
        } else if (uiHint instanceof UIHint.FractionalSlider) {
            // Prepare and add scale value property
            UIHint.FractionalSlider uiHintFractionalSlider = (UIHint.FractionalSlider) uiHint;
            return new ManagementPropertyFractionalSlider((Double) value, this, uiHintFractionalSlider.min, uiHintFractionalSlider.max);
        } else if (uiHint instanceof UIHint.IntegralSlider) {
            // Prepare and add scale value property
            UIHint.IntegralSlider uiHintIntegralSlider = (UIHint.IntegralSlider) uiHint;
            return new ManagementPropertyIntegralSlider((Integer) value, this, (int) uiHintIntegralSlider.min, (int) uiHintIntegralSlider.max);
        } else if (uiHint instanceof UIHint.PercentageSlider) {
            // Prepare and add scale percent property
            return new ManagementPropertyPercentageSlider((Double) value, this);
        } else if (uiHint instanceof UIHint.EnumDropDown) {
            // Prepare and add scale percent property
            UIHint.EnumDropDown uiHintEnumDropDown = (UIHint.EnumDropDown) uiHint;
            ArrayList<Enum<?>> possibleValues = new ArrayList<Enum<?>>();
            Collections.addAll(possibleValues, uiHintEnumDropDown.possibleValues);
            return new ManagementPropertyEnumDropDown((Enum<?>) value, this, possibleValues);
        }
        IM.INSTANCES.getMH().writeErrorMessage("Unknown type in prepareItem()!");
        IM.INSTANCES.getMH().showQuickMessage("Unknown type in prepareItem()!");
        return null;
    }

    /**
     * Prepare the ui item by property.
     *
     * @param property includes the value
     */
    @SuppressWarnings("unchecked")
    private ManagementProperty preparePropertyItem(Property<?> property) {
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
        if (property.getUiHint() instanceof UIHint.EditText) {
            // Prepare and add edit text property
            return new ManagementPropertyEditText((Property<String>) property, this);
        } else if (property.getUiHint() instanceof UIHint.EditNumber) {
            // Prepare and add edit number property
            return new ManagementPropertyEditNumber((Property<Number>) property, this);
        } else if (property.getUiHint() instanceof UIHint.ToggleButton) {
            // Prepare and add toggle button property
            UIHint.ToggleButton uiHint = (UIHint.ToggleButton) property.getUiHint();
            // ToDo save this values at the server
            uiHint.valueOff = property.getName() + " " + getString(R.string.off);
            uiHint.valueOn = property.getName() + " " + getString(R.string.on);
            return new ManagementPropertyToggleButton((Property<Boolean>) property, this, uiHint.valueOff, uiHint.valueOn);
        } else if (property.getUiHint() instanceof UIHint.FractionalSlider) {
            // Prepare and add scale value property
            UIHint.FractionalSlider uiHint = (UIHint.FractionalSlider) property.getUiHint();
            return new ManagementPropertyFractionalSlider((Property<Double>) property, this, uiHint.min, uiHint.max);
        } else if (property.getUiHint() instanceof UIHint.IntegralSlider) {
            // Prepare and add scale value property
            UIHint.IntegralSlider uiHint = (UIHint.IntegralSlider) property.getUiHint();
            return new ManagementPropertyIntegralSlider((Property<Integer>) property, this, (int) uiHint.min, (int) uiHint.max);
        } else if (property.getUiHint() instanceof UIHint.PercentageSlider) {
            // Prepare and add scale percent property
            return new ManagementPropertyPercentageSlider((Property<Double>) property, this);
        } else if (property.getUiHint() instanceof UIHint.EnumDropDown) {
            // Prepare and add scale percent property
            UIHint.EnumDropDown uiHint = (UIHint.EnumDropDown) property.getUiHint();
            ArrayList<Enum<?>> possibleValues = new ArrayList<Enum<?>>();
            Collections.addAll(possibleValues, uiHint.possibleValues);
            return new ManagementPropertyEnumDropDown((Property<Enum<?>>) property, this, possibleValues);
        }
        IM.INSTANCES.getMH().writeErrorMessage("Unknown type in prepareItem(): " + property.getValueType().toString());
        IM.INSTANCES.getMH().showQuickMessage("Unknown type in prepareItem()!");
        return null;
    }

    /**
     * Prepare a text view for the item name.
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
     * Display the loaded data.
     *
     * @param mainLayout the layout that will be filled with the detailed data
     */
    protected abstract void displayDetailData(LinearLayout mainLayout);
}
