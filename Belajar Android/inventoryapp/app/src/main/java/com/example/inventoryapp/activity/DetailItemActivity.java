package com.example.inventoryapp.activity; // Ganti dengan package project Anda

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inventoryapp.R;
import com.example.inventoryapp.database.DatabaseHelper;
import com.example.inventoryapp.model.Item;

public class DetailItemActivity extends AppCompatActivity {

    private EditText etCode, etName, etStock, etPrice;
    private Button btnUpdate, btnDelete;
    private DatabaseHelper databaseHelper;
    private int itemId;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);

        databaseHelper = new DatabaseHelper(this);

        etCode = findViewById(R.id.etCodeDetail);
        etName = findViewById(R.id.etNameDetail);
        etStock = findViewById(R.id.etStockDetail);
        etPrice = findViewById(R.id.etPriceDetail);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        itemId = getIntent().getIntExtra("ITEM_ID", -1);
        if (itemId != -1) {
            loadItemData(itemId);
        } else {
            Toast.makeText(this, "ID Barang tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItem();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });
    }

    private void loadItemData(int id) {
        currentItem = databaseHelper.getItem(id);
        if (currentItem != null) {
            etCode.setText(currentItem.getCode());
            etName.setText(currentItem.getName());
            etStock.setText(String.valueOf(currentItem.getStock()));
            etPrice.setText(String.valueOf(currentItem.getPrice()));
        } else {
            Toast.makeText(this, "Barang tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateItem() {
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

            currentItem.setCode(code);
            currentItem.setName(name);
            currentItem.setStock(stock);
            currentItem.setPrice(price);

            if (databaseHelper.updateItem(currentItem) > 0) {
                Toast.makeText(this, "Barang berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal memperbarui barang.", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Stok dan Harga harus berupa angka.", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Barang")
                .setMessage("Apakah Anda yakin ingin menghapus barang ini?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void deleteItem() {
        databaseHelper.deleteItem(currentItem);
        Toast.makeText(this, "Barang berhasil dihapus!", Toast.LENGTH_SHORT).show();
        finish();
    }
}