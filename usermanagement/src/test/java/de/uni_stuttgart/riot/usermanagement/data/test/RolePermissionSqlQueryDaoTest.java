package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.RolePermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class RolePermissionSqlQueryDaoTest extends DaoTestBase {

    @Test
    public void insertAndFindTest() throws DatasourceException {
        RolePermissionSqlQueryDAO dao = new RolePermissionSqlQueryDAO(getDataSource());
        RolePermission testrolePermission = new RolePermission(1l, 2l, -1l);
        dao.insert(testrolePermission);
        RolePermission findRolePermission = dao.findBy(testrolePermission.getId());
        assertEquals(findRolePermission, findRolePermission);
    }

    @Test
    public void FindUpdateFindTest() throws DatasourceUpdateException, DatasourceFindException {
        RolePermissionSqlQueryDAO dao = new RolePermissionSqlQueryDAO(getDataSource());
        RolePermission findRolePermission = dao.findBy(new Long(1));
        // RolePermissions are not mutable at the time, only to test the update funktion.
        dao.update(findRolePermission);
        RolePermission findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findRolePermission);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceFindException, DatasourceDeleteException {
        RolePermissionSqlQueryDAO dao = new RolePermissionSqlQueryDAO(getDataSource());
        RolePermission rolePermission = dao.findBy(new Long(1));
        dao.delete(rolePermission);
        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceUpdateException {
        RolePermissionSqlQueryDAO dao = new RolePermissionSqlQueryDAO(getDataSource());
        dao.update(new RolePermission(new Long(32), new Long(32), new Long(32)));
    }

    @Test
    public void findAllTest() throws DatasourceFindException {
        RolePermissionSqlQueryDAO dao = new RolePermissionSqlQueryDAO(getDataSource());
        Collection<RolePermission> RolePermission = dao.findAll();
        assertEquals(4, RolePermission.size());
    }

}