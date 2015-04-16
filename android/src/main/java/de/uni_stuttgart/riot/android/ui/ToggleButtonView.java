package de.uni_stuttgart.riot.android.ui;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;
import de.uni_stuttgart.riot.android.Callback;

/**
 * An {@link EditText} field that makes its value accessible as a property.
 */
public class ToggleButtonView extends ToggleButton implements PropertyView<Boolean> {

    private Callback<Boolean> listener;

    /**
     * Creates a new EditNumberView.
     * 
     * @param context
     *            The Android context.
     * @param textOff
     *            The button label if it is off.
     * @param textOn
     *            The button label if it is on.
     */
    public ToggleButtonView(Context context, String textOff, String textOn) {
        super(context);
        super.setTextOff(textOff);
        super.setTextOn(textOn);
        super.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Callback<Boolean> l = listener;
                if (l != null) {
                    l.callback(getValue());
                }
            }
        });
    }

    @Override
    public void setValue(Boolean value) {
        setChecked(value == Boolean.TRUE);
    }

    @Override
    public Boolean getValue() {
        return isChecked();
    }

    @Override
    public void setListener(Callback<Boolean> listener) {
        this.listener = listener;
    }

    @Override
    public View toView() {
        return this;
    }

}
