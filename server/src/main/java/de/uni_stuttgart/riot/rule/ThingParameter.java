package de.uni_stuttgart.riot.rule;

import java.util.EnumSet;
import java.util.Set;

import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.references.ThingPermissionDeniedException;
import de.uni_stuttgart.riot.thing.AuthenticatingThingBehavior;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;

/**
 * A {@link RuleParameter} that references a {@link Thing}.
 * 
 * @author Philipp Keck
 *
 * @param <T>
 *            The type of the referenced thing (does not need to be the exact type, may be a supertype, even {@link Thing} itself).
 */
public class ThingParameter<T extends Thing> extends ReferenceParameter<T> {

    private final Set<ThingPermission> requiredPermissions;

    /**
     * Creates a new thing reference parameter. Note that the constructor is internal. Rules are required to instantiate their parameters
     * through {@link Rule#newThingParameter(String, Class, ThingPermission...)}.
     * 
     * @param rule
     *            The rule that this parameter belongs to.
     * @param name
     *            The parameter name.
     * @param thingType
     *            The type of the things referenced by the parameter.
     * @param requiredPermissions
     *            The permissions that the rule owner needs to have on the target thing for the rule to operate.
     */
    ThingParameter(Rule rule, String name, Class<T> thingType, ThingPermission... requiredPermissions) {
        super(rule, name, thingType);
        if (requiredPermissions == null || requiredPermissions.length == 0 || requiredPermissions[0] == null) {
            this.requiredPermissions = EnumSet.noneOf(ThingPermission.class);
        } else {
            this.requiredPermissions = EnumSet.of(requiredPermissions[0], requiredPermissions);
        }
    }

    @Override
    public Long getId() {
        return super.get();
    }

    @Override
    public T getTarget() throws ResolveReferenceException {
        T thing = super.getTarget();
        if (thing == null) {
            return null;
        }

        // We need to check the permission!
        AuthenticatingThingBehavior behavior = (AuthenticatingThingBehavior) thing.getBehavior();
        long ownerId = getRule().getOwnerId();
        if (!behavior.canAccess(ownerId, requiredPermissions)) {
            throw new ThingPermissionDeniedException("The rule's owner " + ownerId + " cannot access thing " + getId() + " with permission " + requiredPermissions);
        }
        return thing;
    }

}
