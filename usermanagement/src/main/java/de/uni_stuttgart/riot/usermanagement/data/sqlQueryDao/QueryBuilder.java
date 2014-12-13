package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Storable;

/**
 * 
 * This class is used to create the {@link PreparedStatement}s for every operation in {@link DAO}.
 * <br>
 * It is used every {@link SqlQueryDAO}
 * @author Jonas Tangermann
 * 
 * @param <T> Type which is handled by the {@link PreparedStatement}s.
 */
public interface QueryBuilder<T extends Storable> {

    /**
     * Builds the delete query for the given object t.
     * <br><br>
     * Such as: <code>DELETE FROM T WHERE T.ID = [t's id]</code>
     * @param t Object to be deleted in the datasource
     * @param connection 
     * @return {@link PreparedStatement} for deletion of t
     * @throws SQLException internal sql error
     */
    PreparedStatement buildDelete(T t, Connection connection) throws SQLException;

    /**
     * Builds the insertion query for the given object t.
     * <br><br>
     * Such as: <code>Insert INTO T (X,Y) VALUES (x,y)</code>
     * @param t Object to be inserted into the datasource
     * @param connection 
     * @return {@link PreparedStatement} for insertion of t
     * @throws SQLException internal sql error
     */
    PreparedStatement buildInsert(T t, Connection connection) throws SQLException;

    /**
     * Builds the update query for the given object t.
     * <br><br>
     * Such as: <code>UPDATE T SET X=x, Y=y WHERE t.id = [t's id]</code>
     * @param t Object to be updated in the datasource
     * @param connection 
     * @return {@link PreparedStatement} for update of t
     * @throws SQLException internal sql error
     */
     PreparedStatement buildUpdate(T t, Connection connection) throws SQLException;

     /**
      * Builds the query for finding an object by its unique id.
      * <br><br>
      * Such as: <code>SELECT * FROM T WHERE t.id = [t's id]</code>
      * @param id the unique object id 
      * @param connection 
      * @return {@link PreparedStatement} for retrieval of the t object
      * @throws SQLException internal sql error
      */
    PreparedStatement buildFindByID(Long id, Connection connection) throws SQLException;

    /**
     * Builds the query for finding objects by several search parameters.
     * <br><br>
     * Such as: <code>SELECT * FROM T WHERE t.id = [t's id] [and | or] [...]</code>
     * @param params the search parameters
     * @param or if true 'or' logic will be used. If false 'and' will be used
     * @param connection 
     * @return {@link PreparedStatement} for retrieval of all matching t objects
     * @throws SQLException internal sql error
     */
    PreparedStatement buildFindBySearchParam(Collection<SearchParameter> params, Connection connection, boolean or) throws SQLException;

    /**
     * Builds the query for the retrival of all objects knowen to the datasource.
     * <br><br>
     * Such as: <code>SELECT * FROM T</code>
     * @param connection 
     * @return {@link PreparedStatement} for retrieval of all T objects.
     * @throws SQLException SQLException internal sql error
     */
    PreparedStatement buildFindAll(Connection connection) throws SQLException;
}
