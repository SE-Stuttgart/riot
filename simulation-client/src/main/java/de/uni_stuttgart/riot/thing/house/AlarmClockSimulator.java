package de.uni_stuttgart.riot.thing.house;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent.Instance;
import de.uni_stuttgart.riot.thing.PropertyListener;

/**
 * Simulates a alarm clock. Fires a alarm event if the configured time (hour,minute) is the current time and the alarm clock is activated.
 *
 */
public class AlarmClockSimulator extends Simulator<AlarmClock> {

    private static final long ONE_DAY = 1000 * 60 * 60 * 24;
    private ScheduledFuture<?> alarmFuture;
    private PropertyListener<Object> setAlarmListener;

    /**
     * Constructor for the {@link AlarmClockSimulator}.
     * 
     * @param thing
     *            Thing to be simulated
     * @param scheduler
     *            The scheduler
     */
    public AlarmClockSimulator(AlarmClock thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    /**
     * Called to start the simulator.
     */
    public void startSimulation() {
        super.startSimulation();
        this.initListeners();
        this.scheduleAlarmTask();
    }

    /**
     * Called to stop the simulator.
     */
    public void stopSimulation() {
        super.stopSimulation();
        this.unregister();
    }

    /**
     * Unregisters the setalarmlistener.
     */
    private void unregister() {
        getThing().getHourProperty().unregister(setAlarmListener);
        getThing().getMinuteProperty().unregister(setAlarmListener);
        getThing().getActivatedProperty().unregister(setAlarmListener);
    }

    /**
     * Inits the listeners for the hour minute and activeded properties. That are used to schedule the alarm task.
     */
    private void initListeners() {
        setAlarmListener = new PropertyListener<Object>() {
            @Override
            public void onFired(Event<? extends Instance<? extends Object>> event, Instance<? extends Object> eventInstance) {
                scheduleAlarmTask();
            }
        };
        getThing().getHourProperty().register(setAlarmListener);
        getThing().getMinuteProperty().register(setAlarmListener);
        getThing().getActivatedProperty().register(setAlarmListener);
    }

    /**
     * Schedules the alarm task to the currently configured time. Cancels the obsolete task.
     */
    private void scheduleAlarmTask() {
        if (alarmFuture != null) {
            alarmFuture.cancel(false);
        }
        int hour = getThing().getHour();
        int min = getThing().getMinute();
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        long delay = calendar.getTimeInMillis() - now;
        if (calendar.getTimeInMillis() < now) {
            delay = delay + ONE_DAY;
        }
        if (getThing().isActivated()) {
            alarmFuture = this.getScheduler().schedule(() -> {
                executeEvent(getThing().getAlarmEvent());
                changePropertyValue(getThing().getAlarmProperty(), true);
            }, delay, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
    }

}
