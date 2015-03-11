package de.uni_stuttgart.riot.reference;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.references.DelegatingReferenceResolver;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.references.TargetNotFoundException;
import de.uni_stuttgart.riot.references.TypedReferenceResolver;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetUserException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * The reference resolver for the server. This resolver delegates the resolving to the individual Logics and DAOs.
 * 
 * @author Philipp Keck
 */
public class ServerReferenceResolver extends DelegatingReferenceResolver {

    /**
     * The resolver for users (just gets them from the user management).
     */
    public static final TypedReferenceResolver<User> USER_RESOLVER = new TypedReferenceResolver<User>() {
        public User resolve(long targetId) throws ResolveReferenceException {
            try {
                return UserManagementFacade.getInstance().getUser(targetId);
            } catch (GetUserException e) {
                // TODO GetUserException should be refactored (i.e. deleted).
                // TODO Then we could distinguish between NotFound and some other errors here.
                throw new TargetNotFoundException(e);
            }
        }
    };

    /**
     * The resolver for things (just gets them from the ThingLogic).
     */
    public static final TypedReferenceResolver<Thing> THING_RESOLVER = new TypedReferenceResolver<Thing>() {
        public Thing resolve(long targetId) throws ResolveReferenceException {
            Thing thing = ThingLogic.getThingLogic().getThing(targetId);
            if (thing == null) {
                throw new TargetNotFoundException("A thing with ID " + targetId + " does not exist!");
            }
            return thing;
        }
    };

    /**
     * Singleton instance.
     */
    private static final ServerReferenceResolver INSTANCE = new ServerReferenceResolver();

    /**
     * Singleton constructor.
     */
    private ServerReferenceResolver() {
        addResolver(User.class, USER_RESOLVER);
        addResolver(Thing.class, THING_RESOLVER);
    }

    /**
     * Singleton instance getter.
     *
     * @return The singleton instance.
     */
    public static ServerReferenceResolver getInstance() {
        return INSTANCE;
    }

}
