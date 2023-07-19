package com.example.guardiannews.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.guardiannews.Utility;
import com.example.guardiannews.model.News;

import java.util.ArrayList;
import java.util.HashMap;

import java.lang.reflect.Field;

public class NewsDB {

    private MyDBHelper dbHelper;
    private Context ourContext;
    private SQLiteDatabase ourDB;

    protected final static String DATABASE_NAME = "NewsDB";
    protected final static int VERSION_NUM = 3;
    public final static String TABLE_NAME = "News";
    public final static String COL_Title = "webTitle";
    public final static String COL_API = "apiUrl";

    public final static String COL_NEWS_ID = "news_id";
    public final static String COL_WEBPUBLICATION_DATE = "webPublicationDate";
    public final static String COL_ROWID = "id";
    public NewsDB(Context context) {
        this.ourContext = context;
    }
    private static NewsDB instance;

    static NewsDB getInstance(final Context context){
        if(instance == null){
            return new NewsDB(context);
        }
        return instance;
    }


    public class MyDBHelper extends SQLiteOpenHelper {



        public MyDBHelper(Context ctx)
        {
            super(ctx, DATABASE_NAME, null, VERSION_NUM);
        }


        //This function gets called if no database file exists.
        //Look on your device in the /data/data/package-name/database directory.
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NEWS_ID + " TEXT,"
                    + COL_Title + " TEXT,"
                    + COL_API + " TEXT,"
                    + COL_WEBPUBLICATION_DATE  + " TEXT);");  // add or remove columns
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

        public <T> String[] get_Item_Cols(Class<T> obj){
            String[] cols= new String[obj.getDeclaredFields().length];
            int i=0;
            for (Field field:obj.getDeclaredFields()) {
                cols[i++]=field.getName();
            } ;
            return  cols;
        }


    }


    public NewsDB open() throws SQLException {
        dbHelper = new MyDBHelper(ourContext);
        ourDB = dbHelper.getWritableDatabase();
        return this;

    }

    public void close() {
        dbHelper.close();
    }

    public <T> long insert_Item(T obj) {
        HashMap map = Utility.ObjectToMap(obj);
        ContentValues cv = dbHelper.makeContentValue(map);
        long result =ourDB.insert(obj.getClass().getSimpleName(), null, cv);
        return  result;
    }

    public <T> int insert_ItemList(ArrayList<T> list){
        int cnt=0;
        try {
            for(T item:list)this.insert_Item(item);
            cnt++;
        }catch (Exception e){
            Log.d("insert_ItemList", e.getMessage());
            //to do : if cnt is same list_size, it will have to rollback
        }
        return cnt;
    }


    public <T> boolean update_Item(T obj) {
        HashMap map = Utility.ObjectToMap(obj);
        ContentValues cv = dbHelper.makeContentValue(map);

        return ourDB.update(obj.getClass().getSimpleName(), cv, COL_ROWID + "=?", new String[]{map.get(COL_ROWID).toString()}) == 1;
    }

    public <T> boolean delete_Item(T obj) {
        HashMap map = Utility.ObjectToMap(obj);
        return ourDB.delete(obj.getClass().getSimpleName(), COL_ROWID + "=?", new String[]{map.get(COL_ROWID).toString()}) == 1;
    }


    public <T> void delete_List(T[] list) {
        for (T item:list) {
            this.delete_Item(item);
        }

    }

    public <T> Cursor getListByForeignKey(Class<T> obj, String foreignKeyName, Object foreingKeyValue) {
        ArrayList<News> list = new ArrayList<>();

        String whereClause = foreignKeyName + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(foreingKeyValue)};
        String orderBy = COL_ROWID + " desc";
        String[] cols= dbHelper.get_Item_Cols(obj);

        Cursor c = ourDB.query(obj.getSimpleName(), cols, whereClause, whereArgs, null, null, null, null);

        return c;
    }

    public <T> Cursor getItem(Class<T> targetClass, int idx) {

        Cursor c=null;
        try {
            T item = targetClass.newInstance();
            //String[] cols = dbHelper.get_Item_Cols(item); //new String[map.keySet().size()];
            String whereClause = COL_ROWID + " = ? ";
            String[] whereArgs = new String[]{String.valueOf(idx)};
            String[] cols= dbHelper.get_Item_Cols(targetClass);

            //String[] cols = dbHelper.get_Item_Cols(item); //new String[map.keySet().size()];
            String orderBy = COL_ROWID + " desc";
            c = ourDB.query(item.getClass().getSimpleName(), cols, whereClause, whereArgs, null, null, orderBy, null);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return c;
    }


    public <T> Cursor get_List(Class<T> targetClass) {


        Cursor c=null;

        String[] cols= dbHelper.get_Item_Cols(targetClass);

        //String[] cols = dbHelper.get_Item_Cols(item); //new String[map.keySet().size()];
        String orderBy = COL_ROWID + " desc";
        c = ourDB.query(targetClass.getSimpleName(), cols, null, null, null, null, orderBy, null);

        return c;
    }
}
