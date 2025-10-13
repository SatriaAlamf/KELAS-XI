package com.komputerkit.intentactivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Barang - Activity untuk menampilkan data barang
 * Menerima dan menampilkan data yang dikirim dari MainActivity
 */
public class Barang extends AppCompatActivity {

    // Deklarasi variable TextView untuk menampilkan data barang
    TextView tvbarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Mengaktifkan fitur Edge-to-Edge
        setContentView(R.layout.activity_barang);

        // Mengatur padding system bars untuk tampilan Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi view dan mengambil data dari Intent
        load();
        ambilData();
    }

    /**
     * Method untuk menginisialisasi view
     * Menghubungkan variable dengan view di layout
     */
    public void load(){
        tvbarang = findViewById(R.id.tvBarang);
    }

    /**
     * Method untuk mengambil data dari Intent
     * dan menampilkannya di TextView
     */
    public void ambilData(){
        // Mengambil data string dari Intent dengan key "ISI"
        String ambil = getIntent().getStringExtra("ISI");

        // Menampilkan data di TextView
        tvbarang.setText(ambil);
    }
}