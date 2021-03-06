package de.uni_stuttgart.riot.server.commons.db;

import java.sql.SQLException;
import java.util.Collection;

import org.sql2o.Connection;
import org.sql2o.Query;

import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;

/**
 * 
 * This class is used to create the sql2o queries for every operation in {@link DAO}. <br>
 * It is used every {@link SqlQueryDAO}
 * 
 * @author Jonas Tangermann
 * 
 */
public interface QueryBuilder {

    /**
     * Builds the delete query for the given object t. <br>
     * <br>
     * Such as: <code>DELETE FROM T WHERE T.ID = [t's id]</code>
     * 
     * @param tableName
     *            the table name in which the query will be executed
     * @param t
     *            Object to be deleted in the datasource
     * @param connection
     *            represents a connection to the database
     * @return {@link Query} for deletion of t
     * @throws SQLException
     *             internal sql error
     */
    Query buildDelete(String tableName, Storable t, Connection connection) throws SQLException;

    /**
     * Builds the delete query for the given id. <br>
     * <br>
     * Such as: <code>DELETE FROM T WHERE T.ID = [id]</code>
     * 
     * @param tableName
     *            the table name in which the query will be executed
     * @param id
     *            ID of the object to be deleted in the datasource
     * @param connection
     *            represents a connection to the database
     * @return {@link Query} for deletion of the object
     * @throws SQLException
     *             internal sql error
     */
    Query buildDelete(String tableName, long id, Connection connection) throws SQLException;

    /**
     * Builds the insertion query for the given object t. <br>
     * <br>
     * Such as: <code>Insert INTO T (X,Y) VALUES (x,y)</code>
     * 
     * @param tableName
     *            the table name in which the query will be executed
     * @param t
     *            Object to be inserted into the datasource
     * @param connection
     *            represents a connection to the database
     * @return {@link Query} for insertion of t
     * @throws SQLException
     *             internal sql error
     */
    Query buildInsert(String tableName, Storable t, Connection connection) throws SQLException;

    /**
     * Builds the update query for the given object t. <br>
     * <br>
     * Such as: <code>UPDATE T SET X=x, Y=y WHERE t.id = [t's id]</code>
     * 
     * @param tableName
     *            the table name in which the query will be executed
     * @param t
     *            Object to be updated in the datasource
     * @param connection
     *            represents a connection to the database
     * @return {@link Query} for update of t
     * @throws SQLException
     *             internal sql error
     */
    Query buildUpdate(String tableName, Storable t, Connection connection) throws SQLException;

    /**
     * Builds the query for finding an object by its unique id. <br>
     * <br>
     * Such as: <code>SELECT * FROM T WHERE t.id = [t's id]</code>
     * 
     * @param tableName
     *            the table name in which the query will be executed
     * @param id
     *            the unique object id
     * @param connection
     *            represents a connection to the database
     * @return {@link Query} for retrieval of the t object
     * @throws SQLException
     *             internal sql error
     */
    Query buildFindByID(String tableName, Long id, Connection connection) throws SQLException;

    /**
     * Builds the query for finding objects by several search parameters. <br>
     * <br>
     * Such as: <code>SELECT * FROM T WHERE t.id = [t's id] [and | or] [...]</code>
     *
     * @param tableName
     *            the table name in which the query will be executed
     * @param params
     *            the search parameters
     * @param connection
     *            represents a connection to the database
     * @param or
     *            if true 'or' logic will be used. If false 'and' will be used
     * @return {@link Query} for retrieval of all matching t objects
     * @throws SQLException
     *             internal sql error
     */
    Query buildFindBySearchParam(String tableName, Collection<SearchParameter> params, Connection connection, boolean or) throws SQLException;

    /**
     * Builds the query for the retrieval of all objects known to the datasource. <br>
     * <br>
     * Such as: <code>SELECT * FROM T</code>
     * 
     * @param tableName
     *            the table name in which the query will be executed
     * @param connection
     *            represents a connection to the database
     * @return {@link Query} for retrieval of all T objects.
     * @throws SQLException
     *             SQLException internal sql error
     */
    Query buildFindAll(String tableName, Connection connection) throws SQLException;

    /**
     * Builds the query for the retrieval of objects using pagination. <br>
     * <br>
     * Such as: <code>SELECT * FROM T LIMIT [limit] OFFSET [offset] </code>
     * 
     * @param tableName
     *            the table name in which the query will be executed
     * @param connection
     *            represents a connection to the database
     * @param offset
     *            the start point
     * @param limit
     *            the number of objects to return
     * @return {@link Query} for retrieval of all T objects.
     * @throws SQLException
     *             SQLException internal sql error
     */
    Query buildFindWithPagination(String tableName, Connection connection, int offset, int limit) throws SQLException;

    /**
     * Builds the query for the retrieval of objects using filter attributes with/without pagination. <br>
     * <br>
     * Such as: <code>SELECT * FROM T WHERE t.att1 = [value1] [and | or] t.att2 = [value2] [LIMIT...] </code>
     * 
     * @param tableName
     *            the table name in which the query will be executed
     * @param connection
     *            represents a connection to the database
     * @param filter
     *            object containing filter attributes and pagination settings.
     * @param clazz
     *            class type of object in table
     * @return {@link Query} for retrieval of filtered T objects.
     * @throws SQLException
     *             SQLException internal sql error
     * @throws DatasourceFindException
     *             invalid filter
     */
    Query buildFindWithFiltering(String tableName, Connection connection, FilteredRequest filter, Class<?> clazz) throws SQLException, DatasourceFindException;

    /**
     * Builds the sql query to get the total number of entities.
     *
     * @param tableName
     *            the table name
     * @param connection
     *            the connection
     * @return the query
     * @throws SQLException
     *             the SQL exception
     */
    Query buildGetTotal(String tableName, Connection connection) throws SQLException;

    /**
     * Builds the query to get the total number of elements that apply to the filter.
     *
     * @param tableName
     *            the table name
     * @param connection
     *            the connection
     * @param filter
     *            the filter
     * @param clazz
     *            the clazz
     * @return the query
     * @throws SQLException
     *             the SQL exception
     * @throws DatasourceFindException
     *             the datasource find exception
     */
    Query buildTotalFoundElements(String tableName, Connection connection, FilteredRequest filter, Class<?> clazz) throws SQLException, DatasourceFindException;
}
