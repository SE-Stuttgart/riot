package de.uni_stuttgart.riot.thing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * This annotation can be used to annotate fields in subclasses of {@link BaseInstance} to tell UI clients which UI to use for this
 * parameter.
 * 
 * @author Philipp Keck
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {

    /**
     * The groupID.
     */
    int group() default -1;

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
     * If the parameter value is a thing reference, this specifies the permissions on the thing that are required to use the parameter.
     */
    ThingPermission[] requires() default { ThingPermission.READ, ThingPermission.CONTROL };

    /**
     * Dummy class for the default value of {@link Parameter#ui()}.
     */
    public static class NoHint extends UIHint {
    }

}
