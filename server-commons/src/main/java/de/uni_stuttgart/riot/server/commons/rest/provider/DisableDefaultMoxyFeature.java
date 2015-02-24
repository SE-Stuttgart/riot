package de.uni_stuttgart.riot.server.commons.rest.provider;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.CommonProperties;

/**
 * Deactivates the default MOXy JSON provider in the application server container. This will allow for alternative JSON wrappers to be used.
 * 
 * @see http://stackoverflow.com/questions/25528948/cant-make-glassfish-use-my-messagebodywriter-in-jax-rs
 * @author Philipp Keck
 */
@Provider
public class DisableDefaultMoxyFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.property(CommonProperties.MOXY_JSON_FEATURE_DISABLE + '.' + context.getConfiguration().getRuntimeType().name().toLowerCase(), true);
        context.property(CommonProperties.MOXY_JSON_FEATURE_DISABLE, true);
        return true;
    }

}
