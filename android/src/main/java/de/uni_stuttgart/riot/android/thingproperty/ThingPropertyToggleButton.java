package de.uni_stuttgart.riot.android.thingproperty;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a ToggleButton ui element.
 */
public class ThingPropertyToggleButton extends ThingProperty<ToggleButton, Boolean> {

    private ToggleButton toggleButton;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param context  is the application context
     */
    public ThingPropertyToggleButton(Property<Boolean> property, Context context) {
        super(property);
        buildElement(context);
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
        this.toggleButton.setTextOff(property.getName() + " " + context.getString(R.string.off));
        this.toggleButton.setTextOn(property.getName() + " " + context.getString(R.string.on));

        // Set the current value
        setValue(property.get());
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
    protected void enableView(boolean value) {
        this.toggleButton.setEnabled(value);
    }

    @Override
    protected void setValue(Boolean value) {
        this.toggleButton.setChecked(value);
    }
}
