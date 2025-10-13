# MediaSocial - Quick Start Guide

## 🚀 Langkah Cepat untuk Menjalankan Aplikasi

### 1. Setup Firebase (WAJIB!)

**Download google-services.json:**
1. Buka https://console.firebase.google.com/
2. Buat proyek baru "MediaSocial"
3. Tambahkan aplikasi Android dengan package: `com.example.mediasocial`
4. Download `google-services.json`
5. **Letakkan di folder `app/`** (sejajar dengan `build.gradle.kts`)

**Aktifkan Firebase Services:**
- ✅ Authentication → Email/Password
- ✅ Firestore Database → Test Mode
- ✅ Storage → Test Mode

### 2. Build Aplikasi

```bash
# Di Android Studio:
# 1. File > Sync Project with Gradle Files
# 2. Build > Clean Project
# 3. Build > Rebuild Project
# 4. Run ▶️
```

### 3. Gunakan Aplikasi

**Registrasi:**
- Buka app → Klik "Daftar"
- Isi: Username, Email, Password
- Klik "Daftar"

**Upload Post:**
- Home → Klik tombol + (FAB)
- Pilih gambar → Tulis deskripsi
- Klik "Posting"

**Like & Notifikasi:**
- Tap ❤️ pada post untuk like
- Lihat notifikasi di tab Notifikasi

## 📋 Checklist Setup

- [ ] `google-services.json` sudah di folder `app/`
- [ ] Firebase Authentication aktif
- [ ] Firestore Database dibuat (test mode)
- [ ] Firebase Storage aktif (test mode)
- [ ] Gradle sync berhasil
- [ ] Build berhasil tanpa error

## ⚠️ Troubleshooting

**Error: google-services.json not found**
→ Pastikan file ada di `app/google-services.json`

**Error: Default FirebaseApp not initialized**
→ Clean & Rebuild project

**Error: PERMISSION_DENIED**
→ Pastikan Firestore dalam test mode

## 📚 Dokumentasi Lengkap

Lihat `README.md` untuk dokumentasi lengkap dan detail.

## 🎯 Fitur yang Sudah Diimplementasikan

✅ Login & Register
✅ Upload Post dengan gambar
✅ Feed postingan
✅ Like/Unlike post
✅ Stories display (horizontal scroll)
✅ Story viewer dengan auto-progress
✅ Notifikasi real-time
✅ Profil pengguna
✅ Logout

## 🔜 Fitur yang Bisa Ditambahkan

- Upload Story
- Comments pada post
- Follow/Unfollow users
- Direct Messages
- Push Notifications
- Edit Profile
- Search Users

---

**Happy Coding! 🚀**
