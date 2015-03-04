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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

<<<<<<< HEAD
import de.uni_stuttgart.riot.android.R;
=======
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.messages.IM;
>>>>>>> RIOT-87:Android:All changes of the last commits
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Fragment that displays all details of a thing.
 * It also provides to edit this information.
 *
 * @author Benny
 */
public class ManagementThingDetailFragment extends ManagementDetailFragment {

    // Is needed to set ids for grouped views
    private static final AtomicInteger GENERATED_ID = new AtomicInteger(1);

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_thing_detail;
    }

    @Override
    protected int getTitleId() {
        return R.string.thing_detail;
    }

    @Override
    protected boolean isInstanceOf(Storable item) {
        return (item instanceof DummyThing);
    }

    @Override
    protected void displayDetailData() {
        if (isInstanceOf(data)) {
            // Add items to the main layout
            for (DummyProperty property : ((DummyThing) data).getDummyProperties()) {
                View preparedItem = prepareItem(property);
                if (preparedItem != null) {
                    ((LinearLayout) view.findViewById(R.id.thing_linear_layout)).addView(preparedItem);
                }
            }

            // En-/Disable all layout items
            enableLayoutItems(this.enableElements); // ToDo maybe not all elements?
        }
    }

    @Override
    protected Storable getData() {
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
     * Update the changed property.
     *
     * @param property is the unchanged property
     * @param value    is the new value
     */
    private void updateProperty(DummyProperty property, Object value) {
        // TODO directly or just on saving??
        if (isInstanceOf(data)) {
            // Save changes local
            ((DummyThing) data).updateProperty(property, value);

            // Send changes directly to the server if it should be updated instant
            if (property.isInstantUpdate()) {
                // ToDo
            }
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
     * @param property for that item the view will be prepared
     * @return the prepared view
     */
    private View prepareItem(DummyProperty property) {
        // Create new layout element
        LinearLayout linearLayoutItem = new LinearLayout(view.getContext());
        LinearLayout.LayoutParams linearLayoutItemParams;

        // Add name of the property
        if (!property.getPropertyType().equals(PropertyType.TOGGLE_BUTTON)) {
            View preparedTextView = prepareTextView(property.getName());
            if (preparedTextView != null) {
                linearLayoutItem.addView(preparedTextView);
            }
            //Get layout params
            linearLayoutItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            // Set linearLayoutItem attributes
            linearLayoutItemParams.topMargin = (int) getResources().getDimension(R.dimen.fragment_margin_between_elements);
            linearLayoutItemParams.bottomMargin = (int) getResources().getDimension(R.dimen.fragment_margin_between_elements);
            linearLayoutItem.setOrientation(LinearLayout.VERTICAL);
        } else {
            //Get layout params
            linearLayoutItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        // Set layout params
        linearLayoutItem.setLayoutParams(linearLayoutItemParams);

        // Prepare the property items
        prepareProprtyItems(property, linearLayoutItem);

        // Return prepared item
        return linearLayoutItem;
    }

    /**
     * Prepare the property items.
     *
     * @param property         includes the value (and ToDo more?)
     * @param linearLayoutItem the layout around the property
     */
    private void prepareProprtyItems(DummyProperty property, LinearLayout linearLayoutItem) {
        final int tTMPmin = -20;
        final int tTMPmax = 10;
        final int tTMPmin2 = 0;
        final int tTMPmax2 = 100;

        // If property is a group element get the sub list and prepare this items
        if (property.getPropertyType().equals(PropertyType.PROPERTY_GROUP)) {
            // Prepare and add property group
            View preparedView = preparePropertyGroup(property);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else if (property.getPropertyType().equals(PropertyType.EDIT_TEXT)) {
            // Prepare and add edit text property
            View preparedView = prepareTextProperty(property, false, false);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else if (property.getPropertyType().equals(PropertyType.EDIT_NUMBER)) {
            // Prepare and add edit number property
            View preparedView = prepareTextProperty(property, true, false);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else if (property.getPropertyType().equals(PropertyType.TOGGLE_BUTTON)) {
            // Prepare and add toggle button property
            View preparedView = prepareToggleButtonProperty(property);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else if (property.getPropertyType().equals(PropertyType.SCALE_VALUE)) {
            // Prepare and add scale value property
            View preparedView = prepareScaleProperty(property, tTMPmin, tTMPmax, R.string.degreeCelsius);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else if (property.getPropertyType().equals(PropertyType.SCALE_PERCENT)) {
            // Prepare and add scale percent property
            View preparedView = prepareScaleProperty(property, tTMPmin2, tTMPmax2, R.string.percent);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else if (property.getPropertyType().equals(PropertyType.DROP_DOWN)) {
            // Prepare and add scale percent property
            View preparedView = prepareDropDownProperty(property);
            if (preparedView != null) {
                linearLayoutItem.addView(preparedView);
            }
        } else {
            // ToDo .. unknown type!!
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
            textView.setText("Irgend ein Feld ohne Namen!"); // ToDo ?
        }

        return textView;
    }

    /**
     * Prepare the property group.
     *
     * @param property the root element of the group
     * @return the prepared property group
     */
    private View preparePropertyGroup(DummyProperty property) {
        // Save sub list
        ArrayList<DummyProperty> subList = property.getSubList();

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
            for (DummyProperty subItem : subList) {
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
        int tmpWidth = relativeLayoutGroup.getMeasuredWidth();
        int tmpPadding = (int) getResources().getDimension(R.dimen.group_padding);
        int tmpPadding2 = (int) getResources().getDimension(R.dimen.fragment_main_margin);

        int availableWidth = relativeLayoutGroup.getMeasuredWidth() - (2 * (int) getResources().getDimension(R.dimen.group_padding)) - (2 * (int) getResources().getDimension(R.dimen.fragment_main_margin));
        availableWidth = availableWidth - 300;
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
                        if(idOfFirstElementInLastRow != 0) {
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
                    usedWidth = 0;
                }
            }
        }

        // ToDo (int usedHeight??? + padding!)
        relativeLayoutGroup.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    /**
     * Prepare a text property field.
     *
     * @param property             includes the value (and ToDo more?)
     * @param onlyNumbers          is true if the input allows just numbers
     * @param allowNegativeNumbers is true if the input of negative numbers is allowed
     * @return the prepared view
     */
    private View prepareTextProperty(final DummyProperty property, boolean onlyNumbers, boolean allowNegativeNumbers) {
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
                updateProperty(property, editText.getText());
            }
        });

        // Set the current value
        if (property.getValue() instanceof String) {
            editText.setText((String) property.getValue());
        } else {
            // Set default values
            editText.setText("");
        }

        return editText;
    }

    /**
     * Prepare a toggle button property field.
     *
     * @param property includes the value (and ToDo more?)
     * @return the prepared view
     */
    private View prepareToggleButtonProperty(final DummyProperty property) {
        // Create new layout elements
        ToggleButton toggleButton = new ToggleButton(view.getContext());

        // Get layout params
        LinearLayout.LayoutParams toggleButtonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        toggleButton.setLayoutParams(toggleButtonParams);

        // Set toggleButton attributes
        toggleButton.setTextOff(String.format("%s %s", property.getName(), getResources().getString(R.string.off)));
        toggleButton.setTextOn(String.format("%s %s", property.getName(), getResources().getString(R.string.on)));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateProperty(property, isChecked);
            }
        });

        // Set the current value
        if (property.getValue() instanceof Boolean) {
            toggleButton.setChecked((Boolean) property.getValue());
        } else {
            // Set default values
            toggleButton.setChecked(false);
        }

        return toggleButton;
    }

    /**
     * Prepare a scale property field.
     *
     * @param property   includes the value (and ToDo more?)
     * @param min        is the minimum value that can be selected
     * @param max        is the maximum value that can be selected
     * @param identifier is the identifier behind the value (e.g. %, °C,...)
     * @return the prepared view
     */
    private View prepareScaleProperty(final DummyProperty property, final int min, final int max, final int identifier) {
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
        setChangeListenerTo(seekBar, editText, property, min);

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
        setActionListenerTo(editText, seekBar, property, min, max);

        // Set textView attributes
        textView.setText(identifier);

        // Set the current value
        if (property.getValue() instanceof Integer) {
            seekBar.setProgress((Integer) property.getValue() - min);
            editText.setText(String.valueOf(property.getValue()));
        } else {
            // Set default values
            seekBar.setProgress(min);
            editText.setText(String.valueOf(min));
        }

        return linearLayoutRow;
    }

    /**
     * Set the action listener to the edit view.
     *
     * @param editText is the text view behind the seek bar that includes the value
     * @param seekBar  is the element that will get the change listener
     * @param property is the unchanged property
     * @param min      is the minimum value that can be selected
     * @param max      is the maximum value that can be selected
     */
    private void setActionListenerTo(final EditText editText, final SeekBar seekBar, final DummyProperty property, final int min, final int max) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    int value = Integer.valueOf(String.valueOf(editText.getText()));
                    if (value > max || value < min) {
                        IM.INSTANCES.getMH().showQuickMessage("Wert liegt nicht im Wertebereicht!!");
                        // ToDo .. Info..
                        editText.setText(String.valueOf(seekBar.getProgress() + min));
                    } else {
                        seekBar.setProgress(value - min);
                        updateProperty(property, value);
                    }
                } catch (Exception e) {
                    IM.INSTANCES.getMH().showQuickMessage("Wert ist keine Zahl!!");
                    // ToDo Info ...
                }
                return false;
            }
        });
    }

    /**
     * Set the change listener to the seek bar.
     *
     * @param seekBar  is the element that will get the change listener
     * @param editText is the text view behind the seek bar that includes the value
     * @param property is the unchanged property
     * @param min      is the minimum value that can be selected
     */
    private void setChangeListenerTo(SeekBar seekBar, final EditText editText, final DummyProperty property, final int min) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editText.setText(String.valueOf(progress + min));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateProperty(property, seekBar.getProgress() + min);
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
     * @param property includes the value (and ToDo more?)
     * @return the prepared view
     */
    private View prepareDropDownProperty(final DummyProperty property) {
        // Create new layout elements
        final Spinner spinner = new Spinner(view.getContext());

        // Get layout params
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        spinner.setLayoutParams(spinnerParams);

        // Get items and selected item from the property
        Object values = property.getValue();
        ArrayList<String> items = new ArrayList<String>();
        int selectedId = -1;
        if (values instanceof HashMap) {
            HashMap<String, Object> map = (HashMap<String, Object>) values;
            if (map.containsKey("items") && map.get("items") instanceof ArrayList) {
                items = (ArrayList<String>) map.get("items");
            }
            if (map.containsKey("selectedId") && map.get("selectedId") instanceof Integer) {
                selectedId = (Integer) map.get("selectedId");
            }
        }

        // Convert the array list to a string array
        String[] itemsAsArray = new String[items.size()];
        itemsAsArray = items.toArray(itemsAsArray);

        // Set spinner attributes
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, itemsAsArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateProperty(property, spinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set the current value
        if (selectedId != -1 && selectedId < items.size()) {
            spinner.setSelection(selectedId);
        }
        return spinner;
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
