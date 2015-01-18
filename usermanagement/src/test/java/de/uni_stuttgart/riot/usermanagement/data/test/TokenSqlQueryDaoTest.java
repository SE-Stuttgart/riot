package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class TokenSqlQueryDaoTest extends DaoTestBase {

    @Test
    public void insertAndFindTest() throws DatasourceInsertException, DatasourceFindException, SQLException {
        TokenSqlQueryDAO dao = new TokenSqlQueryDAO(this.getConn(), false);
        Token testToken = new Token(new Long(42), new Long(1), "TestToken", "R", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis() + 10000), true);
        dao.insert(testToken);
        Token findToken = dao.findBy(testToken.getId());
        assertEquals(testToken.getTokenValue(), findToken.getTokenValue());
    }

    @Test
    public void findUpdateFindTest() throws DatasourceFindException, DatasourceUpdateException, SQLException {
        TokenSqlQueryDAO dao = new TokenSqlQueryDAO(this.getConn(), false);
        Token findToken = dao.findBy(new Long(1));
        findToken.setTokenValue("testvalue");
        dao.update(findToken);
        Token findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findToken);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceDeleteException, DatasourceFindException, SQLException {
        TokenSqlQueryDAO dao = new TokenSqlQueryDAO(this.getConn(), false);
        Token token = dao.findBy(new Long(1));
        dao.delete(token);
        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceUpdateException, SQLException {
        TokenSqlQueryDAO dao = new TokenSqlQueryDAO(this.getConn(), false);
        dao.update(new Token(new Long(32), new Long(34), "", "", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), true));
    }

    @Test
    public void findAllTest() throws DatasourceFindException, SQLException {
        TokenSqlQueryDAO dao = new TokenSqlQueryDAO(this.getConn(), false);
        Collection<Token> token = dao.findAll();
        assertEquals(3, token.size());
    }
}
