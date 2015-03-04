/**
 * Copyright 2013-2014 Universitaet Stuttgart FMI SchulScheduler Team
 * Team members: Mark Aukschlat, Philipp Keck, Mathias Landwehr, Dominik Lekar,
 * Dennis Maseluk, Alexander Miller, Sebastian Pirk, Sven Schnaible
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uni_stuttgart.riot.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.math.BigDecimal;

/**
 * Ein Eingabefeld für Zahlen mit Buttons zum Erhöhen/Verringern des Wertes per Mausklick.<br>
 * Quelle: http://myjavafx.blogspot.de/2013/05/developing-numberspinner-control.html (mit Genehmigung per ICQ)
 * https://bitbucket.org/sco0ter/extfx/src (Original ist unter MIT Lizenz)
 * 
 * @author Christian Schudt
 * @author Philipp Keck
 */
public class NumberSpinner extends TextField {

    /**
     * Der aktuelle Wert des Feldes.
     */
    private final ObjectProperty<Number> value = new SimpleObjectProperty<Number>(this, "value") {
        @Override
        protected void invalidated() {
            if (!isBound() && value.get() != null) {
                if (maxValue.get() != null && value.get().doubleValue() > maxValue.get().doubleValue()) {
                    set(maxValue.get());
                }
                if (minValue.get() != null && value.get().doubleValue() < minValue.get().doubleValue()) {
                    set(minValue.get());
                }
            }
        }
    };

    /**
     * Der maximal erlaubte Wert für das Feld (inklusive, Standard ist kein Limit).
     */
    private final ObjectProperty<Number> maxValue = new SimpleObjectProperty<Number>(this, "maxValue") {
        @Override
        protected void invalidated() {
            if (maxValue.get() != null) {
                if (minValue.get() != null && maxValue.get().doubleValue() < minValue.get().doubleValue()) {
                    throw new IllegalArgumentException("maxValue must not be greater than minValue");
                }
                if (value.get() != null && value.get().doubleValue() > maxValue.get().doubleValue()) {
                    value.set(maxValue.get());
                }
            }
        }
    };

    /**
     * Der minimal erlaubte Wert für das Feld (inklusive, Standard ist kein Limit).
     */
    private final ObjectProperty<Number> minValue = new SimpleObjectProperty<Number>(this, "minValue") {
        @Override
        protected void invalidated() {
            if (minValue.get() != null) {
                if (maxValue.get() != null && maxValue.get().doubleValue() < minValue.get().doubleValue()) {
                    throw new IllegalArgumentException("minValue must not be smaller than maxValue");
                }
                if (value.get() != null && value.get().doubleValue() < minValue.get().doubleValue()) {
                    value.set(minValue.get());
                }
            }
        }
    };

    /**
     * Die Schrittweite beim Klick der Buttons (Standardwert ist 1).
     */
    private final ObjectProperty<Number> stepWidth = new SimpleObjectProperty<Number>(this, "stepWidth", 1);

    /**
     * Das verwendete Format für die Zahlen.
     */
    private final ObjectProperty<StringConverter<Number>> numberStringConverter = new SimpleObjectProperty<>(this, "numberStringConverter", new NumberStringConverter());

    /**
     * Erstellt eine neue NumberSpinner-Komponente für die alleinstehende Verwendung.
     */
    public NumberSpinner() {
        this(true);
    }

    /**
     * Erstellt eine neue NumberSpinner-Komponente mit der CSS-Klasse "number-spinner".
     * 
     * @param standalone
     *            True, wenn der Spinner alleine (und nicht in einer {@link NumberSpinnerCell}) verwendet wird.
     */
    public NumberSpinner(boolean standalone) {
        getStyleClass().add("number-spinner");
        if (standalone) {
            getStyleClass().add("standalone");
        }
        setFocusTraversable(false);
    }

    /**
     * Wird vom Skin aufgerufen, wenn das Textfeld fokussiert wurde.
     * 
     * @param focusedInternal
     *            True, wenn das Textfeld fokussiert ist.
     */
    void setFocusedInternal(boolean focusedInternal) {
        setFocused(focusedInternal);
    }

    /**
     * Der aktuelle Wert des Feldes. Ein Wert von null, {@link Double#NaN} oder andere unendliche Werte werden als leeres Textfeld
     * angezeigt.
     * 
     * @return Die value-Property.
     * @see #getValue()
     * @see #setValue(Number)
     */
    public final ObjectProperty<Number> valueProperty() {
        return value;
    }

    /**
     * Holt den aktuellen Wert.
     * 
     * @return Der aktuelle Wert des Feldes.
     * @see #valueProperty()
     */
    public final Number getValue() {
        return value.get();
    }

    /**
     * Setzt den Wert des Felds.
     * 
     * @param value
     *            Der neue Wert des Feldes.
     * @see #valueProperty()
     */
    public final void setValue(final Number value) {
        this.value.set(value);
    }

    /**
     * Der maximal erlaubte Wert für das Feld (inklusive). Ein Wert von null bedeutet, dass es keine Begrenzung gibt.
     * 
     * @return Die maxValue-Property.
     * @see #getMaxValue()
     * @see #setMaxValue(Number)
     */
    public final ObjectProperty<Number> maxValueProperty() {
        return maxValue;
    }

    /**
     * Ermittelt den maximal erlaubten Wert.
     * 
     * @return Der maximal erlaubte Wert für das Feld (inklusive).
     * @see #maxValueProperty()
     */
    public final Number getMaxValue() {
        return maxValue.get();
    }

