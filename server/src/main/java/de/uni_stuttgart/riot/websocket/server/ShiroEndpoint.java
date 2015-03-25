package de.uni_stuttgart.riot.websocket.server;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.ThreadState;

import de.uni_stuttgart.riot.usermanagement.security.AccessToken;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;
import de.uni_stuttgart.riot.websocket.BaseEndpoint;
import de.uni_stuttgart.riot.websocket.WebSocketMessage;

/**
 * This class enables Shiro's authentication mechanisms in web sockets. The connection must be made on an URI that contains a path-param
 * named <tt>token</tt>. Note that endpoints inheriting from this class must be instantiated with {@link ShiroLoginConfigurator}. This class
 * ensures that all further communications, which happens in one of the pooled threads of Tyrus, is done within the context of the
 * respective subject, i.e., it ensures that the right user is logged in.
 * 
 * @author Philipp Keck
 */
public abstract class ShiroEndpoint extends BaseEndpoint {

    /**
     * The parameter name of the token parameter.
     */
    public static final String TOKEN_PARAM = "token";

    /**
     * Key of the Shiro {@link Subject} in the web socket {@link Session#getUserProperties()}.
     */
    static final String SUBJECT_KEY = "shiro_subject";

    @Override
    public final void onOpen(final Session session, EndpointConfig config) {

        // Retrieve the Shiro subject.
        Subject subject = (Subject) config.getUserProperties().get(SUBJECT_KEY);
        if (subject == null) {
            throw new IllegalStateException("subject missing! This endpoint must be initialized by ShiroLoginConfigurator!");
        }
        config.getUserProperties().remove(SUBJECT_KEY);
        session.getUserProperties().put(SUBJECT_KEY, subject);

        // Authenticate the user. Close the connection if not authenticated.
        try {
            String token = session.getPathParameters().get(TOKEN_PARAM);
            if (token == null) {
                throw new AuthenticationException("Missing token");
            }

            // Shiro login
            subject.login(new AccessToken(token));
        } catch (AuthenticationException e) {
            try {
                session.close();
            } catch (IOException e1) {
                logger.error("Error when aborting web socket connection after failed login", e1);
            }
            return;
        }

        // Build a Shiro ThreadState that we can use to set the subject of the current thread.
        ThreadState threadState = new SubjectThreadState(subject);
        try {
            threadState.bind();
            onOpen(session);
        } finally {
            threadState.restore();
        }

        session.addMessageHandler(WebSocketMessage.class, new MessageHandler.Whole<WebSocketMessage>() {
            public synchronized void onMessage(WebSocketMessage message) {
                try {
                    threadState.bind();
                    ShiroEndpoint.this.onMessage(message, session);
                } finally {
                    threadState.restore();
                }
            }
        });
    }

    /**
     * This method will be called after the session has been opened and the user has been logged in. In this method, the user will be logged
     * in on the current thread, that is, all methods of {@link UserManagementFacade} etc. will work.
     * 
     * @param session
     *            The session.
     */
    public void onOpen(Session session) {
    }

    @Override
    public final void onClose(Session session, CloseReason closeReason) {
        Subject subject = (Subject) session.getUserProperties().get(SUBJECT_KEY);
        if (subject == null) {
            onClose(session);
        } else {
            ThreadState threadState = new SubjectThreadState(subject);
            try {
                threadState.bind();
                onClose(session);
            } finally {
                threadState.restore();
            }
        }
        session.getUserProperties().remove(SUBJECT_KEY);
        super.onClose(session, closeReason);
    }

    /**
     * This method will be called when the session has been closed. In this method, the user will be logged in on the current thread, that
     * is, all methods of {@link UserManagementFacade} etc. will work.
     * 
     * @param session
     *            The session.
     */
    public void onClose(Session session) {
    }

}
