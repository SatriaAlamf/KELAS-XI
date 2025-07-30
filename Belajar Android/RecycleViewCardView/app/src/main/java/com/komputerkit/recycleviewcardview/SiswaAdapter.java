package com.komputerkit.recycleviewcardview;

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

public class SiswaAdapter extends RecyclerView.Adapter<SiswaAdapter.ItemViewHolder>{

    private Context context;
    private List<Siswa> dataSiswa;

    public SiswaAdapter(Context context, List<Siswa> dataSiswa) {
        this.context = context;
        this.dataSiswa = dataSiswa;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_siswa, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Siswa siswa = dataSiswa.get(position);
        holder.tvNama.setText(siswa.getNamaLengkap());
        holder.tvAlamat.setText(siswa.getKotaAsal());

        holder.tvMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.tvMenu);
            popupMenu.inflate(R.menu.menu_option);

            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                int pos = holder.getAdapterPosition();

                if (itemId == R.id.menu_simpan) {
                    Toast.makeText(context, "Data sudah disimpan", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_hapus) {
                    dataSiswa.remove(pos);
                    notifyItemRemoved(pos);
                    Toast.makeText(context, "Data sudah dihapus", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return dataSiswa.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView tvNama, tvAlamat, tvMenu;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvMenu = itemView.findViewById(R.id.tvMenu);
        }
    }
}
