# 📱 PANDUAN SETUP FIREBASE - APLIKASI BLOG ANDROID

## Langkah 1: Buat Proyek Firebase
1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Klik "Add project" atau "Tambahkan proyek"
3. Masukkan nama proyek (contoh: "Blog App")
4. Ikuti langkah-langkah setup hingga selesai

## Langkah 2: Tambahkan Aplikasi Android
1. Di dashboard Firebase, klik ikon Android
2. Masukkan package name: **com.example.myapplication**
3. Masukkan nickname (opsional): "Blog App Android"
4. Download file **google-services.json**
5. **PENTING**: Copy file google-services.json ke folder `app/` (ganti file template yang sudah ada)

## Langkah 3: Aktifkan Firebase Authentication
1. Di menu Firebase Console, pilih **Authentication**
2. Klik tab **Sign-in method**
3. Aktifkan **Email/Password**
   - Toggle ke ON
   - Klik Save

## Langkah 4: Aktifkan Cloud Firestore
1. Di menu Firebase Console, pilih **Firestore Database**
2. Klik **Create database**
3. Pilih mode:
   - **Test mode** (untuk development) - data terbuka selama 30 hari
   - **Production mode** (untuk production) - gunakan rules keamanan
4. Pilih lokasi server (pilih yang terdekat, contoh: asia-southeast1)
5. Klik **Enable**

### Rules Firestore untuk Development (Test Mode):
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2025, 12, 31);
    }
  }
}
```

### Rules Firestore untuk Production (Recommended):
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Blogs collection
    match /blogs/{blogId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if request.auth != null && request.auth.uid == resource.data.authorId;
    }
    
    // Users collection
    match /users/{userId} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Langkah 5: Aktifkan Firebase Storage
1. Di menu Firebase Console, pilih **Storage**
2. Klik **Get started**
3. Pilih mode security rules:
   - Untuk development, gunakan rules yang permissive
4. Pilih lokasi server (sama dengan Firestore)
5. Klik **Done**

### Rules Storage untuk Development:
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## Langkah 6: Verifikasi Setup
1. Pastikan file `google-services.json` sudah ada di folder `app/`
2. Sync Gradle dengan klik **Sync Now** atau menu File > Sync Project with Gradle Files
3. Build project dan pastikan tidak ada error

## ✅ Checklist Setup Firebase
- [ ] Proyek Firebase sudah dibuat
- [ ] File google-services.json sudah didownload dan ditempatkan di folder app/
- [ ] Firebase Authentication (Email/Password) sudah diaktifkan
- [ ] Cloud Firestore sudah diaktifkan dengan rules yang sesuai
- [ ] Firebase Storage sudah diaktifkan dengan rules yang sesuai
- [ ] Gradle sync berhasil tanpa error

## 🔧 Troubleshooting
- **Error: google-services.json not found**: Pastikan file ada di `app/google-services.json`
- **Error: Default FirebaseApp is not initialized**: Pastikan plugin google-services sudah ditambahkan di build.gradle
- **Authentication failed**: Pastikan Email/Password provider sudah diaktifkan di Firebase Console
- **Permission denied Firestore**: Periksa rules Firestore, pastikan sesuai dengan mode yang dipilih

## 📚 Struktur Data Firestore

### Collection: `blogs`
```
blogs/
  └── {blogId}/
      ├── blogId: String
      ├── title: String
      ├── content: String
      ├── authorId: String
      ├── authorName: String
      ├── authorEmail: String
      ├── timestamp: Timestamp
      ├── imageUrl: String
      └── likeCount: Number
```

### Collection: `users`
```
users/
  └── {userId}/
      ├── userId: String
      ├── name: String
      ├── email: String
      ├── profileImageUrl: String (optional)
      └── savedBlogs: Array<String> (array of blogIds)
```

## 🚀 Siap Lanjut!
Setelah semua setup selesai, aplikasi siap untuk dikembangkan dengan fitur lengkap Firebase.
