package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class PermissionSqlQueryDaoTest extends DaoTestBase {

    @Test
    public void insertAndFindTest() throws DatasourceException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(getDataSource());
        Permission testPermission = new Permission(new Long(42), "TestPermission");
        dao.insert(testPermission);
        Permission findPermission = dao.findBy(testPermission.getId());
        assertEquals(testPermission, findPermission);
    }

    @Test
    public void FindUpdateFindTest() throws DatasourceException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(getDataSource());
        Permission findPermission = dao.findBy(new Long(1));
        findPermission.setPermissionValue("testPermissionName");
        dao.update(findPermission);
        Permission findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findPermission);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(getDataSource());
        try {
            Permission Permission = dao.findBy(new Long(1));
            dao.delete(Permission);
        } catch (DatasourceFindException e) {
            fail(e.getMessage()); // Shouldn't occur yet
        }

        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(getDataSource());
        dao.update(new Permission(new Long(32), ""));
    }

    @Test
    public void findByParamTest() throws DatasourceException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(getDataSource());
        LinkedList<SearchParameter> param = new LinkedList<>();
        param.add(new SearchParameter(SearchFields.PERMISSIONVALUE, "x"));
        param.add(new SearchParameter(SearchFields.PERMISSIONVALUE, "y"));

        Collection<Permission> ps = dao.findBy(param, true);
        assertEquals(2, ps.size());
    }

    @Test
    public void findAllTest() throws DatasourceException {
        PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(getDataSource());

        Collection<Permission> Permission = dao.findAll();
        assertEquals(4, Permission.size());
    }

}
