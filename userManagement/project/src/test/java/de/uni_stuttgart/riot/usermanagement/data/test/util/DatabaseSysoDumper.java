package de.uni_stuttgart.riot.usermanagement.data.test.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.RolePermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.UserRoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserRole;

public class DatabaseSysoDumper {

	public final DataSource ds;

	public static void main(String[] args) {
		DatabaseSysoDumper dumper = new DatabaseSysoDumper();
		dumper.printOverview();
	}
	
	public void printFull(){
		this.fullDatabaseDumpPrint(new DatabaseSysoDumper.AllPrinter(), this.ds);
	}
	
	public void printOverview(){
		this.fullDatabaseDumpPrint(new DatabaseSysoDumper.OverviewPrinter(), this.ds);
	}
	
	public void fullDatabaseDumpPrint(Printer printer, DataSource ds){
		UserSqlQueryDao userDao = new UserSqlQueryDao(ds);
		UserRoleSqlQueryDAO userRoleDao = new UserRoleSqlQueryDAO(ds);
		RoleSqlQueryDAO roleDao = new RoleSqlQueryDAO(ds);
		RolePermissionSqlQueryDAO rolePermissionDao = new RolePermissionSqlQueryDAO(ds);
		PermissionSqlQueryDAO permissionDao = new PermissionSqlQueryDAO(ds);
		System.out.println(printer.getHeadLine());
		try {
			Collection<User> users = userDao.findAll();
			Iterator<User> iUser = users.iterator();
			while(iUser.hasNext()){
				User current = iUser.next();
				System.out.println(printer.getUserString(current));
				LinkedList<SearchParameter> params = new LinkedList<SearchParameter>();
				params.add(new SearchParameter(SearchFields.USERID, current.getId()));
				Collection<UserRole> userRoles = userRoleDao.findBy(params,false);
				for (UserRole userRole: userRoles) {
					Role role = roleDao.findBy(userRole.getRoleID());
					System.out.println("\t <-"+printer.getUserRoleString(userRole)+ "-> " + printer.getRoleString(role));
					LinkedList<SearchParameter> rolePSearch = new LinkedList<SearchParameter>();
					rolePSearch.add(new SearchParameter(SearchFields.ROLEID, role.getId()));
					Collection<RolePermission> rolePermissions = rolePermissionDao.findBy(rolePSearch,false);
					for (RolePermission rolePermission: rolePermissions) {
						Permission permission = permissionDao.findBy(rolePermission.getPermissionID());
						System.out.println("\t\t <-"+printer.getRolePermissionString(rolePermission)+ "-> " + printer.getPermissionString(permission));
					}
				}
			}
		} catch (DatasourceFindException e) {
			e.printStackTrace();
		}
	}
	
	public interface Printer{
		public String getUserString(User t);
		public String getHeadLine();
		public String getUserRoleString(UserRole t);
		public String getRoleString(Role t);
		public String getRolePermissionString(RolePermission t);
		public String getPermissionString(Permission t);
	}
	
	public class AllPrinter implements Printer {
		public String getUserString(User t) {
			return t.toString();
		}
		public String getUserRoleString(UserRole t) {
			return t.toString();
		}
		public String getRoleString(Role t) {
			return t.toString();
		}
		public String getRolePermissionString(RolePermission t) {
			return t.toString();
		}
		public String getPermissionString(Permission t) {
			return t.toString();
		}
		@Override
		public String getHeadLine() {
			return "";
		}
	}
	
	public class OverviewPrinter implements Printer {
		public String getUserString(User t) {
			return t.getUsername();
		}
		public String getUserRoleString(UserRole t) {
			return "--";
		}
		public String getRoleString(Role t) {
			return t.getRoleName();
		}
		public String getRolePermissionString(RolePermission t) {
			return "--";
		}
		public String getPermissionString(Permission t) {
			return t.getPermissionValue();
		}
		public String getHeadLine() {
			return "Username\tRole\tPermission";
		}
	}

	public DatabaseSysoDumper() {
		PGSimpleDataSource ds = new PGSimpleDataSource();
		ds.setDatabaseName("umdb");
		ds.setUser("umuser");
		ds.setPassword("1q2w3e4r");
		ds.setPortNumber(5432);
		ds.setServerName("localhost");
		this.ds = ds;
	}

}
