package de.uni_stuttgart.riot.android.database;


public class DatabaseAccess {

    private static RIOTDatabase database;

    public static RIOTDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(RIOTDatabase database) {
        DatabaseAccess.database = database;
    }

}
