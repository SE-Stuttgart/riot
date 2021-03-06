package de.uni_stuttgart.riot.server.test;

import java.lang.reflect.Field;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.notification.server.NotificationLogic;
import de.uni_stuttgart.riot.reference.ServerReferenceResolver;
import de.uni_stuttgart.riot.rule.server.RuleLogic;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.server.ThingLogic;

/**
 * This class contains helper method to reset singletons and other static parts of the server application.
 * 
 * @author Philipp Keck
 */
public abstract class ResetHelper {

    /**
     * Cannot instantiate.
     */
    private ResetHelper() {
    }

    public static void resetThingLogic() {
        resetInstanceFieldOf(ThingLogic.class);
    }

    public static void resetNotificationLogic() {
        resetInstanceFieldOf(NotificationLogic.class);
    }

    public static void resetRuleLogic() {
        resetInstanceFieldOf(RuleLogic.class);
    }

    private static void resetInstanceFieldOf(Class<?> clazz) {
        try {
            Field instanceField = clazz.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetServerReferenceResolver() {
        ServerReferenceResolver.getInstance().addResolver(Thing.class, ServerReferenceResolver.THING_RESOLVER);
        ServerReferenceResolver.getInstance().addResolver(User.class, ServerReferenceResolver.USER_RESOLVER);
    }

}
