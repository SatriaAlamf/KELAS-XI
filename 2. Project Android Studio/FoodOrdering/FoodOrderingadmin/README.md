# Food Ordering Admin App

Aplikasi Admin untuk mengelola sistem pemesanan makanan. Dibangun dengan Kotlin, XML layouts (no Jetpack Compose), dan Firebase.

## Fitur Utama

### 1. **Dashboard**
- Statistik real-time pesanan hari ini
- Total pendapatan hari ini
- Jumlah pesanan yang pending
- Total produk yang tersedia
- Daftar pesanan terbaru

### 2. **Manajemen Pesanan (Orders)**
- Melihat semua pesanan dengan filter berdasarkan status:
  - Pending (Menunggu)
  - Confirmed (Dikonfirmasi)
  - Preparing (Sedang Disiapkan)
  - Out for Delivery (Dalam Pengiriman)
  - Delivered (Terkirim)
  - Cancelled (Dibatalkan)
- Update status pesanan secara real-time
- Melihat detail pesanan lengkap
- Informasi pelanggan (nama, nomor telepon)
- Detail item yang dipesan
- Total harga dan waktu pemesanan

### 3. **Manajemen Makanan (Foods)**
- Tambah makanan baru dengan fitur:
  - Nama makanan
  - Deskripsi
  - Harga
  - Diskon (%)
  - Kategori
  - Gambar (upload atau Firebase image link)
  - Waktu persiapan
  - Status vegetarian/pedas
  - Bahan-bahan (ingredients)
  - Status ketersediaan
- Edit makanan yang sudah ada
- Hapus makanan
- Toggle ketersediaan makanan (on/off)
- Pencarian makanan

### 4. **Manajemen Kategori (Categories)**
- Tambah kategori baru
- Edit kategori
- Hapus kategori
- Toggle status aktif/non-aktif kategori
- Gambar kategori dengan Firebase image link

## Teknologi yang Digunakan

- **Kotlin** - Bahasa pemrograman
- **XML Layouts** - UI/UX design (NO Jetpack Compose)
- **Firebase Authentication** - Login admin
- **Firebase Firestore** - Database real-time
- **Firebase Storage** - Penyimpanan gambar
- **Material Design Components** - UI components
- **Glide** - Image loading
- **ViewBinding** - View access
- **Navigation Component** - Fragment navigation
- **Coroutines** - Asynchronous operations

## Struktur Firebase

### Collections:
1. **admin_users** - Data admin
2. **categories** - Kategori makanan
3. **foods** - Data makanan
4. **orders** - Pesanan dari user
5. **users** - Data customer

## Cara Setup

### 1. Firebase Configuration
1. Buat project baru di [Firebase Console](https://console.firebase.google.com/)
2. Tambahkan aplikasi Android dengan package name: `com.jetpackcompose.foodorderingadmin`
3. Download file `google-services.json`
4. Letakkan file tersebut di folder `app/`

### 2. Firebase Authentication
1. Aktifkan Email/Password authentication di Firebase Console
2. Buat akun admin pertama:
   - Email: admin@foodordering.com
   - Password: admin123 (atau sesuai keinginan)

### 3. Firestore Security Rules
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Admin users collection
    match /admin_users/{userId} {
      allow read, write: if request.auth != null;
    }
    
    // Categories collection
    match /categories/{categoryId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Foods collection
    match /foods/{foodId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Orders collection
    match /orders/{orderId} {
      allow read, write: if request.auth != null;
    }
    
    // Users collection
    match /users/{userId} {
      allow read: if request.auth != null;
    }
  }
}
```

### 4. Storage Security Rules
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /food_images/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    match /category_images/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

### 5. Register Admin Account
Setelah aplikasi berjalan pertama kali:
1. Login dengan email dan password yang sudah dibuat
2. Sistem akan memeriksa apakah user terdaftar sebagai admin di collection `admin_users`
3. Jika belum ada, tambahkan manual di Firestore Console:

```json
{
  "id": "USER_UID_FROM_AUTH",
  "name": "Admin",
  "email": "admin@foodordering.com",
  "role": "ADMIN",
  "permissions": [],
  "isActive": true,
  "createdAt": 1234567890000
}
```

## Cara Menggunakan Firebase Image Link

### Untuk menambahkan gambar dengan Firebase link:

1. **Upload gambar ke Firebase Storage**:
   - Buka Firebase Console → Storage
   - Upload gambar ke folder `food_images/` atau `category_images/`
   - Klik gambar → klik "Get Download URL"
   - Copy link yang muncul

2. **Paste link di aplikasi**:
   - Saat menambah/edit makanan atau kategori
   - Paste link Firebase di field "Image URL"
   - Atau gunakan tombol camera untuk pick image dari device

Contoh Firebase image link:
```
https://firebasestorage.googleapis.com/v0/b/YOUR_PROJECT.appspot.com/o/food_images%2Fimage.jpg?alt=media&token=YOUR_TOKEN
```

## Fitur Keamanan

- Hanya admin yang terdaftar bisa login
- Verifikasi di collection `admin_users` 
- Session management dengan SharedPreferences
- Logout otomatis jika tidak ada sesi valid

## Integrasi dengan User App

Aplikasi admin ini terintegrasi sempurna dengan aplikasi user:
- Database Firebase yang sama
- Model data yang konsisten
- Real-time synchronization
- Perubahan di admin langsung terlihat di user app

## Build & Run

1. Sync Gradle
2. Pastikan `google-services.json` sudah ada
3. Build project
4. Run di emulator atau device

## Troubleshooting

### Jika gagal login:
- Pastikan email/password sudah terdaftar di Firebase Authentication
- Pastikan user terdaftar di collection `admin_users`

### Jika gambar tidak muncul:
- Pastikan URL gambar valid
- Cek koneksi internet
- Pastikan Storage Rules mengizinkan read

### Jika data tidak muncul:
- Cek Firestore Rules
- Pastikan ada data di collection yang sesuai
- Cek koneksi internet

## Dependencies

Lihat file `build.gradle.kts` untuk daftar lengkap dependencies.

## License

Aplikasi ini dibuat untuk tujuan pembelajaran dan pengembangan.

## Contact & Support

Untuk pertanyaan dan dukungan, hubungi developer.

---

**Catatan**: Pastikan untuk menggunakan Firebase image link atau upload gambar melalui aplikasi untuk mendapatkan URL yang valid.
