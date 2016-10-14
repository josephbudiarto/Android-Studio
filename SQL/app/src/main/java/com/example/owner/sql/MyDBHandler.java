package com.example.owner.sql;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1; //Kalau update databse structure harus di increment
    private static final String DATABASE_NAME = "products.db"; //Files name
    public static final String TABLE_PRODUCTS = "products"; //Table name
    //Field Name
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCTNAME = "productname";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE "+ TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCTNAME + " TEXT" +
                ");";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP_TABLE_IF_EXISTS" + TABLE_PRODUCTS);
        onCreate(sqLiteDatabase);
    }

    //ADD NEW ROW TO THE DATABASE
    public void addProduct(products product){
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCTNAME, product.get_productname());
        SQLiteDatabase db = getWritableDatabase(); // equals to the database we are going to right into
        db.insert(TABLE_PRODUCTS,null,values);
        db.close();
    }

    //Delete Product from DATABASE
    public void deleteProduct(String productName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PRODUCTS + " WHERE "+ COLUMN_PRODUCTNAME + " = \'" + productName+ "\';");
    }
    //Print OUT DATABASE AS STRING
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1";

        //Cursor point to a location in your result
        Cursor c = db.rawQuery(query,null);
        //move to the first row in your result
        c.moveToFirst();

        while(!c.isAfterLast()){
            if(c.getString(c.getColumnIndex("productname")) != null){
                dbString += c.getString(c.getColumnIndex("productname"));
                dbString += "\n";
            }
            c.moveToNext();

        }
        db.close();
        return dbString;
    }
}
