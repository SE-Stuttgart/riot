package de.uni_stuttgart.riot.android.management;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

<<<<<<< HEAD
import de.uni_stuttgart.riot.android.R;
=======
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.android.messages.IM;
<<<<<<< HEAD
>>>>>>> RIOT-87:Android:All changes of the last commits
import de.uni_stuttgart.riot.commons.rest.data.Storable;
=======
import de.uni_stuttgart.riot.thing.PropertyDescription;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingDescription;
>>>>>>> RIOT-87:Android:Get things from the server
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Fragment that displays all details of a thing.
 * It also provides to edit this information.
 *
 * @author Benny
 */
public class ManagementThingDetailFragment extends ManagementDetailFragment {

    // Is needed to set ids for grouped views
    private static final AtomicInteger GENERATED_ID = new AtomicInteger(1);
    private String viewTitle = "";

    private Thing theThing;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_thing_detail;
    }

    @Override
    protected String getTitle() {
        return viewTitle;
    }

    @Override
    protected boolean isInstanceOf(Object item) {
        return (item instanceof ThingDescription);
    }

    @Override
    protected void displayDetailData() {
        if (isInstanceOf(data)) {
            // Save the thing
            if (!doGetThing((ThingDescription) data)) {
                // Set a default title if it was not successful
                viewTitle = getResources().getString(R.string.thing_detail);
                return;
            }

            // Save the view title
            viewTitle = this.theThing.getName();

            // Add items to the main layout
            for (PropertyDescription propertyDescription : ((ThingDescription) data).getProperties()) {
                View preparedItem = prepareItem(propertyDescription);
                if (preparedItem != null) {
                    ((LinearLayout) view.findViewById(R.id.thing_linear_layout)).addView(preparedItem);
                }
            }

            // En-/Disable all layout items
            enableLayoutItems(this.enableElements); // ToDo maybe not all elements?
        }
    }

    @Override
    protected Object getData() {
        try {
            // Get the thing description with the given id
            for (ThingDescription thingDescription : RIOTApiClient.getInstance().getDeviceBehavior().getDescriptions()) {
                if (this.itemId == thingDescription.getThingId()) {
                    return thingDescription;
                }
            }
        } catch (Exception e) {
            // FIXME output message!!
            IM.INSTANCES.getMH().writeErrorMessage("Problems by getting data: " + e.getMessage());
        }

        // Load dummy objects if the above method was not successful
        return getThing(this.itemId);
    }

    @Override
    protected void setOnBackClick() {
        callOtherFragment(new ManagementThingListFragment());
    }

    @Override
    protected void callThisFragment(boolean edit) {
        Bundle args = new Bundle();
        args.putLong(ManagementFragment.BUNDLE_OBJECT_ID, this.itemId);
        args.putBoolean(BUNDLE_ENABLE_ELEMENTS, edit);
        callOtherFragment(new ManagementThingDetailFragment(), args);
    }

    @Override
    protected void saveChanges() {
        // TODO !!!
        IM.INSTANCES.getMH().showQuickMessage("Changes should be changed now :)");
    }

    /**
     * Tires to save the thing of this ThingDescription.
     *
     * @param thingDescription the description of the thing
     * @return true if it was successful - false if not
     */
    private boolean doGetThing(ThingDescription thingDescription) {
        try {
            this.theThing = RIOTApiClient.getInstance().getDeviceBehavior().getThingByDiscription(thingDescription);
            return true;
        } catch (Exception e) {
            // FIXME output message!!
            IM.INSTANCES.getMH().writeErrorMessage("Problems by saving thing: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update the changed property.
     *
     * @param propertyDescription is the unchanged property
     * @param value               is the new value
     */
    private void updateProperty(PropertyDescription propertyDescription, Object value) {
        // theThing.getProperty(propertyDescription.getName()).setValue(value);
        // TODO directly or just on saving??
        if (isInstanceOf(data)) {
            // Save changes local
//            ((ThingDescription) data).updateProperty(property, value);

            // Send changes directly to the server if it should be updated instant
//            if (property.isInstantUpdate()) {
//                // ToDo
//            }
        }
        // Note:
        // Aenderung eines properties ruft immer diese Methode auf.
        // Hier wird das property lokal geaendert.
        // Gibt es einen "instant-update" flag oder attribut oder aehnliches wird es direkt an den server geschickt.
        // Beim klick auf den save-button werden alle lokal geaenderten properties an den server geschickt.
        // (problem bei instant-update properties und dem abort-button)
    }


    /**
     * Generate a value suitable for use in View.setId(int).
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    private static int generateViewId() {
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
     * Prepare an item for the appropriate property type.
     *
     * @param propertyDescription for that item the view will be prepared
     * @return the prepared view
     */
    private View prepareItem(PropertyDescription propertyDescription) {
        // Create new layout element
        LinearLayout linearLayoutItem = new LinearLayout(view.getContext());
        LinearLayout.LayoutParams linearLayoutItemParams;

        // Add name of the propertyDescription
        if (!isPropertyType(propertyDescription, UIHint.ToggleButton.class)) {
//        if (!isPropertyType(propertyDescription, PropertyType.ToggleButton)) {
            View preparedTextView = prepareTextView(propertyDescription.getName());
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

        // Prepare the propertyDescription items
        preparePropertyItems(propertyDescription, linearLayoutItem);

        // Change top padding of the first element in the group
//        if (isPropertyType(propertyDescription, PropertyType.GROUP)) {
//            ((ViewGroup) linearLayoutItem.getChildAt(1)).getChildAt(0).setPadding(0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) / 2, 0, (int) getResources().getDimension(R.dimen.fragment_margin_between_elements) / 2);
//        }

        // Return prepared item
        return linearLayoutItem;
    }

    /**
     * Check if the propertyDescription is from this property type.
     *
     * @param propertyDescription the property that will be checked
     * @param propertyType        the property type that will be checked
     * @return
     */
    private boolean isPropertyType(PropertyDescription propertyDescription, Class<?> propertyType) {
        if (propertyDescription.getValueType().equals(propertyType)) {
            return true;
        }
        return false;
    }

    /**
     * Prepare the property items.
     *
     * @param propertyDescription includes the value
     * @param linearLayoutItem    the layout around the property
     */
    private void preparePropertyItems(PropertyDescription propertyDescription, LinearLayout linearLayoutItem) {
        // Split method into sub methods because it is to complex for maven
        if (preparePropertyItems1(propertyDescription, linearLayoutItem)) {
            return;
        }
        if (preparePropertyItems2(propertyDescription, linearLayoutItem)) {
            return;
        }

        // Show a quick message that this type is unknown (ToDo log this?)
        IM.INSTANCES.getMH().showQuickMessage("Unknown type in preparePropertyItems(): " + propertyDescription.getValueType().toString());
    }

    /**
     * Prepare the property items.
     *
     * @param propertyDescription includes the value
     * @param linearLayoutItem    the layout around the property
     * @return true if a item was prepared | false if no item was prepared
     */
    private boolean preparePropertyItems1(PropertyDescription propertyDescription, LinearLayout linearLayoutItem) {
        // If property is a group element get the sub list and prepare this items
//        if (isPropertyType(propertyDescription, PropertyType.GROUP)) {
//            // Prepare and add property group
//            View preparedView = preparePropertyGroup(propertyDescription);
//            if (preparedView != null) {
//                linearLayoutItem.addView(preparedView);
//            }
//        } else
        if (isPropertyType(propertyDescription, UIHint.EditText.class)) {
//        if (isPropertyType(propertyDescription, PropertyType.EditText)) {
            // Prepare and add edit text property
            View preparedView = prepareTextProperty(propertyDescription, false, false);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else if (isPropertyType(propertyDescription, UIHint.EditNumber.class)) {
//        } else if (isPropertyType(propertyDescription, PropertyType.EditNumber)) {
            // Prepare and add edit number property
            View preparedView = prepareTextProperty(propertyDescription, true, false);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else if (isPropertyType(propertyDescription, UIHint.ToggleButton.class)) {
//        } else if (isPropertyType(propertyDescription, PropertyType.ToggleButton)) {
            // Prepare and add toggle button property
            View preparedView = prepareToggleButtonProperty(propertyDescription);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Prepare the property items.
     *
     * @param propertyDescription includes the value
     * @param linearLayoutItem    the layout around the property
     * @return true if a item was prepared | false if no item was prepared
     */
    private boolean preparePropertyItems2(PropertyDescription propertyDescription, LinearLayout linearLayoutItem) {
        // If property is a group element get the sub list and prepare this items
        if (isPropertyType(propertyDescription, UIHint.FractionalSlider.class)) {
//        if (isPropertyType(propertyDescription, PropertyType.FractionalSlider)) {
            // Prepare and add scale value property
            UIHint.FractionalSlider uiHint = (UIHint.FractionalSlider) propertyDescription.getUiHint();
            View preparedView = prepareDoubleSliderProperty(propertyDescription, uiHint.min, uiHint.max, R.string.degreeCelsius);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else if (isPropertyType(propertyDescription, UIHint.IntegralSlider.class)) {
//        } else if (isPropertyType(propertyDescription, PropertyType.IntegralSlider)) {
            // Prepare and add scale value property
            UIHint.IntegralSlider uiHint = (UIHint.IntegralSlider) propertyDescription.getUiHint();
            // TODO TODO evtl als int speichern!!
            View preparedView = prepareLongSliderProperty(propertyDescription, uiHint.min, uiHint.max, R.string.degreeFahrenheit);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else if (isPropertyType(propertyDescription, UIHint.PercentageSlider.class)) {
//        } else if (isPropertyType(propertyDescription, PropertyType.PercentageSlider)) {
            // Prepare and add scale percent property
            final int min = 0;
            final int max = 100;
            View preparedView = prepareLongSliderProperty(propertyDescription, min, max, R.string.percent);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else if (isPropertyType(propertyDescription, UIHint.DropDown.class)) {
//        } else if (isPropertyType(propertyDescription, PropertyType.DropDown)) {
            // Prepare and add scale percent property
            UIHint.DropDown uiHint = (UIHint.DropDown) propertyDescription.getUiHint();
            ArrayList<Enum<?>> possibleValues = new ArrayList<Enum<?>>();
            for (Enum<?> singleEnum : uiHint.possibleValues) { // TODO TODO evtl als ArrayList speichern!!
                possibleValues.add(singleEnum);
            }
            View preparedView = prepareDropDownProperty(propertyDescription, possibleValues);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Prepare a text view for the property name.
     *
     * @param text is the text that will be displayed
     * @return the prepared view
     */
    private View prepareTextView(String text) {
        // Create new layout elements
        TextView textView = new TextView(view.getContext());

        // Get layout params
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        textView.setLayoutParams(textViewParams);

        // Set the current value
        if (text != null) {
            textView.setText(text);
        } else {
            // Set default values
            textView.setText(getResources().getText(R.string.default_item_title));
        }

        return textView;
    }

    /**
     * Prepare the property group.
     *
     * @param propertyDescription the root element of the group
     * @return the prepared property group
     */
    private View preparePropertyGroup(PropertyDescription propertyDescription) {
        // Save sub list
        ArrayList<PropertyDescription> subList = null; //propertyDescription.getSubList();
// TODO TODO Handle propertyType group!!

        // Create new layout element
        final RelativeLayout relativeLayoutGroup = new RelativeLayout(view.getContext());

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
            for (PropertyDescription subItem : subList) {
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
        int availableWidth = relativeLayoutGroup.getMeasuredWidth(); // - (2 * (int) getResources().getDimension(R.dimen.fragment_main_margin));
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
     * Prepare a text property field.
     *
     * @param propertyDescription  includes the value
     * @param onlyNumbers          is true if the input allows just numbers
     * @param allowNegativeNumbers is true if the input of negative numbers is allowed
     * @return the prepared view
     */
    private View prepareTextProperty(final PropertyDescription propertyDescription, boolean onlyNumbers, boolean allowNegativeNumbers) {
        // Create new layout elements
        final EditText editText = new EditText(view.getContext());

        // Get layout params
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        editText.setLayoutParams(editTextParams);

        // Set text edit attributes
        if (onlyNumbers) {
            if (allowNegativeNumbers) {
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateProperty(propertyDescription, editText.getText());
            }
        });

        // Set the current value
        Object value = getValue(propertyDescription);
        if (value != null && value instanceof String) {
            editText.setText((String) value);
        } else {
            // Set default values
            editText.setText("");
        }

        return editText;
    }

    /**
     * Prepare a toggle button property field.
     *
     * @param propertyDescription includes the value
     * @return the prepared view
     */
    private View prepareToggleButtonProperty(final PropertyDescription propertyDescription) {
        // Create new layout elements
        ToggleButton toggleButton = new ToggleButton(view.getContext());

        // Get layout params
        LinearLayout.LayoutParams toggleButtonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        toggleButton.setLayoutParams(toggleButtonParams);

        // Set toggleButton attributes
        toggleButton.setTextOff(String.format("%s %s", propertyDescription.getName(), getResources().getString(R.string.off)));
        toggleButton.setTextOn(String.format("%s %s", propertyDescription.getName(), getResources().getString(R.string.on)));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateProperty(propertyDescription, isChecked);
            }
        });

        // Set the current value
        Object value = getValue(propertyDescription);
        if (value != null && value instanceof Boolean) {
            toggleButton.setChecked((Boolean) value);
        } else {
            // Set default values
            toggleButton.setChecked(false);
        }

        return toggleButton;
    }

    /**
     * Prepare a slider property field with long values.
     *
     * @param propertyDescription includes the value
     * @param min                 is the minimum value that can be selected
     * @param max                 is the maximum value that can be selected
     * @param identifier          is the identifier behind the value (e.g. %, °C,...)
     * @return the prepared view
     */
    private View prepareLongSliderProperty(final PropertyDescription propertyDescription, long min, long max, int identifier) {
        // ToDo maybe a long value is too big for the slider?
        return prepareSliderProperty(propertyDescription, (int) min, (int) max, identifier, 1);

    }

    /**
     * Prepare a slider property field with double values.
     *
     * @param propertyDescription includes the value
     * @param min                 is the minimum value that can be selected
     * @param max                 is the maximum value that can be selected
     * @param identifier          is the identifier behind the value (e.g. %, °C,...)
     * @return the prepared view
     */
    private View prepareDoubleSliderProperty(final PropertyDescription propertyDescription, double min, double max, int identifier) {

        // Get number of decimal places
        final int ten = 10;
        int minFactor = 0;
        int maxFactor = 0;
        int factor;
        String decimalSeparator = "\\" + String.valueOf(DecimalFormatSymbols.getInstance().getDecimalSeparator());

        String[] minSplit = Double.toString(min).split(decimalSeparator);
        if (minSplit.length > 1) {
            minFactor = (int) Math.pow(ten, minSplit[1].length());
        }

        String[] maxSplit = Double.toString(max).split(decimalSeparator);
        if (maxSplit.length > 1) {
            maxFactor = (int) Math.pow(ten, maxSplit[1].length());
        }

        factor = Math.max(minFactor, maxFactor);

        return prepareSliderProperty(propertyDescription, (int) (min * factor), (int) (max * factor), identifier, factor);
    }

    /**
     * Prepare a slider property field.
     *
     * @param propertyDescription includes the value
     * @param min                 is the minimum value that can be selected
     * @param max                 is the maximum value that can be selected
     * @param identifier          is the identifier behind the value (e.g. %, °C,...)
     * @param factor              is the factor that is used for the output value
     * @return the prepared view
     */
    private View prepareSliderProperty(final PropertyDescription propertyDescription, final int min, final int max, final int identifier, int factor) {
        final float tTMPlowWeight = (float) 0.2;
        final float tTMPhighWeight = (float) 0.8;

        // Calculate the real max value for the seek bar
        final int realMax = max - min;

        // Create new layout elements
        LinearLayout linearLayoutRow = new LinearLayout(view.getContext());
        final SeekBar seekBar = new SeekBar(view.getContext());
        LinearLayout linearLayoutText = new LinearLayout(view.getContext());
        final EditText editText = new EditText(view.getContext());
        TextView textView = new TextView(view.getContext());

        // Link the layout elements
        linearLayoutText.addView(editText);
        linearLayoutText.addView(textView);
        linearLayoutRow.addView(seekBar);
        linearLayoutRow.addView(linearLayoutText);

        // Get layout params
        LinearLayout.LayoutParams linearLayoutRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams seekBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams linearLayoutTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // int editTextWidth = (int) getResources().getDimension(R.dimen.slider_edit_text_width); // ToDo use factor? for dynamic size
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        linearLayoutRow.setLayoutParams(linearLayoutRowParams);
        seekBar.setLayoutParams(seekBarParams);
        linearLayoutText.setLayoutParams(linearLayoutTextParams);
        editText.setLayoutParams(editTextParams);
        textView.setLayoutParams(textViewParams);

        // Set linearLayoutRow attributes
        linearLayoutRow.setOrientation(LinearLayout.HORIZONTAL);

        // Set seekBar attributes
        seekBarParams.weight = tTMPlowWeight;
        seekBarParams.gravity = Gravity.CENTER;

        seekBar.setMax(realMax);
        setChangeListenerTo(seekBar, editText, propertyDescription, min, factor);

        // Set linearLayoutText attributes
        linearLayoutTextParams.weight = tTMPhighWeight;
        linearLayoutTextParams.gravity = Gravity.CENTER;
        linearLayoutText.setOrientation(LinearLayout.HORIZONTAL);

        // Set editText attributes
        if (min < 0) {
            editText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        setActionListenerTo(editText, seekBar, propertyDescription, min, max, factor);

        // Set textView attributes
        textView.setText(identifier);

        // Set the current value
        setCurrentSliderValue(seekBar, editText, propertyDescription, min, factor);

        return linearLayoutRow;
    }

    /**
     * Set the start value of the slider.
     *
     * @param seekBar             is the element that will get the change listener
     * @param editText            is the text view behind the seek bar that includes the value
     * @param propertyDescription includes the value
     * @param factor              is the factor that is used for the output value
     */
    private void setCurrentSliderValue(SeekBar seekBar, EditText editText, PropertyDescription propertyDescription, int min, int factor) {
        // ToDo optimize this method
        Object value = getValue(propertyDescription);
        if (value != null && value instanceof Number) {
            double val = 0;
            // Check if value is '0'
            if (!value.equals(0)) {
                try {
                    if (value instanceof Integer) {
                        val = ((Integer) value).doubleValue();
                    } else if (value instanceof Long) {
                        val = (Long) value;
                    } else if (value instanceof Float) {
                        val = (Float) value;
                    } else if (value instanceof Double) {
                        val = (Double) value;
                    } else {
                        // Show a quick message that there was an error
                        IM.INSTANCES.getMH().showQuickMessage("Unknown number type in setCurrentSliderValue()!");
                    }
                } catch (ClassCastException e) {
                    // Show a quick message that there was an error (ToDo log this?)
                    IM.INSTANCES.getMH().showQuickMessage("ERROR while casting: " + e.getMessage());
                }
            }
            seekBar.setProgress((int) (val * factor) - min);
            setSliderEditText(editText, val, factor);
        } else {
            // Set default values
            seekBar.setProgress(min);
            setSliderEditText(editText, (double) (min / factor), factor);
        }
    }

    /**
     * Set the text of the view next to the slider.
     *
     * @param editText is the text view behind the seek bar that includes the value
     * @param value    is the value for that text view
     * @param factor   is the factor that is used for the output value
     */
    private void setSliderEditText(EditText editText, double value, int factor) {
        if (factor == 1) {
            editText.setText(String.valueOf((int) value));
        } else {
            editText.setText(String.valueOf(value));
        }
    }

    /**
     * Set the action listener to the edit view.
     *
     * @param editText            is the text view behind the seek bar that includes the value
     * @param seekBar             is the element that will get the change listener
     * @param propertyDescription is the unchanged property
     * @param min                 is the minimum value that can be selected
     * @param max                 is the maximum value that can be selected
     * @param factor              is the factor that is used for the output value
     */
    private void setActionListenerTo(final EditText editText, final SeekBar seekBar, final PropertyDescription propertyDescription, final int min, final int max, final int factor) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    int value = (int) (Double.valueOf(String.valueOf(editText.getText())) * factor);
                    if (value > max || value < min) {
                        // Show a quick message that there was an error
                        IM.INSTANCES.getMH().showQuickMessage("Value is not in valuation!");

                        // Reset displayed text
                        setSliderEditText(editText, (double) (seekBar.getProgress() + min) / factor, factor);
                    } else {
                        seekBar.setProgress(value - min);
                        updateProperty(propertyDescription, (double) value / factor);
                    }
                } catch (Exception e) {
                    // Show a quick message that there was an error (ToDo log this?)
                    IM.INSTANCES.getMH().showQuickMessage("Value is not a number!");
                }
                return false;
            }
        });
    }

    /**
     * Set the change listener to the seek bar.
     *
     * @param seekBar             is the element that will get the change listener
     * @param editText            is the text view behind the seek bar that includes the value
     * @param propertyDescription is the unchanged property
     * @param min                 is the minimum value that can be selected
     * @param factor              is the factor that is used for the output value
     */
    private void setChangeListenerTo(SeekBar seekBar, final EditText editText, final PropertyDescription propertyDescription, final int min, final int factor) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setSliderEditText(editText, (double) (progress + min) / factor, factor);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateProperty(propertyDescription, (double) (seekBar.getProgress() + min) / factor);
            }
        });
    }

    /**
     * En-/disables all items on this detail layout.
     *
     * @param value is an boolean value to en-/disable this items
     */
    private void enableLayoutItems(boolean value) {
        enableChildItems((ViewGroup) view.findViewById(R.id.thing_linear_layout), value);
    }

    /**
     * Prepare a drop down field.
     *
     * @param propertyDescription includes the value
     * @param enums
     * @return the prepared view
     */
    private View prepareDropDownProperty(final PropertyDescription propertyDescription, ArrayList<Enum<?>> enums) {
        // Create new layout elements
        final Spinner spinner = new Spinner(view.getContext());

        // Get layout params
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        spinner.setLayoutParams(spinnerParams);

        // Convert the enum array list to a string array
        int enumLength = enums.size();
        String[] itemsAsArray = new String[enumLength];
        for (int i = 0; i < enumLength; i++) {
            itemsAsArray[i] = enums.get(i).toString();
        }

        // Set spinner attributes
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, itemsAsArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateProperty(propertyDescription, spinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set the current value
        Object value = getValue(propertyDescription);
        if (value != null && value instanceof Enum<?>) {
            int index = enums.indexOf(value);
            if (index != -1) {
                spinner.setSelection(index);
            }
        }

        return spinner;
    }

    /**
     * Return the value of this property.
     *
     * @param propertyDescription the description of this property
     * @return the value of this property
     */
    private Object getValue(PropertyDescription propertyDescription) {
        return theThing.getProperty(propertyDescription.getName()).getValue();
    }

    /**
     * En-/disables all child items of the given view.
     *
     * @param view  the item that sub items should be disabled
     * @param value is an boolean value to en-/disable this items
     */
    private void enableChildItems(ViewGroup view, boolean value) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View subView = view.getChildAt(i);
            if (subView instanceof ViewGroup) {
                enableChildItems((ViewGroup) subView, value);
            }
            subView.setEnabled(value);
        }
    }
}
