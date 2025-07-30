package com.example.kalkulatorapp; // Sesuaikan dengan package Anda

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // 1. Deklarasi Variabel, tambahkan untuk tombol baru
    EditText editTextAngkaPertama, editTextAngkaKedua;
    TextView textViewHasil;
    Button buttonTambah, buttonKurang, buttonKali, buttonBagi, buttonModulus, buttonClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. Inisialisasi, hubungkan variabel dengan ID di XML
        editTextAngkaPertama = findViewById(R.id.editTextAngkaPertama);
        editTextAngkaKedua = findViewById(R.id.editTextAngkaKedua);
        textViewHasil = findViewById(R.id.textViewHasil);
        buttonTambah = findViewById(R.id.buttonTambah);
        buttonKurang = findViewById(R.id.buttonKurang);
        buttonKali = findViewById(R.id.buttonKali);
        buttonBagi = findViewById(R.id.buttonBagi);
        // Inisialisasi tombol baru
        buttonModulus = findViewById(R.id.buttonModulus);
        buttonClear = findViewById(R.id.buttonClear);

        // 3. Menetapkan Aksi (Listener) untuk setiap tombol
        buttonTambah.setOnClickListener(v -> hitung('+'));
        buttonKurang.setOnClickListener(v -> hitung('-'));
        buttonKali.setOnClickListener(v -> hitung('*'));
        buttonBagi.setOnClickListener(v -> hitung('/'));

        // Listener untuk tombol Modulus
        buttonModulus.setOnClickListener(v -> hitung('%'));

        // Listener untuk tombol Clear
        buttonClear.setOnClickListener(v -> {
            editTextAngkaPertama.setText("");
            editTextAngkaKedua.setText("");
            textViewHasil.setText("Hasil: 0");
            editTextAngkaPertama.requestFocus(); // Fokuskan kursor ke input pertama
        });
    }

    // 4. Method utama untuk kalkulasi (diperbarui dengan case '%')
    private void hitung(char operator) {
        String strAngka1 = editTextAngkaPertama.getText().toString();
        String strAngka2 = editTextAngkaKedua.getText().toString();

        if (strAngka1.isEmpty() || strAngka2.isEmpty()) {
            Toast.makeText(this, "Mohon masukkan kedua angka", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double angka1 = Double.parseDouble(strAngka1);
            double angka2 = Double.parseDouble(strAngka2);
            double hasil = 0;

            switch (operator) {
                case '+':
                    hasil = angka1 + angka2;
                    break;
                case '-':
                    hasil = angka1 - angka2;
                    break;
                case '*':
                    hasil = angka1 * angka2;
                    break;
                case '/':
                    if (angka2 == 0) {
                        Toast.makeText(this, "Tidak bisa dibagi dengan nol!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    hasil = angka1 / angka2;
                    break;
                // Tambahkan case untuk Modulus
                case '%':
                    if (angka2 == 0) {
                        Toast.makeText(this, "Tidak bisa modulus dengan nol!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    hasil = angka1 % angka2;
                    break;
            }

            // Menampilkan hasil, jika hasil adalah bilangan bulat, tampilkan tanpa koma
            if (hasil == (long) hasil) {
                textViewHasil.setText(String.format("Hasil: %d", (long) hasil));
            } else {
                textViewHasil.setText(String.format("Hasil: %.2f", hasil));
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format angka tidak valid", Toast.LENGTH_SHORT).show();
        }
    }
}