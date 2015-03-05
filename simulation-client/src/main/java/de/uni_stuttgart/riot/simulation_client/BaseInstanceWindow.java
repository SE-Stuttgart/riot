package de.uni_stuttgart.riot.simulation_client;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
     * Creates a new window from the given description. The window allows the user to create a new instance.
     * 
     * @param description
     *            The instance description.
     */
    private BaseInstanceWindow(BaseInstanceDescription description) {
        this.description = description;
        this.mayCreateNewWritableProperties = true;
        setScene(new Scene(produceContent()));
        getScene().getStylesheets().add("styles.css");
    }

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
                valueControl = produceControlWithProperty(parameter.getName(), parameter.getValueType(), parameter.getUiHint());
            } else {
                throw new IllegalStateException("The description contains a parameter that is not present in the given parameter properties values!");
            }

            container.addRow(i, label, valueControl);
            GridPane.setHalignment(label, HPos.RIGHT);
            GridPane.setHgrow(valueControl, Priority.ALWAYS);
            valueControl.setMaxWidth(Double.MAX_VALUE);
            i++;
        }

        VBox vbox = new VBox(OUTER_PADDING, container, buttonContainer);
        vbox.setPadding(new Insets(OUTER_PADDING));
        return vbox;
    }

    /**
     * Produces a property to hold the current value of a parameter and constructs a UI for the parameter with respect to the given UI hint.
     * 
     * @param propertyName
     *            The name of the property.
     * @param valueType
     *            The type of the parameter value.
     * @param uiHint
     *            The UI hint for the parameter.
     * @return The control for the parameter.
     */
    private <T> Control produceControlWithProperty(String propertyName, Class<T> valueType, UIHint uiHint) {
        Property<T> property = new SimpleObjectProperty<T>();
        parameterProperties.put(propertyName, property);
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

    /**
     * Shows an editable window with empty initial values for the user to create a new instance. No window is shown if the specified type
     * does not have any parameters, in which case a new instance will be returned immediately.
     * 
     * @param <I>
     *            The instance type to be created.
     * @param instanceType
     *            The instance type to be created.
     * @param thingId
     *            The id of the thing to create this instance for.
     * @param name
     *            The name of the event/action to create this instance for.
     * @param objectMapper
     *            The JSON object mapper to be used (this is needed internally for creating the instance dynamically).
     * @return The created instance or <tt>null</tt>, if the user aborted.
     */
    public static <I extends BaseInstance> I enterNewInstance(Class<I> instanceType, long thingId, String name, ObjectMapper objectMapper) {
        if (Modifier.isAbstract(instanceType.getModifiers())) {
            throw new IllegalArgumentException("Cannot create new instances of abstract type " + instanceType);
        }

        // We construct a JSON node because this is the most reliable method to instantiate a generic instance.
        ObjectNode node = objectMapper.createObjectNode();
        node.put("thingId", thingId);
        node.put("name", name);

        // Only show the window if there are any parameters.
        BaseInstanceDescription description = BaseInstanceDescription.create(instanceType);
        if (!description.getParameters().isEmpty()) {

            // Prepare the window.
            BaseInstanceWindow<I> window = new BaseInstanceWindow<>(description);
            window.setTitle(name + " - New" + description.getInstanceType().getSimpleName());

            AtomicBoolean result = new AtomicBoolean(false);
            Button okButton = new Button("OK");
            okButton.setOnAction((actionEvent) -> {
                for (ObservableValue<?> value : window.parameterProperties.values()) {
                    if (value.getValue() == null) {
                        return; // User must first fill in all fields.
                    }
                }

                result.set(true);
                window.close();
            });
            Button cancelButton = new Button("Cancel");
            cancelButton.setOnAction((actionEvent) -> {
                window.close();
            });
            okButton.setMaxWidth(Double.MAX_VALUE);
            cancelButton.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(okButton, Priority.ALWAYS);
            HBox.setHgrow(cancelButton, Priority.ALWAYS);
            window.buttonContainer.getChildren().addAll(okButton, cancelButton);

            // Show the window.
            window.showAndWait();
            if (!result.get()) {
                return null;
            }

            // Write the results to the JSON node.
            for (Map.Entry<String, ObservableValue<?>> parameterValue : window.parameterProperties.entrySet()) {
                Object value = parameterValue.getValue().getValue();
                node.put(parameterValue.getKey(), (JsonNode) objectMapper.valueToTree(value));
            }
        }

        // Call the instance constructor with the JSON node.
        try {
            Constructor<I> constructor = instanceType.getConstructor(JsonNode.class);
            node.put("time", System.currentTimeMillis());
            return constructor.newInstance(node);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(instanceType + " must specify a single-argument constructor that accepts a JsonNode!");
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
