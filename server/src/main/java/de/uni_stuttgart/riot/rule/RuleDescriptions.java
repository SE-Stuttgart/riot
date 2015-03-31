package de.uni_stuttgart.riot.rule;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ClassFilter;

import org.apache.commons.lang3.reflect.TypeUtils;

import de.uni_stuttgart.riot.thing.ParameterDescription;

/**
 * Helper class for creating {@link RuleDescription}s.
 * 
 * @author Philipp Keck
 */
public abstract class RuleDescriptions {

    private static final Map<Class<? extends Rule>, RuleDescription> DESCRIPTIONS = new HashMap<>();
    private static boolean discoveredClasspath = false;

    /**
     * Cannot instantiate.
     */
    private RuleDescriptions() {
    }

    /**
     * Parses the given type as a subclass of {@link Rule} and retrieves the corresponding rule description.
     * 
     * @param type
     *            The rule type to describe (fully qualified class name). Possible errors are {@link ClassNotFoundException} and
     *            {@link ClassCastException}.
     * @return The rule description.
     * @throws ClassNotFoundException
     *             When the given type does not exist.
     */
    public static RuleDescription get(String type) throws ClassNotFoundException {
        return get(Class.forName(type).asSubclass(Rule.class));
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

                // The parameter types need to be unwrapped, i.e. RuleParameter<X> becomes X.
                Type fieldType = field.getGenericType();
                if (!TypeUtils.isAssignable(fieldType, RuleParameter.class)) {
                    continue;
                } else if (TypeUtils.isAssignable(fieldType, ThingParameter.class)) {
                    fieldType = TypeUtils.getTypeArguments(fieldType, ThingParameter.class).get(ThingParameter.class.getTypeParameters()[0]);
                } else if (TypeUtils.isAssignable(fieldType, ReferenceParameter.class)) {
                    fieldType = TypeUtils.getTypeArguments(fieldType, ReferenceParameter.class).get(ReferenceParameter.class.getTypeParameters()[0]);
                } else {
                    fieldType = TypeUtils.getTypeArguments(fieldType, RuleParameter.class).get(RuleParameter.class.getTypeParameters()[0]);
                }
                if (fieldType == null) {
                    throw new IllegalArgumentException("Failed to retrieve type of rule parameter field " + field);
                }
                parameters.add(ParameterDescription.create(field, fieldType));
            }
            clazz = clazz.getSuperclass();
        }
        return new RuleDescription(type.getName(), parameters);
    }

    /**
     * Gets all known rule descriptions.
     * 
     * @param searchClassPath
     *            If true, the class path will be searched for more rules that could be used.
     * @return A collection of all known rule descriptions.
     */
    public static Collection<RuleDescription> getAll(boolean searchClassPath) {
        if (searchClassPath && !discoveredClasspath) {
            discoverRulesOnClassPath();
        }
        return DESCRIPTIONS.values();
    }

    /**
     * Discovers all {@link Rule} classes on the class path and creates descriptions for them.
     */
    private static void discoverRulesOnClassPath() {
        discoveredClasspath = true;
        for (Class<?> clazz : CPScanner.scanClasses(new ClassFilter().classOnly().packageName("de.uni_stuttgart.riot.*").superClass(Rule.class))) {
            if (clazz.getPackage().getName().startsWith("de.uni_stuttgart.riot.rule.test")) {
                // Eclipse-GlassFish plugin will also deploy src/test/java, so we need to exclude this here.
                continue;
            }
            get(clazz.asSubclass(Rule.class));
        }
    }

}
