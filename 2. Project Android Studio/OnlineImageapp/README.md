# Online Image Gallery App

Aplikasi Android untuk melihat, mencari, dan mengelola gambar online dengan fitur-fitur modern dan UI yang menarik.

## 📱 Fitur Utama

### ✨ Fitur Inti
- **Grid Gallery**: Menampilkan gambar dalam format grid yang responsif
- **Image Search**: Pencarian gambar real-time dengan debouncing
- **Image Detail**: View gambar ukuran penuh dengan zoom dan pan
- **Pull to Refresh**: Refresh konten dengan gesture swipe
- **Infinite Scroll**: Loading gambar tambahan secara otomatis

### 🎯 Fitur Interaksi
- **Favorite System**: Simpan gambar favorit dengan toggle heart
- **Share Image**: Bagikan gambar ke aplikasi lain
- **Download Image**: Simpan gambar ke galeri perangkat
- **Image Tags**: Tampilkan tag/kategori gambar
- **Loading States**: Animasi loading dan error handling

## 🛠️ Teknologi yang Digunakan

### 📦 Libraries & Dependencies
- **Kotlin**: Bahasa pemrograman utama
- **View Binding**: Binding layout dengan type-safe
- **Material Design 3**: UI components modern dari Google
- **Glide**: Image loading dan caching
- **Retrofit**: HTTP client untuk API calls
- **OkHttp**: Networking dengan logging interceptor
- **PhotoView**: Zoom dan pan pada image detail
- **Coroutines**: Asynchronous programming
- **ViewModel & LiveData**: MVVM architecture
- **RecyclerView**: Efficient list display
- **SwipeRefreshLayout**: Pull-to-refresh functionality

### 🏗️ Arsitektur
- **MVVM Pattern**: Model-View-ViewModel
- **Repository Pattern**: Data abstraction layer
- **Single Activity**: Modern navigation approach
- **Reactive Programming**: LiveData observables

## 📋 Struktur Project

```
app/src/main/java/com/example/onlineimageapp/
├── data/
│   ├── model/          # Data classes (ImageItem, UnsplashPhoto, etc.)
│   ├── network/        # API services dan networking
│   └── repository/     # Repository untuk data management
├── ui/
│   ├── adapter/        # RecyclerView adapters
│   ├── viewmodel/      # ViewModels untuk UI logic
│   └── ImageDetailActivity.kt
├── utils/              # Utility classes
└── MainActivity.kt     # Activity utama
```

## 🚀 Cara Menjalankan

### Prerequisites
- Android Studio Arctic Fox atau lebih baru
- Android SDK API level 26 (Android 8.0) atau lebih tinggi
- Koneksi internet untuk loading gambar

### Langkah Instalasi
1. Clone repository ini
2. Buka project di Android Studio
3. Sync Gradle dependencies
4. Run aplikasi di device/emulator

### API Configuration
Aplikasi menggunakan dummy data dan Picsum Photos API sebagai fallback. Untuk production:
1. Daftar di [Unsplash Developers](https://unsplash.com/developers)
2. Dapatkan API key
3. Update `API_KEY` di `UnsplashApiService.kt`

## 📱 Screenshots & Demo

### Main Features
- **Grid Gallery**: Tampilan grid gambar dengan overlay informasi
- **Search**: Real-time search dengan hasil yang relevan
- **Detail View**: Fullscreen image dengan zoom, info, dan actions
- **Responsive**: Adaptif untuk berbagai ukuran layar

### UI/UX Highlights
- Material Design 3 dengan dark/light theme support
- Smooth animations dan transitions
- Loading states dan error handling
- Intuitive gestures dan navigation

## 🔧 Kustomisasi

### Mengubah Grid Layout
```kotlin
// Di MainActivity.kt
val spanCount = 3 // Ubah jumlah kolom
val layoutManager = GridLayoutManager(this, spanCount)
```

### Mengubah Image Source
```kotlin
// Di ImageRepository.kt
// Ganti URL API atau tambah sumber gambar lain
const val BASE_URL = "https://api.your-image-service.com/"
```

### Styling Customization
- Edit `themes.xml` untuk mengubah color scheme
- Modifikasi `item_image.xml` untuk layout item
- Update `activity_main.xml` untuk main layout

## 🐛 Troubleshooting

### Common Issues
1. **Images not loading**: Periksa koneksi internet dan API key
2. **Download gagal**: Pastikan storage permission diberikan
3. **Layout issues**: Periksa constraint layouts dan dimensions

### Debug Tips
- Enable logging di `ApiClient.kt`
- Periksa Logcat untuk error messages
- Test di berbagai ukuran layar

## 📈 Pengembangan Lanjutan

### Fitur yang Bisa Ditambahkan
- **Local Database**: Room database untuk offline storage
- **User Authentication**: Login dan profile management
- **Image Upload**: Upload gambar ke server
- **Advanced Filters**: Filter by color, size, orientation
- **Sharing Collections**: Share kumpulan gambar favorit
- **Background Download**: Download dalam background
- **Image Editing**: Basic editing tools

### Performance Optimizations
- **Image Caching**: Implementasi cache strategy yang lebih advanced
- **Pagination**: Optimasi loading dengan pagination
- **Memory Management**: Optimasi penggunaan memory
- **Network Optimization**: Compress dan optimize network calls

## 📄 Lisensi

Project ini dibuat untuk tujuan pembelajaran dan demonstrasi. Gambar-gambar yang digunakan berasal dari sumber yang legal dan free-to-use.

## 🤝 Kontribusi

Contributions welcome! Silakan fork repository ini dan buat pull request untuk improvements.

---

**Happy Coding!** 🚀📱✨