package de.uni_stuttgart.riot.android.database;

import java.util.LinkedList;
import java.util.List;

import de.uni_stuttgart.riot.android.NotificationType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FilterDatabase extends SQLiteOpenHelper {

	private final static int DATABASE_VERION = 1;
	private final static String DATABASE_NAME = "RIOT2";
	private final static String TABLE_FILTER = "filter";

	private final static String COLUMN_ID = "id";
	private final static String COLUMN_TYPE = "type";
	private final static String COLUMN_CHECKED = "is_checked";

	public FilterDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERION);
	}

	// Creating tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LANGUAGE_TABLE = "CREATE TABLE " + TABLE_FILTER + "("
				+ COLUMN_ID + " TEXT PRIMARY KEY," + COLUMN_TYPE + " TEXT,"
				+ COLUMN_CHECKED + " INTEGER)";

		db.execSQL(CREATE_LANGUAGE_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older version of the table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER);

		// Create table again
		onCreate(db);
	}

	public int updateFilterSetting(CharSequence id, NotificationType type,
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
		values.put(COLUMN_ID, id.toString());
		values.put(COLUMN_TYPE, type.toString());
		values.put(COLUMN_CHECKED, newIsChecked);

		int i = db.update(TABLE_FILTER, // table
				values, // column/value
				COLUMN_ID + " = ?", // selections
				new String[] { id.toString() });

		// 4. close
		db.close();

		return i;
	}

	public boolean getFilterSettings(NotificationType type) {

		boolean isChecked = false;
		int i = 0;

		String query = "SELECT  * FROM " + TABLE_FILTER;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				if (cursor.getString(cursor.getColumnIndex(COLUMN_TYPE))
						.equals(type.toString())) {
					i = cursor.getInt(cursor.getColumnIndex(COLUMN_CHECKED));
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
