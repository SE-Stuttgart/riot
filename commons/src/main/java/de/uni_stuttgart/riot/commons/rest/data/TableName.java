package de.uni_stuttgart.riot.commons.rest.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for applying the DB-Table name to a {@link Storable}.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface TableName {

    /**
     * Contains the DB-Table name.
     * @return
     */
    String value();

}
