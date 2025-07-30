package com.example.counterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator; // Untuk animasi
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    // Ini buat nyimpen referensi ke teks angka dan tombol-tombol
    private TextView teksAngka;
    private MaterialButton tombolTambah;
    private MaterialButton tombolKurang;
    private FloatingActionButton tombolReset;

    // Ini nilai penghitung kita, mulai dari 0
    private int nilaiSekarang = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ngasih tau aplikasi tampilan kita ada di mana
        setContentView(R.layout.activity_main);

        // Cari elemen-elemen di tampilan pakai ID yang udah kita kasih
        teksAngka = findViewById(R.id.teksAngka);
        tombolTambah = findViewById(R.id.tombolTambah);
        tombolKurang = findViewById(R.id.tombolKurang);
        tombolReset = findViewById(R.id.tombolReset);

        // Ketika tombol TAMBAH diklik:
        tombolTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nilaiSekarang++; // Angka ditambah 1
                tampilkanAngkaDenganAnimasi(); // Update angka di layar dengan animasi
            }
        });

        // Ketika tombol KURANG diklik:
        tombolKurang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nilaiSekarang--; // Angka dikurang 1
                tampilkanAngkaDenganAnimasi(); // Update angka di layar dengan animasi
            }
        });

        // Ketika tombol RESET diklik:
        tombolReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nilaiSekarang = 0; // Setel ulang angka jadi 0
                tampilkanAngkaDenganAnimasi(); // Update angka di layar dengan animasi
            }
        });

        // Pastikan angka awal udah bener pas aplikasi dibuka
        tampilkanAngka(); // Panggil fungsi tanpa animasi untuk tampilan awal
    }

    // Fungsi buat nampilin nilai "nilaiSekarang" ke "teksAngka"
    // Ditambah animasi kecil biar lebih hidup
    private void tampilkanAngkaDenganAnimasi() {
        // Animasi kecil: angka sedikit membesar lalu kembali normal
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(teksAngka, "scaleX", 1.0f, 1.05f, 1.0f); // Animasi lebih halus
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(teksAngka, "scaleY", 1.0f, 1.05f, 1.0f);
        scaleX.setDuration(150); // Durasi animasi lebih cepat
        scaleY.setDuration(150);

        scaleX.start();
        scaleY.start();

        // Update teks angka setelah animasi dimulai
        teksAngka.setText(String.valueOf(nilaiSekarang));
    }

    // Fungsi dasar untuk menampilkan angka tanpa animasi (dipakai saat aplikasi pertama kali dibuka)
    private void tampilkanAngka() {
        teksAngka.setText(String.valueOf(nilaiSekarang));
    }
}