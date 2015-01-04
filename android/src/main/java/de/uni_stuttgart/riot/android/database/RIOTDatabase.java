package de.uni_stuttgart.riot.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.uni_stuttgart.riot.android.NotificationType;
import de.uni_stuttgart.riot.android.communication.Notification;

public class RIOTDatabase extends SQLiteOpenHelper {

	private final static int DATABASE_VERION = 1;
	private final static String DATABASE_NAME = "Database";

	private static final String TABLE_FILTER = "filter";
	private final static String FILTER_COLUMN_ID = "id";
	private final static String FILTER_COLUMN_TYPE = "type";
	private final static String FILTER_COLUMN_CHECKED = "is_checked";

	private final static String TABLE_NOTIFICATION = "notification";
	private final static String NOTIFICATION_COLUMN_TITLE = "title";
	private final static String NOTIFICATION_COLUMN_TYPE = "type";
	private final static String NOTIFICATION_COLUMN_ID = "id";
	private final static String NOTIFICATION_COLUMN_CONTENT = "content";
	private final static String NOTIFICATION_COLUMN_DATE = "date";

	private final static String TABLE_LANGUAGE = "language";
	private final static String LANGUAGE_COLUMN_ID = "id";
	private final static String LANGUAGE_COLUMN_DESC = "description";

	public RIOTDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERION);
	}

	/**
	 * Creating the tables.
	 */
	public void onCreate(SQLiteDatabase db) {
		String CREATE_FILTER_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_FILTER + "(" + FILTER_COLUMN_ID + " TEXT PRIMARY KEY,"
				+ FILTER_COLUMN_TYPE + " TEXT," + FILTER_COLUMN_CHECKED
				+ " INTEGER)";

		String CREATE_NOTIFICATION_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_NOTIFICATION + "(" + NOTIFICATION_COLUMN_ID
				+ " INTEGER PRIMARY KEY," + NOTIFICATION_COLUMN_TYPE + " TEXT,"
				+ NOTIFICATION_COLUMN_TITLE + " TEXT,"
				+ NOTIFICATION_COLUMN_CONTENT + " TEXT, "
				+ NOTIFICATION_COLUMN_DATE + " TEXT )";

		String CREATE_LANGUAGE_TABLE = "CREATE TABLE IF NOT EXISTS "
				+ TABLE_LANGUAGE + "(" + LANGUAGE_COLUMN_ID
				+ " TEXT PRIMARY KEY," + LANGUAGE_COLUMN_DESC + " TEXT)";

		db.execSQL(CREATE_FILTER_TABLE);
		db.execSQL(CREATE_NOTIFICATION_TABLE);
		db.execSQL(CREATE_LANGUAGE_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older version of the table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE);

		// Create table again
		onCreate(db);

	}

	/**
	 * 
	 * @param id
	 * @param type
	 * @param isChecked
	 * @return
	 */
	public void updateFilterSetting(CharSequence id, NotificationType type,
			boolean isChecked) {

		SQLiteDatabase db = this.getWritableDatabase();

		int newIsChecked;

		// 1 = checked, 0 = unchecked
		if (isChecked == true) {
			newIsChecked = 1;
		} else {
			newIsChecked = 0;
		}

		ContentValues values = new ContentValues();
		values.put(FILTER_COLUMN_ID, id.toString());
		values.put(FILTER_COLUMN_TYPE, type.toString());
		values.put(FILTER_COLUMN_CHECKED, newIsChecked);

		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FILTER + " WHERE "
				+ FILTER_COLUMN_ID + " = ?", new String[] { id.toString() });

		if (cursor.moveToFirst()) {
			System.out.println("Eintrag update");
			db.update(TABLE_FILTER, // table
					values, // column/value
					FILTER_COLUMN_ID + " = ?", // selections
					new String[] { id.toString() });
		} else {
			System.out.println("Eintrag neu anlegen");
			db.insert(TABLE_FILTER, null, values);
		}

		cursor.close();
		db.close();
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public boolean getFilterSettings(NotificationType type) {

		boolean isChecked = false;
		int i = 0;

		String query = "SELECT * FROM " + TABLE_FILTER;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				if (cursor.getString(cursor.getColumnIndex(FILTER_COLUMN_TYPE))
						.equals(type.toString())) {
					i = cursor.getInt(cursor
							.getColumnIndex(FILTER_COLUMN_CHECKED));
				}
			} while (cursor.moveToNext());
		}

		// 1 = checked, 0 = unchecked
		if (i == 1) {
			isChecked = true;
		} else {
			isChecked = false;
		}

		cursor.close();
		db.close();

		return isChecked;
	}

	public void updateNotificationEntries(Notification notification) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(NOTIFICATION_COLUMN_TITLE, notification.getTitle());
		values.put(NOTIFICATION_COLUMN_TYPE, notification.getType().toString());
		values.put(NOTIFICATION_COLUMN_ID, notification.getId());
		values.put(NOTIFICATION_COLUMN_CONTENT, notification.getContent());
		values.put(NOTIFICATION_COLUMN_DATE, notification.getDate().toString());

		db.close();
	}

	public boolean getNotificationEntries(NotificationType type) {
		return true;
	}

	/**
	 * Add a new language into the table language.
	 * 
	 * @param id String like "en" or "de"
	 * @param description set the description
	 */
	public void addLanguage(String id, String description) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(LANGUAGE_COLUMN_ID, id);
		values.put(LANGUAGE_COLUMN_DESC, description);

		db.insert(TABLE_LANGUAGE, null, values);
		db.close();
	}

	/**
	 * 
	 * Get the total count of all entries which are stored in in the language
	 * table.
	 * 
	 * @return the number of records which are stored in the language table
	 */
	public int getCount() {
		String countQuery = "SELECT  * FROM " + TABLE_LANGUAGE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int i = cursor.getCount();
		cursor.close();

		// return count
		return i;
	}

	/**
	 * Delete all records from the language table.
	 */
	public void deleteAllLanguages() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_LANGUAGE);
		db.close();
	}

	/**
	 * Returns the actual language.
	 * 
	 * @return the chosen language as a String
	 */
	public String getLanguage() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_LANGUAGE, // a. table
				null, // b. column names
				null, // c. selections
				null, // d. selections args
				null, // e. group by
				null, // f. having
				null, // g. order by
				null);
		if (cursor != null) {
			cursor.moveToFirst();
		}

		String id = cursor.getString(0);
		return id;
	}
}
