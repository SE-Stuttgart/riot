package de.uni_stuttgart.riot.android.managementproperty;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import de.uni_stuttgart.riot.android.communication.ActivityPropertyListener;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.WritableProperty;

/**
 * Created by Benny on 19.03.2015.
 * This generic parent class helps to handle properties combined with an ui element.
 *
 * @param <T> stands for the ui element type.
 * @param <V> stands for the value type of the ui element.
 */
public abstract class ManagementProperty<T, V> {

    protected Property<V> property;
    private ActivityPropertyListener<V> propertyListener;
    private V value;
    private boolean silentValueUpdate;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     */
    public ManagementProperty(final Property<V> property) {
        this.property = property;
    }

    /**
     * Constructor.
     *
     * @param value is used for non property elements
     */
    public ManagementProperty(final V value) {
        this.value = value;
    }

    /**
     * Build the element.
     *
     * @param activity is the application activity
     */
    public void buildElement(final Activity activity) {
        // Initialize the element
        initElement(activity.getApplicationContext());

        // Set the current value
        if (this.property != null) {
            setValue(this.property.get());
        } else if (this.value != null) {
            setValue(this.value);
        }

        // Check if the property is writable
        if (this.property != null) {
            if (this.property instanceof WritableProperty) {
                enableView(true);
                setChangeListenerAndUpdateProperty();
            } else {
                enableView(false);
            }
        } else {
            // Disable non property elements
            enableView(false);
            setChangeListenerAndUpdateProperty();
        }

        // Initialize the property listener for that element
        this.silentValueUpdate = false;
        this.propertyListener = new ActivityPropertyListener<V>(activity) {
            @Override
            public void onChange(Property<? extends V> p, V oldValue, V newValue) {
                if (!oldValue.equals(newValue)) { // TODO WARUM KOMMT DA SO VIEL AN??
                    silentValueUpdate = true;
                    setValue(newValue);
                    silentValueUpdate = false;
                }

            }
        };
    }

    /**
     * Register a listener for the property.
     */
    public void bind() {
        if (this.property != null) {
            this.property.register(this.propertyListener);
        }
    }

    /**
     * Unregister the listener for the property.
     */
    public void unbind() {
        if (this.property != null) {
            this.property.unregister(this.propertyListener);
        }
    }

    /**
     * Get the view element of the ui element.
     *
     * @return the view
     */
    public View getView() {
        return (View) getUiElement();
    }

    /**
     * Get the ui element.
     *
     * @return the ui element
     */
    public abstract T getUiElement();

    /**
     * Set the value of the property.
     *
     * @param val is the new value
     */
    protected void updateProperty(final V val) {
        if (!silentValueUpdate) {
            new Thread() {

                @Override
                public void run() {
                    if (property != null) {
                        ((WritableProperty<V>) property).set(val);
                    }
                }
            }.start();
        }
    }

    /**
     * En-/disables all child items of the given view.
     *
     * @param view the item that sub items should be disabled
     * @param val  is an boolean value to en-/disable this items
     */
    protected void enableChildItems(ViewGroup view, boolean val) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View subView = view.getChildAt(i);
            if (subView instanceof ViewGroup) {
                enableChildItems((ViewGroup) subView, val);
            }
            subView.setEnabled(val);
        }
    }

    /**
     * Build the element.
     *
     * @param context is the application context
     */
    protected abstract void initElement(Context context);

    /**
     * Set a change listener for that element and update the property inside.
     */
    protected abstract void setChangeListenerAndUpdateProperty();

    /**
     * Enable the view element.
     *
     * @param val is true if it should be enabled
     */
    protected abstract void enableView(boolean val);

    /**
     * Set the value of the ui element.
     *
     * @param value is the new value
     */
    protected abstract void setValue(V value);
}
