package de.uni_stuttgart.riot.commons.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used on unit test methods or classes to provide a list of .sql file names, which will be executed before the test.
 * Note: If the annotation is present on both the class and the test method, the latter will override the former, i.e. the files in the
 * annotation of the class will be ignored.
 * 
 * @author Philipp Keck
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface TestData {

    /**
     * Contains the files to be executed.
     * 
     * @return A list of file names pointing to .sql files.
     */
    String[] value();

}
