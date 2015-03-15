package de.uni_stuttgart.riot.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.ResultSetHandler;

import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleDescription;
import de.uni_stuttgart.riot.rule.RuleDescriptions;
import de.uni_stuttgart.riot.rule.RuleStatus;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.QueryBuilder;
import de.uni_stuttgart.riot.server.commons.db.QueryBuilderImpl;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.server.commons.db.exception.NotFoundException;
import de.uni_stuttgart.riot.server.commons.rest.PaginatedCollection;
import de.uni_stuttgart.riot.thing.ParameterDescription;

/**
 * A DAO for {@link RuleConfiguration}s. Note that {@link RuleConfiguration}s are actually the instances that users need to store.
 * {@link Rule}s themselves never need to be stored.
 * 
 * TODO This class should be refactored and somehow merged with ThingDAO and the other DAOs. The base SqlQueryDAO currently is not open
 * enough to support everything that is required for using custom ResultHandlers, etc.
 * 
 * @author Philipp Keck
 */
public class RuleConfigurationDAO implements DAO<RuleConfiguration> {

    private static final String TABLE_NAME = "rules";

    private final QueryBuilder queryBuilder = new QueryBuilderImpl();

    /**
     * Opens or retrieves the connection to be used by this DAO.
     * 
     * @return The connection.
     * @throws SQLException
     *             When opening a connection fails.
     */
    private Connection getConnection() throws SQLException {
        return ConnectionMgr.openConnection();
    }

    @Override
    public void insert(RuleConfiguration r) throws DatasourceInsertException {
        try (Connection connection = getConnection()) {
            boolean hasId = r.getId() != null && r.getId() != 0;
            String query;
            if (hasId) {
                query = "INSERT INTO rules (id, type, ownerID, name, status) VALUES (:id, :type, :ownerID, :name, :status)";
            } else {
                query = "INSERT INTO rules (type, ownerID, name, status) VALUES (:type, :ownerID, :name, :status)";
            }
            try (Query stmt = connection.createQuery(query)) {
                if (hasId) {
                    stmt.addParameter("id", r.getId());
                }
                stmt.addParameter("type", r.getType());
                stmt.addParameter("ownerID", r.getOwnerId());
                stmt.addParameter("name", r.getName());
                stmt.addParameter("status", r.getStatus().name());

                long key = stmt.executeUpdate().getKey(Long.class);
                if (key == 0) {
                    throw new DatasourceInsertException("Error on inserting new rule");
                } else if (hasId) {
                    if (key != r.getId()) {
                        throw new DatasourceInsertException("Returned wrong ID on inserting new rule!");
                    }
                } else {
                    if (key <= 0) {
                        throw new DatasourceInsertException("Returned nonpositive ID on inserting new rule!");
                    }
                    r.setId(key);
                }
            }

            saveRuleParameters(connection, r);
        } catch (SQLException e) {
            throw new DatasourceInsertException(e);
        }
    }

    @Override
    public void update(RuleConfiguration r) throws DatasourceUpdateException {
        try (Connection connection = getConnection()) {

            String query = "UPDATE rules SET name = :name, ownerID = :ownerID, status = :status WHERE id = :id";
            try (Query stmt = connection.createQuery(query)) {
                stmt.addParameter("id", r.getId());
                stmt.addParameter("name", r.getName());
                stmt.addParameter("ownerID", r.getOwnerId());
                stmt.addParameter("status", r.getStatus().name());
                int res = stmt.executeUpdate().getResult();
                if (res == 0) {
                    throw new DatasourceUpdateException("NoRuleConfiguration to update!");
                }
            }

            saveRuleParameters(connection, r);

        } catch (SQLException e) {
            throw new DatasourceUpdateException(e);
        }
    }

    /**
     * Writes the parameters of the rule configuration to the database.
     * 
     * @param connection
     *            The connection to be used.
     * @param r
     *            The rule configuration that contains the parameters.
     * @throws SQLException
     *             When writing to the database fails.
     */
    private void saveRuleParameters(Connection connection, RuleConfiguration r) throws SQLException {
        String propertyQuery = "INSERT INTO ruleparameters (ruleID, name, val) VALUES (:ruleID, :name, :val) ON DUPLICATE KEY UPDATE val = :val";
        try (Query stmt = connection.createQuery(propertyQuery)) {
            for (Map.Entry<String, Object> parameterValue : r.getParameterValues().entrySet()) {
                stmt.addParameter("ruleID", r.getId());
                stmt.addParameter("name", parameterValue.getKey());
                stmt.addParameter("val", DBUtils.valueToString(parameterValue.getValue()));
                stmt.addToBatch();
            }
            stmt.executeBatch();
        }
    }

    @Override
    public void delete(RuleConfiguration r) throws DatasourceDeleteException {
        delete(r.getId());
    }

