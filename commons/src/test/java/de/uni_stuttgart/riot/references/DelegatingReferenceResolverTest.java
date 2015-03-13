package de.uni_stuttgart.riot.references;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.test.TestThing;

/**
 * Unit test for {@link DelegatingReferenceResolver}.
 * 
 * @author Philipp Keck
 */
public class DelegatingReferenceResolverTest {

    @Test
    public void testGenericResolving() throws ResolveReferenceException {

        TestReferenceable test1 = new TestReferenceable(11L);
        TestReferenceable test2 = new TestReferenceable(12L);
        TestSubReferenceable sub1 = new TestSubReferenceable(13L);

        DelegatingReferenceResolver resolver = new DelegatingReferenceResolver();

        @SuppressWarnings("unchecked")
        TypedReferenceResolver<TestReferenceable> innerResolver = mock(TypedReferenceResolver.class);
        resolver.addResolver(TestReferenceable.class, innerResolver);
        when(innerResolver.resolve(11L)).thenReturn(test1);
        when(innerResolver.resolve(12L)).thenReturn(test2);
        when(innerResolver.resolve(13L)).thenReturn(sub1);

        assertThat(resolver.resolve(11L, TestReferenceable.class), sameInstance(test1));
        assertThat(resolver.resolve(12L, TestReferenceable.class), sameInstance(test2));
        assertThat(resolver.resolve(13L, TestReferenceable.class), sameInstance((TestReferenceable) sub1));
        assertThat(resolver.resolve(13L, TestSubReferenceable.class), sameInstance(sub1));

        assertThat(resolver.resolve(11L, TestReferenceable.class, TestReferenceable.class), sameInstance(test1));
        assertThat(resolver.resolve(12L, TestReferenceable.class, TestReferenceable.class), sameInstance(test2));
        assertThat(resolver.resolve(13L, TestSubReferenceable.class, TestReferenceable.class), sameInstance(sub1));
    }

    @Test(expected = UnsupportedReferenceTypeException.class)
    public void shouldThrowOnUnsupported() throws ResolveReferenceException {
        DelegatingReferenceResolver resolver = new DelegatingReferenceResolver();

        @SuppressWarnings("unchecked")
        TypedReferenceResolver<TestReferenceable> innerResolver = mock(TypedReferenceResolver.class);
        resolver.addResolver(TestReferenceable.class, innerResolver);

        resolver.resolve(10L, Thing.class);
    }

    @Test(expected = UnsupportedReferenceTypeException.class)
    public void shouldThrowOnUnsupportedSupertype() throws ResolveReferenceException {
        DelegatingReferenceResolver resolver = new DelegatingReferenceResolver();

        @SuppressWarnings("unchecked")
        TypedReferenceResolver<TestReferenceable> innerResolver = mock(TypedReferenceResolver.class);
        resolver.addResolver(TestReferenceable.class, innerResolver);

        resolver.resolve(10L, TestThing.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(expected = RuntimeException.class)
    public void shouldThrowOnWrongTypes() throws ResolveReferenceException {
        DelegatingReferenceResolver resolver = new DelegatingReferenceResolver();
        resolver.resolve(10L, (Class) ThisIsNotEvenReferenceable.class);
    }

    @Test(expected = TargetNotFoundException.class)
    public void shouldHandExceptionThrough() throws ResolveReferenceException {
        DelegatingReferenceResolver resolver = new DelegatingReferenceResolver();

        @SuppressWarnings("unchecked")
        TypedReferenceResolver<TestReferenceable> innerResolver = mock(TypedReferenceResolver.class);
        resolver.addResolver(TestReferenceable.class, innerResolver);
        when(innerResolver.resolve(11L)).thenThrow(new TargetNotFoundException("TestMessage"));
        resolver.resolve(11L, TestReferenceable.class);
    }

    public static class ThisIsNotEvenReferenceable {

    }

}
