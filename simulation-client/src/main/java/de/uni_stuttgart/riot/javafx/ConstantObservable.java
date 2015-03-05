package de.uni_stuttgart.riot.javafx;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * A JavaFX observable value that constantly returns the same value.
 * 
 * @param <T>
 *            The type of the value.
 * @author Philipp Keck
 */
public class ConstantObservable<T> implements ObservableValue<T> {

    private final T value;

    /**
     * Creates a new constant observable value.
     * 
     * @param value
     *            The value.
     */
    public ConstantObservable(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void addListener(InvalidationListener listener) {
    }

    @Override
    public void removeListener(InvalidationListener listener) {
    }

    @Override
    public void addListener(ChangeListener<? super T> listener) {
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
    }

}
