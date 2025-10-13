# FIREBASE SETUP - MediaSocial

## ⚠️ PENTING - File google-services.json

File `google-services.json` WAJIB ditambahkan sebelum aplikasi dapat berjalan!

### Cara Mendapatkan google-services.json:

1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Buat proyek baru atau pilih proyek yang sudah ada
3. Tambahkan aplikasi Android:
   - Package name: `com.example.mediasocial`
   - App nickname: MediaSocial
4. Download file `google-services.json`
5. **Letakkan file di folder ini** (`app/google-services.json`)

### Struktur File (Contoh):

```json
{
  "project_info": {
    "project_number": "YOUR_PROJECT_NUMBER",
    "firebase_url": "https://YOUR_PROJECT.firebaseio.com",
    "project_id": "YOUR_PROJECT_ID",
    "storage_bucket": "YOUR_PROJECT.appspot.com"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "YOUR_APP_ID",
        "android_client_info": {
          "package_name": "com.example.mediasocial"
        }
      }
    }
  ]
}
```

## Firebase Services yang Dibutuhkan:

### 1. Authentication
- Aktifkan Email/Password sign-in method

### 2. Cloud Firestore
- Buat database
- Mode: Test mode (untuk development)
- Lokasi: asia-southeast1 (atau sesuai kebutuhan)

### 3. Storage
- Aktifkan Firebase Storage
- Mode: Test mode (untuk development)

## Setup Lengkap

Lihat file `README.md` di root project untuk instruksi lengkap setup Firebase.
