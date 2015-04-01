package de.uni_stuttgart.riot.android.managementproperty;

import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a EditText ui element for numbers.
 */
public class ManagementPropertyEditNumber extends ManagementProperty<EditText, Number> {

    private EditText editText;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param activity is the current activity
     */
    public ManagementPropertyEditNumber(Property<Number> property, Activity activity) {
        super(property);
        buildElement(activity);
    }

    /**
     * Constructor.
     *
     * @param value    is used for non property elements
     * @param activity is the current activity
     */
    public ManagementPropertyEditNumber(Number value, Activity activity) {
        super(value);
        buildElement(activity);
    }

    /**
     * Allow just positive or also negative numbers.
     *
     * @param allowNegative is true to allow negative numbers
     */
    public void setAllowNegative(boolean allowNegative) {
        if (allowNegative) {
            editText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    @Override
    public EditText getUiElement() {
        return this.editText;
    }

    @Override
    protected void initElement(Context context) {
        this.editText = new EditText(context);

        // Get layout params
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set layout params
        editText.setLayoutParams(editTextParams);

        // Set default
        setAllowNegative(true);
    }

    @Override
    protected void setChangeListenerAndUpdateProperty() {
        this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                updateProperty(Double.parseDouble(editText.getText().toString()));
                return false;
            }
        });
    }

    @Override
    protected void enableView(boolean val) {
        this.editText.setEnabled(val);
    }

    @Override
    protected void setValue(Number value) {
        this.editText.setText(String.valueOf(value));
    }
}
