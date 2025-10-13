package com.example.inventoryapp.activity; // Ganti dengan package project Anda

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventoryapp.R;
import com.example.inventoryapp.database.DatabaseHelper;
import com.example.inventoryapp.model.Item;

public class AddItemActivity extends AppCompatActivity {

    private EditText etCode, etName, etStock, etPrice;
    private Button btnSave;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        databaseHelper = new DatabaseHelper(this);

        etCode = findViewById(R.id.etCode);
        etName = findViewById(R.id.etName);
        etStock = findViewById(R.id.etStock);
        etPrice = findViewById(R.id.etPrice);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
    }

    private void saveItem() {
        String code = etCode.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (code.isEmpty() || name.isEmpty() || stockStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int stock = Integer.parseInt(stockStr);
            double price = Double.parseDouble(priceStr);

            Item item = new Item(0, code, name, stock, price); // ID akan diisi otomatis oleh DB

            if (databaseHelper.addItem(item)) {
                Toast.makeText(this, "Barang berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                finish(); // Tutup activity ini dan kembali ke MainActivity
            } else {
                Toast.makeText(this, "Gagal menambahkan barang. Kode mungkin sudah ada.", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Stok dan Harga harus berupa angka.", Toast.LENGTH_SHORT).show();
        }
    }
}