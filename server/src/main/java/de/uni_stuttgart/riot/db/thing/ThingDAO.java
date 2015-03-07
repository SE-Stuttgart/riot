package de.uni_stuttgart.riot.db.thing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.ResultSetHandler;

import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
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
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingFactory;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.remote.ServerThingBehavior;

/**
 * A DAO for {@link Thing}s. Note that this DAO works differently than the usual SqlQueryDAOs because it handles all types of things at once
 * and does not store everything about them. Especially the structure of a thing (the events, actions, properties and their names) is not
 * persisted, since it is reflected statically in the respective subclasses of {@link Thing}.
 * 
 * @author Philipp Keck
 */
public class ThingDAO implements DAO<Thing> {

    private static final String TABLE_NAME = "things";

    private final QueryBuilder queryBuilder = new QueryBuilderImpl();
    private final ThingBehaviorFactory<ServerThingBehavior> behaviorFactory;

    /**
     * Creates a new DAO.
     * 
     * @param behaviorFactory
     *            The factory for thing behaviors. When new things are created from the database, this behavior factory will be used.
     */
    public ThingDAO(ThingBehaviorFactory<ServerThingBehavior> behaviorFactory) {
        this.behaviorFactory = behaviorFactory;
    }

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
    public void delete(Thing t) throws DatasourceDeleteException {
        delete(t.getId());
    }

    @Override
    public void delete(long id) throws DatasourceDeleteException {
        try (Connection connection = getConnection()) {
            try (Query stmt = queryBuilder.buildDelete(TABLE_NAME, id, connection)) {
                int res = stmt.executeUpdate().getResult();
                if (res == 0) {
                    throw new DatasourceDeleteException("Nothing to delete!");
                }
            }
        } catch (SQLException e) {
            throw new DatasourceDeleteException(e);
        }
    }

    @Override
    public void insert(Thing t) throws DatasourceInsertException {
        ThingState state = ThingState.create(t);

        try (Connection connection = getConnection()) {
            boolean hasId = t.getId() != null && t.getId() != 0;
            String query;
            if (hasId) {
                query = "INSERT INTO things (id, type, ownerID, name) VALUES (:id, :type, :ownerID, :name)";
            } else {
                query = "INSERT INTO things (type, ownerID, name) VALUES (:type, :ownerID, :name)";
            }
            try (Query stmt = connection.createQuery(query)) {
                if (hasId) {
                    stmt.addParameter("id", t.getId());
                }
                stmt.addParameter("type", t.getClass().getName());
                stmt.addParameter("ownerID", t.getOwnerId());
                stmt.addParameter("name", t.getName());

                long key = stmt.executeUpdate().getKey(Long.class);
                if (key == 0) {
                    throw new DatasourceInsertException("Error on inserting new thing");
                } else if (hasId) {
                    if (key != t.getId()) {
                        throw new DatasourceInsertException("Returned wrong ID on inserting new thing!");
                    }
                } else {
                    if (key <= 0) {
                        throw new DatasourceInsertException("Returned nonpositive ID on inserting new thing!");
                    }
                    t.setId(key);
                }
            }

            updateThingState(connection, t.getId(), state);
        } catch (SQLException e) {
            throw new DatasourceInsertException(e);
        }

    }

    @Override
    public void update(Thing t) throws DatasourceUpdateException {
        ThingState state = ThingState.create(t);

        try (Connection connection = getConnection()) {

            String query = "UPDATE things SET name = :name, ownerID = :ownerID WHERE id = :id";
            try (Query stmt = connection.createQuery(query)) {
                stmt.addParameter("id", t.getId());
                stmt.addParameter("name", t.getName());
                stmt.addParameter("ownerID", t.getOwnerId());
                int res = stmt.executeUpdate().getResult();
                if (res == 0) {
                    throw new DatasourceUpdateException("Nothing to update!");
                }
            }

            updateThingState(connection, t.getId(), state);

        } catch (SQLException e) {
            throw new DatasourceUpdateException(e);
        }
    }

