package com.example.guardiannews.db;


import android.content.Context;
import android.database.Cursor;

import com.example.guardiannews.Utility;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NewsRepository {

    private NewsDB db;


    public NewsRepository(Context context) {
        db=NewsDB.getInstance(context);
    }


    public  <T> ArrayList getListByKey(Class<T> obj, String keyName, Object keyVal){
        ArrayList list=null;

        try {
            db.open();
            list= convertCursorToArrayList(db.getListByForeignKey(obj, keyName, keyVal), obj);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }


        return list;

    }


    public  <T> ArrayList getList(Class<T> obj){
        ArrayList<T> list=null;


        try {
            db.open();
            Cursor c=db.get_List(obj);
            list= convertCursorToArrayList(c,obj);
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.close();
        }
        return  list;
    }

    public  <T> ArrayList getList(Class<T> obj, String foreignKeyName, Object foreignKeyVal){
        ArrayList<T> list=null;

        db.open();
        try {
            Cursor c=db.getListByForeignKey(obj,foreignKeyName,foreignKeyVal);

            list= convertCursorToArrayList(c,obj );

        } catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
        return  list;
    }

    public  <T> T getItem(Class<T> obj, int idx){
        T result=null;

        db.open();
        try {
            result= convertCursorToArrayList(db.getItem(obj,idx),obj).get(0);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }


        return  result;
    }

    public  <T> boolean update_Item( T obj){
        boolean result = false;

        db.open();
        try {
            result=db.update_Item(obj);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }

        return  result;
    }

    public  <T> boolean delete_Item(T obj){
        boolean result = false;
        db.open();
        try {
            result=db.delete_Item(obj);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
    
        return  result;
    }

    public  <T> int delete_List( List<T> list){
        int result=0;

        db.open();
        try {
            for (T item: list) {
                if(db.delete_Item(item)){
                    result++;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }

        return  result;
    }



    public  <T> int insert_Item(T obj){
        int result=0;

        db.open();
        try {
            result=(int)db.insert_Item(obj);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }

        return  result;
    }

    public  <T> int insert_List(ArrayList<T> list){
        int result=0;
        db.open();
        try {
            result=db.insert_ItemList(list);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }

        return result;
    }



    //s //get_List by using Hash map
    public  <T> ArrayList get_ListByHashMap(Class<T> obj){
        ArrayList<T> list = new ArrayList<>();
        db.open();
        Cursor c=db.get_List(obj);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                T newItem = null;
                newItem = obj.newInstance();
                HashMap<String, Object> itemCols = Utility.getCursorToColumnList(c);
                T temp = (T) Utility.setData(newItem, itemCols);
                list.add(temp);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }finally {
            db.close();
        }


        return list;
    }

    //get_Item by using Hash map
    public <T> T get_ItemByHashMap( Class<T> obj, int id){
        db.open();
        Cursor c=db.getItem(obj,id);
        T newItem=null;
        try {
            T temp = obj.newInstance();
            c.moveToFirst();
            HashMap<String, Object> itemCols = Utility.getCursorToColumnList(c);
            newItem = (T) Utility.setData(temp, itemCols);


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }finally {
            c.close();
        }


        return newItem;
    }



    public static <T> ArrayList<T> convertCursorToArrayList(Cursor cursor, Class<T> target) {
        ArrayList<T> list = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    T rowObject = target.newInstance();

                    int totalColumn = cursor.getColumnCount();
                    for (int i = 0; i < totalColumn; i++) {
                        String columnName = cursor.getColumnName(i);

                        int columnType = cursor.getType(i);
                        switch (columnType) {
                            case Cursor.FIELD_TYPE_FLOAT:
                                float floatValue = cursor.getFloat(i);
                                setFieldValue(rowObject, columnName, floatValue);
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                int intValue = cursor.getInt(i);
                                setFieldValue(rowObject, columnName, intValue);
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                String stringValue = cursor.getString(i);
                                setFieldValue(rowObject, columnName, stringValue);
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                                // Handle NULL values if needed
                                break;
                            case Cursor.FIELD_TYPE_BLOB:
                                // Handle BLOB values if needed
                                break;
                        }
                    }

                    list.add(rowObject);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return list;
    }

    private static <T> void setFieldValue(T object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

//    public <T> void DeleteItemsAsyncTask(T... list){
//        //  db.open();
//        new DeleteItemAsyncTask<T>(db).execute(list);
//
//    }

}
