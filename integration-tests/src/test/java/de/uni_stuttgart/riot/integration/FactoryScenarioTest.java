package de.uni_stuttgart.riot.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.thing.BasePropertyListener;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.factory.ThingStatusEvent;
import de.uni_stuttgart.riot.thing.factory.machine.Machine;
import de.uni_stuttgart.riot.thing.factory.machine.MachineSimulator;
import de.uni_stuttgart.riot.thing.factory.robot.Robot;
import de.uni_stuttgart.riot.thing.factory.robot.RobotSimulator;
import de.uni_stuttgart.riot.thing.factory.robot.RobotStatus;

/**
 * Tests the {@link RefillMaterialRule} and {@link EmptyFullTankRule} rules and the {@link Machine} and {@link Robot} things.
 * 
 * @author Philipp Keck
 */
public class FactoryScenarioTest extends BaseScenarioTest {

    @Test
    public void scenarioFactory() throws RequestException, IOException {

        // Assumption.
        assertThat(Machine.PROCESSED_PIECES_TANK_SIZE, is(lessThan(Machine.MATERIAL_TANK_SIZE)));

        // Set up the things. Here, we only use one machine and one robot.
        // In the future, we could use a pool of running machines and available robots,
        // and change the .setMachine() of a robot dynamically, so that whenever a machine needs
        // something, some free robot will go get it. This could be done in a rule that
        // references two collections of machines and robots, respectively.
        TestBehavior<Machine> machine = simulateThing(Machine.class, MachineSimulator.class, "My machine");
        TestBehavior<Robot> robot = simulateThing(Robot.class, RobotSimulator.class, "My robot");
        robot.getThing().setMachine(machine.getThing());

        // Set up listeners.
        @SuppressWarnings("unchecked")
        EventListener<ThingStatusEvent> robotListener = mock(EventListener.class);
        robot.getThing().getStatusChangedEvent().register(robotListener);
        @SuppressWarnings("unchecked")
        BasePropertyListener<Integer> machineOutputListener = mock(BasePropertyListener.class);
        machine.getThing().getProcessedPiecesTankProperty().register(machineOutputListener);

        // Initialize the machine and robot.
        machine.getThing().setPowerSwitch(true);
        robot.getThing().setPowerSwitch(true);
        assertThat(machine.getThing().getMaterialTank(), is(Machine.MATERIAL_TANK_SIZE));
        assertThat(machine.getThing().getProcessedPiecesTank(), is(0));

        // Let the machine fill its output tank.
        machine.getThing().pressStart();
        fetchAllUpdates(); // Everything will run in 0 time, because simulations are infinitely fast.
        fetchAllUpdates();

        // Some material should be gone.
        assertThat(machine.getThing().getMaterialTank(), is(Machine.MATERIAL_TANK_SIZE - Machine.PROCESSED_PIECES_TANK_SIZE));

        // The output should have run full and then been emptied.
        assertThat(machine.getThing().getProcessedPiecesTank(), is(0));
        ArgumentCaptor<Integer> machineTankHistory = ArgumentCaptor.forClass(Integer.class);
        verify(machineOutputListener, times(2)).onChange(Mockito.same(machine.getThing().getProcessedPiecesTankProperty()), Mockito.anyInt(), machineTankHistory.capture());
        assertThat(machineTankHistory.getAllValues(), contains(Machine.PROCESSED_PIECES_TANK_SIZE, 0));

        // The robot should have been activated and then returned to idle.
        ArgumentCaptor<ThingStatusEvent> robotStatusCaptor = ArgumentCaptor.forClass(ThingStatusEvent.class);
        verify(robotListener, times(2)).onFired(Mockito.same(robot.getThing().getStatusChangedEvent()), robotStatusCaptor.capture());
        List<String> robotStatuses = robotStatusCaptor.getAllValues().stream().map(ThingStatusEvent::getStatus).collect(Collectors.toList());
        assertThat(robotStatuses, contains(RobotStatus.ON_THE_WAY_EMPTY_MODUS.toString(), RobotStatus.IDLE.toString()));

    }

}
