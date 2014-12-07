package de.uni_stuttgart.riot.usermanagement.data.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ RolePermissionSqlQueryDaoTest.class, RoleSqlQueryDaoTest.class,
		TokenRoleSqlQueryDaoTest.class, TokenSqlQueryDaoTest.class,
		UserRoleSqlQueryDaoTest.class, UserSqlQeryDaoTest.class, PermissionSqlQueryDaoTest.class, MemoryDaoTest.class })
public class AllTests {

}
