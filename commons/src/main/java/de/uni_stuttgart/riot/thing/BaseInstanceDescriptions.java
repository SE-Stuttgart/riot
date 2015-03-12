package de.uni_stuttgart.riot.thing;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for creating {@link BaseInstanceDescription}s.
 * 
 * @author Philipp Keck
 */
public abstract class BaseInstanceDescriptions {

    private static final Map<Class<? extends BaseInstance>, BaseInstanceDescription> DESCRIPTIONS = new HashMap<Class<? extends BaseInstance>, BaseInstanceDescription>();

    /**
     * Cannot instantiate.
     */
    private BaseInstanceDescriptions() {
    }

    /**
     * Gets an instance description.
     * 
     * @param instanceType
     *            The type to describe.
     * @return The instance description.
     */
    public static BaseInstanceDescription get(Class<? extends BaseInstance> instanceType) {
        if (instanceType == null) {
            throw new IllegalArgumentException("instanceType must not be null!");
        }

        BaseInstanceDescription result = DESCRIPTIONS.get(instanceType);
        if (result == null) {
            result = create(instanceType);
            DESCRIPTIONS.put(instanceType, result);
        }
        return result;
    }

    /**
     * Creates a new instance description.
     * 
     * @param instanceType
     *            The type to describe.
     * @return The instance description.
     */
    private static BaseInstanceDescription create(Class<? extends BaseInstance> instanceType) {
        List<ParameterDescription> parameters = new ArrayList<ParameterDescription>();
        Class<?> clazz = instanceType;
        while (clazz != BaseInstance.class) {
            // We detect the "parameters" by checking which fields are declared (privately) in the class.
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    parameters.add(ParameterDescription.create(field, field.getGenericType()));
                }
            }
            clazz = clazz.getSuperclass();
        }
        return new BaseInstanceDescription(instanceType, parameters);
    }

    /**
     * Creates a map of all parameters (as keys) and their values in this instance. Note that the map may be empty and does not contain the
     * base parameters declared in {@link BaseInstance}. Do not call this method repeatedly, it may be slow.
     * 
     * @param instance
     *            The instance to get the values from.
     * @return The parameters and their values.
     */
    public static Map<String, Object> getParameterValues(BaseInstance instance) {
        if (instance == null) {
            throw new IllegalArgumentException("instance may not be null!");
        }

        Map<String, Object> result = new HashMap<String, Object>();
        Class<?> clazz = instance.getClass();
        while (clazz != BaseInstance.class) {
            // We detect the "parameters" by checking which fields are declared (privately) in the class.
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    try {
                        field.setAccessible(true);
                        result.put(field.getName(), field.get(instance));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }

}
