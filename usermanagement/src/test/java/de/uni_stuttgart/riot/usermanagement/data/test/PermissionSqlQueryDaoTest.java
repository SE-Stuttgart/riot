package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class PermissionSqlQueryDaoTest extends DaoTestBase {

    @Test
    public void insertAndFindTest() throws DatasourceException, SQLException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(this.getConn(), false);
        Permission testPermission = new Permission(new Long(42), "TestPermission");
        dao.insert(testPermission);
        Permission findPermission = dao.findBy(testPermission.getId());
        assertEquals(testPermission, findPermission);
    }

    @Test
    public void findUpdateFindTest() throws DatasourceException, SQLException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(this.getConn(), false);
        Permission findPermission = dao.findBy(new Long(1));
        findPermission.setPermissionValue("testPermissionName");
        dao.update(findPermission);
        Permission findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findPermission);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceException, SQLException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(this.getConn(), false);
        Permission permission = dao.findBy(new Long(1));
        dao.delete(permission);
        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceException, SQLException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(this.getConn(), false);
        dao.update(new Permission(new Long(32), ""));
    }

    @Test
    public void findByParamTest() throws DatasourceException, SQLException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(this.getConn(), false);
        LinkedList<SearchParameter> param = new LinkedList<>();
        param.add(new SearchParameter(SearchFields.PERMISSIONVALUE, "x"));
        param.add(new SearchParameter(SearchFields.PERMISSIONVALUE, "y"));

        Collection<Permission> ps = dao.findBy(param, true);
        assertEquals(2, ps.size());
    }

    @Test
    public void findAllTest() throws DatasourceException, SQLException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(this.getConn(), false);

        Collection<Permission> permission = dao.findAll();
        assertEquals(4, permission.size());
    }

}
