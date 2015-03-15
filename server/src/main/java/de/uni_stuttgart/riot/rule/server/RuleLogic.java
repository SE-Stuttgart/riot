package de.uni_stuttgart.riot.rule.server;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.db.RuleConfigurationDAO;
import de.uni_stuttgart.riot.rule.IllegalRuleConfigurationException;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleStatus;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.thing.server.ThingLogic;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * This is the main interface for {@link Rule} handling on the server. Note that many actions are actually based on
 * {@link RuleConfiguration}s instead of {@link Rule}s.
 */
public class RuleLogic {

    /**
     * Instance of the singleton pattern.
     */
    private static RuleLogic instance;

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(RuleLogic.class);

    /**
     * Contains all known rules. These need to be held in memory to allow for links like event listeners, etc. It is important to use a Map
     * implementation that ensures a consistent order on the entries because paginated requests are done directly on this Map.
     */
    private final Map<Long, Rule> rules = new TreeMap<>();

    /**
     * The RuleConfiguration DAO.
     */
    private final RuleConfigurationDAO ruleConfigurationDAO = new RuleConfigurationDAO();

    /**
     * The User Management Facade.
     */
    private final UserManagementFacade umFacade = UserManagementFacade.getInstance();

    /**
     * Creates a new RuleLogic instance.
     * 
     * @throws DatasourceFindException
     *             If the rules could not be loaded.
     */
    private RuleLogic() throws DatasourceFindException {
        this.initStoredRules();
    }

