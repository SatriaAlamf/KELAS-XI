package com.komputerkit.intentactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * MainActivity - Activity utama untuk aplikasi Intent
 * Mengirim data dari EditText ke Activity lain menggunakan Intent
 */
public class MainActivity extends AppCompatActivity {

    // Deklarasi variable EditText untuk input barang
    EditText etbarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Mengaktifkan fitur Edge-to-Edge
        setContentView(R.layout.activity_main);

        // Mengatur padding system bars untuk tampilan Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi view
        load();
    }

    /**
     * Method untuk menginisialisasi view
     * Menghubungkan variable dengan view di layout
     */
    public void load(){
        etbarang = findViewById(R.id.etBarang);
    }

    /**
     * Method yang dipanggil saat tombol barang diklik
     * Mengirim data ke Activity Barang menggunakan Intent
     * @param view View yang diklik (Button)
     */
    public void btnBarang(View view) {
        // Mengambil text dari EditText
        String barang = etbarang.getText().toString();

        // Membuat Intent untuk berpindah ke Activity Barang
        Intent intent = new Intent(this, Barang.class);

        // Menambahkan data ke Intent dengan key "ISI"
        intent.putExtra("ISI", barang);

        // Memulai Activity baru
        startActivity(intent);
    }
}