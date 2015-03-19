package de.uni_stuttgart.riot.android.thingproperty;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormatSymbols;

import de.uni_stuttgart.riot.android.R;
import de.uni_stuttgart.riot.android.messages.IM;
import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a SeekBar ui element as fractional slider.
 */
public class ThingPropertyFractionalSlider extends ThingProperty<LinearLayout, Double> {

    private LinearLayout linearLayoutRow;
    private SeekBar seekBar;
    private EditText editText;

    private float min;
    private float max;
    private int factor;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param context  is the application context
     * @param min      is the minimum possible value
     * @param max      is the maximum possible value
     */
    public ThingPropertyFractionalSlider(Property<Double> property, Context context, float min, float max) {
        super(property);
        this.min = min;
        this.max = max;
        calculateFactor(context);
        buildElement(context);
    }

    @Override
    public LinearLayout getUiElement() {
        return this.linearLayoutRow;
    }

    @Override
    protected void initElement(Context context) {
        this.linearLayoutRow = new LinearLayout(context);

        // Calculate the real max value for the seek bar
        final int realMax = (int) (max - min);

        // Create new layout elements
        this.seekBar = new SeekBar(context);
        LinearLayout linearLayoutText = new LinearLayout(context);
        this.editText = new EditText(context);
        TextView textView = new TextView(context);

        // Link the layout elements
        linearLayoutText.addView(editText);
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

        // Set the current value
        setValue(property.get());

        final float tmp1 = 0.2f;
        final float tmp2 = 0.8f;

        // Set seekBar attributes
        seekBarParams.weight = tmp1;// TODO Resoucrces$NotFoundException ... context.getResources().getDimension(R.dimen.low_weight);
        seekBarParams.gravity = Gravity.CENTER;

        this.seekBar.setMax(realMax);

        // Set linearLayoutText attributes
        linearLayoutTextParams.weight = tmp2;// TODO Resoucrces$NotFoundException ... context.getResources().getDimension(R.dimen.high_weight);
        linearLayoutTextParams.gravity = Gravity.CENTER;
        linearLayoutText.setOrientation(LinearLayout.HORIZONTAL);

        // Set editText attributes
        if (min < 0) {
            this.editText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
        } else {
            this.editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        // Set textView attributes
        // TODO textView.setText(identifier);
    }

    @Override
    protected void setChangeListenerAndUpdateProperty() {
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                if (fromUser) {
                    setValue((double) (progress + min) / factor);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar s) {
                updateProperty((double) (s.getProgress() + min) / factor);
            }
        });

        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    double value = Double.valueOf(editText.getText().toString());
                    if (value > max || value < min) {
                        // Show a quick message that there was an error
                        IM.INSTANCES.getMH().showQuickMessage("Value is invalid!");

                        // Reset displayed text
                        editText.setText(String.valueOf((seekBar.getProgress() + min) / factor));
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
    protected void enableView(boolean value) {
        enableChildItems(this.linearLayoutRow, value);
    }

    @Override
    protected void setValue(Double value) {
        editText.setText(String.valueOf(value));
        seekBar.setProgress((int) (value - min) * this.factor);
    }

    /**
     * Calculate the maximum number of decimal places and the factor to handle floating numbers with the slider.
     *
     * @param context is the application context
     */
    private void calculateFactor(Context context) {
        final int ten = 10;
        int minSplitLen = 0;
        int maxSplitLen = 0;

        String decimalSeparator = context.getString(R.string.backslash) + String.valueOf(DecimalFormatSymbols.getInstance().getDecimalSeparator());

        String[] minSplit = Double.toString(min).split(decimalSeparator);

        if (minSplit.length > 1) {
            minSplitLen = minSplit[1].length();
        }

        String[] maxSplit = Double.toString(max).split(decimalSeparator);
        if (maxSplit.length > 1) {
            maxSplitLen = minSplit[1].length();
        }

        // Save number of decimal places
        int numberOfDecimalPlaces = Math.max(minSplitLen, maxSplitLen);

        // Save factor to calculate
        this.factor = (int) Math.pow(ten, numberOfDecimalPlaces);
    }
}
