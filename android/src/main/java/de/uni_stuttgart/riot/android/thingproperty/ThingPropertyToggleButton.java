package de.uni_stuttgart.riot.android.thingproperty;

import android.app.Activity;
import android.content.Context;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a ToggleButton ui element.
 */
public class ThingPropertyToggleButton extends ThingProperty<ToggleButton, Boolean> {

    private ToggleButton toggleButton;
    private String valueOn;
    private String valueOff;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param activity is the current activity
     * @param valueOff the displayed value if the button is toggled off
     * @param valueOn  the displayed value if the button is toggled on
     */
    public ThingPropertyToggleButton(Property<Boolean> property, Activity activity, String valueOff, String valueOn) {
        super(property);
        this.valueOff = valueOff;
        this.valueOn = valueOn;
        buildElement(activity);
    }

    /**
     * Constructor.
     *
     * @param value    is used for non property elements
     * @param activity is the current activity
     * @param valueOff the displayed value if the button is toggled off
     * @param valueOn  the displayed value if the button is toggled on
     */
    public ThingPropertyToggleButton(Boolean value, Activity activity, String valueOff, String valueOn) {
        super(value);
        this.valueOff = valueOff;
        this.valueOn = valueOn;
        buildElement(activity);
    }

    @Override
    public ToggleButton getUiElement() {
        return this.toggleButton;
    }

    @Override
    protected void initElement(Context context) {
        this.toggleButton = new ToggleButton(context);

        // Get layout params
        LinearLayout.LayoutParams toggleButtonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        this.toggleButton.setLayoutParams(toggleButtonParams);

        // Set toggleButton attributes
        this.toggleButton.setTextOff(this.valueOff);
        this.toggleButton.setTextOn(this.valueOn);
    }

    @Override
    protected void setChangeListenerAndUpdateProperty() {
        this.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                updateProperty(isChecked);
            }
        });
    }

    @Override
    protected void enableView(boolean val) {
        this.toggleButton.setEnabled(val);
    }

    @Override
    protected void setValue(Boolean value) {
        this.toggleButton.setChecked(value);
    }
}
