package de.uni_stuttgart.riot.android.managementproperty;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;

import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a Spinner ui element as enum drop down.
 */
public class ManagementPropertyEnumDropDown extends ManagementProperty<Spinner, Enum<?>> {

    private Spinner spinner;
    private ArrayList<Enum<?>> enumList;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param activity is the current activity
     * @param enumList includes the possible enum values
     */
    public ManagementPropertyEnumDropDown(Property<Enum<?>> property, Activity activity, ArrayList<Enum<?>> enumList) {
        super(property);
        this.enumList = enumList;
        buildElement(activity);
    }

    /**
     * Constructor.
     *
     * @param value    is used for non property elements
     * @param activity is the current activity
     * @param enumList includes the possible enum values
     */
    public ManagementPropertyEnumDropDown(Enum<?> value, Activity activity, ArrayList<Enum<?>> enumList) {
        super(value);
        this.enumList = enumList;
        buildElement(activity);
    }

    @Override
    public Spinner getUiElement() {
        return this.spinner;
    }

    @Override
    protected void initElement(Context context) {
        this.spinner = new Spinner(context);

        // Get layout params
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        this.spinner.setLayoutParams(spinnerParams);

        // Convert the enum array list to a string array
        if (this.enumList != null) {
            int enumLength = this.enumList.size();
            String[] itemsAsArray = new String[enumLength];
            for (int i = 0; i < enumLength; i++) {
                itemsAsArray[i] = this.enumList.get(i).toString();
            }

            // Set spinner attributes
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, itemsAsArray);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.spinner.setAdapter(spinnerAdapter);
        }
    }

    @Override
    protected void setChangeListenerAndUpdateProperty() {
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (enumList != null) {
                    updateProperty(enumList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void enableView(boolean val) {
        enableChildItems(this.spinner, val);
    }

    @Override
    protected void setValue(Enum<?> value) {
        if (value != null && enumList != null) {
            int index = enumList.indexOf(value);
            if (index != -1) {
                spinner.setSelection(index);
            }
        }
    }
}
