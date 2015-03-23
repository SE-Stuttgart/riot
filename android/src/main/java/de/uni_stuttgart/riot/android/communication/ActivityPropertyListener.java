package de.uni_stuttgart.riot.android.communication;

import java.lang.ref.WeakReference;

import android.app.Activity;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent.Instance;
import de.uni_stuttgart.riot.thing.PropertyListener;

/**
 * Convenience base class for {@link PropertyListener}, can be registered with {@link Property#register(PropertyListener)}, delivers the
 * changed property and old and new value. The callback will be executed on the UI thread associated with the given activity, which is
 * referenced weakly.
 * 
 * @author Philipp Keck
 *
 * @param <V>
 *            The type of the property's values.
 */
public abstract class ActivityPropertyListener<V> implements PropertyListener<V> {

    private final WeakReference<Activity> activity;

    /**
     * Creates a new instance.
     * 
     * @param activity
     *            The activity that needs the listener. The {@link #onChange(Property, Object, Object)} method will be executed in the UI
     *            thread of that activity.
     */
    protected ActivityPropertyListener(Activity activity) {
        this.activity = new WeakReference<Activity>(activity);
    }

    @Override
    public final void onFired(final Event<? extends Instance<? extends V>> event, final Instance<? extends V> eventInstance) {
        Activity a = activity.get();
        if (a == null) {
            event.unregister(this);
        } else {
            @SuppressWarnings("unchecked")
            final Property<? extends V> property = ((PropertyChangeEvent<? extends V>) event).getProperty();
            a.runOnUiThread(new Runnable() {
                public void run() {
                    onChange(property, eventInstance.getOldValue(), eventInstance.getNewValue());
                }
            });
        }
    }

    /**
     * This method will be fired whenever one of the properties that the listener is registered to has changed (unless it was a silent
     * change). The method will be executed on the UI thread of the activity passed to the constructor.
     * 
     * @param property
     *            The property that changed.
     * @param oldValue
     *            Its old value.
     * @param newValue
     *            Its new value.
     */
    public abstract void onChange(Property<? extends V> property, V oldValue, V newValue);

}