    @Override
    public void delete(long id) throws DatasourceDeleteException {
        try (Connection connection = getConnection()) {
            try (Query stmt = queryBuilder.buildDelete(TABLE_NAME, id, connection)) {
                int res = stmt.executeUpdate().getResult();
                if (res == 0) {
                    throw new DatasourceDeleteException("NoRuleConfiguration to delete!");
                }
            }
        } catch (SQLException e) {
            throw new DatasourceDeleteException(e);
        }
    }

    @Override
    public RuleConfiguration findBy(long id) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindByID(TABLE_NAME, id, connection);
            RuleConfiguration result = stmt.executeAndFetchFirst(new RuleConfigurationFetcher());
            if (result == null) {
                throw new NotFoundException();
            }
            return result;
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<RuleConfiguration> findBy(Collection<SearchParameter> searchParams, boolean or) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindBySearchParam(TABLE_NAME, searchParams, connection, or);
            return stmt.executeAndFetch(new RuleConfigurationFetcher());
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<RuleConfiguration> findAll() throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindAll(TABLE_NAME, connection);
            return stmt.executeAndFetch(new RuleConfigurationFetcher());
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<RuleConfiguration> findAll(int offset, int limit) throws DatasourceFindException {
        if (offset < 0 || limit < 1) {
            throw new DatasourceFindException("Invalid parameter value");
        }
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindWithPagination(TABLE_NAME, connection, offset, limit);
            // pagination
            PaginatedCollection<RuleConfiguration> resultWithPag = new PaginatedCollection<RuleConfiguration>(stmt.executeAndFetch(new RuleConfigurationFetcher()));
            resultWithPag.setLimit(limit);
            resultWithPag.setOffset(offset);

            stmt = queryBuilder.buildGetTotal(TABLE_NAME, connection);
            resultWithPag.setTotal(stmt.executeAndFetchFirst(Integer.class));

            return resultWithPag;
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<RuleConfiguration> findAll(FilteredRequest filter) throws DatasourceFindException {
        if (filter == null || filter.getFilterAttributes() == null || filter.getFilterAttributes().isEmpty()) {
            throw new DatasourceFindException("Filter is null or has no filter attributes.");
        }

        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindWithFiltering(TABLE_NAME, connection, filter, RuleConfiguration.class);
            Collection<RuleConfiguration> result = stmt.executeAndFetch(new RuleConfigurationFetcher());

            // pagination
            if (filter.getLimit() > 0 && filter.getOffset() >= 0) {
                PaginatedCollection<RuleConfiguration> resultWithPag = new PaginatedCollection<RuleConfiguration>(result);
                resultWithPag.setLimit(filter.getLimit());
                resultWithPag.setOffset(filter.getOffset());
                stmt = queryBuilder.buildTotalFoundElements(TABLE_NAME, connection, filter, RuleConfiguration.class);

                resultWithPag.setTotal(stmt.executeAndFetchFirst(Integer.class));
                return resultWithPag;
            } else {
                return result;
            }

        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public RuleConfiguration findByUniqueField(SearchParameter searchParameter) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
            searchParams.add(searchParameter);
            Query stmt = queryBuilder.buildFindBySearchParam(TABLE_NAME, searchParams, connection, false);
            RuleConfiguration result = stmt.executeAndFetchFirst(new RuleConfigurationFetcher());
            if (result == null) {
                throw new NotFoundException();
            }
            return result;
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    /**
     * A handler for entries in a result set of rule configurations that will construct a new RuleConfiguration instance for every line and
     * retrieve the current parameter values of the configuration from the ruleparameters table and then return the filled rule.
     */
    private class RuleConfigurationFetcher implements ResultSetHandler<RuleConfiguration> {

        @Override
        public RuleConfiguration handle(ResultSet resultSet) throws SQLException {
            long id = resultSet.getLong("id");
            String type = resultSet.getString("type");
            RuleStatus status = RuleStatus.valueOf(resultSet.getString("status"));
            String name = resultSet.getString("name");
            Long ownerId = resultSet.getLong("ownerId");

            RuleDescription description;
            try {
                description = RuleDescriptions.get(type);
            } catch (ClassNotFoundException | ClassCastException e) {
                throw new SQLException("Could not interpret " + type + " as class name of a Rule implementation!", e);
            }
            Map<String, Object> parameterValues = new HashMap<>();
            try (Connection connection = getConnection()) {
                String query = "SELECT name, val FROM ruleparameters WHERE ruleID = :ruleID";
                try (Query stmt = connection.createQuery(query)) {
                    stmt.addParameter("ruleID", id);
                    for (UnresolvedEntry value : stmt.executeAndFetch(UnresolvedEntry.class)) {
                        ParameterDescription parameter = description.getParameterByName(value.name);
                        if (parameter == null) {
                            throw new SQLException("Database contains value for unknown parameter " + value.name);
                        }
                        parameterValues.put(parameter.getName(), DBUtils.stringToValue(value.val, parameter.getInternalValueType()));
                    }
                }
            }

            return new RuleConfiguration(id, type, status, name, ownerId, parameterValues);
        }
    }

    /**
     * Helper class that contains an entry as a String.
     */
    private static class UnresolvedEntry {
        public String name;
        public String val;
    }

}
