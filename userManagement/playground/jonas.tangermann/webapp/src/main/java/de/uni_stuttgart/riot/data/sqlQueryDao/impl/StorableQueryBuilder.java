package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import de.uni_stuttgart.riot.data.storable.Storable;
import de.uni_stuttgart.riot.data.storable.User;

public class StorableQueryBuilder {
	
	public PreparedStatement buildDelete(Storable t, Connection connection,String query) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setLong(1, t.getID());
		return stmt;
	}
	
	public PreparedStatement buildFindByID(Long id, Connection connection,String query)throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setLong(1, id);
		return stmt;
	}
	
	public PreparedStatement buildFindBySearchParam(Collection<String> params,
			Connection connection, String query) throws SQLException {
		query += this.getINQustionmarks(params.size());
		PreparedStatement stmt = connection.prepareStatement(query);
		Iterator<String> i = params.iterator();
		int count = 1;
		while(i.hasNext()){
			stmt.setString(count, i.next());
			count++;
		}
		System.out.println(stmt);
		return stmt;
	}
	
	public String getINQustionmarks(int amount){
		StringBuffer res = new StringBuffer();
		res.append("(");
		for(int i=0; i<amount;i++){
			res.append("?");
			if(i<amount-1) res.append(",");
		}
		res.append(")");
		return res.toString();
	}
}
