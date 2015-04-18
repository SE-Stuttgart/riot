package de.uni_stuttgart.riot.simulation_client;

import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.lang3.ClassUtils;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.uni_stuttgart.riot.thing.Property;

/**
 * Helper class for unit tests that need to test a simulator.
 * 
 * @author Philipp Keck
 */
public abstract class SimulatorHelper {

    /**
     * Cannot instantiate.
     */
    private SimulatorHelper() {
    }

    /**
     * Modifies the given simulator so that it can be tested. The following changes will be made:
     * <ul>
     * <li>The simulator is transformed into a Mockito spy. This may be useful to stub other methods like
     * {@link Simulator#random(double, double)}.</li>
     * <li>{@link Simulator#delay(long, Runnable)} is executed immediately.</li>
     * <li>{@link Simulator#linearChange(de.uni_stuttgart.riot.thing.Property, double, long, int)} is executed immediately.</li>
     * <li>{@link Simulator#linearChange(de.uni_stuttgart.riot.thing.Property, float, long, int)} is executed immediately.</li>
     * <li>{@link Simulator#scheduleAtFixedRate(Runnable, long, long)} will be ignored.</li>
     * </ul>
     * 
     * @param simulator
     *            The simulator.
     * @return A spy for the simulator (only use that in the future).
     */
    @SuppressWarnings("unchecked")
    public static <S extends Simulator<?>> S spySimulator(S simulator) {
        final S spy = spy(simulator);

        doAnswer(new Answer<ScheduledFuture<?>>() {
            public ScheduledFuture<?> answer(InvocationOnMock invocation) throws Throwable {
                ((Runnable) invocation.getArguments()[1]).run();
                return mock(ScheduledFuture.class);
            }
        }).when(spy).delay(anyLong(), any());

        doAnswer(new Answer<ScheduledFuture<?>>() {
            public ScheduledFuture<?> answer(InvocationOnMock invocation) throws Throwable {
                Property<Integer> property = (Property<Integer>) invocation.getArguments()[0];
                spy.changePropertyValue(property, Math.round((Float) invocation.getArguments()[1]));
                return mock(ScheduledFuture.class);
            }
        }).when(spy).linearChange((Property<Integer>) any(), anyFloat(), anyLong(), anyInt());

        doAnswer(new Answer<ScheduledFuture<?>>() {
            public ScheduledFuture<?> answer(InvocationOnMock invocation) throws Throwable {
                Property<Double> property = (Property<Double>) invocation.getArguments()[0];
                spy.changePropertyValue(property, (Double) invocation.getArguments()[1]);
                return mock(ScheduledFuture.class);
            }
        }).when(spy).linearChange((Property<Double>) any(), anyDouble(), anyLong(), anyInt());

        doAnswer(new Answer<ScheduledFuture<?>>() {
            public ScheduledFuture<?> answer(InvocationOnMock invocation) throws Throwable {
                return mock(ScheduledFuture.class);
            }
        }).when(spy).scheduleAtFixedRate(any(), anyLong(), anyLong());

        return spy;
    }

    /**
     * Calls a non-public method through reflection.
     * 
     * @param target
     *            The target object.
     * @param name
     *            The method name.
     * @param arguments
     *            The arguments (may be empty).
     * @return The methods return value.
     * @throws ReflectiveOperationException
     *             When reflection fails.
     */
    public static Object invokeDeclaredMethod(Object target, String name, Object... arguments) throws ReflectiveOperationException {
        Method method = null;
        Class<?> clazz = target.getClass();
        while (method == null) {
            try {
                method = clazz.getDeclaredMethod(name, ClassUtils.toClass(arguments));
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
                if (clazz == null) {
                    throw e;
                }
            }
        }
        method.setAccessible(true);
        return method.invoke(target, arguments);
    }

}
