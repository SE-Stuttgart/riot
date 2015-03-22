package de.uni_stuttgart.riot.thing.test;

import static org.mockito.Mockito.*;

import java.util.Collection;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.uni_stuttgart.riot.references.DelegatingReferenceResolver;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.AuthenticatingThingBehavior;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;

/**
 * A behavior for testing things. This behavior exposes most of the capabilities of a behavior so that unit tests can access these features.
 * 
 * @author Philipp Keck
 *
 */
public class TestThingBehavior extends ThingBehavior implements AuthenticatingThingBehavior {

    public boolean executePropertyChangesDirectly = false;

    @Override
    protected <A extends ActionInstance> void userFiredAction(A actionInstance) {
        // Ignore.
    }

    @Override
    protected <V> void userModifiedProperty(WritableProperty<V> property, V newValue) {
        if (executePropertyChangesDirectly) {
            notifyListeners(property.getChangeEvent(), new PropertyChangeEvent.Instance<V>(property.getChangeEvent(), property.get(), newValue));
        } else {
            super.userModifiedProperty(property, newValue);
        }
    }

    public <V> void setThingProperty(Property<V> property, V value) {
        ThingBehavior.silentSetThingProperty(property, value);
    }

    public <V> void setThingProperty(String propertyName, V value) {
        if (value == null) {
            throw new NullPointerException("This implementation does not support null values!");
        }
        @SuppressWarnings("unchecked")
        Property<V> property = (Property<V>) getThing().getProperty(propertyName, value.getClass());
        ThingBehavior.silentSetThingProperty(property, value);
    }

    @Override
    public <E extends EventInstance> void notifyListeners(Event<E> event, E eventInstance) {
        super.notifyListeners(event, eventInstance);
    }

    @Override
    protected DelegatingReferenceResolver getDelegatingResolver() {
        return super.getDelegatingResolver();
    }

    @SuppressWarnings("unchecked")
    public static ThingBehaviorFactory<TestThingBehavior> getMockFactory() {
        ThingBehaviorFactory<TestThingBehavior> mockBehaviorFactory = mock(ThingBehaviorFactory.class);
        when(mockBehaviorFactory.newBehavior(any(Class.class))).thenAnswer(new Answer<TestThingBehavior>() {
            public TestThingBehavior answer(InvocationOnMock invocation) throws Throwable {
                return new TestThingBehavior();
            }
        });
        return mockBehaviorFactory;
    }

    @Override
    public boolean canAccess(long userId, ThingPermission permission) {
        return true; // Allow anything.
    }

    @Override
    public boolean canAccess(long userId, Collection<ThingPermission> permissions) {
        return true; // Allow anything.
    }

}
