package com.example.projekt_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "sklep.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE produkty ("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "nazwa TEXT NOT NULL,"+
                "opis TEXT,"+
                "cena REAL NOT NULL,"+
                "ilosc_kupionych_sztuk INTEGER DEFAULT 0,"+
                "data_dodania TEXT NOT NULL,"+
                "user_id INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login TEXT NOT NULL UNIQUE," +
                "haslo TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS produkty");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public void insertProduct(String nazwa, String opis, double cena, int userId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nazwa", nazwa);
        values.put("opis", opis);
        values.put("cena", cena);
        values.put("ilosc_kupionych_sztuk", 0);
        values.put("data_dodania", getCurrentDateTime());
        values.put("user_id", userId);
        db.insert("produkty", null, values);
        db.close();
    }

    public Cursor getAllProducts(int userId){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM produkty WHERE user_id=?", new String[]{String.valueOf(userId)});
    }

    public void updateProducts(int id, String nazwa, String opis, double cena, int userId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nazwa", nazwa);
        values.put("opis", opis);
        values.put("cena", cena);
        db.update("produkty", values, "id=? AND user_id=?",
                new String[]{String.valueOf(id), String.valueOf(userId)});
        db.close();
    }

    public void deleteProduct(int id, int userId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("produkty", "id=? AND user_id=?",
                new String[]{String.valueOf(id), String.valueOf(userId)});
        db.close();
    }

    public void incrementPurchases(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE produkty SET ilosc_kupionych_sztuk = ilosc_kupionych_sztuk + 1 WHERE id=?",
                new Object[]{id});
        db.close();
    }

    public int checkLogin(String login, String haslo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE login=? AND haslo=?",
                new String[]{login, haslo});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    public int registerUser(String login, String haslo) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE login=?", new String[]{login});
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return -1;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("login", login);
        values.put("haslo", haslo);
        long id = db.insert("users", null, values);
        db.close();
        return (int) id;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}