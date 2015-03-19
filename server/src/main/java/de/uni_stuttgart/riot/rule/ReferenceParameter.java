package de.uni_stuttgart.riot.rule;

import java.util.Objects;

import de.uni_stuttgart.riot.references.Referenceable;
import de.uni_stuttgart.riot.references.ResolvableReference;
import de.uni_stuttgart.riot.references.ResolveReferenceException;

/**
 * A {@link RuleParameter} that references another entity.
 * 
 * @author Philipp Keck
 *
 * @param <R>
 *            The type of the referenced entity.
 */
public class ReferenceParameter<R extends Referenceable<? super R>> extends RuleParameter<Long> implements ResolvableReference<R> {

    private final Class<R> targetType;

    /**
     * Creates a new reference rule parameter. Note that the constructor is internal. Rules are required to instantiate their parameters
     * through {@link Rule#newReferenceParameter(String, Class)}.
     * 
     * @param rule
     *            The rule that this parameter belongs to.
     * @param name
     *            The parameter name.
     * @param targetType
     *            The type of the entites referenced by the parameter.
     */
    ReferenceParameter(Rule rule, String name, Class<R> targetType) {
        super(rule, name, Long.class);
        this.targetType = Objects.requireNonNull(targetType);
    }

    @Override
    public Long getId() {
        return super.get();
    }

    @Override
    public R getTarget() throws ResolveReferenceException {
        return getRule().getResolver().resolve(getId(), targetType);
    }

    /**
     * Gets the target type.
     * 
     * @return The type of the entites referenced by the parameter.
     */
    public Class<R> getTargetType() {
        return targetType;
    }

}
