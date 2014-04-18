package edu.uqac.multimedia.uqacmons;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProfsDbAdapter {
	public static final String KEY_NAME = "name";
	public static final String KEY_BIO = "bio";
	public static final String KEY_CAPTURED = "captured";
	
	public static final String KEY_ROWID = "_id";
	
	
	private static final String TAG = "ProfsDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static final String DATABASE_CREATE = 
			"create table professors (_id integer primary key autoincrement,"
			+ "name text not null, bio text not null);";
	
	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "professors";
	private static final int DATABASE_VERSION = 3;
	
	private final Context mCtx;
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		 @Override
	        public void onCreate(SQLiteDatabase db) {

	            db.execSQL(DATABASE_CREATE);
	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
	                    + newVersion + ", which will destroy all old data");
	            db.execSQL("DROP TABLE IF EXISTS notes");
	            onCreate(db);
	        }
	}
	
	public ProfsDbAdapter(Context ctx){
		this.mCtx = ctx;
	}
	public ProfsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    public long createProfs(String name, String bio, Boolean captured) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_BIO, bio);
        initialValues.put(KEY_CAPTURED, captured);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public Cursor fetchAllProfs() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                KEY_BIO, }, null, null, null, null, null);
    }
    public Cursor fetchProfs(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_BIO,KEY_CAPTURED}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public boolean updateNote(long rowId, String captured) {
        ContentValues args = new ContentValues();
        args.put(KEY_CAPTURED, captured);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