    /**
     * Getter for {@link RuleLogic}.
     * 
     * @return Instance of {@link RuleLogic}
     * @throws DatasourceFindException
     *             Exception on initialization of Rule due to datasource exception.
     */
    public static RuleLogic getRuleLogic() {
        if (instance == null) {
            try {
                instance = new RuleLogic();
            } catch (DatasourceFindException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    /**
     * Reads all stored rules from the database.
     * 
     * @throws DatasourceFindException
     */
    private void initStoredRules() throws DatasourceFindException {

        // First check that all things are loaded by instantiating the ThingLogic.
        ThingLogic.getThingLogic();

        // Then fetch all the rules from the DB and start the enabled ones.
        for (RuleConfiguration config : ruleConfigurationDAO.findAll()) {
            try {
                launchRule(config);
            } catch (IllegalRuleConfigurationException e) {
                logger.error("Error launching rule {}", config.getId(), e);
            }
        }
    }

    /**
     * Creates a Rule instance for the given configuration and starts it. Note the rule will only actually be started if
     * {@link RuleConfiguration#getStatus()} is {@link RuleStatus#ACTIVE}. If the operation is successful, the rule will be added to
     * {@link #rules}. This method throws {@link IllegalRuleConfigurationException}
     * 
     * @param config
     *            The rule configuration.
     * @return The rule that executes the configuration.
     * @throws IllegalRuleConfigurationException
     *             When the operation failed because the configuration was invalid.
     */
    private Rule launchRule(RuleConfiguration config) throws IllegalRuleConfigurationException {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null!");
        } else if (config.getId() == null || config.getId().equals(0)) {
            throw new IllegalRuleConfigurationException("ID must not be empty!");
        } else if (config.getOwnerId() == 0) {
            throw new IllegalRuleConfigurationException("ownerID must not be empty!");
        }

        Rule rule;
        try {
            Class<? extends Rule> ruleClass = Class.forName(config.getType()).asSubclass(Rule.class);
            rule = ruleClass.newInstance();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IllegalRuleConfigurationException("Invalid rule type", e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not start rule of type " + config.getType(), e);
        }

        rule.setConfiguration(config);
        rules.put(config.getId(), rule);
        rule.startExecution();
        return rule;
    }

    /**
     * Returns a RuleConfiguration by its ID.
     * 
     * @param id
     *            The ID of the rule.
     * @return The rule or <tt>null</tt> if it could not be found.
     */
    public RuleConfiguration getRuleConfiguration(long id) {
        Rule rule = rules.get(id);
        return rule == null ? null : rule.getConfiguration();
    }

    /**
     * Gets a stream of all rules that are owned by the given user.
     * 
     * @param userId
     *            The id of the user. If this is <tt>null</tt>, it will be substituted by the ID of the current user.
     * @return The matching rules.
     */
    private Stream<RuleConfiguration> getRulesStream(Long userId) {
        long finalUserId = (userId == null) ? umFacade.getCurrentUserId() : userId;
        return rules.values().stream().map(Rule::getConfiguration).filter((rule) -> finalUserId == rule.getOwnerId());
    }

    /**
     * Returns a filtered collection of those rules that the current user owns.
     * 
     * @param filter
     *            The filter.
     * @param userId
     *            The id of the user. If this is <tt>null</tt>, it will be substituted by the ID of the current user.
     * @return The rules that match the filter.
     */
    public Collection<RuleConfiguration> findRules(FilteredRequest filter, Long userId) {
        Stream<RuleConfiguration> stream = getRulesStream(userId);

        if (!filter.getFilterAttributes().isEmpty()) {
            stream = stream.filter(rule -> {
                Predicate<? super FilterAttribute> predicate = (attribute) -> {
                    switch (attribute.getFieldName()) {
                    case "id":
                    case "ruleID":
                        return attribute.evaluate(rule.getId());
                    case "name":
                        return attribute.evaluate(rule.getName());
                    case "type":
                        return attribute.evaluate(rule.getClass().getName()) || attribute.evaluate(rule.getClass().getSimpleName());
                    case "status":
                        return attribute.evaluate(rule.getStatus().name());
                    default:
                        return false;
                    }
                };

                if (filter.isOrMode()) {
                    return filter.getFilterAttributes().stream().anyMatch(predicate);
                } else {
                    return filter.getFilterAttributes().stream().allMatch(predicate);
                }
            });

        }

        stream = stream.skip(filter.getOffset());
        if (filter.getLimit() > 0) {
            stream = stream.limit(filter.getLimit());
        }

        return stream.collect(Collectors.toList());
    }

    /**
     * Returns a paginated collection of rules owned by the given user.
     * 
     * @param offset
     *            The offset (first index to be returned).
     * @param limit
     *            The number of rules to be returned.
     * @param userId
     *            The id of the user. If this is <tt>null</tt>, it will be substituted by the ID of the current user.
     * @return The matching collection of rules.
     */
    public Collection<RuleConfiguration> findRules(int offset, int limit, Long userId) {
        Stream<RuleConfiguration> stream = getRulesStream(userId).skip(offset);
        if (limit > 0) {
            stream = stream.limit(limit);
        }
        return stream.collect(Collectors.toList());
    }

    /**
     * Returns a collection of all known rules owned by the given user.
     * 
     * @param userId
     *            The id of the user. If this is <tt>null</tt>, it will be substituted by the ID of the current user.
     * @return All rules.
     */
    public Collection<RuleConfiguration> getAllRules(Long userId) {
        return getRulesStream(userId).collect(Collectors.toList());
    }

    /**
     * Adds a new rule (and launches it if the rule configuration says that it is active).
     * 
     * @param config
     *            The rule configuration.
     * @param ownerId
     *            The User ID of the user who owns the rule. This will override {@link RuleConfiguration#getOwnerId()}.
     * @return The persisted configuration of the new rule.
     * @throws DatasourceInsertException
     *             Exception during creation of the rule or during insertion of the configuration into the datasource.
     */
    public synchronized RuleConfiguration addNewRule(RuleConfiguration config, long ownerId) throws DatasourceInsertException {
        try {
            config.setOwnerId(ownerId);
            ruleConfigurationDAO.insert(config);
            try {
                launchRule(config);
            } catch (Exception e2) {
                ruleConfigurationDAO.delete(config.getId());
                throw e2;
            }
            return config;
        } catch (Exception e) {
            throw new DatasourceInsertException(e);
        }
    }

    /**
     * Updates the rule configuration of an existing rule. This can also be used to launch or stop the rule by setting
     * {@link RuleConfiguration#getStatus()}.
     * 
     * @param config
     *            The new rule configuration. This must include the {@link RuleConfiguration#getId()} of the old rule.
     * @throws DatasourceUpdateException
     *             When updating the configuration in the database fails or when relaunching the rule fails.
     */
    public synchronized void updateRuleConfiguration(RuleConfiguration config) throws DatasourceUpdateException {
        try {
            if (config.getId() == null || config.getId().equals(0)) {
                throw new DatasourceUpdateException("Missing ID!");
            }

            Rule rule = rules.get(config.getId());
            if (rule == null) {
                throw new DatasourceUpdateException("Unknown rule with ID " + config.getId());
            } else if (rule.getConfiguration().getOwnerId() != config.getOwnerId()) {
                throw new DatasourceUpdateException("Must not change owner ID!");
            }

            ruleConfigurationDAO.update(config);
            rule.setConfiguration(config);
            rule.startExecution();
        } catch (Exception e) {
            throw new DatasourceUpdateException(e);
        }
    }

    /**
     * Deletes a rule. Includes stopping the rule and removal of the rule from the datasource.
     * 
     * @param id
     *            rule id of the rule to be unregistered.
     * @throws DatasourceDeleteException
     *             Exception during removal form datasource, e.g. no rule with given id exists in the datasourc or stopping failed.
     */
    public synchronized void deleteRule(long id) throws DatasourceDeleteException {
        Rule rule = rules.get(id);
        if (rule == null) {
            throw new DatasourceDeleteException("Rule with id " + id + " does not exist!");
        }
        rule.stopExecution();
        ruleConfigurationDAO.delete(id);
        rules.remove(id);
    }

}
