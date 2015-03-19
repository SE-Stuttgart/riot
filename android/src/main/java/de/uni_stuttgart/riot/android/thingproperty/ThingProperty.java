package de.uni_stuttgart.riot.android.thingproperty;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import de.uni_stuttgart.riot.thing.BasePropertyListener;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.WritableProperty;

/**
 * Created by Benny on 19.03.2015.
 * This generic parent class helps to handle properties combined with an ui element.
 * @param <T> stands for the ui element type.
 * @param <V> stands for the value type of the ui element.
 */
public abstract class ThingProperty<T, V> {

    protected Property<V> property;
    private BasePropertyListener<V> basePropertyListener;

    /**
     * Constructor.
     *
     * @param property for that the element will be implemented
     */
    public ThingProperty(final Property<V> property) {
        this.property = property;
    }

    /**
     * Build the element.
     *
     * @param context is the application context
     */
    public void buildElement(Context context) {
        // Initialize the element
        initElement(context);

        // Check if the property is writable
        if (this.property instanceof WritableProperty) {
            enableView(true);
            setChangeListenerAndUpdateProperty();
        } else {
            enableView(false);
        }

        // Initialize the property listener for that element
        this.basePropertyListener = new BasePropertyListener<V>() {
            @Override
            public void onChange(Property<? extends V> p, V oldValue, V newValue) {
                setValue(newValue);
            }
        };
    }

    /**
     * Register a listener for the property.
     */
    public void bind() {
        this.property.register(this.basePropertyListener);
    }

    /**
     * Unregister the listener for the property.
     */
    public void unbind() {
        this.property.unregister(this.basePropertyListener);
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
     * @param value is the new value
     */
    protected void updateProperty(final V value) {
        new Thread() {

            @Override
            public void run() {
                ((WritableProperty<V>) property).set(value);
            }
        }.start();
    }

    /**
     * En-/disables all child items of the given view.
     *
     * @param view  the item that sub items should be disabled
     * @param value is an boolean value to en-/disable this items
     */
    protected void enableChildItems(ViewGroup view, boolean value) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View subView = view.getChildAt(i);
            if (subView instanceof ViewGroup) {
                enableChildItems((ViewGroup) subView, value);
            }
            subView.setEnabled(value);
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
     * @param value is true if it should be enabled
     */
    protected abstract void enableView(boolean value);

    /**
     * Set the value of the ui element.
     *
     * @param value is the new value
     */
    protected abstract void setValue(V value);
}
