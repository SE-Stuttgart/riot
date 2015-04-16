package de.uni_stuttgart.riot.javafx;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;
import de.uni_stuttgart.riot.simulation_client.SimulatedThingBehavior;
import de.uni_stuttgart.riot.simulation_client.ThingPropertyInternalBinding;
import de.uni_stuttgart.riot.thing.ui.UIHint;
import de.uni_stuttgart.riot.thing.ui.UIHint.EnumDropDown;
import de.uni_stuttgart.riot.thing.ui.UIHint.EditNumber;
import de.uni_stuttgart.riot.thing.ui.UIHint.EditText;
import de.uni_stuttgart.riot.thing.ui.UIHint.FractionalSlider;
import de.uni_stuttgart.riot.thing.ui.UIHint.IntegralSlider;
import de.uni_stuttgart.riot.thing.ui.UIHint.PercentageSlider;
import de.uni_stuttgart.riot.thing.ui.UIHint.ToggleButton;

/**
 * Helper class for producing JavaFX UI elements for Thing properties.
 * 
 * @author Philipp Keck
 */
// CHECKSTYLE: CyclomaticComplexity OFF
// CHECKSTYLE: JavaNCSS OFF
public abstract class UIProducer {

    /**
     * Cannot instantiate.
     */
    private UIProducer() {
    }

    /**
     * Produces a control that uses the changePropertyValue function in the {@link SimulatedThingBehavior} to change property values, so it
     * sets the values internally instead of firing set actions.
     * 
     * @param <T>
     *            The type of the property values.
     * @param thingProperty
     *            The property to produce the control for.
     * @param behavior
     *            The behavior of the thing (used for manipulating the property values).
     * @return A control bound to the given property.
     */
    public static <T> Control produceInternalControl(de.uni_stuttgart.riot.thing.Property<T> thingProperty, SimulatedThingBehavior behavior) {
        return produceControl(ThingPropertyInternalBinding.create(thingProperty, behavior), thingProperty.getUiHint(), thingProperty.getValueType());
    }

    /**
     * Creates a control for the given JavaFX Observable (or property), respecting the UI hint.
     * 
     * @param <T>
     *            The type of the property values.
     * @param property
     *            The property. If this is actually an instance of {@link Property}, the resulting control will be editable.
     * @param hint
     *            The UI hint.
     * @param valueType
     *            The type of the property values.
     * @return The control.
     */
    @SuppressWarnings("unchecked")
    public static <T> Control produceControl(ObservableValue<T> property, UIHint hint, Class<T> valueType) {
        if (hint instanceof IntegralSlider) {
            IntegralSlider uiHint = (IntegralSlider) hint;
            if (valueType != Integer.class && valueType != Long.class) {
                throw new IllegalArgumentException("IntegralSlider can only be used with Integer or Long, not " + valueType);
            } else if (property instanceof Property) {
                return produceIntegralSlider((Property<Number>) property, uiHint.min, uiHint.max);
            } else {
                return produceProgressBar((ObservableValue<Number>) property, uiHint.min, uiHint.max);
            }

        } else if (hint instanceof FractionalSlider) {
            FractionalSlider uiHint = (FractionalSlider) hint;
            if (valueType != Float.class && valueType != Double.class) {
                throw new IllegalArgumentException("FractionalSlider can only be used with Float or Double, not " + valueType);
            } else if (property instanceof Property) {
                return produceFractionalSlider((Property<Number>) property, uiHint.min, uiHint.max);
            } else {
                return produceProgressBar((ObservableValue<Number>) property, uiHint.min, uiHint.max);
            }

        } else if (hint instanceof PercentageSlider) {
            if (valueType != Float.class && valueType != Double.class) {
                throw new IllegalArgumentException("PercentageSlider can only be used with Float or Double, not " + valueType);
            } else if (property instanceof Property) {
                return produceFractionalSlider((Property<Number>) property, 0.0, 1.0);
            } else {
                return produceProgressBar((ObservableValue<Number>) property, 0.0, 1.0);
            }

        } else if (hint instanceof ToggleButton) {
            if (valueType != Boolean.class) {
                throw new IllegalArgumentException("Cannot use ToggleButton for type " + valueType);
            }
            return produceCheckBox((ObservableValue<Boolean>) property);

        } else if (hint instanceof EditText) {
            if (valueType != String.class) {
                throw new IllegalArgumentException("Cannot use EditText for type " + valueType);
            }
            return produceTextField((ObservableValue<String>) property);

        } else if (hint instanceof EditNumber) {
            if (!Number.class.isAssignableFrom(valueType)) {
                throw new IllegalArgumentException("Cannot use EditNumber for type " + valueType);
            } else if (valueType == Float.class || valueType == Double.class || valueType == BigDecimal.class) {
                return produceNumberSpinner((ObservableValue<Number>) property, ((EditNumber) hint).decimalPlaces);
            } else {
                return produceNumberSpinner((ObservableValue<Number>) property, 0);
            }

        } else if (hint instanceof EnumDropDown) {
            if (!Enum.class.isAssignableFrom(valueType)) {
                throw new IllegalArgumentException("DropDown can only be used for enums, not for " + valueType);
            }
            @SuppressWarnings("rawtypes")
            ObservableValue typedProperty = (ObservableValue) property;
            @SuppressWarnings("rawtypes")
            Class rawClass = valueType;
            return produceChoiceBox(typedProperty, rawClass);

        } else {
            Label valueDisplay = new Label(Objects.toString(property.getValue()));
            valueDisplay.textProperty().bind(Bindings.convert(property));
            return valueDisplay;
        }
    }

