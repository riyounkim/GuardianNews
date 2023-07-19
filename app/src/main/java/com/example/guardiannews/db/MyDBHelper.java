package com.example.guardiannews.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class MyDBHelper extends SQLiteOpenHelper {
    protected final static String DATABASE_NAME = "NewsDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "News";
    public final static String COL_Title = "Title";
    public final static String COL_API = "API";

    public final static String COL_NEWS_ID = "NEWS_ID";
    public final static String COL_WEBPUBLICATION_DATE = "WEBPUBLICATIONDATE";
    public final static String COL_ID = "_id";


    public MyDBHelper(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    //This function gets called if no database file exists.
    //Look on your device in the /data/data/package-name/database directory.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_Title + " TEXT,"
                + COL_API  + " TEXT);");  // add or remove columns
    }


    //this function gets called if the database version on your device is lower than VERSION_NUM
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }






    public ContentValues makeContentValue(HashMap<String, Object> data) {
        ContentValues cv = new ContentValues();
        for (String key : data.keySet()) {
            if(!key.equalsIgnoreCase("id")){
                if(data.get(key) instanceof String )cv.put(key, (String)data.get(key));
                else if(data.get(key) instanceof Boolean ) cv.put(key, (Boolean) data.get(key));
                else if(data.get(key) instanceof Integer )cv.put(key, (Integer) data.get(key));
                else if(data.get(key) instanceof Float )cv.put(key, (Float) data.get(key));
                else if(data.get(key) instanceof Double ) cv.put(key, (Double) data.get(key));
                else if(data.get(key) instanceof Byte )cv.put(key, (Byte) data.get(key));
            }
        }
        return cv;
    }


}
