package de.uni_stuttgart.riot.rule;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.riot.reference.ServerReferenceResolver;
import de.uni_stuttgart.riot.references.ReferenceResolver;
import de.uni_stuttgart.riot.references.Referenceable;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.references.TargetNotFoundException;
import de.uni_stuttgart.riot.thing.BaseInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.PropertyListener;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A {@link Rule} gets a set of {@link RuleParameter}s and uses those together with other surrounding infrastructure to execute its
 * behavior. Most rules will register to a couple of events and then execute some actions when appropriate. Telling from their inner
 * construction, {@link Rule}s are a mix of {@link Thing}s and {@link BaseInstance}s. Their {@link RuleParameter}s are not as dynamic as a
 * thing's properties, but they are also managed by the framework (and not simple fields). The most notable difference is that {@link Rule}
 * objects can't leave the server. They are managed and executed on the server. Clients only operate on {@link RuleDescription}s to display
 * proper setup UIs for rules, and {@link RuleConfiguration}s to send the configuration data to the server. So if a user display's a list of
 * running rules on a client device, he is actually presented a list of running {@link RuleConfiguration}s. Similarly, rules are not
 * persisted in the database, only their configurations are persisted.
 * 
 * @author Philipp Keck
 */
public abstract class Rule {

    private static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(3);
    private final Logger logger = LoggerFactory.getLogger(Rule.class);
    private RuleConfiguration configuration;
    private boolean running;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append(" [");
        if (configuration == null) {
            builder.append("No config, ");
            builder.append(running ? "Running" : "Not running");
        } else {
            builder.append(configuration.getId());
            builder.append(", ");
            builder.append(configuration.getName());
            builder.append(", ");
            builder.append(configuration.getStatus());
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Gets the rule configuration.
     * 
     * @return The configuration of the rule.
     */
    public RuleConfiguration getConfiguration() {
        if (configuration == null) {
            throw new IllegalStateException("Cannot use the rule before its configuration has been set!");
        }
        return configuration;
    }

    /**
     * Checks if the rule configuration has already been set.
     * 
     * @return True if the rule is ready to start.
     */
    public boolean hasConfiguration() {
        return configuration != null;
    }

    /**
     * Sets a new rule configuration. This will temporarily stop the rule execution, swap the configuration and start it again.
     * 
     * @param configuration
     *            The new configuration.
     */
    public void setConfiguration(RuleConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must not be null!");
        } else if (configuration.getId() == null || configuration.getId().equals(0)) {
            throw new IllegalArgumentException("Cannot run rule with empty ID!");
        } else if (!getClass().getName().equals(configuration.getType())) {
            throw new IllegalArgumentException("The configuration is for " + configuration.getType() + " instead of " + getClass());
        }
        boolean wasRunning = this.running;
        stopExecution();
        this.configuration = configuration;
        if (wasRunning) {
            startExecution();
        }
    }

    /**
     * Determines if the rule is currently being executed.
     * 
     * @return True if the rule is running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Gets the resolver.
     * 
     * @return The resolver which is used to resolve any {@link ReferenceParameter}s used by the rule.
     */
    protected ReferenceResolver getResolver() {
        return ServerReferenceResolver.getInstance();
    }

    /**
     * Gets the scheduler.
     * 
     * @return The scheduler which is used to schedule deferred and periodic tasks of the rule.
     */
    protected ScheduledExecutorService getScheduler() {
        return SCHEDULER;
    }

    /**
     * Creates a new rule parameter. <b>Important:</b> You must absolutely make sure that the <tt>parameterName</tt> matches the field that
     * you are assigning this parameter to. A {@link UIHint} for the parameter can be specified using the {@link Parameter} annotation.
     * 
     * @param <V>
     *            The type of the parameter's values.
     * @param parameterName
     *            The name of the parameter. <b>Important:</b> You must absolutely make sure that the <tt>parameterName</tt> matches the
     *            field that you are assigning this parameter to.
     * @param valueType
     *            The type of the parameter's values.
     * @return The rule parameter instance.
     */
    protected <V> RuleParameter<V> newParameter(String parameterName, Class<V> valueType) {
        return new RuleParameter<V>(this, parameterName, valueType);
    }

    /**
     * Creates a new reference rule parameter. <b>Important:</b> You must absolutely make sure that the <tt>parameterName</tt> matches the
     * field that you are assigning this parameter to. A {@link UIHint} for the parameter can be specified using the {@link Parameter}
     * annotation.
     * 
     * @param <R>
     *            The type of the entites referenced by the parameter.
     * @param parameterName
     *            The name of the parameter. <b>Important:</b> You must absolutely make sure that the <tt>parameterName</tt> matches the
     *            field that you are assigning this parameter to.
     * @param targetType
     *            The type of the entites referenced by the parameter.
     * @return The rule parameter instance.
     */
    protected <R extends Referenceable<? super R>> ReferenceParameter<R> newReferenceParameter(String parameterName, Class<R> targetType) {
        return new ReferenceParameter<R>(this, parameterName, targetType);
    }

    /**
     * This method should initialize the execution. When this method is called, all parameters are filled with their values. The
     * implementation should register to events, register schedulers, etc. Note: The caller ensures that this method is only called on an
     * uninitialized rule or on one that has been shut down previously.
     * 
     * @throws ResolveReferenceException
     *             When resolving one of the parameters fails.
     */
    protected abstract void initialize() throws ResolveReferenceException;

    /**
     * This method should shutdown the execution, i.e. it should unregister and stop everything. Please do not call
     * {@link ScheduledExecutorService#shutdown()}, unregister all of your individual tasks instead. Note: The caller ensures that this
     * method is called only on an initialized thing.
     * 
     * @throws ResolveReferenceException
     *             When resolving one of the parameters fails.
     */
    protected abstract void shutdown() throws ResolveReferenceException;

