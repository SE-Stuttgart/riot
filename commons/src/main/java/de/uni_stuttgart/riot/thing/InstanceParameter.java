package de.uni_stuttgart.riot.thing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * This annotation can be used to annotate fields in subclasses of {@link BaseInstance} to tell UI clients which UI to use for this
 * parameter.
 * 
 * @author Philipp Keck
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface InstanceParameter {

    /**
     * The type of UI to be used.
     */
    Class<? extends UIHint> ui() default NoHint.class;

    /**
     * The minimum value allowed for the parameter.
     */
    double min() default Double.MIN_VALUE;

    /**
     * The maximum value allowed for the parameter.
     */
    double max() default Double.MAX_VALUE;

    /**
     * Dummy class for the default value of {@link InstanceParameter#ui()}.
     */
    public static class NoHint extends UIHint {
    }

}
