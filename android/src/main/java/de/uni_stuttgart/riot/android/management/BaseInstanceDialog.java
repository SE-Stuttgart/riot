package de.uni_stuttgart.riot.android.management;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.uni_stuttgart.riot.android.Callback;
import de.uni_stuttgart.riot.android.ui.PropertyView;
import de.uni_stuttgart.riot.android.ui.UIProducer;
import de.uni_stuttgart.riot.thing.BaseInstance;
import de.uni_stuttgart.riot.thing.BaseInstanceDescription;
import de.uni_stuttgart.riot.thing.BaseInstanceDescriptions;
import de.uni_stuttgart.riot.thing.ParameterDescription;

/**
 * An Android dialog for displaying existing action or event instances, or for allowing the user to create new ones.
 * 
 * @author Philipp Keck
 *
 * @param <I>
 *            The type of the instance.
 */
public class BaseInstanceDialog<I extends BaseInstance> extends Dialog {

    /**
     * The description of the instance to be created.
     */
    private final BaseInstanceDescription description;

    /**
     * The views for the parameters.
     */
    private final Map<String, PropertyView<?>> parameterViews = new HashMap<String, PropertyView<?>>();

    /**
     * The OK button
     */
    private final Button okButton;

    /**
     * Creates a new dialog.
     * 
     * @param context
     *            The Android context.
     * @param description
     *            The description of the instance to be created.
     */
    public BaseInstanceDialog(Context context, BaseInstanceDescription description) {
        super(context);
        this.description = description;
        this.okButton = new Button(context);
        this.okButton.setText(android.R.string.ok);
        super.setContentView(produceContent(context));
    }

    /**
     * Creates a new dialog and displays the given instance in read-only mode.
     * 
     * @param context
     *            The Android context.
     * @param description
     *            The description of the instance to be created.
     * @param instance
     *            The instance to be displayed.
     */
    public BaseInstanceDialog(Context context, BaseInstanceDescription description, I instance) {
        this(context, description);
        if (!description.getInstanceType().isInstance(instance)) {
            throw new IllegalArgumentException(instance + " is not an instance of " + description.getInstanceType());
        }
        Map<String, Object> parameterValues = BaseInstanceDescriptions.getParameterValues(instance);
        for (Map.Entry<String, Object> parameter : parameterValues.entrySet()) {
            setViewValue(parameterViews.get(parameter.getKey()), parameter.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private static <V> void setViewValue(PropertyView<V> view, Object value) {
        // Helper method to bind the V type.
        view.setValue((V) value);
    }

    /**
     * Creates a view that contains all parameters and a corresponding control as a list.
     * 
     * @return The view.
     */
    private View produceContent(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        for (ParameterDescription parameter : description.getParameters()) {
            TextView label = new TextView(context);
            label.setText(parameter.getName());
            layout.addView(label);

            PropertyView<?> view = UIProducer.producePropertyView(context, parameter.getUiHint(), parameter.getValueType(), parameter.getName());
            layout.addView(view.toView());
            parameterViews.put(parameter.getName(), view);
        }
        layout.addView(okButton);
        return layout;
    }

    /**
     * Displays the given instance (readonly) in an Android dialog. This will do nothing if the instance has no parameters.
     * 
     * @param <I>
     *            The type of the instance.
     * @param context
     *            The Android context.
     * @param instance
     *            The instance.
     * @return The dialog or <tt>null</tt> if none needed to be displayed.
     */
    @SuppressWarnings("unchecked")
    public static <I extends BaseInstance> BaseInstanceDialog<I> displayInstance(Context context, I instance) {
        return displayInstance(context, (Class<I>) instance.getClass(), instance);
    }

    /**
     * Displays the given instance (readonly) in an Android dialog. This will do nothing if the instance has no parameters.
     * 
     * 
     * @param <I>
     *            The type of the instance.
     * @param context
     *            The Android context.
     * @param instanceType
     *            The type of the instance.
     * @param instance
     *            The instance.
     * @return The dialog or <tt>null</tt> if none needed to be displayed.
     */
    public static <I extends BaseInstance> BaseInstanceDialog<I> displayInstance(Context context, Class<? extends BaseInstance> instanceType, I instance) {
        // Prepare the dialog.
        BaseInstanceDescription description = BaseInstanceDescriptions.get(instanceType);
        if (description.getParameters().isEmpty()) {
            return null;
        }
        final BaseInstanceDialog<I> dialog = new BaseInstanceDialog<I>(context, description, instance);
        dialog.setTitle(instance.getName() + " - " + description.getInstanceType().getSimpleName());
        dialog.okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    /**
     * Shows an Android {@link Dialog} with fields for each instance parameter and empty initial values for the user to create a new
     * instance. No window is shown if the specified type does not have any parameters, in which case a new instance will be returned
     * immediately.
     * 
     * @param <I>
     *            The instance type to be created.
     * @param context
     *            The Android context.
     * @param instanceType
     *            The instance type to be created.
     * @param thingId
     *            The id of the thing to create this instance for.
     * @param name
     *            The name of the event/action to create this instance for.
     * @param objectMapper
     *            The JSON object mapper to be used (this is needed internally for creating the instance dynamically).
     * @param callback
     *            The callback that will receive the newly created instance.
     * @return The dialog or <tt>null</tt> if none needed to be displayed.
     */
    public static <I extends BaseInstance> BaseInstanceDialog<I> enterNewInstance(Context context, final Class<I> instanceType, final long thingId, final String name, final ObjectMapper objectMapper, final Callback<? super I> callback) {
        if (Modifier.isAbstract(instanceType.getModifiers())) {
            throw new IllegalArgumentException("Cannot create new instances of abstract type " + instanceType);
        }

        // We construct a JSON node because this is the most reliable method to instantiate a generic instance.
        final ObjectNode node = objectMapper.createObjectNode();
        node.put("thingId", thingId);
        node.put("name", name);

        // Only show the window if there are any parameters.
        BaseInstanceDescription description = BaseInstanceDescriptions.get(instanceType);
        if (description.getParameters().isEmpty()) {
            // Nothing to do, just create the instance and call the callback.
            I instance;
            try {
                Constructor<I> constructor = instanceType.getConstructor(JsonNode.class);
                node.put("time", System.currentTimeMillis());
                instance = constructor.newInstance(node);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(instanceType + " must specify a single-argument constructor that accepts a JsonNode!");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            callback.callback(instance);
            return null;

        } else {

            // Prepare the dialog.
            final BaseInstanceDialog<I> dialog = new BaseInstanceDialog<I>(context, description);
            dialog.setTitle(name);

            dialog.okButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    for (PropertyView<?> parameterView : dialog.parameterViews.values()) {
                        if (parameterView.getValue() == null) {
                            return; // User must first fill in all fields.
                        }
                    }

                    dialog.dismiss();

                    // Write the results to the JSON node.
                    for (Map.Entry<String, PropertyView<?>> parameter : dialog.parameterViews.entrySet()) {
                        Object value = parameter.getValue().getValue();
                        node.put(parameter.getKey(), (JsonNode) objectMapper.valueToTree(value));
                    }
                    // Call the instance constructor with the JSON node.
                    I instance;
                    try {
                        Constructor<I> constructor = instanceType.getConstructor(JsonNode.class);
                        node.put("time", System.currentTimeMillis());
                        instance = constructor.newInstance(node);
                    } catch (NoSuchMethodException e) {
                        throw new IllegalArgumentException(instanceType + " must specify a single-argument constructor that accepts a JsonNode!");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    callback.callback(instance);
                }
            });

            dialog.show();
            return dialog;
        }

    }
}