    /**
     * Writes the state of the thing to the database.
     * 
     * @param connection
     *            The connection to be used.
     * @param state
     *            The state of the thing.
     * @throws SQLException
     *             When writing to the database fails.
     */
    private void updateThingState(Connection connection, long thingID, ThingState state) throws SQLException {
        if (state.isEmpty()) {
            return;
        }

        String propertyQuery = "INSERT INTO propertyvalues (thingID, name, val) VALUES (:thingID, :name, :val) ON DUPLICATE KEY UPDATE val = :val";
        try (Query stmt = connection.createQuery(propertyQuery)) {
            for (Map.Entry<String, Object> propertyValue : state.getPropertyValues().entrySet()) {
                stmt.addParameter("thingID", thingID);
                stmt.addParameter("name", propertyValue.getKey());
                stmt.addParameter("val", valueToString(propertyValue.getValue()));
                stmt.addToBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Transforms a value to a String representation for storage in the database.
     * 
     * @param value
     *            The value to be transformed.
     * @return The string representation.
     */
    private static String valueToString(Object value) {
        return value.toString();
    }

    /**
     * Calls the <tt>valueOf</tt> method of the given class <tt>valueType</tt> to convert the given <tt>value</tt>.
     * 
     * @param value
     *            The value.
     * @param valueType
     *            The value type.
     * @return The converted value.
     */
    private static <T> T stringToValue(String value, Class<T> valueType) {
        if (valueType == String.class) {
            return valueType.cast(value);
        }
        try {
            @SuppressWarnings("unchecked")
            T result = (T) valueType.getMethod("valueOf", String.class).invoke(null, value);
            return result;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The type " + valueType + " is not supported!");
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception when calling " + valueType + "#valueOf(" + value + ")");
        }
    }

    @Override
    public Thing findBy(long id) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindByID(TABLE_NAME, id, connection);
            Thing result = stmt.executeAndFetchFirst(new ThingFetcher());
            if (result == null) {
                throw new NotFoundException();
            }
            return result;
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<Thing> findBy(Collection<SearchParameter> searchParams, boolean or) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindBySearchParam(TABLE_NAME, searchParams, connection, or);
            return stmt.executeAndFetch(new ThingFetcher());
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<Thing> findAll() throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindAll(TABLE_NAME, connection);
            return stmt.executeAndFetch(new ThingFetcher());
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<Thing> findAll(int offset, int limit) throws DatasourceFindException {
        if (offset < 0 || limit < 1) {
            throw new DatasourceFindException("Invalid parameter value");
        }
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindWithPagination(TABLE_NAME, connection, offset, limit);
            // pagination
            PaginatedCollection<Thing> resultWithPag = new PaginatedCollection<Thing>(stmt.executeAndFetch(new ThingFetcher()));
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
    public Collection<Thing> findAll(FilteredRequest filter) throws DatasourceFindException {
        if (filter == null || filter.getFilterAttributes() == null || filter.getFilterAttributes().isEmpty()) {
            throw new DatasourceFindException("Filter is null or has no filter attributes.");
        }

        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindWithFiltering(TABLE_NAME, connection, filter, Thing.class);
            Collection<Thing> result = stmt.executeAndFetch(new ThingFetcher());

            // pagination
            if (filter.getLimit() > 0 && filter.getOffset() >= 0) {
                PaginatedCollection<Thing> resultWithPag = new PaginatedCollection<Thing>(result);
                resultWithPag.setLimit(filter.getLimit());
                resultWithPag.setOffset(filter.getOffset());
                stmt = queryBuilder.buildTotalFoundElements(TABLE_NAME, connection, filter, Thing.class);

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
    public Thing findByUniqueField(SearchParameter searchParameter) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
            searchParams.add(searchParameter);
            Query stmt = queryBuilder.buildFindBySearchParam(TABLE_NAME, searchParams, connection, false);
            Thing result = stmt.executeAndFetchFirst(new ThingFetcher());
            if (result == null) {
                throw new NotFoundException();
            }
            return result;
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    /**
     * A handler for entries in a result set of things that will construct a new Thing instance for every line and retrieve the current
     * state of the Thing from the PropertyValue table and then return the filled thing.
     */
    private class ThingFetcher implements ResultSetHandler<Thing> {

        @Override
        public Thing handle(ResultSet resultSet) throws SQLException {
            long thingID = resultSet.getLong("id");
            String typeName = resultSet.getString("type");
            String thingName = resultSet.getString("name");
            ServerThingBehavior behavior = ThingFactory.create(typeName, thingID, thingName, behaviorFactory);
            Thing thing = behavior.getThing();
            thing.setOwnerId(resultSet.getLong("ownerID"));

            // Fetch more information about the thing.
            try (Connection connection = getConnection()) {

                // Its current property values.
                String query = "SELECT name, val FROM propertyvalues WHERE thingID = :thingID";
                try (Query stmt = connection.createQuery(query)) {
                    stmt.addParameter("thingID", thingID);
                    for (UnresolvedEntry value : stmt.executeAndFetch(UnresolvedEntry.class)) {
                        Property<?> property = thing.getProperty(value.name);
                        if (property == null) {
                            throw new SQLException("Database contains value for unknown property " + value.name);
                        }
                        ThingState.silentSetThingProperty(property, stringToValue(value.val, property.getValueType()));
                    }
                }

                // The user permissions for the thing.
                String query2 = "SELECT * FROM things_users WHERE thingID = :thingID";
                try (Query stmt = connection.createQuery(query2)) {
                    stmt.addParameter("thingID", thingID);
                    for (ThingUser thingUser : stmt.executeAndFetch(ThingUser.class)) {
                        behavior.addUserPermission(thingUser.getUserID(), thingUser.getPermission());
                    }
                }
            }

            return thing;
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
