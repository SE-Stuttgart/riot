package de.uni_stuttgart.riot.android.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import de.uni_stuttgart.riot.android.Callback;

/**
 * An {@link EditText} field that makes its value accessible as a property.
 */
public class EditTextView extends EditText implements PropertyView<String> {

    private Callback<String> listener;

    /**
     * Creates a new EditNumberView.
     * 
     * @param context
     *            The Android context.
     */
    public EditTextView(Context context) {
        super(context);
        super.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                Callback<String> l = listener;
                if (l != null) {
                    l.callback(getValue());
                }
            }
        });
    }

    @Override
    public void setValue(String value) {
        super.setText(value);
    }

    @Override
    public String getValue() {
        return super.getText().toString();
    }

    @Override
    public void setListener(Callback<String> listener) {
        this.listener = listener;
    }

    @Override
    public View toView() {
        return this;
    }

}
