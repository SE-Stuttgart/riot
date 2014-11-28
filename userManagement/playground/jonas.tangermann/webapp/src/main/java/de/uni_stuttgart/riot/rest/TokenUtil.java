package de.uni_stuttgart.riot.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class TokenUtil {

	private static final String Query = "SELECT true as Res, * FROM tokens WHERE tokenValue = ? AND issuedate <= now() AND expirationdate >= now()";

	public static boolean isTokenValid(String token) {
		Connection conn = null;
		try{
			DataSource ds = Manager.getUsermanagementManager().getDataSource();
			conn = ds.getConnection();
			PreparedStatement stmt = conn.prepareStatement(Query);
			stmt.setString(1, token);
			ResultSet result = stmt.executeQuery();
			while(result.next()){
				return true;
			}
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
