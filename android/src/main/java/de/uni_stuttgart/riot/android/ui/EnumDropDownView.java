package de.uni_stuttgart.riot.android.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import de.uni_stuttgart.riot.android.Callback;

/**
 * A {@link Spinner} field that contains all values of a specified enum type.
 * 
 * @param <E>
 *            The type of the enum.
 */
public class EnumDropDownView<E extends Enum<E>> extends Spinner implements PropertyView<E> {

    private final Class<E> valueType;
    private final ArrayList<String> itemNames;
    private Callback<E> listener;

    /**
     * Creates a new EditNumberView.
     * 
     * @param context
     *            The Android context.
     * @param valueType
     *            The concrete enum type.
     */
    public EnumDropDownView(Context context, Class<E> valueType) {
        super(context);
        this.valueType = valueType;

        itemNames = new ArrayList<String>();
        for (E value : valueType.getEnumConstants()) {
            itemNames.add(value.name());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, itemNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        super.setAdapter(adapter);

        super.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Callback<E> l = listener;
                if (l != null) {
                    l.callback(getValue());
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                Callback<E> l = listener;
                if (l != null) {
                    l.callback(null);
                }
            }
        });
    }

    @Override
    public void setValue(E value) {
        super.setSelection(itemNames.indexOf(value.name()));
    }

    @Override
    public E getValue() {
        Object value = super.getSelectedItem();
        if (value == null) {
            return null;
        } else {
            return Enum.valueOf(valueType, value.toString());
        }
    }

    @Override
    public void setListener(Callback<E> listener) {
        this.listener = listener;
    }

    @Override
    public View toView() {
        return this;
    }

    /**
     * Creates a new EditNumberView. This is a workaround method to allow Java 6 to bind the type parameter throug the <tt>valueType</tt>
     * paramter without using the diamond operator.
     * 
     * @param <E>
     *            The concrete enum type.
     * @param context
     *            The Android context.
     * @param valueType
     *            The concrete enum type.
     * @return The view.
     */
    public static <E extends Enum<E>> EnumDropDownView<E> create(Context context, Class<E> valueType) {
        return new EnumDropDownView<E>(context, valueType);
    }

}
