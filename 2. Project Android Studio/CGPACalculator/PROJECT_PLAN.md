# CGPA Calculator - Rencana Proyek Komprehensif

## 1. Nama dan Tujuan Proyek

**Nama Resmi**: CGPA Calculator  
**Tujuan Utama**: Aplikasi Android untuk menghitung Cumulative Grade Point Average (CGPA) mahasiswa berdasarkan input nilai dan kredit mata kuliah dari berbagai semester dengan interface yang user-friendly menggunakan Jetpack Compose.

## 2. Daftar Fitur Utama (Fungsionalitas)

### Fungsionalitas Inti
- ✅ **Input Grade & Credit**: Interface untuk memasukkan nama mata kuliah, grade (A+ sampai F), dan jumlah kredit
- ✅ **Perhitungan CGPA Otomatis**: Menghitung CGPA keseluruhan (All Time CGPA) secara real-time
- ✅ **Tampilan Hasil Semester Sebelumnya**: History semua semester yang telah diinput
- ✅ **Manajemen Mata Kuliah**: Tambah, hapus mata kuliah dalam satu semester
- ✅ **Manajemen Semester**: Simpan semester, hapus semester, reset semua data

### Fitur Tambahan
- ✅ **Academic Standing**: Menampilkan status akademik (Summa Cum Laude, Magna Cum Laude, dll)
- ✅ **Statistik Lengkap**: Total kredit, total semester, persentase nilai
- ✅ **Validasi Input**: Validasi data input untuk mencegah kesalahan
- ✅ **Interface Panduan**: Instruksi penggunaan untuk user baru

## 3. Teknologi Utama (Tech Stack)

### Framework UI Utama
- **Jetpack Compose**: Framework UI declarative modern untuk Android
- **Material Design 3**: Design system untuk konsistensi UI/UX

### Bahasa Pemrograman
- **Kotlin**: Bahasa pemrograman utama untuk Android development

### Komponen Jetpack Compose yang Digunakan

#### Input Components
- `TextField` & `OutlinedTextField`: Input nama mata kuliah dan kredit
- `ExposedDropdownMenuBox`: Dropdown untuk pemilihan grade
- `Button` & `OutlinedButton`: Tombol aksi (tambah, simpan, reset)

#### Layout Components
- `Column` & `Row`: Pengaturan layout vertikal dan horizontal
- `LazyColumn`: List scrollable untuk mata kuliah dan semester
- `Scaffold`: Struktur layout utama dengan TopAppBar
- `Card`: Container untuk grouping konten

#### Display Components
- `Text`: Menampilkan informasi dan label
- `Icon`: Icon untuk aksi (delete, dll)
- `Divider`: Pemisah visual antar section

## 4. Struktur Data/Logika Aplikasi

### Data Classes

#### Course.kt
```kotlin
data class Course(
    val id: String,
    val courseName: String,
    val grade: String,
    val credit: Double
) {
    fun getGradePoint(): Double // Konversi grade ke poin (4.0 scale)
    fun getQualityPoints(): Double // Grade point × credit
    fun isValid(): Boolean // Validasi data
}
```

#### Semester.kt
```kotlin
data class Semester(
    val id: String,
    val semesterName: String,
    val courses: List<Course>
) {
    fun getSemesterGPA(): Double // GPA untuk semester ini
    fun getTotalCredits(): Double // Total kredit semester
    fun getTotalQualityPoints(): Double // Total quality points
}
```

#### CGPAResult.kt
```kotlin
data class CGPAResult(
    val cgpa: Double,
    val totalCredits: Double,
    val totalQualityPoints: Double,
    val totalSemesters: Int
) {
    fun getFormattedCGPA(): String // Format CGPA ke 2 desimal
    fun getCGPAPercentage(): Double // CGPA dalam persentase
    fun getAcademicStanding(): String // Status akademik
}
```

### State Management dengan Compose

#### CGPAViewModel.kt
- **State Variables**: Menggunakan `mutableStateOf` untuk reactive state
  - `courseName`: State untuk nama mata kuliah
  - `selectedGrade`: State untuk grade yang dipilih
  - `creditHours`: State untuk jumlah kredit
  - `currentCourses`: State untuk daftar mata kuliah semester aktif
  - `semesters`: State untuk semua semester
  - `cgpaResult`: State untuk hasil CGPA

- **State Management Pattern**:
  ```kotlin
  private val _courseName = mutableStateOf("")
  val courseName: State<String> = _courseName
  ```

### Logika Perhitungan CGPA

#### Grade Point Conversion
- A+, A = 4.0
- A- = 3.7
- B+ = 3.3
- B = 3.0
- B- = 2.7
- C+ = 2.3
- C = 2.0
- C- = 1.7
- D+ = 1.3
- D = 1.0
- F = 0.0

