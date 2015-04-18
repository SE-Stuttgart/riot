package de.uni_stuttgart.riot.thing;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.uni_stuttgart.riot.thing.test.TestThing;

/**
 * Tests the framework base-class {@link ThingBehavior} and especially the detection of wrong uses of the class.
 * 
 * @author Philipp Keck
 */
public class ThingFactoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static class FakeBehavior extends ThingBehavior {
        protected <A extends ActionInstance> void userFiredAction(A actionInstance) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnEmptyType() {
        ThingFactory.resolveType(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDetectMissingType() {
        ThingFactory.resolveType("de.uni_stuttgart.does.not.Exist");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDetectWrongType() {
        ThingFactory.resolveType("java.util.List");
    }

    @Test
    public void shouldCreateThing() {
        TestThing thing = ThingFactory.create(TestThing.class, 11L, new FakeBehavior());
        assertThat(thing.getBehavior(), instanceOf(FakeBehavior.class));
        assertThat(thing.getId(), is(11L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnMissingConstructor() {
        ThingFactory.create(MissingConstructorThing.class, 12L, new FakeBehavior());
    }

    public static class MissingConstructorThing extends Thing {
        public MissingConstructorThing() {
            super(null);
        }
    }

    @Test
    public void shouldCallBehaviorFactory() {

        // Create a behavior factory.
        @SuppressWarnings("unchecked")
        ThingBehaviorFactory<FakeBehavior> behaviorFactory = mock(ThingBehaviorFactory.class);
        when(behaviorFactory.newBehavior(TestThing.class)).thenAnswer(new Answer<FakeBehavior>() {
            public FakeBehavior answer(InvocationOnMock invocation) throws Throwable {
                return new FakeBehavior();
            }
        });

        // Create an existing TestThing.
        TestThing thing1 = ThingFactory.create(TestThing.class, 42L, behaviorFactory);

        // Register the thing in the factory.
        when(behaviorFactory.existingBehavior(42L)).thenReturn((FakeBehavior) thing1.getBehavior());

        // Recreate it, should return the same thing.
        TestThing thing2 = ThingFactory.create(TestThing.class, 42L, behaviorFactory);
        assertThat(thing2, sameInstance(thing1));

        // Try recreating it with the wrong type.
        try {
            ThingFactory.create(Device.class, 42L, behaviorFactory);
            fail();
        } catch (IllegalStateException e) {
            // Expected
        }

        // Create another one, should be different then.
        TestThing thing3 = ThingFactory.create(TestThing.class, 43L, behaviorFactory);
        assertThat(thing3, not(sameInstance(thing1)));
        assertThat(thing3.getBehavior(), not(sameInstance(thing1.getBehavior())));

    }

    @Test
    public void testSingleUseFactory() {
        FakeBehavior behavior = new FakeBehavior();
        SingleUseThingBehaviorFactory<FakeBehavior> behaviorFactory = new SingleUseThingBehaviorFactory<ThingFactoryTest.FakeBehavior>(behavior);
        ThingFactory.create(TestThing.class, 22L, behaviorFactory);
        thrown.expect(IllegalStateException.class);
        ThingFactory.create(TestThing.class, 22L, behaviorFactory);
    }
}
