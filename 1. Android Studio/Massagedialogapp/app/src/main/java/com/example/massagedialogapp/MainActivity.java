package com.example.massagedialogapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // --- DEKLARASI VARIABEL KOMPONEN UI ---
    // Variabel ini akan menghubungkan kode kita dengan komponen Button di layout (file .xml).
    private Button tombolToast;
    private Button tombolAlertSederhana;
    private Button tombolAlertDenganPilihan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Metode onCreate() adalah metode pertama yang dipanggil saat Activity dibuat.
        // Di sinilah kita melakukan semua pengaturan awal.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Menghubungkan file Java ini dengan layout activity_main.xml

        // --- INISIALISASI KOMPONEN UI ---
        // Menghubungkan variabel Button yang sudah dideklarasi dengan komponen di layout berdasarkan ID-nya.
        tombolToast = findViewById(R.id.tombolToast);
        tombolAlertSederhana = findViewById(R.id.tombolAlertSederhana);
        tombolAlertDenganPilihan = findViewById(R.id.tombolAlertDenganPilihan);

        // --- PENGATURAN EVENT LISTENER UNTUK SETIAP TOMBOL ---
        // Event listener bertugas untuk "mendengarkan" jika ada interaksi dari pengguna (seperti klik).

        // Listener untuk tombol "Tampilkan Toast"
        tombolToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ketika tombol diklik, panggil metode tampilkanToast()
                tampilkanToast();
            }
        });

        // Listener untuk tombol "Tampilkan Alert Message"
        tombolAlertSederhana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ketika tombol diklik, panggil metode tampilkanAlertSederhana()
                tampilkanAlertSederhana();
            }
        });

        // Listener untuk tombol "Tampilkan Alert Dengan Button"
        tombolAlertDenganPilihan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ketika tombol diklik, panggil metode tampilkanAlertDenganPilihan()
                tampilkanAlertDenganPilihan();
            }
        });
    }

    /**
     * Metode ini berfungsi untuk membuat dan menampilkan pesan Toast.
     * Toast adalah notifikasi sederhana yang muncul sebentar di layar dan kemudian menghilang.
     */
    private void tampilkanToast() {
        // Membuat Toast dengan konteks aplikasi (MainActivity.this), pesan yang ingin ditampilkan,
        // dan durasi tampilnya (LENGTH_SHORT atau LENGTH_LONG).
        Toast.makeText(MainActivity.this, "Ini adalah pesan Toast!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Metode ini berfungsi untuk menampilkan AlertDialog sederhana.
     * Dialog ini hanya berisi informasi dan satu tombol "OK" untuk menutupnya.
     */
    private void tampilkanAlertSederhana() {
        // AlertDialog.Builder adalah kelas pembantu untuk membuat AlertDialog.
        AlertDialog.Builder pembangunDialog = new AlertDialog.Builder(this);

        // Mengatur judul dan pesan dari dialog.
        pembangunDialog.setTitle("Info Penting");
    pembangunDialog.setMessage("Ini adalah contoh Alert Message!. Klik OK untuk menutup.");

        // Menambahkan tombol positif (biasanya untuk aksi utama seperti "OK" atau "Setuju").
        pembangunDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perintah yang dijalankan saat tombol "OK" diklik.
                dialog.dismiss(); // Menutup dialog.
                Toast.makeText(MainActivity.this, "Anda menutup Alert Message.", Toast.LENGTH_SHORT).show(); // Memberi respons berupa Toast.
            }
        });

        // Setelah semua diatur, buat dan tampilkan dialognya.
        AlertDialog dialog = pembangunDialog.create();
        dialog.show();
    }

    /**
     * Metode ini berfungsi untuk menampilkan AlertDialog dengan tiga pilihan tombol.
     * Setiap tombol akan memberikan respons yang berbeda saat diklik.
     */
    private void tampilkanAlertDenganPilihan() {
        // Membuat instance dari AlertDialog.Builder.
        AlertDialog.Builder pembangunDialog = new AlertDialog.Builder(this);

        // Mengatur judul dan pesan utama dari dialog konfirmasi.
        pembangunDialog.setTitle("Konfirmasi Tindakan");
        pembangunDialog.setMessage("Apakah Anda yakin ingin melanjutkan tindakan ini?");

        // Mengatur agar dialog tidak bisa ditutup dengan menekan tombol kembali (back button) atau area luar dialog.
        // Pengguna harus memilih salah satu tombol yang tersedia.
        pembangunDialog.setCancelable(false);

        // Menambahkan tombol positif ("Ya").
        pembangunDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Respons ketika pengguna memilih "Ya".
                Toast.makeText(MainActivity.this, "Anda memilih 'Ya'. Tindakan dilanjutkan.", Toast.LENGTH_SHORT).show();
            }
        });

        // Menambahkan tombol negatif ("Tidak").
        pembangunDialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Respons ketika pengguna memilih "Tidak".
                Toast.makeText(MainActivity.this, "Anda memilih 'Tidak'. Tindakan dibatalkan.", Toast.LENGTH_SHORT).show();
            }
        });

        // Menambahkan tombol netral ("Batal").
        pembangunDialog.setNeutralButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Respons ketika pengguna memilih "Batal".
                dialog.dismiss(); // Menutup dialog tanpa melakukan apa-apa.
                Toast.makeText(MainActivity.this, "Anda membatalkan pilihan.", Toast.LENGTH_SHORT).show();
            }
        });

        // Membuat dan menampilkan dialog dari builder yang telah dikonfigurasi.
        AlertDialog dialog = pembangunDialog.create();
        dialog.show();
    }
}