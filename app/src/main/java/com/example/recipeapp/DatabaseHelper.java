package com.example.recipeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Menu.db";
    public static final String TABLE_NAME = "menu_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "INGREDIENTS";
    public static final String COL_4 = "METHODS";
    public static final String COL_5 = "IMAGE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, INGREDIENTS TEXT, METHODS TEXT, IMAGE BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, String ingredients, String methods, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, ingredients);
        contentValues.put(COL_4, methods);
        contentValues.put(COL_5, image);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData(String name, String wantedData) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select "+wantedData+" from " + TABLE_NAME + " WHERE NAME = " +"'" +name+"'" + "LIMIT 1", null);
        return result;
    }

    public boolean updateData(String name, String ingredients, String methods) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_3, ingredients);
        contentValues.put(COL_4, methods);
        db.update(TABLE_NAME, contentValues, "NAME = ?", new String[]{name});
        return true;
    }

    public boolean updateImage(String name, byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5, image);
        db.update(TABLE_NAME, contentValues, "NAME = ?", new String[]{name});
        return true;
    }

    public Integer deleteData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, " NAME = ?", new String[]{name});
    }
}

