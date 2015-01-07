package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class UserSqlQeryDaoTest extends DaoTestBase {

    @Test
    public void insertAndFindTest() throws DatasourceInsertException, DatasourceFindException, SQLException {
        UserSqlQueryDao dao = new UserSqlQueryDao(this.getConn(),false);
        UMUser testuser = new UMUser(new Long(42), "TestUser", "TestUserPW", "TestUserSalt", 42);
        dao.insert(testuser);
        UMUser findUser = dao.findBy(testuser.getId());
        assertEquals(testuser, findUser);
    }

    @Test
    public void FindUpdateFindTest() throws DatasourceUpdateException, DatasourceFindException, SQLException {
        UserSqlQueryDao dao = new UserSqlQueryDao(this.getConn(),false);
        UMUser findUser = dao.findBy(new Long(1));
        findUser.setUsername("TestUser2");
        dao.update(findUser);
        UMUser findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findUser);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceDeleteException, DatasourceFindException, SQLException {
        UserSqlQueryDao dao = new UserSqlQueryDao(this.getConn(),false);
        UMUser User = dao.findBy(new Long(1));
        dao.delete(User);
        UMUser u = dao.findBy(new Long(1));
        System.out.println(u);
    }

    @Test
    public void findAllTest() throws DatasourceFindException, SQLException {
        UserSqlQueryDao dao = new UserSqlQueryDao(this.getConn(),false);
        Collection<UMUser> user = dao.findAll();
        assertEquals(3, user.size());
    }

    @Test
    public void findUniqueTest() throws DatasourceFindException, SQLException {
        UserSqlQueryDao dao = new UserSqlQueryDao(this.getConn(),false);
        UMUser user = dao.findByUniqueField(new SearchParameter(SearchFields.USERNAME, "Yoda"));
        assertEquals(user.getHashedPassword(), "yPYMjqXzWOPKaAKNJXfEw7Gu3EnckZmoWUuEhOqz/7IqGd4Ub+3/X3uANlO0mkIOqIMhxhUi/ieU1KZt2BK+eg==");
    }
}
