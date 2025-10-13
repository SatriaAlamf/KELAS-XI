package com.komputerkit.recycleviewcardview;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvDataSiswa;
    SiswaAdapter siswaAdapter;
    List<Siswa> daftarSiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inisialisasiRecyclerView();
        isiDataAwal();
    }

    public void inisialisasiRecyclerView(){
        rvDataSiswa = findViewById(R.id.rcvSiswa);
        rvDataSiswa.setLayoutManager(new LinearLayoutManager(this));
    }

    public void isiDataAwal(){
        daftarSiswa = new ArrayList<>();
        daftarSiswa.add(new Siswa("Alif Ramadhan", "Bandung"));
        daftarSiswa.add(new Siswa("Dewi Lestari", "Semarang"));
        daftarSiswa.add(new Siswa("Andika Pratama", "Medan"));
        daftarSiswa.add(new Siswa("Citra Kirana", "Makassar"));
        daftarSiswa.add(new Siswa("Reza Mahendra", "Palembang"));
        daftarSiswa.add(new Siswa("Laila Amalia", "Bogor"));
        daftarSiswa.add(new Siswa("Fauzan Hakim", "Malang"));

        siswaAdapter = new SiswaAdapter(this, daftarSiswa);
        rvDataSiswa.setAdapter(siswaAdapter);
    }

    public void btnTambah(View view) {
        daftarSiswa.add(new Siswa("Nadya Zahra", "Solo"));
        siswaAdapter.notifyDataSetChanged();
        rvDataSiswa.scrollToPosition(daftarSiswa.size() - 1);
    }
}
