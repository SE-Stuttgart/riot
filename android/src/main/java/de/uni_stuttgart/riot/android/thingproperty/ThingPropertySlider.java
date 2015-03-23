package de.uni_stuttgart.riot.android.thingproperty;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a SeekBar ui element as fractional slider.
 *
 * @param <V> stands for the value type of the ui element.
 */
public abstract class ThingPropertySlider<V> extends ThingProperty<LinearLayout, V> {

    protected LinearLayout linearLayoutRow;
    protected SeekBar seekBar;
    protected EditText editText;

    protected V min;
    protected V max;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param context  is the application context
     * @param min      is the minimum possible value
     * @param max      is the maximum possible value
     */
    public ThingPropertySlider(Property<V> property, Context context, V min, V max) {
        super(property);
        this.min = min;
        this.max = max;
        thingsBeforeBuildElement();
        buildElement(context);
    }

    /**
     * Constructor.
     *
     * @param value   is used for testing
     * @param context is the application context
     * @param min     is the minimum possible value
     * @param max     is the maximum possible value
     */
    public ThingPropertySlider(V value, Context context, V min, V max) {
        super(value);
        this.min = min;
        this.max = max;
        thingsBeforeBuildElement();
        buildElement(context);
    }

    @Override
    public LinearLayout getUiElement() {
        return this.linearLayoutRow;
    }

    @Override
    protected void initElement(Context context) {
        this.linearLayoutRow = new LinearLayout(context);

        // Create new layout elements
        this.seekBar = new SeekBar(context);
        LinearLayout linearLayoutText = new LinearLayout(context);
        this.editText = new EditText(context);
        TextView textView = new TextView(context);

        // Link the layout elements
        linearLayoutText.addView(this.editText);
        linearLayoutText.addView(textView);
        this.linearLayoutRow.addView(this.seekBar);
        this.linearLayoutRow.addView(linearLayoutText);

        // Get layout params
        LinearLayout.LayoutParams linearLayoutRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams seekBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams linearLayoutTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // int editTextWidth = (int) getDimension(R.dimen.slider_edit_text_width); // ToDo use factor? for dynamic size
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        this.linearLayoutRow.setLayoutParams(linearLayoutRowParams);
        this.seekBar.setLayoutParams(seekBarParams);
        linearLayoutText.setLayoutParams(linearLayoutTextParams);
        this.editText.setLayoutParams(editTextParams);
        textView.setLayoutParams(textViewParams);

        // Set linearLayoutRow attributes
        this.linearLayoutRow.setOrientation(LinearLayout.HORIZONTAL);

        final float tmp1 = 0.2f;
        final float tmp2 = 0.8f;

        // Set seekBar attributes
        seekBarParams.weight = tmp1; // TODO Resources$NotFoundException ... context.getResources().getDimension(R.dimen.low_weight);
        seekBarParams.gravity = Gravity.CENTER;

        this.seekBar.setMax(getRealMax());

        // Set linearLayoutText attributes
        linearLayoutTextParams.weight = tmp2; // TODO Resources$NotFoundException ... context.getResources().getDimension(R.dimen.high_weight);
        linearLayoutTextParams.gravity = Gravity.CENTER;
        linearLayoutText.setOrientation(LinearLayout.HORIZONTAL);

        // Set editText attributes
        setEditTextAttributes();

        // Set textView attributes
        // TODO textView.setText(identifier);
    }

    @Override
    protected void setChangeListenerAndUpdateProperty() {
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                if (fromUser) {
                    setValue(handleSeekBarValue(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar s) {
                updateProperty(handleSeekBarValue(s.getProgress()));
            }
        });

        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    V value = getValueOfEditText();
                    if (isValueValid(value)) {
                        // Show a quick message that there was an error
                        IM.INSTANCES.getMH().showQuickMessage("Value is invalid!");

                        // Reset displayed text
                        setValue(handleSeekBarValue(seekBar.getProgress()));
                    } else {
                        setValue(value);
                        updateProperty(value);
                    }
                } catch (Exception e) {
                    // Show a quick message that there was an error (ToDo log this?)
                    IM.INSTANCES.getMH().showQuickMessage("Value is not a number!");
                }
                return false;
            }
        });
    }

    @Override
    protected void enableView(boolean val) {
        enableChildItems(this.linearLayoutRow, val);
    }

    /**
     * Do things that must be done before building the element.
     */
    protected abstract void thingsBeforeBuildElement();

    /**
     * Help to handle the value of the seek bar.
     *
     * @param value of the seek bar
     * @return the value to go further
     */
    protected abstract V handleSeekBarValue(Integer value);

    /**
     * Get the real max value for the seek bar.
     *
     * @return the max value for the seek bar (because min is 0)
     */
    protected abstract int getRealMax();

    /**
     * Set the attributes for the edit text
     */
    protected abstract void setEditTextAttributes();

    /**
     * Get the numeric value of the edit text.
     *
     * @return the numeric value
     */
    protected abstract V getValueOfEditText();

    /**
     * Check if the given value is valid.
     *
     * @param value the numeric value of the edit text
     * @return true if the value is between the max and min value
     */
    protected abstract boolean isValueValid(V value);
}
