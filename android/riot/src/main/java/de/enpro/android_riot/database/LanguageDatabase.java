package de.enpro.android_riot.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LanguageDatabase extends SQLiteOpenHelper {

	private final static int DATABASE_VERION = 1;
	private final static String DATABASE_NAME = "RIOT";
	private final static String TABLE_LANGUAGE = "language";

	private final static String KEY_ID = "id";
	private final static String KEY_DESC = "description";

	public LanguageDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERION);
		// TODO Auto-generated constructor stub
	}

	// Creating tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LANGUAGE_TABLE = "CREATE TABLE " + TABLE_LANGUAGE + "("
				+ KEY_ID + " TEXT PRIMARY KEY," + KEY_DESC + " TEXT)";
		db.execSQL(CREATE_LANGUAGE_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older version of the table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE);

		// Create table again
		onCreate(db);
	}

	public void addLanguage(String id, String description) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, id);
		values.put(KEY_DESC, description);

		db.insert(TABLE_LANGUAGE, null, values);
		db.close();
	}

	public int getCount() {
		String countQuery = "SELECT  * FROM " + TABLE_LANGUAGE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int i = cursor.getCount();
		cursor.close();

		// return count
		return i;
	}

	public void deleteAllLanguages() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_LANGUAGE);
		db.close();
	}
	
	public String getLanguage() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor =
	            db.query(TABLE_LANGUAGE, // a. table
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
