package de.uni_stuttgart.riot.android.management;

import android.app.Activity;
import android.view.View;
import de.uni_stuttgart.riot.android.Callback;
import de.uni_stuttgart.riot.android.communication.ActivityPropertyListener;
import de.uni_stuttgart.riot.android.ui.PropertyView;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.WritableProperty;

/**
 * This binding binds a {@link Thing}'s {@link Property} to a {@link PropertyView}. This class takes care of the threading and prevents
 * changes from being propagated back. If the property does not inherit {@link WritableProperty}, the view will be read-only and changes
 * will only be propagated from the property to the view.
 * 
 * @author Philipp Keck
 *
 * @param <V>
 *            The type of the property's values.
 */
public class ManagementPropertyBinding<V> {

    private final Property<V> property;
    private final PropertyView<V> view;

    private final Callback<V> viewListener;
    private final ActivityPropertyListener<V> propertyListener;

    private volatile boolean updating;

    /**
     * Creates a new instance.
     * 
     * @param property
     *            The thing property.
     * @param view
     *            The Android view (must also inherit {@link View}).
     * @param activity
     *            The activity that the view will be used in.
     */
    public ManagementPropertyBinding(Property<V> property, PropertyView<V> view, Activity activity) {
        this.property = property;
        this.view = view;

        if (property instanceof WritableProperty) {
            final WritableProperty<V> writableProperty = (WritableProperty<V>) property;
            this.viewListener = new Callback<V>() {
                public void callback(V value) {
                    synchronized (ManagementPropertyBinding.this) {
                        if (updating) {
                            return;
                        }
                        updating = true;
                        try {
                            writableProperty.set(value);
                        } finally {
                            updating = false;
                        }
                    }
                }
            };
        } else {
            this.view.setEnabled(false);
            this.viewListener = null;
        }

        this.propertyListener = new ActivityPropertyListener<V>(activity) {
            public void onChange(Property<? extends V> property2, V oldValue, V newValue) {
                synchronized (ManagementPropertyBinding.this) {
                    if (updating) {
                        return;
                    }
                    updating = true;
                    try {
                        V viewValue = ManagementPropertyBinding.this.view.getValue();
                        if (!equal(viewValue, newValue)) {
                            ManagementPropertyBinding.this.view.setValue(newValue);
                        }
                    } finally {
                        updating = false;
                    }
                }
            }
        };
    }

    /**
     * Binds the property, so that values will be propagated.
     */
    public void bind() {
        this.view.setListener(viewListener);
        this.property.register(propertyListener);
    }

    /**
     * Unbinds the property, so that values are not propagated anymore.
     */
    public void unbind() {
        this.view.setListener(null);
        this.property.unregister(propertyListener);
    }

    /**
     * Checks if the two values are equal. Makes an epsilon-check for fractional numbers.
     * 
     * @param first
     *            The first value.
     * @param second
     *            The second value.
     * @return True iff they are equal.
     */
    private static <V> boolean equal(V first, V second) {
        if (first instanceof Number && second instanceof Number) {
            double dblFirst = ((Number) first).doubleValue();
            double dblSecond = ((Number) second).doubleValue();
            return Math.abs(dblFirst - dblSecond) < 0.000001; // NOCS FIXME This is a hack.
        } else {
            return first == null ? second == null : first.equals(second);
        }
    }

}
