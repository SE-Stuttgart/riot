package de.uni_stuttgart.riot.thing;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class to describe the structure of {@link BaseInstances}, that is of action and event instances. The class describes which additional
 * parameters are present on the class. Note that the fields in {@link BaseInstance} itself need to be present on all instances, of course,
 * but are not reported separately.
 * 
 * @author Philipp Keck
 */
public class BaseInstanceDescription {

    private final Class<? extends BaseInstance> instanceType;
    private final List<InstanceParameterDescription> parameters;

    /**
     * Instantiates a new BaseInstanceDescription. Use {@link #create(Class)} to retrieve an instance.
     * 
     * @param instanceType
     *            The type being described.
     * @param parameters
     *            The parameters of the type.
     */
    @JsonCreator
    private BaseInstanceDescription(@JsonProperty("instanceType") Class<? extends BaseInstance> instanceType, @JsonProperty("parameters") List<InstanceParameterDescription> parameters) {
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
     * @return The parameters of the type.
     */
    public List<InstanceParameterDescription> getParameters() {
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
        List<InstanceParameterDescription> parameters = new ArrayList<InstanceParameterDescription>();
        Class<?> clazz = instanceType;
        while (clazz != BaseInstance.class) {
            // We detect the "parameters" by checking which fields are declared (privately) in the class.
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    parameters.add(InstanceParameterDescription.create(field));
                }
            }
            clazz = clazz.getSuperclass();
        }
        return new BaseInstanceDescription(instanceType, parameters);
    }

}
