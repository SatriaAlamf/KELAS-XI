package com.example.inventoryapp.activity; // Ganti dengan package project Anda

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView; // Gunakan android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.inventoryapp.R;
import com.example.inventoryapp.adapter.ItemAdapter;
import com.example.inventoryapp.database.DatabaseHelper;
import com.example.inventoryapp.model.Item;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnItemClickListener {

    private RecyclerView recyclerViewItems;
    private ItemAdapter itemAdapter;
    private DatabaseHelper databaseHelper;
    private List<Item> itemList = new ArrayList<>();
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        itemAdapter = new ItemAdapter(this, itemList, this);
        recyclerViewItems.setAdapter(itemAdapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        loadItems(); // Memuat item saat aktivitas dibuat
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems(); // Memuat ulang item setiap kali aktivitas kembali ke foreground
    }

    private void loadItems() {
        itemList.clear(); // Bersihkan daftar lama
        itemList.addAll(databaseHelper.getAllItems()); // Ambil semua item
        itemAdapter.updateList(itemList); // Perbarui adapter
    }

    private void performSearch(String query) {
        List<Item> searchResult = databaseHelper.searchItems(query);
        itemAdapter.updateList(searchResult);
    }

    @Override
    public void onItemClick(Item item) {
        Intent intent = new Intent(MainActivity.this, DetailItemActivity.class);
        intent.putExtra("ITEM_ID", item.getId());
        startActivity(intent);
    }
}