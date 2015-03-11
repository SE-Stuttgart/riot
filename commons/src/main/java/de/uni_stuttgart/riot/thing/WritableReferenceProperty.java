package de.uni_stuttgart.riot.thing;

import de.uni_stuttgart.riot.references.Referenceable;
import de.uni_stuttgart.riot.references.ResolvableReference;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.references.WritableReference;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A writable property that references a {@link Referenceable} entity and stores its value as the {@link Long} ID of the entity.
 * 
 * @author Philipp Keck
 *
 * @param <R>
 *            The type of the referenced entity.
 */
public class WritableReferenceProperty<R extends Referenceable<? super R>> extends WritableProperty<Long> implements ResolvableReference<R>, WritableReference<R> {

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
    WritableReferenceProperty(Thing thing, String name, Class<R> targetType, UIHint uiHint) {
        super(thing, name, Long.class, null, uiHint);
        this.targetType = targetType;
    }

    @Override
    public Long getId() {
        return super.get();
    }

    @Override
    public void setId(Long newId) {
        super.set(newId);
    }

    @Override
    public void setTarget(R target) {
        if (target.getId() == null) {
            throw new IllegalArgumentException("Cannot create a reference to a entity that has no ID set!");
        }
        super.set(target == null ? null : target.getId());
    }

    @Override
    public R getTarget() throws ResolveReferenceException {
        return getThing().getBehavior().resolve(getId(), targetType);
    }

    /**
     * Gets the target type, that is the type of the referenced entities. Note that {@link Property#getValueType()} will always return
     * {@link Long}.
     * 
     * @return The target type.
     */
    public Class<R> getTargetType() {
        return targetType;
    }

}
