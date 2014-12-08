package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.usermanagement.data.storable.Storable;

/**
 * 
 * This class is used to create an object from the values of an {@link ResultSet}.
 * It is used in every {@link SqlQueryDAO}.
 * @author Jonas Tangermann
 *
 * @param <T> Type that should be instantiated by {@link #build(ResultSet)}.
 */
public interface ObjectBuilder<T extends Storable> {

    /**
     * Creates a objcet of type T using the given {@link ResultSet}. 
     * @param resultSet contains the values for the new object
     * @return the new created object
     * @throws SQLException on sql internal error
     */
    T build(ResultSet resultSet) throws SQLException;

}
