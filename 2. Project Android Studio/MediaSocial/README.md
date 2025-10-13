# MediaSocial - Android Social Media App

Aplikasi media sosial native Android yang dibangun dengan Kotlin, menggunakan Firebase sebagai backend, dan mengimplementasikan arsitektur MVVM modern.

## 🚀 Fitur Utama

### ✅ Autentikasi Pengguna
- Login dan Register dengan Firebase Authentication
- Manajemen sesi pengguna otomatis
- Validasi input dan error handling

### ✅ Stories (Cerita)
- Tampilan horizontal stories di bagian atas feed
- Story viewer dengan progress bar otomatis (5 detik per story)
- Indikator visual untuk stories yang belum dilihat
- Auto-expire setelah 24 jam
- Track siapa saja yang sudah melihat story

### ✅ Feed dan Posts
- Feed postingan dengan infinite scroll
- Upload postingan dengan gambar dan deskripsi
- Like/unlike postingan dengan real-time update
- Tampilan jumlah likes
- Timestamp relatif (2 jam yang lalu, dll)

### ✅ Notifikasi
- Notifikasi untuk likes pada postingan
- Indikator status read/unread dengan warna background
- Mark as read otomatis saat diklik
- Real-time notifications

### ✅ Profil Pengguna
- Tampilan profil dengan foto, username, email, dan bio
- Statistik jumlah postingan dan stories
- Grid view untuk postingan pengguna
- Logout functionality

## 📋 Prasyarat

- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 11 atau lebih tinggi
- Android SDK API Level 26 (Android 8.0) atau lebih tinggi
- Akun Firebase (gratis)

## 🔧 Setup Firebase

### 1. Buat Proyek Firebase

1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Klik **"Add project"** atau **"Create a project"**
3. Masukkan nama proyek: **MediaSocial**
4. Aktifkan Google Analytics (opsional)
5. Klik **"Create project"**

### 2. Tambahkan Aplikasi Android ke Firebase

1. Di Firebase Console, klik ikon Android
2. Masukkan package name: `com.example.mediasocial`
3. Masukkan app nickname: **MediaSocial**
4. (Opsional) Masukkan SHA-1 certificate fingerprint untuk fitur tambahan
5. Klik **"Register app"**

### 3. Download google-services.json

1. Download file `google-services.json` dari Firebase Console
2. Letakkan file tersebut di folder: `app/` (folder root dari modul app)
3. **PENTING**: File ini wajib ada agar aplikasi dapat terhubung ke Firebase

### 4. Setup Firebase Authentication

1. Di Firebase Console, buka **Authentication**
2. Klik **"Get started"**
3. Pilih tab **"Sign-in method"**
4. Aktifkan **Email/Password**:
   - Klik pada "Email/Password"
   - Toggle **"Enable"**
   - Klik **"Save"**

### 5. Setup Cloud Firestore

1. Di Firebase Console, buka **Firestore Database**
2. Klik **"Create database"**
3. Pilih **"Start in test mode"** (untuk development)
4. Pilih lokasi server (misalnya: asia-southeast1)
5. Klik **"Enable"**

#### Struktur Database Firestore:

```
firestore/
├── users/
│   └── {userId}/
│       ├── uid: String
│       ├── username: String
│       ├── email: String
│       ├── profileImageUrl: String
│       ├── bio: String
│       └── createdAt: Timestamp
│
├── posts/
│   └── {postId}/
│       ├── postId: String
│       ├── userId: String
│       ├── username: String
│       ├── userProfileImage: String
│       ├── imageUrl: String
│       ├── description: String
│       ├── timestamp: Timestamp
│       ├── likes: Array<String>
│       ├── likesCount: Number
│       └── commentsCount: Number
│
├── stories/
│   └── {storyId}/
│       ├── storyId: String
│       ├── userId: String
│       ├── username: String
│       ├── userProfileImage: String
│       ├── imageUrl: String
│       ├── timestamp: Timestamp
│       ├── expiresAt: Timestamp
│       └── viewedBy: Array<String>
│
└── notifications/
    └── {notificationId}/
        ├── notificationId: String
        ├── userId: String (penerima)
        ├── fromUserId: String
        ├── fromUsername: String
        ├── fromUserProfileImage: String
        ├── type: String (LIKE, COMMENT, FOLLOW, STORY, MENTION)
        ├── postId: String
        ├── postImageUrl: String
        ├── message: String
        ├── timestamp: Timestamp
        └── isRead: Boolean
```

#### Firestore Security Rules (Production):

Untuk production, ganti rules dengan yang lebih secure:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Users collection
    match /users/{userId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && request.auth.uid == userId;
      allow update: if request.auth != null && request.auth.uid == userId;
      allow delete: if request.auth != null && request.auth.uid == userId;
    }
    
    // Posts collection
    match /posts/{postId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if request.auth != null;
      allow delete: if request.auth != null && 
                      resource.data.userId == request.auth.uid;
    }
    
    // Stories collection
    match /stories/{storyId} {
      allow read: if request.auth != null;
      allow create: if request.auth != null;
      allow update: if request.auth != null;
      allow delete: if request.auth != null && 
                      resource.data.userId == request.auth.uid;
    }
    
    // Notifications collection
    match /notifications/{notificationId} {
      allow read: if request.auth != null && 
                    resource.data.userId == request.auth.uid;
      allow create: if request.auth != null;
      allow update: if request.auth != null && 
                      resource.data.userId == request.auth.uid;
      allow delete: if request.auth != null && 
                      resource.data.userId == request.auth.uid;
    }
  }
}
```

### 6. Setup Firebase Storage

1. Di Firebase Console, buka **Storage**
2. Klik **"Get started"**
3. Pilih **"Start in test mode"**
4. Pilih lokasi yang sama dengan Firestore
5. Klik **"Done"**

#### Storage Structure:

```
storage/
├── profile_images/
│   └── {userId}_{uuid}.jpg
├── posts/
│   └── {userId}_{uuid}.jpg
└── stories/
    └── {userId}_{uuid}.jpg
