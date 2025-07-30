package com.example.konvertersuhuapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText inputSuhu;
    private TextView hasilSuhu;
    private Spinner spinnerKonversi;
    private Button tombolKonversi;
    private String[] opsiKonversi = {
            "Celsius ke Fahrenheit",
            "Celsius ke Kelvin",
            "Celsius ke Réaumur",
            "Fahrenheit ke Celsius",
            "Fahrenheit ke Kelvin",
            "Fahrenheit ke Réaumur",
            "Kelvin ke Celsius",
            "Kelvin ke Fahrenheit",
            "Kelvin ke Réaumur",
            "Réaumur ke Celsius",
            "Réaumur ke Fahrenheit",
            "Réaumur ke Kelvin"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputSuhu = findViewById(R.id.input_suhu);
        hasilSuhu = findViewById(R.id.hasil_suhu);
        spinnerKonversi = findViewById(R.id.spinner_konversi);
        tombolKonversi = findViewById(R.id.tombol_konversi);

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, opsiKonversi);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKonversi.setAdapter(adapter);

        // Listener untuk tombol konversi
        tombolKonversi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konversiSuhu();
            }
        });
    }

    private void konversiSuhu() {
        String input = inputSuhu.getText().toString();
        if (input.isEmpty()) {
            hasilSuhu.setText("0.0");
            return;
        }

        try {
            double nilaiSuhu = Double.parseDouble(input);
            String opsi = spinnerKonversi.getSelectedItem().toString();
            double hasil;

            switch (opsi) {
                case "Celsius ke Fahrenheit":
                    hasil = (nilaiSuhu * 9 / 5) + 32;
                    hasilSuhu.setText(String.format("%.2f °F", hasil));
                    break;
                case "Celsius ke Kelvin":
                    hasil = nilaiSuhu + 273.15;
                    hasilSuhu.setText(String.format("%.2f K", hasil));
                    break;
                case "Celsius ke Réaumur":
                    hasil = nilaiSuhu * 4 / 5;
                    hasilSuhu.setText(String.format("%.2f °R", hasil));
                    break;
                case "Fahrenheit ke Celsius":
                    hasil = (nilaiSuhu - 32) * 5 / 9;
                    hasilSuhu.setText(String.format("%.2f °C", hasil));
                    break;
                case "Fahrenheit ke Kelvin":
                    hasil = (nilaiSuhu - 32) * 5 / 9 + 273.15;
                    hasilSuhu.setText(String.format("%.2f K", hasil));
                    break;
                case "Fahrenheit ke Réaumur":
                    hasil = (nilaiSuhu - 32) * 4 / 9;
                    hasilSuhu.setText(String.format("%.2f °R", hasil));
                    break;
                case "Kelvin ke Celsius":
                    hasil = nilaiSuhu - 273.15;
                    hasilSuhu.setText(String.format("%.2f °C", hasil));
                    break;
                case "Kelvin ke Fahrenheit":
                    hasil = (nilaiSuhu - 273.15) * 9 / 5 + 32;
                    hasilSuhu.setText(String.format("%.2f °F", hasil));
                    break;
                case "Kelvin ke Réaumur":
                    hasil = (nilaiSuhu - 273.15) * 4 / 5;
                    hasilSuhu.setText(String.format("%.2f °R", hasil));
                    break;
                case "Réaumur ke Celsius":
                    hasil = nilaiSuhu * 5 / 4;
                    hasilSuhu.setText(String.format("%.2f °C", hasil));
                    break;
                case "Réaumur ke Fahrenheit":
                    hasil = (nilaiSuhu * 9 / 4) + 32;
                    hasilSuhu.setText(String.format("%.2f °F", hasil));
                    break;
                case "Réaumur ke Kelvin":
                    hasil = (nilaiSuhu * 5 / 4) + 273.15;
                    hasilSuhu.setText(String.format("%.2f K", hasil));
                    break;
            }
        } catch (NumberFormatException e) {
            hasilSuhu.setText("Masukkan angka valid");
        }
    }
}