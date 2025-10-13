package com.komputerkit.sqlitedatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter untuk menampilkan daftar barang dalam RecyclerView.
 * Kelas ini menangani pembuatan tampilan item barang dan interaksi dengan datanya.
 */
public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {

    /** Context aplikasi untuk mengakses resources dan activity */
    Context context;
    /** Daftar barang yang akan ditampilkan dalam RecyclerView */
    List<Barang> barangList;

    /**
     * Constructor untuk BarangAdapter
     * @param context Context aplikasi untuk mengakses resources
     * @param barangList List barang yang akan ditampilkan
     */
    public BarangAdapter(Context context, List<Barang> barangList) {
        this.barangList = barangList;
        this.context = context;
    }

    /**
     * Membuat ViewHolder baru untuk menampung item barang
     * @param parent ViewGroup induk tempat item akan ditambahkan
     * @param viewType Tipe view yang akan dibuat
     * @return ViewHolder baru yang berisi tampilan item barang
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_barang, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Mengisi data barang ke dalam ViewHolder pada posisi tertentu
     * @param holder ViewHolder yang akan diisi dengan data
     * @param position Posisi item dalam list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set data barang ke dalam TextView
        holder.tvBarang.setText(barangList.get(position).getBarang());
        holder.tvStok.setText(barangList.get(position).getStok());
        holder.tvHarga.setText(barangList.get(position).getHarga());

        // Simpan posisi saat ini untuk digunakan dalam listener
        final int currentPosition = position;

        // Set click listener untuk menu popup
        holder.tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat popup menu baru
                PopupMenu popupMenu = new PopupMenu(context, holder.tvMenu);
                // Inflate layout menu dari resources
                popupMenu.inflate(R.menu.menu_item);

                // Set listener untuk item menu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Dapatkan id item menu yang diklik
                        int id = item.getItemId();

                        // Handle aksi berdasarkan item yang dipilih
                        if (id == R.id.ubah) {
                            // Panggil method untuk update data di MainActivity
                            ((MainActivity)context).selectUpdate(barangList.get(currentPosition).getIdbarang());
                        } else if (id == R.id.hapus) {
                            // Panggil method untuk hapus data di MainActivity
                            ((MainActivity)context).deleteData(barangList.get(currentPosition).getIdbarang());
                        }

                        return true; // Menandakan event telah ditangani
                    }
                });

                popupMenu.show();
            }
        });

    }

    /**
     * Mendapatkan jumlah total item dalam list
     * @return Jumlah item dalam list barang
     */
    @Override
    public int getItemCount() {
        return barangList.size();
    }

    /**
     * ViewHolder untuk menyimpan referensi view-view dalam item barang
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /** TextView untuk menampilkan nama barang */
        TextView tvBarang;
        /** TextView untuk menampilkan stok barang */
        TextView tvStok;
        /** TextView untuk menampilkan harga barang */
        TextView tvHarga;
        /** TextView untuk menampilkan menu popup */
        TextView tvMenu;

        /**
         * Constructor ViewHolder
         * @param itemView View yang merepresentasikan satu item barang
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inisialisasi view-view dalam item
            tvBarang = itemView.findViewById(R.id.tvBarang);
            tvStok = itemView.findViewById(R.id.tvStok);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvMenu = itemView.findViewById(R.id.tvMenu);
        }
    }
}