#### CGPA Formula
```
CGPA = Σ(Grade Point × Credit Hours) / Σ(Total Credit Hours)
```

#### Quality Points Calculation
```
Quality Points = Grade Point × Credit Hours
Total Quality Points = Σ(Quality Points dari semua mata kuliah)
```

## 5. Perancangan UI (Berdasarkan Komponen Compose)

### Struktur Layout Utama

#### MainScreen Layout
```
Scaffold(
  topBar = TopAppBar
  content = Column(
    - CGPAResultSection (Priority display)
    - CourseInputSection
    - CourseListSection
    - SemesterActionSection
    - SemesterHistorySection
    - InstructionsSection
  )
)
```

### Komponen UI Detail

#### 1. CGPAResultSection
- **Container**: `Card` dengan `primaryContainer` background
- **Layout**: `Column` dengan center alignment
- **Content**: 
  - CGPA besar dengan `displayLarge` typography
  - Academic standing badge
  - Statistik dalam `Row` layout (semester, kredit, persentase)

#### 2. CourseInputSection
- **Container**: `Card` dengan elevated shadow
- **Input Fields**:
  - `OutlinedTextField` untuk nama mata kuliah
  - `ExposedDropdownMenuBox` untuk grade selection
  - `OutlinedTextField` dengan `KeyboardType.Decimal` untuk kredit
- **Action**: `Button` untuk menambah mata kuliah

#### 3. CourseListSection
- **Container**: `LazyColumn` dalam `Card`
- **Items**: Setiap mata kuliah dalam `Card` terpisah
- **Layout**: `Row` dengan course info dan delete `IconButton`
- **Summary**: Total courses dan credits di bawah

#### 4. SemesterActionSection
- **Input**: `OutlinedTextField` untuk nama semester
- **Actions**: `Row` dengan `Button` (Save) dan `OutlinedButton` (Reset)

#### 5. SemesterHistorySection
- **Container**: `LazyColumn` untuk scrollable list
- **Items**: Expandable semester cards dengan course details
- **Header**: Semester name, GPA, credits, course count
- **Actions**: Delete `IconButton` untuk setiap semester

### Design Principles

#### Color Scheme
- **Primary**: Blue (#1976D2) untuk aksi utama
- **Success**: Green untuk grade A
- **Warning**: Orange untuk grade rendah
- **Error**: Red untuk grade F
- **Surface**: Cards dengan elevation untuk grouping

#### Typography Hierarchy
- **Display Large**: CGPA utama (57sp, Bold)
- **Headline**: Section headers (24-32sp, Bold)
- **Title**: Semester names (22sp, Bold)
- **Body**: Regular text (16sp, Normal)
- **Label**: Small info text (11sp, Medium)

#### Spacing & Layout
- **Padding**: 16dp untuk screen margins
- **Card Spacing**: 12-16dp antar cards
- **Internal Padding**: 16-20dp dalam cards
- **Component Spacing**: 8-12dp antar komponen

## 6. Arsitektur Aplikasi

### MVVM Pattern
- **Model**: Data classes (Course, Semester, CGPAResult)
- **View**: Composable screens dan components
- **ViewModel**: CGPAViewModel untuk business logic dan state

### State Flow
```
User Input → ViewModel → State Update → UI Recomposition
```

### Data Flow
```
Input → Validation → State Update → CGPA Calculation → UI Update
```

## 7. Fitur Implementasi

### ✅ Sudah Diimplementasi
1. Complete data models dengan validasi
2. State management dengan Compose
3. UI components lengkap dan responsive
4. CGPA calculation algorithm
5. Semester management
6. Course management
7. Academic standing calculation
8. Input validation
9. Material Design 3 theming
10. Comprehensive user instructions

### 🚀 Struktur Project Final
```
app/src/main/java/com/example/cgpacalculator/
├── model/
│   ├── Course.kt
│   ├── Semester.kt
│   └── CGPAResult.kt
├── viewmodel/
│   └── CGPAViewModel.kt
├── ui/
│   ├── components/
│   │   ├── CourseInputSection.kt
│   │   ├── CourseListSection.kt
│   │   ├── CGPAResultSection.kt
│   │   ├── SemesterActionSection.kt
│   │   └── SemesterHistorySection.kt
│   ├── screen/
│   │   └── CGPACalculatorScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt
```

Proyek CGPA Calculator ini telah diimplementasikan secara komprehensif dengan semua fitur yang diminta, menggunakan modern Android development practices dengan Jetpack Compose dan Material Design 3.