package de.uni_stuttgart.riot.rule.client;

import java.io.IOException;
import java.util.Collection;

import de.uni_stuttgart.riot.clientlibrary.BaseClient;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleDescription;

/**
 * Rest Client for handling {@link Rule} operations.
 */
public class RuleClient extends BaseClient {

    private static final String RULES_PREFIX = "rules/";

    private static final String POST_ADD_RULE = "rules";
    private static final String GET_RULE = RULES_PREFIX;
    private static final String PUT_UPDATE_RULE = RULES_PREFIX;
    private static final String DELETE_RULE = RULES_PREFIX;

    private static final String GET_RULE_DESCRIPTION = RULES_PREFIX + "description/";
    private static final String GET_RULE_DESCRIPTIONS = RULES_PREFIX + "descriptions";

    /**
     * Creates a new RuleClient.
     * 
     * @param connector
     *            The {@link ServerConnector} to be used.
     */
    public RuleClient(ServerConnector connector) {
        super(connector);
    }

    /**
     * Gets a description of a rule type. See {@link RuleDescription} for details about the JSON format.
     *
     * @param type
     *            The fully qualified class name to identify the rule type.
     * @return A description of the rule's structure.
     * @throws NotFoundException
     *             When the given rule type does not exist.
     * @throws RequestException
     *             When executing the request failed.
     * @throws IOException
     *             When a network error occured.
     */
    public RuleDescription getRuleDescription(String type) throws IOException, RequestException, NotFoundException {
        return getConnector().doGET(GET_RULE_DESCRIPTION + type, RuleDescription.class);
    }

    /**
     * Gets a list of all known rule descriptions. This can be used to display to the user and then let him/her choose which rule to
     * instantiate.
     * 
     * @return All rule descriptions.
     * @throws RequestException
     *             When executing the request failed.
     * @throws IOException
     *             When a network error occured.
     */
    public Collection<RuleDescription> getRuleDescriptions() throws IOException, RequestException {
        try {
            return getConnector().doGETCollection(GET_RULE_DESCRIPTIONS, RuleDescription.class);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Gets the current configuration of a rule. This is a JSON object containing the rule's type, name, id, parameters and status.
     *
     * @param id
     *            The id of the rule.
     * @return The rule's configuration.
     * @throws NotFoundException
     *             When a rule with the given ID does not exist.
     * @throws RequestException
     *             When executing the request failed.
     * @throws IOException
     *             When a network error occured.
     */
    public RuleConfiguration getRule(long id) throws IOException, RequestException, NotFoundException {
        return getConnector().doGET(GET_RULE + id, RuleConfiguration.class);
    }

    /**
     * Returns all rules that are assigned to the logged-in user.
     * 
     * @return The list of rules.
     * @throws RequestException
     *             When the request to the server failed.
     * @throws IOException
     *             When a network error occured or the result format could not be read.
     */
    public Collection<RuleConfiguration> getRules() throws RequestException, IOException {
        try {
            return getConnector().doGETCollection(RULES_PREFIX, RuleConfiguration.class);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Creates a new rule and starts it.
     *
     * @param config
     *            The new rule's configuration. ownerID and ID do not need to be set and will be overridden.
     * @return The new rule configuration (as sent back by the server, will contain onwerID and ID).
     * @throws RequestException
     *             When executing the request failed.
     * @throws IOException
     *             When a network error occured.
     */
    public RuleConfiguration addNewRule(RuleConfiguration config) throws IOException, RequestException {
        return getConnector().doPOST(POST_ADD_RULE, config, RuleConfiguration.class);
    }

    /**
     * Updates the configuration of a rule. This can be used to start and stop the rule.
     * 
     * @param config
     *            The new configuration for the rule.
     * @throws RequestException
     *             When executing the request failed.
     * @throws IOException
     *             When a network error occured.
     */
    public void updateRuleConfiguration(RuleConfiguration config) throws IOException, RequestException {
        getConnector().doPUT(PUT_UPDATE_RULE + config.getId(), config);
    }

    /**
     * Deletes the rule with the given id.
     *
     * @param id
     *            the id
     * @throws RequestException
     *             When executing the request failed.
     * @throws IOException
     *             When a network error occured.
     */
    public void deleteRule(long id) throws IOException, RequestException {
        getConnector().doDELETE(DELETE_RULE + id);
    }
}
