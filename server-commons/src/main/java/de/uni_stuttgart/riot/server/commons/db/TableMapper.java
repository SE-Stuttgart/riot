package de.uni_stuttgart.riot.server.commons.db;

/**
 * FIXME refactor.
 *
 */
public class TableMapper {

    private TableMapper() {
    }

    /**
     * gets table name based on the given Class Name.
     * 
     * @param className
     *            the class name
     * @return table name
     */
    public static String getTableName(String className) {
        if (className.equals("Permission")) {
            return "permissions";
        } else if (className.equals("Role")) {
            return "roles";
        } else if (className.equals("RolePermission")) {
            return "roles_permissions";
        } else if (className.equals("Token")) {
            return "tokens";
        } else if (className.equals("UMUser")) {
            return "users";
        } else if (className.equals("TokenRole")) {
            return "tokens_roles";
        } else if (className.equals("UserRole")) {
            return "users_roles";
        } else if (className.equals("CalendarEntry")) {
            return "calendarEntries";
        } else if (className.equals("Contact")) {
            return "contacts";
        } else if (className.equals("ConfigurationEntry")) {
            return "configuration";
        } else if (className.equals("UserPermission")) {
            return "users_permissions";
        }
        return className;
    }

}
