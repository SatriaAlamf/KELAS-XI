package com.example.quotesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Deklarasi variabel untuk elemen UI
    private TextView tvQuote;
    private TextView tvQuoteSource;
    private Button btnShare;

    // Array kutipan dan sumber untuk variasi
    private String[] quotes = {
            "The only way to do great work is to love what you do. Stay hungry, stay foolish.",
            "Innovation distinguishes between a leader and a follower.", 
            "The future belongs to those who believe in the beauty of their dreams.",
            "It is during our darkest moments that we must focus to see the light.",
            "Success is not final, failure is not fatal: it is the courage to continue that counts."
    };

    private String[] quoteSources = {
            "- Steve Jobs",
            "- Steve Jobs", 
            "- Eleanor Roosevelt",
            "- Aristotle",
            "- Winston Churchill"
    };

    private int currentQuoteIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menyembunyikan Action Bar untuk tampilan fullscreen
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Inisialisasi elemen UI
        initializeViews();
        
        // Set kutipan awal
        displayCurrentQuote();

        // Set OnClickListener untuk tombol share
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQuote();
            }
        });

        // Optional: OnClickListener untuk kutipan agar bisa ganti kutipan dengan tap
        tvQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuote();
            }
        });
    }

    /**
     * Inisialisasi semua elemen UI
     */
    private void initializeViews() {
        tvQuote = findViewById(R.id.tv_quote);
        tvQuoteSource = findViewById(R.id.tv_quote_source);
        btnShare = findViewById(R.id.btn_share);
    }

    /**
     * Menampilkan kutipan saat ini
     */
    private void displayCurrentQuote() {
        tvQuote.setText(quotes[currentQuoteIndex]);
        tvQuoteSource.setText(quoteSources[currentQuoteIndex]);
    }

    /**
     * Mengganti ke kutipan berikutnya (fitur bonus)
     */
    private void changeQuote() {
        currentQuoteIndex = (currentQuoteIndex + 1) % quotes.length;
        displayCurrentQuote();
    }

    /**
     * Fungsi untuk berbagi kutipan menggunakan Implicit Intent
     */
    private void shareQuote() {
        // Mengambil teks kutipan dan sumber dari TextView
        String quoteText = tvQuote.getText().toString();
        String quoteSource = tvQuoteSource.getText().toString();
        
        // Menggabungkan kutipan dan sumber
        String shareText = quoteText + "\n\n" + quoteSource + "\n\nShared from Quote App";

        // Membuat Intent untuk berbagi
        Intent shareIntent = new Intent();
        
        // Set Action Intent ke ACTION_SEND
        shareIntent.setAction(Intent.ACTION_SEND);
        
        // Set Type Intent ke text/plain
        shareIntent.setType("text/plain");
        
        // Menambahkan data kutipan ke Intent menggunakan EXTRA_TEXT
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        
        // Optional: Menambahkan subject untuk aplikasi email
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_quote_title));

        // Membuat chooser untuk menampilkan opsi aplikasi berbagi
        Intent chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_quote_title));
        
        // Memulai Activity untuk berbagi
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooserIntent);
        }
    }
}