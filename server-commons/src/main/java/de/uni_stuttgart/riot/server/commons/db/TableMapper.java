package de.uni_stuttgart.riot.server.commons.db;


//FIXME refactor
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
