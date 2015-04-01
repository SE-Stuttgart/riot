package de.uni_stuttgart.riot.android.managementproperty;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import de.uni_stuttgart.riot.thing.Property;

/**
 * Created by Benny on 19.03.2015.
 * This class provides a EditText ui element.
 */
public class ManagementPropertyEditText extends ManagementProperty<EditText, String> {

    private EditText editText;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     * @param activity is the current activity
     */
    public ManagementPropertyEditText(Property<String> property, Activity activity) {
        super(property);
        buildElement(activity);
    }

    /**
     * Constructor.
     *
     * @param value    is used for non property elements
     * @param activity is the current activity
     */
    public ManagementPropertyEditText(String value, Activity activity) {
        super(value);
        buildElement(activity);
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
        this.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // Update property on leaving view
                if (!b) {
                    updateProperty(editText.getText().toString());
                }
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
