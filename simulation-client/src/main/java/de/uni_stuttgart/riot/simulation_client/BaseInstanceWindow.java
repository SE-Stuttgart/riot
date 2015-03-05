package de.uni_stuttgart.riot.simulation_client;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.uni_stuttgart.riot.javafx.ConstantObservable;
import de.uni_stuttgart.riot.javafx.UIProducer;
import de.uni_stuttgart.riot.thing.BaseInstance;
import de.uni_stuttgart.riot.thing.BaseInstanceDescription;
import de.uni_stuttgart.riot.thing.InstanceParameterDescription;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A window for displaying existing action or event instances, or for allowing the user to create new ones.
 * 
 * @author Philipp Keck
 *
 * @param <I>
 *            The type of the instance.
 */
public class BaseInstanceWindow<I extends BaseInstance> extends Stage {

    private static final int OUTER_PADDING = 10;
    private static final int INNER_SPACING = 5;

    /**
     * The description of the instance to be created.
     */
    private final BaseInstanceDescription description;

    /**
     * The current values of the parameters for the new instance.
     */
    private final Map<String, ObservableValue<?>> parameterProperties = new HashMap<>();

    /**
     * Container for the buttons at the bottom of the window.
     */
    private final HBox buttonContainer = new HBox(INNER_SPACING);

    /**
     * If true, the producer methods will create new properties whenever they encounter a parameter that is not yet present in
     * {@link #parameterProperties}.
     */
    private final boolean mayCreateNewWritableProperties;

    /**
     * Creates a new window from the given description. The window allows the user to view an existing instance (readonly).
     * 
     * @param description
     *            The instance description.
     */
    private BaseInstanceWindow(BaseInstanceDescription description, I instance) {
        this.description = description;
        this.mayCreateNewWritableProperties = false;
        for (InstanceParameterDescription parameter : description.getParameters()) {
            parameterProperties.put(parameter.getName(), getConstantParameterValue(description.getInstanceType(), instance, parameter.getName(), parameter.getValueType()));
        }
        setScene(new Scene(produceContent()));
        getScene().getStylesheets().add("styles.css");
    }

    /**
     * Creates a constant readonly value by reading the current parameter value from a BaseInstance.
     * 
     * @param <T>
     *            The type of the value.
     * @param instanceType
     *            The type of the BaseInstance.
     * @param instance
     *            The BaseInstance to read from.
     * @param parameterName
     *            The name of the parameter, that is, the name of the field in the subclass of BaseInstance.
     * @param valueType
     *            The type of the value.
     * @return A constant observable containing the value.
     */
    private <T> ObservableValue<T> getConstantParameterValue(Class<? extends BaseInstance> instanceType, I instance, String parameterName, Class<T> valueType) {
        if (!instanceType.isInstance(instance)) {
            throw new IllegalArgumentException(instance + " is not an instance of " + instanceType);
        }

        Class<?> clazz = instanceType;
        while (clazz != BaseInstance.class) {
            try {
                Field field = clazz.getDeclaredField(parameterName);
                if (InstanceParameterDescription.getBoxedType(field.getType()) != valueType) {
                    throw new IllegalArgumentException("The paramter " + parameterName + " in " + instanceType + " is of type " + field.getType() + ", but expected type is " + valueType);
                }
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                ConstantObservable<T> result = new ConstantObservable<T>((T) field.get(instance));
                return result;
            } catch (NoSuchFieldException e) {
                // Try on the super class.
                clazz = clazz.getSuperclass();
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("The type " + instanceType + " does not have the parameter " + parameterName);
    }

    /**
     * Creates the window content.
     * 
     * @return The content of the window.
     */
    private Parent produceContent() {
        GridPane container = new GridPane();
        container.setHgap(INNER_SPACING);
        container.setVgap(INNER_SPACING);
        int i = 0;
        for (InstanceParameterDescription parameter : description.getParameters()) {
            Label label = new Label(parameter.getName());
            Control valueControl;
            if (parameterProperties.containsKey(parameter.getName())) {
                valueControl = produceControlForExistingObservable(parameter.getName(), parameter.getValueType(), parameter.getUiHint());
            } else if (mayCreateNewWritableProperties) {
                valueControl = produceControlWithProperty(parameter.getValueType(), parameter.getUiHint());
            } else {
                throw new IllegalStateException("The description contains a parameter that is not present in the given parameter properties values!");
            }

            container.addRow(i, label, valueControl);
            GridPane.setHalignment(label, HPos.RIGHT);
            GridPane.setHgrow(valueControl, Priority.ALWAYS);
            i++;
        }

        VBox vbox = new VBox(container, buttonContainer);
        vbox.setPadding(new Insets(OUTER_PADDING));
        return vbox;
    }

    /**
     * Produces a property to hold the current value of a parameter and constructs a UI for the parameter with respect to the given UI hint.
     * 
     * @param valueType
     *            The type of the parameter value.
     * @param uiHint
     *            The UI hint for the parameter.
     * @return The control for the parameter.
     */
    private <T> Control produceControlWithProperty(Class<T> valueType, UIHint uiHint) {
        Property<T> property = new SimpleObjectProperty<T>();
        return UIProducer.produceControl(property, uiHint, valueType);
    }

    /**
     * Constructs the UI for a parameter that already has a value which exists in {@link #parameterProperties}.. The UI will be bound to the
     * given observable and reflect its content. It will not be editable. The UI will follow the UI hint.
     * 
     * @param parameterName
     *            The key of the observable in {@link #parameterProperties}.
     * @param valueType
     *            The type of the parameter value.
     * @param uiHint
     *            The UI hint for the parameter.
     * @return The control for the readonly parameter.
     */
    @SuppressWarnings("unchecked")
    private <T> Control produceControlForExistingObservable(String parameterName, Class<T> valueType, UIHint uiHint) {
        return UIProducer.produceControl((ObservableValue<T>) parameterProperties.get(parameterName), uiHint, valueType);
    }

    /**
     * Displays the given instance (readonly) in a window. This will return <tt>null</tt> and do nothing if the instance has no parameters.
     * 
     * @param <I>
     *            The type of the instance.
     * @param instance
     *            The instance.
     * @return The window displaying the instance. It is already opened non-modally. Or <tt>null</tt> if the instance has no parameters.
     */
    @SuppressWarnings("unchecked")
    public static <I extends BaseInstance> BaseInstanceWindow<I> displayInstance(I instance) {
        return displayInstance((Class<I>) instance.getClass(), instance);
    }

    /**
     * Displays the given instance (readonly) in a window. This will return <tt>null</tt> and do nothing if the instance has no parameters.
     * 
     * 
     * @param <I>
     *            The type of the instance.
     * @param instanceType
     *            The type of the instance.
     * @param instance
     *            The instance.
     * @return The window displaying the instance. It is already opened non-modally. Or <tt>null</tt> if the instance has no parameters.
     */
    public static <I extends BaseInstance> BaseInstanceWindow<I> displayInstance(Class<? extends BaseInstance> instanceType, I instance) {
        // Prepare the window.
        BaseInstanceDescription description = BaseInstanceDescription.create(instanceType);
        if (description.getParameters().isEmpty()) {
            return null;
        }
        BaseInstanceWindow<I> window = new BaseInstanceWindow<I>(description, instance);
        window.setTitle(instance.getName() + " - " + description.getInstanceType().getSimpleName());

        // Show the window.
        window.showAndWait();
        return window;
    }

}
