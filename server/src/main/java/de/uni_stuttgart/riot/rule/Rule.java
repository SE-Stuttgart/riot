package de.uni_stuttgart.riot.rule;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.riot.notification.Notification;
import de.uni_stuttgart.riot.notification.NotificationBuilder;
import de.uni_stuttgart.riot.notification.NotificationSeverity;
import de.uni_stuttgart.riot.reference.ServerReferenceResolver;
import de.uni_stuttgart.riot.references.ReferenceResolver;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.references.TargetNotFoundException;
import de.uni_stuttgart.riot.references.ThingPermissionDeniedException;
import de.uni_stuttgart.riot.thing.BaseInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.PropertyListener;
import de.uni_stuttgart.riot.thing.Thing;

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

    /**
     * Creates a new rule instance. This will initialize all fields annotated with {@link Parameter}.
     */
    public Rule() {
        try {
            initializeParameterFields();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
        } else if (configuration.getOwnerId() == 0) {
            throw new IllegalArgumentException("configuration must specify the owner ID!");
        }
        boolean wasRunning = this.running;
        stopExecution();

        // We create a copy of the new configuration so that nobody is able to modify it during runtime from the outside.
        this.configuration = configuration.copy();
        if (wasRunning) {
            startExecution();
        }
    }

    /**
     * Gets the owner of the rule. Throws an exception if the owner is not set!
     * 
     * @return The owner's user ID.
     */
    protected long getOwnerId() {
        long ownerId = getConfiguration().getOwnerId();
        if (ownerId == 0) {
            throw new IllegalStateException("The owner ID of a rule must not be empty!");
        }
        return ownerId;
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
     * Initializes all fields annotated with {@link Parameter} and fills them with helper objects that provide the parameter values.
     * 
     * @throws IllegalAccessException
     *             When a field could not be accessed.
     * @throws IllegalArgumentException
     *             When something went wrong when setting a parameter instance to a parameter field.
     */
    private void initializeParameterFields() throws IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = getClass();
        while (clazz != Rule.class) {
            // We detect the "parameters" by checking which RuleParameter fields are declared in the class.
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                // Only use RuleParameter-Fields.
                Type fieldType = field.getGenericType();
                if (!TypeUtils.isAssignable(fieldType, RuleParameter.class)) {
                    continue;
                }

                // Get common information.
                field.setAccessible(true);
                Parameter annotation = field.getAnnotation(Parameter.class);
                String parameterName = field.getName();

                // Create instance based on field type.
                if (TypeUtils.isAssignable(fieldType, ThingParameter.class)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Thing> thingType = (Class<? extends Thing>) TypeUtils.getTypeArguments(fieldType, ThingParameter.class).get(ThingParameter.class.getTypeParameters()[0]);
                    field.set(this, new ThingParameter<>(this, parameterName, thingType, annotation == null ? null : annotation.requires()));

                } else if (TypeUtils.isAssignable(fieldType, ReferenceParameter.class)) {
                    @SuppressWarnings("rawtypes")
                    Class targetType = (Class) TypeUtils.getTypeArguments(fieldType, ReferenceParameter.class).get(ReferenceParameter.class.getTypeParameters()[0]);
                    if (Thing.class.isAssignableFrom(targetType)) {
                        throw new IllegalArgumentException("ReferenceParameters for Things are not permitted, please use ThingParameter!");
                    }
                    @SuppressWarnings("unchecked")
                    ReferenceParameter<?> paramObject = new ReferenceParameter<>(this, parameterName, targetType);
                    field.set(this, paramObject);

                } else {
                    Class<?> valueType = (Class<?>) TypeUtils.getTypeArguments(fieldType, RuleParameter.class).get(RuleParameter.class.getTypeParameters()[0]);
                    field.set(this, new RuleParameter<>(this, parameterName, valueType));
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Creates a new notification sender. The title and message key will be generated canonically from the rule class name and the specified
     * notification name.
     * 
     * @param name
     *            The name of the created notifications.
     * @return The notification sender.
     */
    protected RuleNotification newNotification(String name) {
        return newNotification(name, null);
    }

    /**
     * Creates a new notification sender.
     * 
     * @param name
     *            The name of the created notifications.
     * @param titleKey
     *            The title key of the created notifications.
     * @param messageKey
     *            The message key of the created notifications.
     * @return The notification sender.
     */
    protected RuleNotification newNotification(String name, String titleKey, String messageKey) {
        return newNotification(name, titleKey, messageKey, null);
    }

    /**
     * Creates a new notification sender. The title and message key will be generated canonically from the rule class name and the specified
     * notification name.
     * 
     * @param name
     *            The name of the created notifications.
     * @param severity
     *            The severity of the created notifications.
     * @return The notification sender.
     */
    protected RuleNotification newNotification(String name, NotificationSeverity severity) {
        String notificationName = getClass().getSimpleName() + "_" + name;
        return newNotification(name, notificationName + "_title", notificationName + "_message", severity);
    }

    /**
     * Creates a new notification sender.
     * 
     * @param name
     *            The name of the created notifications.
     * @param titleKey
     *            The title key of the created notifications.
     * @param messageKey
     *            The message key of the created notifications.
     * @param severity
     *            The severity of the created notifications.
     * @return The notification sender.
     */
    protected RuleNotification newNotification(String name, String titleKey, String messageKey, NotificationSeverity severity) {
        Notification prototype = NotificationBuilder.create() //
                .name(name) //
                .titleKey(titleKey).messageKey(messageKey) //
                .severity(severity) //
                .build();
        return new RuleNotification(this, prototype);
    }

    /**
     * This method should initialize the execution. When this method is called, all parameters are filled with their values. The
     * implementation should register to events, register schedulers, etc. Note: The caller ensures that this method is only called on an
     * uninitialized rule or on one that has been shut down previously.
     * 
     * @throws ResolveReferenceException
     *             When resolving one of the parameters fails.
     * @throws IllegalRuleConfigurationException
     *             When the validation of the rule configuration (including parameters) failed.
     */
    protected abstract void initialize() throws ResolveReferenceException, IllegalRuleConfigurationException;

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
            } catch (ResolveReferenceException | IllegalRuleConfigurationException e) {
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
        RuleStatus errorStatus;
        if (e instanceof TargetNotFoundException) {
            errorStatus = RuleStatus.FAILED_REFERENCES;
        } else if (e instanceof ThingPermissionDeniedException) {
            errorStatus = RuleStatus.FAILED_PERMISSIONS;
        } else {
            errorStatus = RuleStatus.FAILED;
        }
        shutdownFailure(errorStatus);
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
     * Convenience method for creating property listeners with lambda expressions.
     * 
     * @param <V>
     *            The value type of the property.
     * @param listener
     *            The lambda expression which is a simple void method with three parameters (the property, the old value and the new value).
     * @return A property listener. Please save this instance, you won't be able to unregister the listener if you call
     *         {@link #onPropertyChange(ExceptionHandledPropertyListener)} a second time.
     */
    protected <V> PropertyListener<V> onPropertyChange(ExceptionHandledPropertyListener<V> listener) {
        return (event, instance) -> {
            try {
                listener.onChange(instance.getOldValue(), instance.getNewValue());
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

    /**
     * This interface is designed for lambda methods. It can be used in conjunction with the creator methods in the {@link Rule} class (see
     * there).
     */
    protected interface ExceptionHandledPropertyListener<V> {
        /**
         * Called when a property changed that the listener was registered to.
         * 
         * @param oldValue
         *            Its old value.
         * @param newValue
         *            Its new value.
         * @throws ResolveReferenceException
         *             This exception will be handled by the base rule implementation.
         */
        void onChange(V oldValue, V newValue) throws ResolveReferenceException;
    }

}
