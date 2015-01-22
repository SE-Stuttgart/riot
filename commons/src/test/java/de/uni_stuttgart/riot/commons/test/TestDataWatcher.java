package de.uni_stuttgart.riot.commons.test;

import java.sql.SQLException;

import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * A JUnit {@link Rule} that watches for {@link TestData} annotations and executes them.
 * 
 * @author Philipp Keck
 */
public class TestDataWatcher extends TestWatcher {

    private final H2DatabaseRule database;

    public TestDataWatcher(H2DatabaseRule database) {
        this.database = database;
    }

    protected void starting(Description description) {
        // Find annotations (check test method, then test classes and its super classes).
        TestData annotation = description.getAnnotation(TestData.class);
        if (annotation == null) {
            Class<?> testClass = description.getTestClass();
            while (annotation == null && testClass != Object.class) {
                annotation = testClass.getAnnotation(TestData.class);
                testClass = testClass.getSuperclass();
            }
        }

        // Execute SQLs, if the annotation was present.
        if (annotation != null && annotation.value() != null && annotation.value().length > 0) {
            try {
                SqlRunner sqlRunner = new SqlRunner(database.getDataSource());
                for (String file : annotation.value()) {
                    sqlRunner.runScript(file);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
