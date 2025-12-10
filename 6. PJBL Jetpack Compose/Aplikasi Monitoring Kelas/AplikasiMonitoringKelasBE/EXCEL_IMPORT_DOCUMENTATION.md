# 📊 Excel Import System Documentation

## 🎯 Overview
Sistem import data melalui file Excel/CSV yang lengkap dengan template, validasi, dan error handling yang baik.

---

## ✨ Fitur Utama

### 1. **Import Data dari Excel/CSV**
- Support format: `.xlsx`, `.xls`, `.csv`
- Maximum file size: 10MB
- Validasi data otomatis
- Error reporting yang detail

### 2. **Template Generator**
- Template Excel yang sudah terformat
- Header dengan warna berbeda untuk setiap entitas
- Sheet instruksi penggunaan
- Dropdown validation (untuk field tertentu)
- Contoh data pada baris ke-2
- Freeze header untuk kemudahan input

### 3. **Data Validation**
- Required field validation
- Unique constraint checking
- Email format validation
- Max length validation
- Dropdown options validation
- Foreign key existence checking

---

## 📁 File Structure

```
app/
├── Helpers/
│   ├── ExcelImporter.php    # Helper untuk import Excel
│   └── ExcelExporter.php    # Helper untuk export Excel
├── Http/Controllers/
│   └── AdminController.php  # Controller dengan method import
└── ...

resources/views/admin/
├── users/index.blade.php     # UI dengan import modal
├── gurus/index.blade.php     # UI dengan import modal
├── mapels/index.blade.php    # UI dengan import modal
└── kelas/index.blade.php     # UI dengan import modal

routes/
└── web.php                   # Routes untuk import & template
```

---

## 🔧 Components

### 1. ExcelImporter Helper

**Location:** `app/Helpers/ExcelImporter.php`

**Methods:**

#### `read($filePath)`
Membaca file Excel/CSV dan mengembalikan array data.

```php
$result = ExcelImporter::read($filePath);
// Returns: ['success' => true, 'data' => [...]]
```

#### `generateTemplate($headers, $filename, $instructions, $dropdowns, $headerColor)`
Generate template Excel dengan formatting.

```php
ExcelImporter::generateTemplate(
    headers: ['Nama', 'Email', 'Role'],
    filename: 'Template_Users.xlsx',
    instructions: ['Isi data sesuai kolom...'],
    dropdowns: [2 => ['admin', 'user']],
    headerColor: '4472C4'
);
```

#### `validate($data, $rules)`
Validasi data sebelum import.

```php
$validation = ExcelImporter::validate($data, [
    0 => ['name' => 'Nama', 'required' => true, 'max_length' => 255],
    1 => ['name' => 'Email', 'required' => true, 'email' => true],
]);
```

**Validation Rules:**
- `required` - Field wajib diisi
- `unique` - Callback untuk cek uniqueness
- `max_length` - Maksimal panjang karakter
- `email` - Validasi format email
- `in` - Harus dalam array tertentu
- `exists` - Callback untuk cek foreign key

---

## 📋 Import Templates

### 1. **Users Template**
**Route:** `admin.users.template`

**Columns:**
| Column | Type | Required | Validation |
|--------|------|----------|------------|
| Nama | Text | Yes | Max 255 chars |
| Email | Email | Yes | Valid email, unique |
| Password | Text | Yes | Min 8 chars |
| Role | Dropdown | Yes | siswa, kurikulum, kepala_sekolah, admin |

**Color:** Blue (`#4472C4`)

---

### 2. **Guru Template**
**Route:** `admin.gurus.template`

**Columns:**
| Column | Type | Required | Validation |
|--------|------|----------|------------|
| Kode Guru | Text | Yes | Max 50 chars, unique |
| Nama Guru | Text | Yes | Max 255 chars |
| Telepon | Text | No | Max 20 chars |

**Color:** Green (`#70AD47`)

---

### 3. **Mata Pelajaran Template**
**Route:** `admin.mapels.template`

**Columns:**
| Column | Type | Required | Validation |
|--------|------|----------|------------|
| Kode Mapel | Text | Yes | Max 20 chars, unique |
| Nama Mapel | Text | Yes | Max 255 chars |
| Deskripsi | Text | No | - |

**Color:** Yellow (`#FFC000`)

---

### 4. **Kelas Template**
**Route:** `admin.kelas.template`

**Columns:**
| Column | Type | Required | Validation |
|--------|------|----------|------------|
| Nama Kelas | Text | Yes | Max 255 chars, unique |

**Color:** Sky Blue (`#5B9BD5`)

---

## 🛣️ Routes

### Template Download Routes
```php
GET /admin/users/template/download    -> downloadTemplateUsers()
GET /admin/gurus/template/download    -> downloadTemplateGurus()
GET /admin/mapels/template/download   -> downloadTemplateMapels()
GET /admin/kelas/template/download    -> downloadTemplateKelas()
```

### Import Routes
```php
POST /admin/users/import    -> importUsers()
POST /admin/gurus/import    -> importGurus()
POST /admin/mapels/import   -> importMapels()
POST /admin/kelas/import    -> importKelas()
```

