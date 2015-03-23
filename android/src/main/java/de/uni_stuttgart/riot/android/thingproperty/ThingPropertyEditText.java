package de.uni_stuttgart.riot.android.thingproperty;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a EditText ui element.
 */
public class ThingPropertyEditText extends ThingProperty<EditText, String> {

    private EditText editText;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param context  is the application context
     */
    public ThingPropertyEditText(Property<String> property, Context context) {
        super(property);
        buildElement(context);
    }

    /**
     * Constructor.
     *
     * @param value   is used for testing
     * @param context is the application context
     */
    public ThingPropertyEditText(String value, Context context) {
        super(value);
        buildElement(context);
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
    }

    @Override
    protected void setChangeListenerAndUpdateProperty() {
        this.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateProperty(s.toString());
            }
        });
    }

    @Override
    protected void enableView(boolean val) {
        this.editText.setEnabled(val);
    }

    @Override
    protected void setValue(String value) {
        this.editText.setText(value);
    }
}
