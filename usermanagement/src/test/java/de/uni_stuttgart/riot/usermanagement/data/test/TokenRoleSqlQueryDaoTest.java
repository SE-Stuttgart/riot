package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.TokenRoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class TokenRoleSqlQueryDaoTest extends DaoTestBase {

    @Test
    public void insertAndFindTest() throws DatasourceException, SQLException {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(this.getConn(),false);
        TokenRole testtokenRole = new TokenRole(new Long(42), new Long(2), new Long(1));
        dao.insert(testtokenRole);
        TokenRole find = dao.findBy(testtokenRole.getId());
        assertEquals(find, testtokenRole);
    }

    @Test
    public void FindUpdateFindTest() throws DatasourceFindException, DatasourceUpdateException, SQLException {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(this.getConn(),false);
        TokenRole find = dao.findBy(new Long(1));
        // TokenRoles are not mutable at the time, only to test the update funktion.
        dao.update(find);
        TokenRole findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, find);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceDeleteException, DatasourceFindException, SQLException {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(this.getConn(),false);
        TokenRole TokenRole = dao.findBy(new Long(1));
        dao.delete(TokenRole);
        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceUpdateException, SQLException {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(this.getConn(),false);
        dao.update(new TokenRole(new Long(32), new Long(32), new Long(32)));
    }

    @Test
    public void findAllTest() throws DatasourceFindException, SQLException {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(this.getConn(),false);
        Collection<TokenRole> TokenRole = dao.findAll();
        assertEquals(4, TokenRole.size());
    }

}
