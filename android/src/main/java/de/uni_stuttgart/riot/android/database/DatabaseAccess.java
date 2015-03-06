package de.uni_stuttgart.riot.android.database;

/**
 * Class with static methods to get the database object.
 * 
 * @author Florian
 *
 */
// CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class DatabaseAccess {

    public DatabaseAccess() {

    }

    private static RIOTDatabase database;

    public static RIOTDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(RIOTDatabase database) {
        DatabaseAccess.database = database;
    }

}
