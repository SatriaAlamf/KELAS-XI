package com.komputerkit.datepicker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Calendar;

/**
 * MainActivity - Activity utama untuk aplikasi Date Picker
 * Menampilkan dialog pemilihan tanggal dan menampilkan hasilnya di EditText
 */
public class MainActivity extends AppCompatActivity {

    // Deklarasi variable EditText untuk menampilkan tanggal
    EditText etTanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Mengatur padding system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Memanggil method load untuk inisialisasi view
        load();
    }

    /**
     * Method untuk inisialisasi view
     * Menghubungkan variable dengan view di layout
     */
    public void load(){
        etTanggal = findViewById(R.id.etTanggal);
    }

    /**
     * Method yang dipanggil saat EditText tanggal diklik
     * Menampilkan dialog pemilihan tanggal
     * @param view View yang diklik (EditText)
     */
    public void etTanggal(View view) {
        // Mengambil tanggal saat ini
        Calendar cal = Calendar.getInstance();
        int tgl = cal.get(Calendar.DAY_OF_MONTH);
        int bln = cal.get(Calendar.MONTH);
        int thn = cal.get(Calendar.YEAR);

        // Membuat dan menampilkan DatePickerDialog
        DatePickerDialog dtp = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int thn, int bln, int tgl) {
                // Mengatur text pada EditText dengan format: dd-mm-yyyy
                // Bulan ditambah 1 karena indeks bulan dimulai dari 0
                etTanggal.setText(tgl+"-"+(bln+1)+"-"+thn);
            }
        }, thn, bln, tgl);

        // Menampilkan dialog
        dtp.show();
    }
}