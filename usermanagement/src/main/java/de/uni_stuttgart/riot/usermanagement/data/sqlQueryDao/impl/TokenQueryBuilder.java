package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;

/**
 * {@link QueryBuilder} for {@link Token}. Used in {@link TokenSqlQueryDAO}.
 * @author Jonas Tangermann
 *
 */
public class TokenQueryBuilder extends StorableQueryBuilder implements QueryBuilder<Token> {

    private static final String DELETE_QUERY = "DELETE FROM tokens WHERE tokens.tokenID = ?";
    private static final String INSERT_QUERY = "INSERT INTO tokens(userID,tokenValue,refreshtokenvalue,expirationdate,valid)VALUES (?,?,?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE tokens SET userid=?, tokenvalue=?,refreshtokenvalue=?, issuedate=?, expirationdate=?, valid=? WHERE tokenID=?";
    private static final String FIND_ID_QUERY = "SELECT tokenid, userid, tokenvalue, refreshtokenvalue, issuedate, expirationdate, valid FROM tokens WHERE tokenID = ?;";
    private static final String FIND_PARAM_QUERY = "SELECT tokenid, userid, tokenvalue, refreshtokenvalue, issuedate, expirationdate, valid FROM tokens ";
    private static final String FIND_ALL_QUERY = "SELECT tokenid, userid, tokenvalue, refreshtokenvalue, issuedate, expirationdate, valid FROM tokens";

    @Override
    public PreparedStatement buildDelete(Token t, Connection connection) throws SQLException {
        return super.buildDelete(t, connection, DELETE_QUERY);
    }

    @Override
    public PreparedStatement buildInsert(Token t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
        stmt.setLong(1, t.getUserID());
        stmt.setString(2, t.getTokenValue());
        stmt.setString(3, t.getRefreshtokenValue());
        stmt.setTimestamp(4, t.getExpirationTime());
        stmt.setBoolean(5, t.isValid());
        return stmt;
    }

    @Override
    public PreparedStatement buildUpdate(Token t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(UPDATE_QUERY);
        stmt.setLong(1, t.getUserID());
        stmt.setString(2, t.getTokenValue());
        stmt.setString(3, t.getRefreshtokenValue());
        stmt.setTimestamp(4, t.getIssueTime());
        stmt.setTimestamp(5, t.getExpirationTime());
        stmt.setBoolean(6, t.isValid());
        stmt.setLong(7, t.getId());
        return stmt;
    }

    @Override
    public PreparedStatement buildFindByID(Long id, Connection connection) throws SQLException {
        return super.buildFindByID(id, connection, FIND_ID_QUERY);
    }

    @Override
    public PreparedStatement buildFindBySearchParam(Collection<SearchParameter> params, Connection connection, boolean or) throws SQLException {
        return super.buildFindBySearchParam(params, connection, FIND_PARAM_QUERY, or);
    }

    @Override
    public PreparedStatement buildFindAll(Connection connection) throws SQLException {
        return super.buildFindAll(connection, FIND_ALL_QUERY);
    }

}
