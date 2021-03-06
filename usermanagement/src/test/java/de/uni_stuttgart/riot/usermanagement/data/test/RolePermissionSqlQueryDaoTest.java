package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.RolePermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class RolePermissionSqlQueryDaoTest extends DaoTestBase {
    
    private RolePermissionSqlQueryDAO dao = new RolePermissionSqlQueryDAO();

    @Test
    public void insertAndFindTest() throws DatasourceException, SQLException {
        RolePermission testrolePermission = new RolePermission(1L, 2L, -1L);
        dao.insert(testrolePermission);
        RolePermission findRolePermission = dao.findBy(testrolePermission.getId());
        assertEquals(findRolePermission, findRolePermission);
    }

    @Test
    public void findUpdateFindTest() throws DatasourceUpdateException, DatasourceFindException, SQLException {
        RolePermission findRolePermission = dao.findBy(new Long(1));
        // RolePermissions are not mutable at the time, only to test the update funktion.
        dao.update(findRolePermission);
        RolePermission findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findRolePermission);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceFindException, DatasourceDeleteException, SQLException {
        RolePermission rolePermission = dao.findBy(new Long(1));
        dao.delete(rolePermission);
        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceUpdateException, SQLException {
        dao.update(new RolePermission(new Long(32), new Long(32), new Long(32)));
    }

    @Test
    public void findAllTest() throws DatasourceFindException, SQLException {
        Collection<RolePermission> rolePermission = dao.findAll();
        assertEquals(4, rolePermission.size());
    }

}