---

## 💻 Usage Guide

### For End Users

#### Step 1: Download Template
1. Buka halaman management (Users, Guru, Mapel, atau Kelas)
2. Klik tombol **"Import Excel"** (orange button)
3. Klik **"Download Template Excel"** (green button)
4. Template akan terdownload

#### Step 2: Fill Data
1. Buka template Excel
2. Baca sheet **"Instruksi"** terlebih dahulu
3. Pindah ke sheet **"Data"**
4. Isi data mulai baris ke-3 (baris 2 adalah contoh)
5. Untuk kolom dropdown, klik cell dan pilih dari dropdown
6. Hapus baris contoh (baris 2) sebelum upload

#### Step 3: Upload File
1. Kembali ke halaman management
2. Klik tombol **"Import Excel"**
3. Klik **"Upload File Excel"**
4. Pilih file yang sudah diisi
5. Klik **"Import Data"**
6. Tunggu proses selesai

#### Step 4: Check Results
- **Success:** Akan muncul notifikasi sukses dengan jumlah data yang diimport
- **Error:** Akan muncul daftar error validasi yang detail

---

### For Developers

#### Adding New Import Feature

**1. Add Import Method in AdminController**

```php
public function importNamaEntitas(Request $request)
{
    $request->validate([
        'file' => 'required|file|mimes:xlsx,xls,csv|max:10240'
    ]);
    
    $file = $request->file('file');
    $result = ExcelImporter::read($file->getPathname());
    
    if (!$result['success']) {
        return redirect()->back()->with('error', $result['message']);
    }
    
    $data = $result['data'];
    
    // Validate
    $validation = ExcelImporter::validate($data, [
        0 => ['name' => 'Field 1', 'required' => true],
        1 => ['name' => 'Field 2', 'required' => true, 'unique' => function($value) {
            return Model::where('field', $value)->exists();
        }],
    ]);
    
    if (!$validation['valid']) {
        return redirect()->back()->with('error', 'Validasi gagal:<br>' . implode('<br>', $validation['errors']));
    }
    
    // Import
    $imported = 0;
    foreach ($data as $row) {
        Model::create([
            'field1' => $row[0],
            'field2' => $row[1],
        ]);
        $imported++;
    }
    
    return redirect()->route('admin.namaentitas')->with('success', "Berhasil import {$imported} data!");
}
```

**2. Add Template Generator Method**

```php
public function downloadTemplateNamaEntitas()
{
    $headers = ['Field 1', 'Field 2', 'Field 3'];
    $instructions = [
        'Isi data sesuai dengan kolom yang tersedia',
        'Field 1: Deskripsi (wajib diisi)',
        'Field 2: Deskripsi (wajib diisi)',
    ];
    $dropdowns = [
        2 => ['Option 1', 'Option 2'] // Index kolom => array options
    ];
    
    $filename = 'Template_Import_NamaEntitas.xlsx';
    $filepath = ExcelImporter::generateTemplate($headers, $filename, $instructions, $dropdowns, 'FF0000');
    
    return response()->download($filepath, $filename);
}
```

**3. Add Routes**

```php
Route::get('/namaentitas/template/download', [AdminController::class, 'downloadTemplateNamaEntitas'])->name('namaentitas.template');
Route::post('/namaentitas/import', [AdminController::class, 'importNamaEntitas'])->name('namaentitas.import');
```

**4. Update View**

Add import button and modal to index view.

---

## 🎨 UI Components

### Import Button
```blade
<button onclick="document.getElementById('importModal').classList.remove('hidden')" 
    class="bg-orange-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-orange-700">
    <i class="fas fa-file-import mr-2"></i>Import Excel
</button>
```

### Import Modal
```blade
<div id="importModal" class="hidden fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
    <div class="relative top-20 mx-auto p-8 border w-full max-w-2xl shadow-2xl rounded-2xl bg-white">
        <div class="flex justify-between items-center mb-6">
            <h3 class="text-2xl font-bold text-gray-800">
                <i class="fas fa-file-import text-orange-600 mr-2"></i>Import Data
            </h3>
            <button onclick="document.getElementById('importModal').classList.add('hidden')" 
                class="text-gray-400 hover:text-gray-600 text-3xl font-bold">&times;</button>
        </div>
        
        <form action="{{ route('admin.entity.import') }}" method="POST" enctype="multipart/form-data" class="space-y-6">
            @csrf
            <div>
                <a href="{{ route('admin.entity.template') }}" 
                    class="inline-flex items-center px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700">
                    <i class="fas fa-file-excel mr-2"></i>Download Template
                </a>
            </div>
            <div>
                <input type="file" name="file" accept=".xlsx,.xls,.csv" required 
                    class="block w-full text-sm border rounded-lg p-3">
            </div>
            <div class="flex justify-end gap-3">
                <button type="button" onclick="document.getElementById('importModal').classList.add('hidden')" 
                    class="px-6 py-3 bg-gray-300 rounded-lg hover:bg-gray-400">Batal</button>
                <button type="submit" 
                    class="px-6 py-3 bg-orange-600 text-white rounded-lg hover:bg-orange-700">Import</button>
            </div>
        </form>
    </div>
</div>
```

