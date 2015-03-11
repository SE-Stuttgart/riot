package de.uni_stuttgart.riot.thing.test;

import de.uni_stuttgart.riot.references.Referenceable;

/**
 * A test class for testing references.
 * 
 * @author Philipp Keck
 */
public class TestReferenceable implements Referenceable<TestReferenceable> {

    private final Long id;

    public TestReferenceable(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

}
