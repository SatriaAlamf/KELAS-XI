package com.example.inventoryapp.adapter; // Ganti dengan package project Anda

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapp.R; // Pastikan ini mengarah ke file R Anda
import com.example.inventoryapp.model.Item;// Import kelas Item Anda

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context context;
    private List<Item> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(Context context, List<Item> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvItemCode.setText("Kode: " + item.getCode());
        holder.tvItemName.setText(item.getName()); // Nama barang lebih menonjol
        holder.tvItemStock.setText("Stok: " + item.getStock());
        holder.tvItemPrice.setText("Harga: Rp " + String.format("%.2f", item.getPrice())); // Format harga

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Method untuk memperbarui daftar item
    public void updateList(List<Item> newList) {
        itemList = newList;
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemCode, tvItemName, tvItemStock, tvItemPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemCode = itemView.findViewById(R.id.tvItemCode);
            tvItemStock = itemView.findViewById(R.id.tvItemStock);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
        }
    }
}