```

#### Storage Security Rules (Production):

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /profile_images/{imageId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null &&
                     request.resource.size < 5 * 1024 * 1024 &&
                     request.resource.contentType.matches('image/.*');
    }
    
    match /posts/{imageId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null &&
                     request.resource.size < 10 * 1024 * 1024 &&
                     request.resource.contentType.matches('image/.*');
    }
    
    match /stories/{imageId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null &&
                     request.resource.size < 5 * 1024 * 1024 &&
                     request.resource.contentType.matches('image/.*');
    }
  }
}
```

## 🏗️ Instalasi dan Build

### 1. Clone atau Download Proyek

Pastikan semua file proyek sudah tersedia di direktori Anda.

### 2. Tambahkan google-services.json

**PENTING**: Letakkan file `google-services.json` yang sudah Anda download dari Firebase Console ke folder `app/`

### 3. Sync Gradle

1. Buka proyek di Android Studio
2. Tunggu hingga Gradle sync selesai
3. Jika ada error, klik **"Sync Now"** atau **File > Sync Project with Gradle Files**

### 4. Build dan Run

1. Pilih emulator atau device fisik
2. Klik tombol **Run** (▶️) atau tekan **Shift + F10**
3. Aplikasi akan ter-install dan berjalan

## 📱 Cara Menggunakan Aplikasi

### Registrasi Akun Baru

1. Buka aplikasi
2. Klik **"Daftar"** di layar login
3. Masukkan username, email, dan password
4. Klik **"Daftar"**
5. Anda akan otomatis login dan masuk ke halaman utama

### Upload Postingan

1. Di halaman Home, klik tombol **FAB (+)** di kanan bawah
2. Klik **"Pilih Gambar"** dan pilih gambar dari galeri
3. Tulis deskripsi postingan
4. Klik **"Posting"**

### Upload Story

Fitur upload story dapat ditambahkan dengan membuat activity serupa dengan CreatePostActivity, tetapi:
- Story akan expire setelah 24 jam
- Tidak ada deskripsi, hanya gambar
- Disimpan di collection `stories`

### Like Postingan

1. Di feed, klik ikon **hati** pada postingan
2. Ikon akan berubah warna merah jika sudah di-like
3. Pemilik postingan akan menerima notifikasi

### Lihat Notifikasi

1. Klik tab **Notifikasi** di bottom navigation
2. Notifikasi yang belum dibaca akan memiliki background biru muda
3. Klik notifikasi untuk mark as read

### Logout

1. Klik tab **Profil** di bottom navigation
2. Scroll ke bawah
3. Klik tombol **"Keluar"**

## 🛠️ Teknologi yang Digunakan

- **Bahasa**: Kotlin
- **UI Framework**: XML Layouts dengan Material Design 3
- **Arsitektur**: MVVM (Model-View-ViewModel)
- **Backend**: Firebase
  - Firebase Authentication
  - Cloud Firestore
  - Firebase Storage
- **Image Loading**: Glide
- **Circular ImageView**: CircleImageView library
- **Async Operations**: Kotlin Coroutines

## 📦 Dependencies Utama

```kotlin
// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")

// Image Loading
implementation("com.github.bumptech.glide:glide:4.16.0")
implementation("de.hdodenhof:circleimageview:3.1.0")

// Lifecycle & ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")

// Navigation
implementation("androidx.navigation:navigation-fragment-ktx:2.8.5")
implementation("androidx.navigation:navigation-ui-ktx:2.8.5")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")
```

## 🐛 Troubleshooting

### Error: google-services.json tidak ditemukan

**Solusi**: Pastikan file `google-services.json` ada di folder `app/` dan sync Gradle lagi.

### Error: Default FirebaseApp is not initialized

**Solusi**: 
1. Pastikan `google-services.json` sudah benar
2. Pastikan plugin `com.google.gms.google-services` sudah ada di `build.gradle`
3. Clean dan rebuild project

### Error: PERMISSION_DENIED

**Solusi**: 
1. Pastikan Firestore rules sudah di-set dengan benar
2. Untuk development, gunakan test mode rules
3. Pastikan user sudah login

### Images tidak loading

**Solusi**:
1. Periksa koneksi internet
2. Pastikan Firebase Storage rules sudah benar
3. Periksa URL gambar di Firestore

## 📝 Catatan Pengembangan Lebih Lanjut

### Fitur yang Bisa Ditambahkan:

1. **Comments System**: Tambahkan collection `comments` di Firestore
2. **User Search**: Implementasi search users dengan Algolia atau Firestore queries
3. **Follow/Unfollow**: Tambahkan collection `followers` dan `following`
4. **Direct Messages**: Implementasi chat dengan Firebase Realtime Database
5. **Push Notifications**: Gunakan Firebase Cloud Messaging (FCM)
6. **Story Creation**: Tambahkan activity untuk upload story
7. **Edit Profile**: Implementasi update profile dengan upload foto profil
8. **Post Comments**: Tampilan detail post dengan comments
9. **Hashtags**: Implementasi hashtag search dan trending topics
10. **Analytics**: Integrate Firebase Analytics untuk tracking user behavior

## 📄 Lisensi

Proyek ini dibuat untuk tujuan pembelajaran dan demonstrasi.

## 👨‍💻 Author

Dibuat dengan ❤️ menggunakan Kotlin dan Firebase

---

**Selamat mengembangkan aplikasi MediaSocial! 🚀**
