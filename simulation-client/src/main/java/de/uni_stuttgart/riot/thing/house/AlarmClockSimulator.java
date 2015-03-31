package de.uni_stuttgart.riot.thing.house;

import java.util.Calendar;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;

/**
 * Simulates a alarm clock. Fires a alarm event if the configured time (hour,minute) is the current time and the alarm clock is activated.
 *
 */
public class AlarmClockSimulator extends Simulator<AlarmClock> {

    private static final long CHECK_INTERVAL = 1000 * 30; // every half minute

    /**
     * Constructor.
     * 
     * @param thing
     *            .
     * @param scheduler
     *            .
     */
    public AlarmClockSimulator(AlarmClock thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
        this.scheduleTimeCheckTask();
    }

    /**
     * Schedules the task that checks the configured alarm time against the current time. (if the alarm clock is activated)
     */
    private void scheduleTimeCheckTask() {
        this.scheduleAtFixedRate(() -> {
            if (AlarmClockSimulator.this.getThing().isActivated()) {
                Calendar cal = Calendar.getInstance();
                int hour = AlarmClockSimulator.this.getThing().getHour();
                int min = AlarmClockSimulator.this.getThing().getMinute();
                if (cal.get(Calendar.HOUR_OF_DAY) == hour && cal.get(Calendar.MINUTE) == min) {
                    executeEvent(AlarmClockSimulator.this.getThing().getAlarmEvent());
                    changePropertyValue(AlarmClockSimulator.this.getThing().getAlarmProperty(), true);
                }
            }
        }, 0, CHECK_INTERVAL);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
    }

}
