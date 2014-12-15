package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenRoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class TokenRoleSqlQueryDaoTest extends DaoTestBase {

    @Test
    public void insertAndFindTest() throws DatasourceException {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(getDataSource());
        TokenRole testtokenRole = new TokenRole(new Long(42), new Long(2), new Long(1));
        dao.insert(testtokenRole);
        TokenRole find = dao.findBy(testtokenRole.getId());
        assertEquals(find, testtokenRole);
    }

    @Test
    public void FindUpdateFindTest() throws DatasourceFindException, DatasourceUpdateException {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(getDataSource());
        TokenRole find = dao.findBy(new Long(1));
        // TokenRoles are not mutable at the time, only to test the update funktion.
        dao.update(find);
        TokenRole findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, find);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceDeleteException, DatasourceFindException {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(getDataSource());
        TokenRole TokenRole = dao.findBy(new Long(1));
        dao.delete(TokenRole);
        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceUpdateException {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(getDataSource());
        dao.update(new TokenRole(new Long(32), new Long(32), new Long(32)));
    }

    @Test
    public void findAllTest() throws DatasourceFindException {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(getDataSource());
        Collection<TokenRole> TokenRole = dao.findAll();
        assertEquals(4, TokenRole.size());
    }

}
