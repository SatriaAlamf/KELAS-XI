package com.komputerkit.sqlitedatabase;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity utama yang menangani manajemen data barang.
 * Menyediakan fungsi untuk menambah, mengubah, menghapus,
 * dan menampilkan data barang dalam database SQLite.
 */
public class MainActivity extends AppCompatActivity {

    /** Instance database helper */
    Database db;
    /** UI Elements untuk input data barang */
    EditText etBarang, etStok, etHarga;
    /** TextView untuk menandakan mode operasi (insert/update) */
    TextView tvPilihan;

    /** List untuk menyimpan data barang dari database */
    List<Barang> databarang = new ArrayList<Barang>();
    /** Adapter untuk RecyclerView */
    BarangAdapter adapter;
    /** RecyclerView untuk menampilkan list barang */
    RecyclerView rcvBarang;

    /** ID barang yang sedang dipilih untuk operasi update/delete */
    String idbarang;

    /**
     * Method yang dipanggil saat Activity pertama kali dibuat
     * Menginisialisasi UI, database, dan memuat data awal
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        load();
        selectData();
    }

    /**
     * Menginisialisasi komponen UI dan database.
     * Dipanggil saat activity pertama kali dibuat.
     */
    public void load(){
        // Inisialisasi database
        db = new Database(this);
        db.buatTabel();

        // Bind UI elements
        etBarang = findViewById(R.id.etBarang);
        etStok = findViewById(R.id.etStok);
        etHarga = findViewById(R.id.etHarga);
        tvPilihan = findViewById(R.id.tvPilihan);
        rcvBarang = findViewById(R.id.rcvBarang);

        // Setup RecyclerView
        rcvBarang.setLayoutManager(new LinearLayoutManager(this));
        rcvBarang.setHasFixedSize(true);
    }

    /**
     * Menyimpan atau mengupdate data barang ke database.
     * @param v View yang memicu method ini (button simpan)
     */
    public void simpan(View v){
        String barang = etBarang.getText().toString();
        String stok = etStok.getText().toString();
        String harga = etHarga.getText().toString();
        String pilihan = tvPilihan.getText().toString();

        if (barang.isEmpty() || stok.isEmpty() || harga.isEmpty()){
            pesan("Data Kosong");
        }else{
            if (pilihan.equals("insert")){
                String sql = "INSERT INTO tblbarang (barang,stok,harga) VALUES ('"+barang+"', "+stok+", "+harga+")";
                if (db.runSQL(sql)){
                    pesan("insert berhasil");
                    selectData();
                }else {
                    pesan("insert gagal");
                }
            } else {
                String sql = "UPDATE tblbarang\n" +
                        "SET barang = \'"+barang+"', stok = "+stok+", harga = "+harga+"\n" +
                        "WHERE idbarang = "+idbarang+";";
                if (db.runSQL(sql)){
                    pesan("Data sudah diubah");
                    selectData();
                }else {
                    pesan("Data gagal diubah");
                }
            }
        }

        // Reset form
        etBarang.setText("");
        etStok.setText("");
        etHarga.setText("");
        tvPilihan.setText("insert");
    }

    /**
     * Menampilkan pesan Toast ke user
     * @param isi Pesan yang akan ditampilkan
     */
    public void pesan(String isi){
        Toast.makeText(this, isi, Toast.LENGTH_SHORT).show();
    }

    /**
     * Mengambil semua data barang dari database dan menampilkannya di RecyclerView.
     * Data diurutkan berdasarkan nama barang secara ascending.
     */
    public void selectData(){
        String sql = "SELECT * FROM tblbarang ORDER BY barang ASC";
        Cursor cursor = db.select(sql);
        databarang.clear();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                // Gunakan getColumnIndexOrThrow untuk menghindari deprecated warning
                String idbarang = cursor.getString(cursor.getColumnIndexOrThrow("idbarang"));
                String barang = cursor.getString(cursor.getColumnIndexOrThrow("barang"));
                String stok = cursor.getString(cursor.getColumnIndexOrThrow("stok"));
                String harga = cursor.getString(cursor.getColumnIndexOrThrow("harga"));

                // Perbaiki urutan parameter sesuai constructor: idbarang, barang, stok, harga
                databarang.add(new Barang(idbarang, barang, stok, harga));
            }
            adapter = new BarangAdapter(this, databarang);
            rcvBarang.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else {
            pesan("Data kosong");
        }
    }

    /**
     * Menampilkan dialog konfirmasi dan menghapus data barang dari database
     * @param id ID barang yang akan dihapus
     */
    public void deleteData(String id){
        idbarang = id;

        // Buat dialog konfirmasi
        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setTitle("Peringatan");
        al.setMessage("Sudah yakin mau dihapus?"); // Perbaiki: gunakan setMessage bukan setTitle lagi
        al.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sql = "DELETE FROM tblbarang WHERE idbarang = "+idbarang+";";
                if (db.runSQL(sql)){
                    pesan("Data sudah dihapus");
                    selectData();
                }else {
                    pesan("Data gagal dihapus");
                }
            }
        });

        al.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        al.show();
    }

    /**
     * Mengambil data barang berdasarkan ID dan menampilkannya di form untuk diupdate
     * @param id ID barang yang akan diupdate
     */
    public void selectUpdate(String id){
        idbarang = id;
        String sql = "SELECT * FROM tblbarang WHERE idbarang = "+id+";";
        Cursor cursor = db.select(sql);
        cursor.moveToNext();

        etBarang.setText(cursor.getString(cursor.getColumnIndexOrThrow("barang")));
        etStok.setText(cursor.getString(cursor.getColumnIndexOrThrow("stok")));
        etHarga.setText(cursor.getString(cursor.getColumnIndexOrThrow("harga")));

        tvPilihan.setText("update");
    }

}