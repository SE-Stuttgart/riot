package de.uni_stuttgart.riot.android.database;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;



import de.uni_stuttgart.riot.android.NotificationType;
import de.uni_stuttgart.riot.android.communication.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotificationDatabase extends SQLiteOpenHelper {

	private final static int DATABASE_VERION = 1;
	private final static String DATABASE_NAME = "RIOT3";
	private final static String TABLE_NOTIFICATION = "notification";

	private final static String COLUMN_TITLE = "title";
	private final static String COLUMN_TYPE = "type";
	private final static String COLUMN_ID = "id";
	private final static String COLUMN_CONTENT = "content";
	private final static String COLUMN_DATE = "date";

	public NotificationDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERION);
	}

	// Creating tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LANGUAGE_TABLE = "CREATE TABLE " + TABLE_NOTIFICATION + "("
				+ COLUMN_TITLE + " TEXT," + COLUMN_TYPE + " TEXT,"
				+ COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_CONTENT + " TEXT, "
				+ COLUMN_DATE + " TEXT )";

		db.execSQL(CREATE_LANGUAGE_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older version of the table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);

		// Create table again
		onCreate(db);
	}

	public int updateNotificationEntry(Notification notification) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_TITLE, notification.getTitle());
		values.put(COLUMN_TYPE, notification.getType().toString());
		values.put(COLUMN_ID, notification.getId());
		values.put(COLUMN_CONTENT, notification.getContent());
		values.put(COLUMN_DATE, notification.getDate().toString());

		int i = db.update(TABLE_NOTIFICATION, // table
				values, // column/value
				COLUMN_TITLE + " = ?", // selections
				new String[] { notification.getId() });

		// 4. close
		db.close();

		return i;
	}

	public boolean getNotificationEntry(NotificationType type) {

		boolean isChecked = false;
		int i = 0;

		String query = "SELECT  * FROM " + TABLE_NOTIFICATION;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				if (cursor.getString(cursor.getColumnIndex(COLUMN_TYPE))
						.equals(type.toString())) {
					i = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
				}
			} while (cursor.moveToNext());
		}

		// 1 = checked, 0 = unchecked
		if (i == 1) {
			isChecked = true;
		} else {
			isChecked = false;
		}

		return isChecked;
	}
}