    /**
     * Setzt den maximalen Wert.
     * 
     * @param maxValue
     *            Der maximal erlaubte Wert für das Feld (inklusive).
     * @see #maxValueProperty()
     */
    public final void setMaxValue(final Number maxValue) {
        this.maxValue.set(maxValue);
    }

    /**
     * Der minimal erlaubte Wert für das Feld (inklusive). Ein Wert von null bedeutet, dass es keine Begrenzung gibt.
     * 
     * @return Die minValue-Property.
     * @see #getMinValue()
     * @see #setMinValue(Number)
     */
    public final ObjectProperty<Number> minValueProperty() {
        return minValue;
    }

    /**
     * Holt den minimalen Wert.
     * 
     * @return Der minimal erlaubte Wert für das Feld (inklusive).
     * @see #minValueProperty()
     */
    public final Number getMinValue() {
        return minValue.get();
    }

    /**
     * Setzt den minimalen Wert.
     * 
     * @param minValue
     *            Der minimal erlaubte Wert für das Feld (inklusive).
     * @see #minValueProperty()
     */
    public final void setMinValue(final Number minValue) {
        this.minValue.set(minValue);
    }

    /**
     * Die Schrittweite, d.h. das Intervall zur Erhöhung bzw. Verringerung des Wertes, wenn die Buttons angeklickt werden.
     * 
     * @return Die stepWidth-Property.
     * @see #getStepWidth()
     * @see #setStepWidth(Number)
     */
    public final ObjectProperty<Number> stepWidthProperty() {
        return stepWidth;
    }

    /**
     * Holt die Schrittweite.
     * 
     * @return Die Schrittweite.
     * @see #stepWidthProperty()
     */
    public final Number getStepWidth() {
        return this.stepWidth.get();
    }

    /**
     * Setzt die Schrittweite.
     * 
     * @param stepWidth
     *            Die Schrittweite.
     * @see #stepWidthProperty()
     */
    public final void setStepWidth(final Number stepWidth) {
        this.stepWidth.setValue(stepWidth);
    }

    /**
     * Der Converter der vom Datenformat ins textuelle Darstellungsformat und zurück konvertiert.
     * 
     * @return Die numberStringConverter-Property.
     * @see #getNumberStringConverter()
     * @see #setNumberStringConverter(javafx.util.converter.StringConverter)
     */
    public final ObjectProperty<StringConverter<Number>> numberStringConverterProperty() {
        return numberStringConverter;
    }

    /**
     * Gibt den Converter zurück.
     * 
     * @return Der Converter der vom Datenformat ins textuelle Darstellungsformat und zurück konvertiert.
     * @see #numberStringConverterProperty()
     */
    public final StringConverter<Number> getNumberStringConverter() {
        return numberStringConverter.get();
    }

    /**
     * Setzt den Converter.
     * 
     * @param numberStringConverter
     *            Der Converter der vom Datenformat ins textuelle Darstellungsformat und zurück konvertiert.
     * @see #numberStringConverterProperty()
     */
    public final void setNumberStringConverter(final StringConverter<Number> numberStringConverter) {
        this.numberStringConverter.set(numberStringConverter);
    }

    /**
     * Dummy.
     * 
     * @return Dummy, gibt immer null zurück. Wird für Bean-Kompatibilität benötigt.
     */
    public final String getPattern() {
        return null;
    }

    /**
     * Setzt einen neuen {@link #setNumberStringConverter(NumberStringConverter)} mit dem gegebenen Pattern.
     * 
     * @param pattern
     *            Das Format in dem die Zahlen angezeigt werden sollen.
     */
    public final void setPattern(final String pattern) {
        setNumberStringConverter(new NumberStringConverter(pattern));
    }

    /**
     * Erhöht den Wert um das in {@link #stepWidthProperty()} festgelegte Intervall.
     */
    public void increment() {
        if (getStepWidth() != null && isFinite(getStepWidth().doubleValue())) {
            if (getValue() != null && isFinite(getValue().doubleValue())) {
                setValue(BigDecimal.valueOf(getValue().doubleValue()).add(BigDecimal.valueOf(getStepWidth().doubleValue())));
            } else {
                if (getMinValue() != null && isFinite(getMinValue().doubleValue())) {
                    setValue(BigDecimal.valueOf(getMinValue().doubleValue()).add(BigDecimal.valueOf(getStepWidth().doubleValue())));
                } else {
                    setValue(BigDecimal.valueOf(getStepWidth().doubleValue()));
                }
            }
        }
    }

    /**
     * Verringert den Wert um das in {@link #stepWidthProperty()} festgelegte Intervall.
     */
    public void decrement() {
        if (getStepWidth() != null && isFinite(getStepWidth().doubleValue())) {
            if (getValue() != null && isFinite(getValue().doubleValue())) {
                setValue(BigDecimal.valueOf(getValue().doubleValue()).subtract(BigDecimal.valueOf(getStepWidth().doubleValue())));
            } else {
                if (getMaxValue() != null && isFinite(getMaxValue().doubleValue())) {
                    setValue(BigDecimal.valueOf(getMaxValue().doubleValue()).subtract(BigDecimal.valueOf(getStepWidth().doubleValue())));
                } else {
                    setValue(BigDecimal.valueOf(getStepWidth().doubleValue()).multiply(new BigDecimal(-1)));
                }
            }
        }
    }

    /**
     * Prüft, ob ein Wert endlich ist.
     * 
     * @param doubleValue
     *            Der zu prüfende Wert.
     * @return True, wenn value endlich ist.
     */
    private boolean isFinite(double doubleValue) {
        return !Double.isInfinite(doubleValue) && !Double.isNaN(doubleValue);
    }

}
