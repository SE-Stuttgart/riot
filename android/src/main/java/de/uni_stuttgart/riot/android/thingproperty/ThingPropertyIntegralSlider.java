package de.uni_stuttgart.riot.android.thingproperty;

import android.content.Context;
import android.text.InputType;
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
 * This class provides a SeekBar ui element as integral slider.
 */
public class ThingPropertyIntegralSlider extends ThingProperty<LinearLayout, Integer> {

    private LinearLayout linearLayoutRow;
    private SeekBar seekBar;
    private EditText editText;

    private int min;
    private int max;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param context  is the application context
     * @param min      is the minimum possible value
     * @param max      is the maximum possible value
     */
    public ThingPropertyIntegralSlider(Property<Integer> property, Context context, int min, int max) {
        super(property);
        this.min = min;
        this.max = max;
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
        final int realMax = max - min;

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
                    setValue(progress + min);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar s) {
                updateProperty(s.getProgress() + min);
            }
        });

        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    int value = Integer.valueOf(editText.getText().toString());
                    if (value > max || value < min) {
                        // Show a quick message that there was an error
                        IM.INSTANCES.getMH().showQuickMessage("Value is invalid!");

                        // Reset displayed text
                        editText.setText(seekBar.getProgress() + min);
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
    protected void setValue(Integer value) {
        editText.setText(String.valueOf(value));
        seekBar.setProgress((int) (value - min));
    }
}
