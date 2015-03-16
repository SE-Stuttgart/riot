package de.uni_stuttgart.riot.websocket;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.db.thing.Notification;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.NotificationEvent;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.remote.ServerThingBehavior;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetUserException;
import de.uni_stuttgart.riot.usermanagement.security.AccessToken;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * The Class WebSocketEndpoint.
 */
@ServerEndpoint("/connect/{token}")
public class WebSocketEndpoint {

    /** The Object Mapper used for serializing/deserializing. */
    private final ObjectMapper om = RiotApplication.produceObjectMapper();

    /** The Logic for things. */
    private final ThingLogic tl = ThingLogic.getThingLogic();

    /** The session things. */
    private final Map<Session, Collection<Thing>> sessionThings = new HashMap<>();

    static {
        // init shiro
        Factory<SecurityManager> factory = new IniSecurityManagerFactory();
        SecurityUtils.setSecurityManager(factory.getInstance());
    }

    /**
     * Instantiates a new web socket endpoint.
     */
    public WebSocketEndpoint() {
        om.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
    }

    /**
     * Called when a websocket connection is opened.
     *
     * @param token
     *            The token of the user.
     * @param peer
     *            The session associated with a connection.
     * @throws DatasourceFindException
     *             The datasource find exception
     * @throws GetUserException
     *             The get user exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @OnOpen
    public void onOpen(@PathParam("token") String token, Session peer) throws DatasourceFindException, GetUserException, IOException {
        // authenticate the user. Close the connection if not authenticated.
        try {
            SecurityUtils.getSubject().login(new AccessToken(token));
        } catch (AuthenticationException e) {
            peer.close();
            return;
        }

        User currentUser = UserManagementFacade.getInstance().getCurrentUser();

        // listen on all notifications from all things, which belong to the current user
        Collection<Thing> allThings = tl.getAllThings(currentUser.getId(), ThingPermission.EXECUTE);
        sessionThings.put(peer, allThings);

        for (Thing thing : allThings) {
            Collection<NotificationEvent<?>> notifcations = thing.getNotifcations();
            if (notifcations != null) {
                ServerThingBehavior behavior = tl.getBehavior(thing.getId());
                for (NotificationEvent<?> notification : notifcations) {
                    behavior.registerToEvent(notification);
                    notification.register(produceNotificationListener(peer));
                }
            }
        }
    }

    /**
     * Called when a websocket connection is closed.
     *
     * @param peer
     *            The session associated with a connection.
     * @throws GetUserException
     *             The get user exception
     * @throws DatasourceFindException
     *             The datasource find exception
     */
    @OnClose
    public void onClose(Session peer) throws GetUserException, DatasourceFindException {
        Collection<Thing> things = sessionThings.get(peer);
        for (Thing thing : things) {
            Collection<NotificationEvent<?>> notifcations = thing.getNotifcations();
            ServerThingBehavior behavior = tl.getBehavior(thing.getId());
            for (NotificationEvent<?> notification : notifcations) {
                behavior.unregisterFromEvent(notification);
            }
        }
    }

    /**
     * Produce a notification listener.
     *
     * @param peer
     *            The session associated with a connection.
     * @return The newly created listener.
     */
    private EventListener<EventInstance> produceNotificationListener(Session peer) {
        return new EventListener<EventInstance>() {

            @Override
            public void onFired(Event<? extends EventInstance> event, EventInstance eventInstance) {
                Notification wtn = new Notification(event.getThing().getId(), event.getName(), eventInstance.getTime());
                try {
                    peer.getBasicRemote().sendText(om.writeValueAsString(wtn));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        };
    }
}
