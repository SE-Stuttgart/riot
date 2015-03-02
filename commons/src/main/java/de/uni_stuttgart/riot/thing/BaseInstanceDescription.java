package de.uni_stuttgart.riot.thing;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class to describe the structure of {@link BaseInstances}, that is of action and event instances. The class describes which additional
 * parameters are present on the class. For each parameter the name of the parameter (key) and its value type (value) is reported in a map.
 * Note that the fields in {@link BaseInstance} itself need to be present on all instances, of course, but are not reported separately.
 * 
 * @author Philipp Keck
 */
public class BaseInstanceDescription {

    private final Class<? extends BaseInstance> instanceType;
    private final Map<String, Class<?>> parameters;

    /**
     * Instantiates a new BaseInstanceDescription. Use {@link #create(Class)} to retrieve an instance.
     * 
     * @param instanceType
     *            The type being described.
     * @param parameters
     *            The parameters of the type.
     */
    @JsonCreator
    private BaseInstanceDescription(@JsonProperty("instanceType") Class<? extends BaseInstance> instanceType, @JsonProperty("parameters") Map<String, Class<?>> parameters) {
        this.instanceType = instanceType;
        this.parameters = parameters;
    }

    /**
     * Gets the instance type.
     * 
     * @return The type being described by this BaseInstanceDescription.
     */
    public Class<? extends BaseInstance> getInstanceType() {
        return instanceType;
    }

    /**
     * Gets the parameters of the type.
     * 
     * @return The parameters of the type (in addition to the ones in {@link BaseInstance}), where the key of the map is the name of the
     *         parameter and the value of the map is the expected value type of the parameter.
     */
    public Map<String, Class<?>> getParameters() {
        return parameters;
    }

    /**
     * Creates a new instance description.
     * 
     * @param instanceType
     *            The type to describe.
     * @return The instance description.
     */
    public static BaseInstanceDescription create(Class<? extends BaseInstance> instanceType) {
        Map<String, Class<?>> parameters = new HashMap<String, Class<?>>();
        Class<?> clazz = instanceType;
        while (clazz != BaseInstance.class) {
            // We detect the "parameters" by checking which fields are declared (privately) in the class.
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    parameters.put(field.getName(), field.getType());
                }
            }
            clazz = clazz.getSuperclass();
        }
        return new BaseInstanceDescription(instanceType, parameters);
    }

}
