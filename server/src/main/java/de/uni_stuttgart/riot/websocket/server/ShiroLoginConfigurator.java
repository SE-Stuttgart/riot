package de.uni_stuttgart.riot.websocket.server;

import javax.servlet.ServletContext;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.util.WebUtils;

/**
 * This class enables Shiro's authentication mechanisms in web sockets by retrieving the Shiro {@link SecurityManager}, creating a new
 * {@link Subject} to be used by the web socket endpoint and storing it.
 * 
 * @author Philipp Keck
 */
public class ShiroLoginConfigurator extends ServerEndpointConfig.Configurator {

    private final ServletContext servletContext;

    /**
     * Creates a new instance.
     * 
     * @param servletContext
     *            The servlet context.
     */
    public ShiroLoginConfigurator(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {

        // Retrieve the SecurityManager used by the web application.
        WebEnvironment environment = WebUtils.getRequiredWebEnvironment(servletContext);
        org.apache.shiro.mgt.SecurityManager securityManager = environment.getSecurityManager();

        // Web sockets need their own Shiro subject for threading reasons.
        Subject subject = new Subject.Builder(securityManager).buildSubject();
        config.getUserProperties().put(ShiroEndpoint.SUBJECT_KEY, subject);
    }

}
