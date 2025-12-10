package com.komputerkit.aplikasimonitoringkelas.api

object ApiConfig {
    // UNTUK DEVICE FISIK via USB (dengan adb reverse): gunakan localhost
    const val BASE_URL = "http://127.0.0.1:8000/api/"
    
    // UNTUK DEVICE FISIK di WiFi yang sama: gunakan IP komputer
    // const val BASE_URL = "http://192.168.1.28:8000/api/"
    
    // UNTUK EMULATOR ANDROID: gunakan 10.0.2.2 (localhost dari komputer host)
    // const val BASE_URL = "http://10.0.2.2:8000/api/"
    
    // Catatan: 
    // - Untuk USB: Jalankan: adb reverse tcp:8000 tcp:8000
    // - Untuk WiFi: Pastikan device dan komputer di WiFi yang sama
    // - Laravel server harus running dengan: php artisan serve
    // - Pastikan AndroidManifest.xml sudah allow cleartext traffic
}
