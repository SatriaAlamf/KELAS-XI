# 🚀 QUICK START GUIDE - BLOG APP ANDROID

## 📋 LANGKAH CEPAT MENJALANKAN APLIKASI

### 1️⃣ **SETUP FIREBASE** (PENTING!)

#### A. Buat Proyek Firebase
1. Buka https://console.firebase.google.com/
2. Klik "Add Project" / "Tambah Proyek"
3. Nama proyek: **Blog App** (atau nama lain)
4. Ikuti wizard hingga selesai

#### B. Tambahkan Aplikasi Android
1. Di Firebase Console, klik icon Android
2. Package name: **com.example.myapplication**
3. Download file **google-services.json**
4. **COPY FILE GOOGLE-SERVICES.JSON KE FOLDER:** `app/google-services.json`
   ⚠️ **WAJIB!** Tanpa file ini, aplikasi tidak akan berjalan!

#### C. Aktifkan Layanan Firebase

**Firebase Authentication:**
1. Buka menu "Authentication" di Firebase Console
2. Tab "Sign-in method"
3. Enable "Email/Password"
4. Klik "Save"

**Cloud Firestore:**
1. Buka menu "Firestore Database"
2. Klik "Create database"
3. Pilih "Start in **test mode**" (untuk development)
4. Pilih lokasi: **asia-southeast1** (atau terdekat)
5. Klik "Enable"

**Firebase Storage:**
1. Buka menu "Storage"
2. Klik "Get started"
3. Pilih "Start in **test mode**"
4. Pilih lokasi yang sama dengan Firestore
5. Klik "Done"

---

### 2️⃣ **BUILD & RUN APLIKASI**

#### Di Android Studio:
```bash
1. Buka project ini di Android Studio
2. Tunggu Gradle sync selesai
3. Jika ada error "google-services.json not found":
   - Pastikan file sudah ada di folder app/
   - Sync Gradle lagi: File > Sync Project with Gradle Files
4. Pilih device/emulator
5. Klik Run (Shift + F10)
```

---

### 3️⃣ **TESTING APLIKASI**

#### Test Flow Lengkap:
1. ✅ **Splash Screen** akan muncul 3 detik
2. ✅ **Welcome Screen** - Klik "Register"
3. ✅ **Register Screen** - Isi:
   - Name: John Doe
   - Email: john@test.com
   - Password: 123456 (min 6 karakter)
   - Confirm Password: 123456
   - Klik "Sign Up"
4. ✅ **Home Screen** akan terbuka (kosong karena belum ada blog)
5. ✅ Klik icon **"+"** di Bottom Navigation
6. ✅ **Add Blog Screen**:
   - Klik gambar untuk pilih foto dari galeri
   - Isi Title: "My First Blog"
   - Isi Content: "This is my first blog post content..."
   - Klik "Publish"
7. ✅ Kembali ke **Home Screen** - Blog baru akan muncul!
8. ✅ Click blog untuk lihat detail
9. ✅ Test Like button (jumlah like bertambah)
10. ✅ Test Save button (blog disimpan)
11. ✅ Klik icon **Profile** di Bottom Navigation
12. ✅ Test "Saved Articles" (blog yang disave muncul)
13. ✅ Test "My Blogs" (blog yang dibuat muncul)
14. ✅ Test Logout

---

## 🐛 TROUBLESHOOTING CEPAT

### ❌ Error: "Default FirebaseApp is not initialized"
**Solusi**: 
- Pastikan `google-services.json` ada di folder `app/`
- Sync Gradle lagi
- Clean & Rebuild project

### ❌ Error: "Permission denied" (Firestore/Storage)
**Solusi**: 
- Buka Firebase Console
- Check Firestore Rules - pastikan test mode aktif
- Check Storage Rules - pastikan test mode aktif

### ❌ Error: "Failed to resolve: firebase-bom"
**Solusi**: 
- Check internet connection
- Sync Gradle lagi
- File > Invalidate Caches and Restart

### ❌ Image tidak bisa dipilih
**Solusi**: 
- Grant permission STORAGE saat diminta
- Atau: Settings > Apps > Blog App > Permissions > Storage > Allow

### ❌ Blog tidak muncul di Home
**Solusi**: 
- Check internet connection
- Check Firestore Console - apakah data tersimpan?
- Check Logcat untuk error details

---

## 📱 REQUIREMENTS

- ✅ Android Studio Arctic Fox atau lebih baru
- ✅ Android SDK API 26 (Android 8.0) atau lebih tinggi
- ✅ Koneksi Internet (untuk Firebase)
- ✅ Google Account (untuk Firebase Console)
- ✅ Device/Emulator dengan Google Play Services

---

## 📁 FILE PENTING

1. **`google-services.json`** - File konfigurasi Firebase (WAJIB!)
2. **`FIREBASE_SETUP_GUIDE.md`** - Panduan detail setup Firebase
3. **`README.md`** - Dokumentasi lengkap aplikasi
4. **`TESTING_CHECKLIST.md`** - Checklist testing sebelum deploy

---

## 🎓 STRUKTUR APLIKASI

```
Blog App
├── Auth Flow: Splash → Welcome → Login/Register
├── Main Flow: Home → Add Blog → Blog Detail
└── Profile Flow: Profile → My Blogs / Saved Articles → Logout
```

---

## ✨ FITUR UTAMA

✅ User Authentication (Register/Login/Logout)  
✅ Create Blog dengan Image Upload  
✅ Read Blog dengan Detail View  
✅ Like Blog (Atomic Increment)  
✅ Save/Bookmark Blog  
✅ Share Blog via Intent  
✅ Profile Management  
✅ My Blogs List  
✅ Saved Articles List  
✅ Real-time Updates  
✅ Error Handling & Validation  

---

## 🎉 SELAMAT MENCOBA!

Jika ada pertanyaan atau issue, check:
1. **FIREBASE_SETUP_GUIDE.md** - Panduan Firebase lengkap
2. **README.md** - Dokumentasi lengkap
3. **TESTING_CHECKLIST.md** - Checklist testing
4. **Logcat** di Android Studio - untuk debug

---

**Happy Coding! 🚀**

*Created with ❤️ for learning Android Development*
