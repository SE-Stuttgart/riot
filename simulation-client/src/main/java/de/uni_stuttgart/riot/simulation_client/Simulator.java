package de.uni_stuttgart.riot.simulation_client;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;

/**
 * A simulator contains the business logic for realistically simulating the runtime behavior of a thing. It can be used in conjunction with
 * {@link SimulatedThingBehavior}. You can specify it as the <tt>simulator</tt> property in your settings file.
 * 
 * @author Philipp Keck
 *
 * @param <T>
 *            The type of simulated thing.
 */
public abstract class Simulator<T extends Thing> {

    /**
     * The thing being simulated.
     */
    private final T thing;

    /**
     * The scheduler responsible for this simulator.
     */
    private final ScheduledThreadPoolExecutor scheduler;

    /**
     * The random values generator.
     */
    private final Random random = new Random();

    /**
     * Creates a new simulator. <b>Important</b>: All subclasses must provide a constructor with exactly these parameters.
     * 
     * @param thing
     *            The thing to simulate.
     * @param scheduler
     *            The scheduler responsible for this simulator.
     */
    public Simulator(T thing, ScheduledThreadPoolExecutor scheduler) {
        this.thing = thing;
        if (!(thing.getBehavior() instanceof SimulatedThingBehavior)) {
            throw new IllegalArgumentException("A simulator may only be used on a SimulatedThingBehavior!");
        }
        this.scheduler = scheduler;
    }

    /**
     * Gets the thing.
     * 
     * @return The thing to simulate.
     */
    protected final T getThing() {
        return thing;
    }

    /**
     * Gets the thing's simulated behavior instance. This should not be exposed to the simulator implementation, so it is private.
     * 
     * @return The behavior.
     */
    private SimulatedThingBehavior getBehavior() {
        return (SimulatedThingBehavior) thing.getBehavior();
    }

    /**
     * Gets the scheduler.
     * 
     * @return A scheduler that can be used to schedule tasks for the simulation.
     */
    protected final ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    /**
     * Convenience method for {@link ScheduledExecutorService#schedule(Runnable, long, java.util.concurrent.TimeUnit)}.
     * 
     * @param milliseconds
     *            The delay in milliseconds.
     * @param runnable
     *            The action to be run.
     */
    protected final void delay(long milliseconds, Runnable runnable) {
        scheduler.schedule(runnable, milliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * Executes a linear change of a property over time.
     * 
     * @param property
     *            The property to be changed.
     * @param end
     *            The end value (the start value is the current value of the property). Note that this will be rounded to integers.
     * @param stepTime
     *            The duration of a single step.
     * @param stepCount
     *            The number of steps. The total duration of the change is <tt>stepTime*stepCount</tt>.
     */
    protected final void linearChange(Property<Integer> property, float end, long stepTime, int stepCount) {
        AtomicReference<ScheduledFuture<?>> future = new AtomicReference<ScheduledFuture<?>>();
        AtomicInteger step = new AtomicInteger(0);
        float start = property.get();
        future.set(scheduler.scheduleAtFixedRate(() -> {
            float currentStep = step.getAndIncrement();
            Platform.runLater(() -> {
                changePropertyValue(property, Math.round(start + (end - start) * (currentStep / stepCount)));
            });
            if (currentStep == stepCount) {
                future.get().cancel(false);
            }
        }, 0, stepTime, TimeUnit.MILLISECONDS));
    }

    /**
     * Executes a linear change of a property over time.
     * 
     * @param property
     *            The property to be changed.
     * @param end
     *            The end value (the start value is the current value of the property).
     * @param stepTime
     *            The duration of a single step.
     * @param stepCount
     *            The number of steps. The total duration of the change is <tt>stepTime*stepCount</tt>.
     */
    protected final void linearChange(Property<Double> property, double end, long stepTime, int stepCount) {
        AtomicReference<ScheduledFuture<?>> future = new AtomicReference<ScheduledFuture<?>>();
        AtomicInteger step = new AtomicInteger(0);
        double start = property.get();
        future.set(scheduler.scheduleAtFixedRate(() -> {
            double currentStep = step.getAndIncrement();
            changePropertyValue(property, start + (end - start) * (currentStep / stepCount));
            if (currentStep == stepCount) {
                future.get().cancel(false);
            }
        }, 0, stepTime, TimeUnit.MILLISECONDS));
    }

    /**
     * Generates a random number.
     * 
     * @param min
     *            Minimum value (inclusive).
     * @param max
     *            Maximum value (exclusive).
     * @return The random number.
     */
    protected final double random(double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    /**
     * Makes a random decision.
     * 
     * @param probability
     *            The probability of returning <tt>true</tt>.
     * @return <tt>true</tt> with the given probability.
     */
    protected final boolean randomDecision(double probability) {
        return random.nextDouble() < probability;
    }

    /**
     * Called to start the simulator. When overriding this method, remember to call the super method.
     */
    public void startSimulation() {
        getBehavior().startSimulation(this);
    }

    /**
     * Called to stop the simulator. When overriding this method, remember to call the super method.
     */
    public void stopSimulation() {
        getBehavior().stopSimulation(this);
    }

    /**
     * Called to shut down the simulator. This should cancel all tasks. When overriding this method, remember to call the super method.
     */
    public void shutdown() {
        stopSimulation();
        scheduler.shutdown();
    }

    /**
     * Called to execute an action of the simulated thing.
     * 
     * @param <A>
     *            The type of the action instance.
     * @param action
     *            The action to execute.
     * @param actionInstance
     *            The action instance.
     */
    protected abstract <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance);

    /**
     * Can be called by the simulator implementation to set a property to a new value. This will fire the property's change event.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param property
     *            The property to be changed.
     * @param newValue
     *            The new value for the property.
     */
    protected final <V> void changePropertyValue(Property<V> property, V newValue) {
        getBehavior().changePropertyValue(property, newValue);
    }

    /**
     * Can be used by the simulator implementation to execute an event.
     * 
     * @param <E>
     *            The type of the event instance.
     * @param eventInstance
     *            The event to execute.
     */
    protected final <E extends EventInstance> void executeEvent(E eventInstance) {
        getBehavior().executeEvent(eventInstance);
    }

    /**
     * Can be used by the simulator implementation to execute an event.
     * 
     * @param event
     *            The event to execute.
     */
    protected final void executeEvent(Event<EventInstance> event) {
        getBehavior().executeEvent(new EventInstance(event));
    }

}
