package de.uni_stuttgart.riot.android.ui;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import de.uni_stuttgart.riot.android.Callback;

/**
 * An {@link EditText} field that is read-only and accepts property values of arbitrary types and displays them using their
 * {@link Object#toString()} method. This will always report <tt>null</tt> as the current value and never fire the listener.
 * 
 * @param <V>
 *            The value type.
 */
public class ToStringView<V> extends EditText implements PropertyView<V> {

    /**
     * Creates a new EditNumberView.
     * 
     * @param context
     *            The Android context.
     */
    public ToStringView(Context context) {
        super(context);
        super.setEnabled(false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(false);
    }

    @Override
    public void setValue(V value) {
        super.setText(value == null ? "" : value.toString());
    }

    @Override
    public V getValue() {
        return null;
    }

    @Override
    public void setListener(Callback<V> listener) {
    }

    @Override
    public View toView() {
        return this;
    }

}
