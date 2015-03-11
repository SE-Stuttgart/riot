package de.uni_stuttgart.riot.thing;

import de.uni_stuttgart.riot.references.Referenceable;
import de.uni_stuttgart.riot.references.ResolvableReference;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A property that references a {@link Referenceable} entity and stores its value as the {@link Long} ID of the entity.
 * 
 * @author Philipp Keck
 *
 * @param <R>
 *            The type of the referenced entity.
 */
public class ReferenceProperty<R extends Referenceable<? super R>> extends Property<Long> implements ResolvableReference<R> {

    private final Class<R> targetType;

    /**
     * Creates a new reference property. Note that the constructor is internal. Things are required to instantiate their properties through
     * {@link Thing#newReferenceProperty(String, Class)}.
     * 
     * @param thing
     *            The thing that this property belongs to.
     * @param name
     *            The property name.
     * @param targetType
     *            The type of the entities referenced by this property.
     * @param uiHint
     *            The UI hint (optional).
     */
    ReferenceProperty(Thing thing, String name, Class<R> targetType, UIHint uiHint) {
        super(thing, name, Long.class, null, uiHint);
        this.targetType = targetType;
    }

    @Override
    public Long getId() {
        return super.get();
    }

    @Override
    public R getTarget() throws ResolveReferenceException {
        return getThing().getBehavior().resolve(getId(), targetType);
    }

    /**
     * Gets the target type, that is the type of the referenced entities. Note that {@link #getValueType()} will always return {@link Long}.
     * 
     * @return The target type.
     */
    public Class<R> getTargetType() {
        return targetType;
    }

}