    /**
     * Starts the execution of the rule, if the configuration says that it should be started.
     */
    public synchronized void startExecution() {
        if (!running && getConfiguration().getStatus() == RuleStatus.ACTIVE) {
            running = true;
            try {
                initialize();
            } catch (ResolveReferenceException e) {
                errorOccured(e);
            }
        }
    }

    /**
     * This method is called when an error occured during initialization or execution of the rule. The rule will be shut down and set to
     * state {@link RuleStatus#FAILED}.
     * 
     * @param e
     *            The exception that occured.
     */
    public void errorOccured(Exception e) {
        logger.error("Error during initializion/execution of " + toString(), e);
        shutdownFailure(e instanceof TargetNotFoundException ? RuleStatus.FAILED_REFERENCES : RuleStatus.FAILED);
    }

    /**
     * Shuts down the execution and sets the given (failure) status.
     * 
     * @param status
     *            The status.
     */
    private void shutdownFailure(RuleStatus status) {
        stopExecution();
        if (hasConfiguration()) {
            getConfiguration().setStatus(status);
        }
    }

    /**
     * Stops the execution of the rule, if it is running.
     */
    public synchronized void stopExecution() {
        if (running) {
            running = false;
            try {
                shutdown();
            } catch (Throwable t) {
                logger.error("Error when shutting down rule of type " + getClass(), t);
            }
        }
    }

    /**
     * Convenience method for creating event listeners with lambda expressions.
     * 
     * @param runnable
     *            The lambda expression which is a simple void method with no parameters that may throw any of the supported exceptions.
     * @return An event listener. Please save this instance, you won't be able to unregister the listener if you call
     *         {@link #onEvent(ExceptionHandledRunnable)} a second time.
     */
    protected EventListener<EventInstance> onEvent(ExceptionHandledRunnable runnable) {
        return (event, instance) -> {
            try {
                runnable.run();
            } catch (ResolveReferenceException e) {
                errorOccured(e);
            }
        };
    }

    /**
     * Convenience method for creating event listeners with lambda expressions.
     * 
     * @param <E>
     *            The type of the event instance.
     * @param runnable
     *            The lambda expression which is a simple void method with a single parameter (the event instance) that may throw any of the
     *            supported exceptions.
     * @return An event listener. Please save this instance, you won't be able to unregister the listener if you call
     *         {@link #onEvent(ExceptionHandledParameterizedRunnable)} a second time.
     */
    protected <E extends EventInstance> EventListener<E> onEvent(ExceptionHandledParameterizedRunnable<E> runnable) {
        return (event, instance) -> {
            try {
                runnable.run(instance);
            } catch (ResolveReferenceException e) {
                errorOccured(e);
            }
        };
    }

    /**
     * Convenience method for creating property listeners with lambda expressions.
     * 
     * @param <V>
     *            The value type of the property.
     * @param runnable
     *            The lambda expression which is a simple void method with no parameters that may throw any of the supported exceptions.
     * @return A property listener. Please save this instance, you won't be able to unregister the listener if you call
     *         {@link #onPropertyChange(ExceptionHandledRunnable)} a second time.
     */
    protected <V> PropertyListener<V> onPropertyChange(ExceptionHandledRunnable runnable) {
        return (event, instance) -> {
            try {
                runnable.run();
            } catch (ResolveReferenceException e) {
                errorOccured(e);
            }
        };
    }

    /**
     * Convenience method for creating property listeners with lambda expressions.
     * 
     * @param <V>
     *            The value type of the property.
     * @param runnable
     *            The lambda expression which is a simple void method with a single parameter (the new value of the changed property) that
     *            may throw any of the supported exceptions.
     * @return A property listener. Please save this instance, you won't be able to unregister the listener if you call
     *         {@link #onPropertyChange(ExceptionHandledRunnable)} a second time.
     */
    protected <V> PropertyListener<V> onPropertyChange(ExceptionHandledParameterizedRunnable<V> runnable) {
        return (event, instance) -> {
            try {
                runnable.run(instance.getNewValue());
            } catch (ResolveReferenceException e) {
                errorOccured(e);
            }
        };
    }

    /**
     * Convenience method for {@link ScheduledExecutorService#schedule(Runnable, long, java.util.concurrent.TimeUnit)} with lambda
     * expressions that does the exception handling.
     * 
     * @param milliseconds
     *            The delay in milliseconds.
     * @param runnable
     *            The action to be run.
     */
    protected final void delay(long milliseconds, ExceptionHandledRunnable runnable) {
        getScheduler().schedule(() -> {
            try {
                runnable.run();
            } catch (ResolveReferenceException e) {
                errorOccured(e);
            }
        }, milliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * This interface is designed for lambda methods. It can be used in conjunction with the creator methods in the {@link Rule} class (see
     * there).
     */
    protected interface ExceptionHandledRunnable {
        /**
         * Called to run the lambda code.
         * 
         * @throws ResolveReferenceException
         *             This exception will be handled by the base rule implementation.
         */
        void run() throws ResolveReferenceException;
    }

    /**
     * This interface is designed for lambda methods. It can be used in conjunction with the creator methods in the {@link Rule} class (see
     * there).
     * 
     * @author Philipp Keck
     */
    protected interface ExceptionHandledParameterizedRunnable<P> {
        /**
         * Called to run the lambda code.
         * 
         * @param p
         *            The parameter.
         * @throws ResolveReferenceException
         *             This exception will be handled by the base rule implementation.
         */
        void run(P p) throws ResolveReferenceException;
    }

}
