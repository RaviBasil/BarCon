package com.ravibasil.thebigdream.barcon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.ravibasil.thebigdream.barcon.textables.modal.Texty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravibasil on 2/3/18.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "barcon";

    // Contacts table name
    private static final String TABLE_TEXTABLES = "textables";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_NAME = "name";
    private static final String KEY_ART = "art";
    private static final String KEY_FAVORITE = "favorite";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TEXTABLES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_ART + " TEXT,"
                + KEY_FAVORITE+ " INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    //add textables at start
    public void addTextables(Texty texty){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY,texty.getCategory());
        values.put(KEY_NAME, texty.getName());
        values.put(KEY_ART, texty.getArt());
        values.put(KEY_FAVORITE,0);

        //inserting row
        db.insert(TABLE_TEXTABLES, null, values);
        db.close(); //closing database connection
    }

    //get data
    public List<Texty> getFavoriteAllTexty(){
        List<Texty> textyList = new ArrayList<Texty>();

        //select all query
        String selectQuery = "SELECT * FROM "
                + TABLE_TEXTABLES+" WHERE "+KEY_FAVORITE+" = 1 ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Texty texty =new Texty();
                texty.setId(Integer.parseInt(cursor.getString(0)));
                texty.setCategory(cursor.getString(1));
                texty.setName(cursor.getString(2));
                texty.setArt(cursor.getString(3));
                texty.setFavorite(Integer.parseInt(cursor.getString(4)));

                textyList.add(texty);

            }while (cursor.moveToNext());
        }
        return textyList;
    }

    //get data
    public List<Texty> getNotFavoriteAllTexty(){
        List<Texty> textyList = new ArrayList<Texty>();

        //select all query
        String selectQuery = "SELECT * FROM "
                + TABLE_TEXTABLES+" WHERE "+KEY_FAVORITE+" = 0 "
                +"ORDER BY "+KEY_CATEGORY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Texty texty =new Texty();
                texty.setId(Integer.parseInt(cursor.getString(0)));
                texty.setCategory(cursor.getString(1));
                texty.setName(cursor.getString(2));
                texty.setArt(cursor.getString(3));
                texty.setFavorite(Integer.parseInt(cursor.getString(4)));

                textyList.add(texty);

            }while (cursor.moveToNext());
        }
        return textyList;
    }
    //updating the list
    public int updateList(int id, int favorite){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID, texty.getId());
        values.put(KEY_FAVORITE, favorite);

        // updating row
        return db.update(TABLE_TEXTABLES, values, KEY_ID + " = "+id,null);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEXTABLES);

        // Create tables again
        onCreate(db);
    }

}
