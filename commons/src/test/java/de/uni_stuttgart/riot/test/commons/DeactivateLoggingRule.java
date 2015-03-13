package de.uni_stuttgart.riot.test.commons;

import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit {@link Rule} that turns off the Log4J logging for specified loggers for the duration of the JUnit test.
 * 
 * @author Philipp Keck
 */
public class DeactivateLoggingRule implements TestRule {

    private final Object[] loggers;

    /**
     * Creates a new rule.
     * 
     * @param loggers
     *            The loggers that should be deactivated. These can either be {@link Class} instances (to deactivate the logger for a single
     *            class) or prefixes/Strings to deactivate logging for entire packages/modules at once. Values other than {@link Class} and
     *            {@link String} are not permitted here.
     */
    public DeactivateLoggingRule(Object... loggers) {
        this.loggers = loggers;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            public void evaluate() throws Throwable {
                final Map<Logger, Level> oldLevels = new IdentityHashMap<Logger, Level>();
                for (Object loggerName : loggers) {
                    Logger logger;
                    if (loggerName instanceof Class) {
                        logger = Logger.getLogger((Class<?>) loggerName);
                    } else if (loggerName instanceof String) {
                        logger = Logger.getLogger(loggerName.toString());
                    } else {
                        throw new IllegalArgumentException("Only Strings and Objects are valid parameters to a DeactivateLoggingRule!");
                    }
                    if (oldLevels.containsKey(logger)) {
                        continue; // Duplicate, ignore.
                    }
                    oldLevels.put(logger, logger.getLevel());
                    logger.setLevel(Level.OFF);
                }

                try {
                    base.evaluate();
                } finally {
                    for (Map.Entry<Logger, Level> entry : oldLevels.entrySet()) {
                        entry.getKey().setLevel(entry.getValue());
                    }
                }
            }
        };
    }
}
