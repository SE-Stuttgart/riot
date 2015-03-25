package de.uni_stuttgart.riot.rule;

import java.util.EnumSet;
import java.util.Set;

import org.apache.shiro.authz.UnauthorizedException;

import de.uni_stuttgart.riot.notification.Notification;
import de.uni_stuttgart.riot.notification.BaseNotificationBuilder;
import de.uni_stuttgart.riot.notification.server.NotificationLogic;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.thing.AuthenticatingThingBehavior;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.server.ThingLogic;

/**
 * A class that can be used in a rule to send notifications.
 * 
 * @author Philipp Keck
 */
public class RuleNotification {

    private final Rule rule;
    private final Notification prototype;

    /**
     * Creates a new RuleNotification.
     * 
     * @param rule
     *            The rule that this notification sender belongs to.
     * @param prototype
     *            A notification object that contains the initial values for all notifications sent with this sender.
     */
    RuleNotification(Rule rule, Notification prototype) {
        this.rule = rule;
        this.prototype = prototype;
    }

    /**
     * Starts creating a new notification.
     * 
     * @return A notification builder. Use the builder methods to refine the notification contents and then call one of the send methods.
     */
    public NotificationSender create() {
        return new NotificationSender();
    }

    /**
     * A special kind of notification builder that allows to send the notifications through the attached rule once it is built.
     */
    public class NotificationSender extends BaseNotificationBuilder<NotificationSender> {

        private NotificationSender() {
            super(prototype.clone());
        }

        /**
         * Sets the {@link Notification#setThingID(Long)}. Note that the owner of the Rule needs to have the {@link ThingPermission#EXECUTE}
         * permission on that thing for the rule to be allowed to send the notification!
         * 
         * @param thing
         *            The thing that the notification refers to.
         * @return The builder.
         */
        public NotificationSender forThing(ThingParameter<? extends Thing> thing) {
            try {
                return forThing(thing.getTarget());
            } catch (ResolveReferenceException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public NotificationSender forThing(Thing thing) {
            if (thing != null) {
                // We need to check the permission!
                AuthenticatingThingBehavior behavior = (AuthenticatingThingBehavior) thing.getBehavior();
                long ownerId = rule.getOwnerId();
                if (!behavior.canAccess(ownerId, ThingPermission.EXECUTE)) {
                    throw new UnauthorizedException("The rule's owner " + ownerId + " does not have the execute permission on thing " + thing.getId());
                }
            }
            return super.forThing(thing);
        }

        /**
         * Sends the built notification to the owner of the rule that built the notification.
         */
        public void sendToRuleOwner() {
            notification.setUserID(rule.getOwnerId());
            NotificationLogic.getNotificationLogic().postNotification(build());
        }

        /**
         * Sends the built notification to the owner of the referenced thing. {@link #forThing(ThingParameter)} must have been called
         * before.
         */
        public void sendToThingOwner() {
            assertHasThing();
            // Note that the rule owner has already been checked for EXECUTE privileges on the Thing,
            // so he is definetly permitted to send a notification to the Thing owner.
            Thing thing = ThingLogic.getThingLogic().getThing(notification.getThingID());
            if (thing == null) {
                throw new IllegalStateException("The specified Thing " + notification.getThingID() + " does not exist!");
            }
            notification.setUserID(thing.getOwnerId());
            NotificationLogic.getNotificationLogic().postNotification(build());
        }

        /**
         * Sends the built notification to all users who have the specified <tt>permissions</tt> on the thing referenced by the
         * notification. {@link #forThing(ThingParameter)} must have been called before.
         * 
         * @param permissions
         *            The permissions that recipients of the notification must have on the thing. If this parameter is empty, the
         *            {@link ThingPermission#READ} permission will be required.
         */
        public void sendToThingUsers(ThingPermission... permissions) {
            assertHasThing();
            Set<ThingPermission> permissionsSet;
            if (permissions.length == 0) {
                permissionsSet = EnumSet.of(ThingPermission.READ);
            } else {
                permissionsSet = EnumSet.of(permissions[0], permissions);
            }
            try {
                NotificationLogic.getNotificationLogic().postNotificationToPermittedUsers(build(), permissionsSet);
            } catch (DatasourceFindException e) {
                throw new IllegalStateException(e);
            }
        }

        private void assertHasThing() {
            if (notification.getThingID() == null || notification.getThingID() < 1) {
                throw new IllegalStateException("forThing() has not been called!");
            }
        }

    }

}
