package com.komputerkit.sqlitedatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Kelas Database untuk menangani operasi SQLite database.
 * Menyediakan fungsi-fungsi dasar untuk membuat tabel,
 * dan melakukan operasi CRUD (Create, Read, Update, Delete).
 */
public class Database extends SQLiteOpenHelper {

    /** Nama database SQLite */
    private static final String DATABASE_NAME = "dbtoko";
    /** Versi database, dinaikkan jika ada perubahan struktur */
    private static final int VERSION = 1;

    /** Instance dari SQLiteDatabase untuk operasi database */
    SQLiteDatabase db;

    /**
     * Constructor Database
     * @param context Context aplikasi untuk inisialisasi database
     */
    public Database(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        db = this.getWritableDatabase();
    }

    /**
     * Menjalankan perintah SQL untuk insert, update, atau delete
     * @param sql Query SQL yang akan dieksekusi
     * @return true jika query berhasil dijalankan, false jika terjadi error
     */
    boolean runSQL(String sql){
        try{
            db.execSQL(sql);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Menjalankan query SELECT dan mengembalikan hasil dalam bentuk Cursor
     * @param sql Query SELECT yang akan dijalankan
     * @return Cursor berisi hasil query, null jika terjadi error
     */
    Cursor select(String sql){
        try {
            return db.rawQuery(sql, null);
        }
        catch (Exception e){
            return null;
        }
    }

    /**
     * Membuat tabel barang dalam database jika belum ada
     * Struktur tabel:
     * - idbarang (INTEGER, PRIMARY KEY, AUTOINCREMENT)
     * - barang (TEXT)
     * - stok (REAL)
     * - harga (REAL)
     */
    public void buatTabel(){
        String tblbarang = "CREATE TABLE \"tblbarang\" (\n" +
                "\t\"idbarang\"\tINTEGER,\n" +
                "\t\"barang\"\tTEXT,\n" +
                "\t\"stok\"\tREAL,\n" +
                "\t\"harga\"\tREAL,\n" +
                "\tPRIMARY KEY(\"idbarang\" AUTOINCREMENT)\n" +
                ");";
        runSQL(tblbarang);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
