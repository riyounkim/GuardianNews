package com.example.guardiannews;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;



public class Utility {

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    public static <T> HashMap<String, Object> ObjectToMap(T v){
        ObjectMapper oMapper = new ObjectMapper();
        oMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        oMapper.setDefaultPropertyInclusion(Value.construct(Include.NON_EMPTY, Include.CUSTOM, null, ExludeEmptyObjects.class));

        // object -> Map
        HashMap map = oMapper.convertValue(v, HashMap.class);
        //System.out.println(map);
        return map;

    }



    public static <T> T setData(T obj, HashMap<String, Object> fields) {
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            Method m = null;
            try {

                for (Method method:obj.getClass().getMethods()) {
                    if(method.getName().equalsIgnoreCase("set"+entry.getKey())){
                        m=method; break;
                    }
                };


                if (m != null) {
                    if (m.getParameterTypes()[0] == boolean.class) {
                        m.invoke(obj,(int)entry.getValue()==0?false:true);
                    }
                    else m.invoke(obj, entry.getValue());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }



    public static void ShowAlertDialog(Activity from, DialogInterface.OnClickListener clickListener, DialogInterface.OnClickListener cancelListener){
        View promptsView = LayoutInflater.from(from).inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                from);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        // set dialog message
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", clickListener);
        alertDialogBuilder.setNegativeButton("Cancel", cancelListener);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }

                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private static class ExludeEmptyObjects{
        @Override
        public boolean equals(Object o) {
            if (o instanceof Map) {
                return ((Map) o).size() == 0;
            }
            if (o instanceof Collection) {
                return ((Collection) o).size() == 0;
            }
            return false;
        }
    }
    public static HashMap<String, Object> getCursorToColumnList(Cursor cursor) {

        int columnCount = cursor.getColumnCount();
        HashMap row = new HashMap(columnCount);
        for(int i=0; i<columnCount;i++){
            switch (cursor.getType(i))  {
                case Cursor.FIELD_TYPE_FLOAT:
                    row.put(cursor.getColumnName(i), cursor.getFloat(i));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    row.put(cursor.getColumnName(i), cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    row.put(cursor.getColumnName(i), cursor.getString(i));
                    break;
            }
        }

        return row;
    }

    public static <T> String[] get_Item_Cols(Class<T> obj){
        String[] cols= new String[obj.getDeclaredFields().length];
        int i=0;
        for (Field field:obj.getDeclaredFields()) {
            cols[i++]=field.getName();
        } ;
        return  cols;
    }



}


