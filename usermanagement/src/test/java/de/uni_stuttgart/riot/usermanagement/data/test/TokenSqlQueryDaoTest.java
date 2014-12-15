package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class TokenSqlQueryDaoTest extends DaoTestBase{

    @Test
    public void insertAndFindTest() throws DatasourceInsertException, DatasourceFindException {
        TokenSqlQueryDAO dao = new TokenSqlQueryDAO(getDataSource());
        Token testToken = new Token(new Long(42),
                new Long(1),
                "TestToken", "R",
                new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()+10000),
                true);
        dao.insert(testToken);
        Token findToken = dao.findBy(testToken.getId());
        assertEquals(testToken, findToken);
    }

    @Test
    public void FindUpdateFindTest() throws DatasourceFindException, DatasourceUpdateException {
        TokenSqlQueryDAO dao = new TokenSqlQueryDAO(getDataSource());
        Token findToken = dao.findBy(new Long(1));
        findToken.setTokenValue("testvalue");
        dao.update(findToken);
        Token findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findToken);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceDeleteException, DatasourceFindException{
        TokenSqlQueryDAO dao = new TokenSqlQueryDAO(getDataSource());
        Token Token = dao.findBy(new Long(1));
        dao.delete(Token);
        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceUpdateException{
        TokenSqlQueryDAO dao = new TokenSqlQueryDAO(getDataSource());
        dao.update(new Token(new Long(32), new Long(34), "","", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),true));
    }

    @Test
    public void findAllTest() throws DatasourceFindException{
        TokenSqlQueryDAO dao = new TokenSqlQueryDAO(getDataSource());
        Collection<Token> Token = dao.findAll();
        assertEquals(3, Token.size());
    }
}
