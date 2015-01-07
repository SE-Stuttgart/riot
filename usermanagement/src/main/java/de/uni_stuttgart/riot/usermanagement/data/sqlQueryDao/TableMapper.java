package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;
import de.uni_stuttgart.riot.usermanagement.data.storable.Storable;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserRole;

public class TableMapper {

	public static String getTableName(String s){
		if(s.equals("Permission")){
			return "permissions";
		} else if(s.equals("Role")){
			return "roles";
		} else if(s.equals("RolePermission")){
			return "roles_permissions";
		} else if(s.equals("Token")){
			return "tokens";
		} else if(s.equals("UMUser")){
			return "users";
		} else if(s.equals("TokenRole")){
			return "tokens_roles";
		} else if(s.equals("UserRole")){
			return "users_roles";
		}
		return "no table (TableMapper!)";
	}
	
}
