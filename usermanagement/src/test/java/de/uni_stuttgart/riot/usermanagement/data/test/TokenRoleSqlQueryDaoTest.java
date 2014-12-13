package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceInsertException;
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
    public void FindUpdateFindTest() {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(getDataSource());
        try {
            TokenRole find = dao.findBy(new Long(1));
            // TokenRoles are not mutable at the time, only to test the update funktion.
            dao.update(find);
            TokenRole findAfterUpdate = dao.findBy(new Long(1));
            assertEquals(findAfterUpdate, find);
        } catch (DatasourceFindException e) {
            fail(e.getMessage());
        } catch (DatasourceUpdateException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteTest() {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(getDataSource());
        try {
            TokenRole TokenRole = dao.findBy(new Long(1));
            dao.delete(TokenRole);
        } catch (DatasourceFindException e) {
            fail(e.getMessage());
        } catch (DatasourceDeleteException e) {
            fail(e.getMessage());
        }
        try {
            dao.findBy(new Long(1));
        } catch (DatasourceFindException e) {
            return;
        }
        fail("Should not be reached because id 1 does not longer exist");
    }

    @Test
    public void errorUpdateTest() {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(getDataSource());
        try {
            dao.update(new TokenRole(new Long(32), new Long(32), new Long(32)));
        } catch (DatasourceUpdateException e) {
            return;
        }
        fail("Should not ne reached because there is no item with id 32");
    }

    @Test
    public void findAllTest() {
        TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(getDataSource());
        try {
            Collection<TokenRole> TokenRole = dao.findAll();
            assertEquals(4, TokenRole.size());
        } catch (DatasourceFindException e) {
            fail(e.getMessage());
        }
    }

}
