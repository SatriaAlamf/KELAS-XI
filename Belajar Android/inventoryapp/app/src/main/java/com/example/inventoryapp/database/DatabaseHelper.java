package com.example.inventoryapp.database; // Ganti dengan package project Anda

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.inventoryapp.model.Item; // Import kelas Item Anda

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "store_manager.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STOCK = "stock";
    public static final String COLUMN_PRICE = "price";

    // SQL statement untuk membuat tabel items
    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE " + TABLE_ITEMS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CODE + " TEXT UNIQUE NOT NULL,"
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_STOCK + " INTEGER NOT NULL,"
            + COLUMN_PRICE + " REAL NOT NULL" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tabel lama jika ada dan buat yang baru
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // --- CRUD Operations ---

    // Menambahkan item baru
    public boolean addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CODE, item.getCode());
        cv.put(COLUMN_NAME, item.getName());
        cv.put(COLUMN_STOCK, item.getStock());
        cv.put(COLUMN_PRICE, item.getPrice());

        long result = db.insert(TABLE_ITEMS, null, cv);
        db.close();
        return result != -1; // Mengembalikan true jika insert berhasil
    }

    // Mendapatkan semua item
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS + " ORDER BY " + COLUMN_NAME + " ASC"; // Urutkan berdasarkan nama
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(0));
                item.setCode(cursor.getString(1));
                item.setName(cursor.getString(2));
                item.setStock(cursor.getInt(3));
                item.setPrice(cursor.getDouble(4));
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    // Mendapatkan item berdasarkan ID
    public Item getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, new String[]{COLUMN_ID,
                        COLUMN_CODE, COLUMN_NAME, COLUMN_STOCK, COLUMN_PRICE},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Item item = new Item(cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getDouble(4));
        cursor.close();
        db.close();
        return item;
    }

    // Memperbarui item
    public int updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CODE, item.getCode());
        cv.put(COLUMN_NAME, item.getName());
        cv.put(COLUMN_STOCK, item.getStock());
        cv.put(COLUMN_PRICE, item.getPrice());

        int result = db.update(TABLE_ITEMS, cv, COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
        return result;
    }

    // Menghapus item
    public void deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }

    // Mencari item berdasarkan nama atau kode
    public List<Item> searchItems(String query) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS +
                " WHERE " + COLUMN_NAME + " LIKE '%" + query + "%'" +
                " OR " + COLUMN_CODE + " LIKE '%" + query + "%' ORDER BY " + COLUMN_NAME + " ASC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(0));
                item.setCode(cursor.getString(1));
                item.setName(cursor.getString(2));
                item.setStock(cursor.getInt(3));
                item.setPrice(cursor.getDouble(4));
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }
}