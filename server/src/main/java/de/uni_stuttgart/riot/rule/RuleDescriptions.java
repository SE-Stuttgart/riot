package de.uni_stuttgart.riot.rule;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;

import de.uni_stuttgart.riot.thing.ParameterDescription;

/**
 * Helper class for creating {@link RuleDescription}s.
 * 
 * @author Philipp Keck
 */
public abstract class RuleDescriptions {

    private static final Map<Class<? extends Rule>, RuleDescription> DESCRIPTIONS = new HashMap<>();

    /**
     * Cannot instantiate.
     */
    private RuleDescriptions() {
    }

    /**
     * Gets a rule description.
     * 
     * @param type
     *            The rule type to describe.
     * @return The rule description.
     */
    public static RuleDescription get(Class<? extends Rule> type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null!");
        }
        RuleDescription result = DESCRIPTIONS.get(type);
        if (result == null) {
            result = create(type);
            DESCRIPTIONS.put(type, result);
        }
        return result;
    }

    /**
     * Creates a new rule description.
     * 
     * @param type
     *            The rule type to describe.
     * @return The rule description.
     */
    private static RuleDescription create(Class<? extends Rule> type) {
        List<ParameterDescription> parameters = new ArrayList<ParameterDescription>();
        Class<?> clazz = type;
        while (clazz != Rule.class) {
            // We detect the "parameters" by checking which RuleParameter fields are declared in the class.
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                Type fieldType = field.getType();
                if (!TypeUtils.isAssignable(fieldType, RuleParameter.class)) {
                    continue;
                }
                fieldType = TypeUtils.getTypeArguments(fieldType, RuleParameter.class).get(RuleParameter.class.getTypeParameters()[0]);
                parameters.add(ParameterDescription.create(field, fieldType));
            }
            clazz = clazz.getSuperclass();
        }
        return new RuleDescription(type.getName(), parameters);
    }

}
