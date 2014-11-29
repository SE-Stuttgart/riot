package de.uni_stuttgart.riot.data.sqlQueryDao;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.data.storable.Storable;

public interface ObjectBuilder<T extends Storable> {
	
	public T build(ResultSet resultSet) throws SQLException;

}
