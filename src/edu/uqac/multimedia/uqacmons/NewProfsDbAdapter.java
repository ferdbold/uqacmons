package edu.uqac.multimedia.uqacmons;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NewProfsDbAdapter 
{
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public NewProfsDbAdapter(Context context) 
    {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public NewProfsDbAdapter createDatabase() throws SQLException 
    {
        try 
        {
            mDbHelper.createDataBase();
        } 
        catch (IOException mIOException) 
        {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public NewProfsDbAdapter open() throws SQLException 
    {
        try 
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } 
        catch (SQLException mSQLException) 
        {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() 
    {
        mDbHelper.close();
    }

     public Cursor getProfsData()
     {
         try
         {
             String sql ="SELECT * FROM ProfsDBTable";

             Cursor mCur = mDb.rawQuery(sql, null);
             if (mCur!=null)
             {
                mCur.moveToNext();
             }
             return mCur;
         }
         catch (SQLException mSQLException) 
         {
             Log.e(TAG, "getProfsData >>"+ mSQLException.toString());
             throw mSQLException;
         }
     }
     
     public boolean updateProf(long rowId, int positive) {
    	 ContentValues cv= new ContentValues();
    	 cv.put("captured","1");
    	 return mDb.update("ProfsDBTable", cv, "KEY_ID" + "=" + rowId, null)>0; 
     }
     
}