    /**
     * Produces a JavaFX slider for an integral Number property.
     * 
     * @param property
     *            The property.
     * @param min
     *            The minimum value for the slider.
     * @param max
     *            The maximum value for the slider.
     * @return The slider.
     */
    private static Slider produceIntegralSlider(Property<Number> property, long min, long max) {
        Slider slider = new Slider();
        slider.setMinorTickCount(0);
        slider.setMajorTickUnit(1.0);
        slider.setSnapToTicks(true);
        slider.setMin(min);
        slider.setMax(max);
        if (property.getValue() != null) {
            slider.setValue(property.getValue().doubleValue());
        }
        slider.valueProperty().bindBidirectional(property);
        addTooltip(slider);
        return slider;
    }

    /**
     * Produces a JavaFX slider for a fractional Number property.
     * 
     * @param property
     *            The property.
     * @param min
     *            The minimum value for the slider.
     * @param max
     *            The maximum value for the slider.
     * @return The slider.
     */
    private static Slider produceFractionalSlider(Property<Number> property, double min, double max) {
        Slider slider = new Slider();
        slider.setMin(min);
        slider.setMax(max);
        if (property.getValue() != null) {
            slider.setValue(property.getValue().doubleValue());
        }
        slider.valueProperty().bindBidirectional(property);
        addTooltip(slider);
        return slider;
    }

    private static void addTooltip(Slider slider) {
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(slider.valueProperty().asString());
        slider.setTooltip(tooltip);
        slider.setOnMousePressed(event -> {
            Scene scene = slider.getScene();
            Window window = scene.getWindow();
            tooltip.show(slider, event.getSceneX() + scene.getX() + window.getX(), event.getSceneY() + scene.getY() + window.getY());
            tooltip.setAutoHide(false);
        });
        slider.setOnMouseReleased(event -> {
            tooltip.hide();
            tooltip.setAutoHide(true);
        });
    }

    /**
     * Creates a progress bar that displays the value of the given property.
     * 
     * @param property
     *            The property.
     * @param min
     *            The minimum value (will be at 0% of the progress bar).
     * @param max
     *            The maximum value (will be at 100% of the progress bar).
     * @return The progress bar.
     */
    private static ProgressBar produceProgressBar(ObservableValue<Number> property, double min, double max) {
        ProgressBar progressBar = new ProgressBar();
        DoubleExpression thingValue = DoubleBinding.doubleExpression(property);
        progressBar.progressProperty().bind(thingValue.subtract(min).divide(max - min));
        return progressBar;
    }

    /**
     * Creates a JavaFX checkbox for the given property.
     * 
     * @param property
     *            The property.
     * @return The toggle button.
     */
    private static CheckBox produceCheckBox(ObservableValue<Boolean> property) {
        CheckBox checkBox = new CheckBox();
        if (property.getValue() != null) {
            checkBox.setSelected(property.getValue());
        }
        if (property instanceof Property) {
            checkBox.selectedProperty().bindBidirectional((Property<Boolean>) property);
        } else {
            checkBox.selectedProperty().bind(property);
            checkBox.setDisable(true);
        }
        return checkBox;
    }

    /**
     * Produces a JavaFX text field for a String property.
     * 
     * @param property
     *            The thing property.
     * @return The text field.
     */
    private static TextField produceTextField(ObservableValue<String> property) {
        TextField textField = new TextField();
        if (property.getValue() != null) {
            textField.setText(property.getValue());
        }
        if (property instanceof Property) {
            textField.textProperty().bindBidirectional((Property<String>) property);
        } else {
            textField.textProperty().bind(property);
            textField.setEditable(false);
        }
        return textField;
    }

    /**
     * Produces a JavaFX number spinner for a Number property.
     * 
     * @param property
     *            The thing property.
     * @param decimalPlaces
     *            The number of decimal places to be displayed.
     * @return The number spinner.
     */
    private static NumberSpinner produceNumberSpinner(ObservableValue<Number> property, int decimalPlaces) {
        NumberSpinner spinner = new NumberSpinner();
        NumberFormat numberFormat;
        if (decimalPlaces < 1) {
            numberFormat = NumberFormat.getIntegerInstance();
        } else {
            numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(decimalPlaces);
        }
        spinner.setNumberStringConverter(new NumberStringConverter(numberFormat));

        if (property.getValue() != null) {
            spinner.setValue(property.getValue());
        }
        if (property instanceof Property) {
            spinner.valueProperty().bindBidirectional((Property<Number>) property);
        } else {
            spinner.valueProperty().bind(property);
            spinner.setEditable(false);
        }
        return spinner;
    }

    /**
     * Produces a JavaFX choice box for a enum property.
     * 
     * @param property
     *            The JavaFX property.
     * @return The choice box.
     */
    private static <E extends Enum<E>> ChoiceBox<E> produceChoiceBox(ObservableValue<E> property, Class<E> valueType) {
        List<E> items = Arrays.asList(valueType.getEnumConstants());
        ChoiceBox<E> choiceBox = new ChoiceBox<E>(FXCollections.observableList(items));
        if (property.getValue() != null) {
            choiceBox.setValue(property.getValue());
        }
        if (property instanceof Property) {
            choiceBox.valueProperty().bindBidirectional((Property<E>) property);
        } else {
            choiceBox.valueProperty().bind(property);
            choiceBox.setDisable(true);
        }
        return choiceBox;
    }

}