---

## 🔍 Error Handling

### Error Messages Format
```
Baris 3: Email wajib diisi
Baris 5: Email 'invalid@email' bukan format email yang valid
Baris 7: Kode Guru 'G001' sudah ada di database
```

### Common Errors

1. **File Format Error**
   - Message: "Error reading file: ..."
   - Solution: Pastikan file format .xlsx, .xls, atau .csv

2. **Validation Error**
   - Message: "Validasi gagal: [list of errors]"
   - Solution: Perbaiki data sesuai error message

3. **Empty File**
   - No error, tapi tidak ada data yang diimport
   - Solution: Pastikan file memiliki data selain header

---

## 🧪 Testing

### Manual Testing Steps

1. **Test Template Download**
   - Akses halaman management
   - Klik Import Excel
   - Klik Download Template
   - Verify: File terdownload dengan benar

2. **Test Valid Import**
   - Isi template dengan data valid
   - Upload file
   - Verify: Data masuk ke database dengan benar

3. **Test Invalid Import**
   - Isi template dengan data invalid (email salah, field kosong, dll)
   - Upload file
   - Verify: Error message muncul dengan jelas

4. **Test Duplicate Import**
   - Import file yang sama 2x
   - Verify: Error unique constraint muncul

5. **Test Large File**
   - Buat file dengan 100+ rows
   - Upload file
   - Verify: Semua data terimpor dengan benar

---

## 📊 Template Features

### 1. Header Styling
- **Bold font** size 12
- **White text** on colored background
- **Centered alignment**
- **Bordered cells**

### 2. Instruction Sheet
- Sheet terpisah dengan nama "Instruksi"
- Step-by-step guide
- Field descriptions
- Important notes

### 3. Data Validation
- **Dropdown lists** untuk field tertentu
- **Input message** saat cell dipilih
- **Error message** untuk invalid input
- Validation applies to 1000 rows

### 4. Example Data
- Row 2 contains example data
- **Gray background** untuk membedakan
- **Italic text** untuk emphasize
- Should be deleted before upload

### 5. Freeze Panes
- Header row frozen
- Easy scrolling saat input banyak data

---

## 🎯 Best Practices

### For Template Design
1. Use clear, descriptive column names
2. Add instruction sheet for complex templates
3. Use dropdown validation for limited options
4. Choose distinct colors for different entities
5. Provide example data

### For Validation
1. Always validate required fields
2. Check uniqueness before insert
3. Validate foreign keys existence
4. Use appropriate error messages
5. Return all errors at once (don't stop at first error)

### For User Experience
1. Show clear error messages
2. Indicate which row has error
3. Show success message with count
4. Make templates easy to download
5. Support multiple file formats

---

## 📝 Color Scheme

| Entity | Color Code | Color Name |
|--------|------------|------------|
| Users | #4472C4 | Blue |
| Guru | #70AD47 | Green |
| Mapel | #FFC000 | Yellow |
| Kelas | #5B9BD5 | Sky Blue |
| Tahun Ajaran | #ED7D31 | Orange |
| Jadwal | #7030A0 | Purple |
| Guru Mengajar | #C00000 | Red |

---

## 🚀 Future Enhancements

### Potential Improvements
1. **Async Processing** - For large files (1000+ rows)
2. **Progress Bar** - Show import progress
3. **Preview Before Import** - Show data preview before actual import
4. **Batch Operations** - Update existing records
5. **Error File Download** - Download Excel with error highlights
6. **Import History** - Track who imported what and when
7. **Rollback Feature** - Undo last import
8. **API Import** - RESTful API for programmatic import

---

## ⚙️ Configuration

### File Upload Limits
```php
// In import methods
$request->validate([
    'file' => 'required|file|mimes:xlsx,xls,csv|max:10240' // 10MB
]);
```

To change max file size, modify the `max:` value (in KB).

### Supported Formats
- `.xlsx` - Excel 2007+
- `.xls` - Excel 97-2003
- `.csv` - Comma Separated Values

---

## 🛠️ Troubleshooting

### Problem: "Error reading file"
**Cause:** File corrupted or wrong format  
**Solution:** Re-download template and try again

### Problem: Import takes too long
**Cause:** Large file or slow server  
**Solution:** Split into smaller files or increase server timeout

### Problem: Validation errors not clear
**Cause:** Missing row number in error message  
**Solution:** Check ExcelImporter::validate() implementation

### Problem: Template not downloading
**Cause:** Storage permission or directory missing  
**Solution:** Run `php artisan storage:link` and check permissions

---

## 📞 Support

For issues or questions:
1. Check this documentation first
2. Review error messages carefully
3. Verify data format matches template
4. Contact system administrator

---

**Last Updated:** October 29, 2025  
**Version:** 1.0.0  
**Author:** System Administrator
