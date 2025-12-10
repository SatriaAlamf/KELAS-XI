# 📊 Import Feature - Extended Documentation

## 🎯 Fitur Import Tambahan

Telah ditambahkan fitur import untuk entitas yang lebih kompleks dengan relasi foreign key.

---

## 🆕 Entitas Baru yang Mendukung Import

### 1. **Tahun Ajaran**
**Route:** `admin.tahun-ajarans.template` & `admin.tahun-ajarans.import`

**Columns:**
| Column | Type | Required | Validation | Contoh |
|--------|------|----------|------------|--------|
| Tahun | Text | Yes | Max 20 chars, unique | 2024/2025 |
| Flag | Dropdown | Yes | 0 atau 1 | 1 |

**Keterangan:**
- Flag: 0 = Tidak Aktif, 1 = Aktif
- Tahun harus unik, tidak boleh duplikat
- Format tahun ajaran: YYYY/YYYY

**Color:** Orange (`#ED7D31`)

---

### 2. **Jadwal** (Complex - dengan Foreign Keys)
**Route:** `admin.jadwals.template` & `admin.jadwals.import`

**Columns:**
| Column | Type | Required | Validation | Contoh |
|--------|------|----------|------------|--------|
| Kode Guru | Text | Yes | Must exist in database | G001 |
| Kode Mapel | Text | Yes | Must exist in database | MTK |
| Tahun Ajaran | Text | Yes | Must exist in database | 2024/2025 |
| Nama Kelas | Text | Yes | Must exist in database | XII RPL 1 |
| Hari | Dropdown | Yes | Senin-Sabtu | Senin |
| Jam Ke | Text | Yes | - | 1 |

**Keterangan:**
- **PENTING:** Semua data referensi (Guru, Mapel, Tahun Ajaran, Kelas) **HARUS sudah ada** di database
- Sistem akan mencari ID berdasarkan kode/nama yang diinput
- Jika data referensi tidak ditemukan, akan muncul error dengan row number

**Dependencies:**
1. Import Guru terlebih dahulu
2. Import Mapel terlebih dahulu
3. Import Tahun Ajaran terlebih dahulu
4. Import Kelas terlebih dahulu
5. Baru import Jadwal

**Color:** Purple (`#7030A0`)

---

### 3. **Guru Mengajar** (Most Complex - dengan Jadwal Reference)
**Route:** `admin.guru-mengajars.template` & `admin.guru-mengajars.import`

**Columns:**
| Column | Type | Required | Validation | Contoh |
|--------|------|----------|------------|--------|
| Kode Guru | Text | Yes | Must exist in database | G001 |
| Nama Kelas | Text | Yes | Must exist in database | XII RPL 1 |
| Kode Mapel | Text | Yes | Must exist in database | MTK |
| Hari | Dropdown | Yes | Senin-Sabtu | Senin |
| Jam Ke | Text | Yes | - | 1 |
| Status | Dropdown | Yes | masuk/tidak_masuk | masuk |
| Keterangan | Text | No | - | Hadir tepat waktu |

**Keterangan:**
- **SANGAT PENTING:** Jadwal dengan kombinasi (Guru, Kelas, Mapel, Hari, Jam) **HARUS sudah ada**
- Sistem akan mencari Jadwal ID berdasarkan kombinasi 5 field pertama
- Jika Jadwal tidak ditemukan, data akan di-skip dengan error message
- Import akan tetap berlanjut untuk row lainnya yang valid

**Dependencies:**
1. Import Guru
2. Import Mapel
3. Import Tahun Ajaran
4. Import Kelas
5. Import Jadwal
6. Baru import Guru Mengajar

**Special Handling:**
- Partial import: Data yang valid akan masuk, yang error akan di-skip
- Error message detail menunjukkan kombinasi yang tidak ditemukan
- Success message menunjukkan berapa data berhasil diimport

**Color:** Red (`#C00000`)

---

## 🔄 Import Workflow

### Urutan Import yang Benar

```
1. Users (Independen)
   ↓
2. Guru (Independen)
   ↓
3. Mapel (Independen)
   ↓
4. Kelas (Independen)
   ↓
5. Tahun Ajaran (Independen)
   ↓
6. Jadwal (Depends on: Guru, Mapel, Kelas, Tahun Ajaran)
   ↓
7. Guru Mengajar (Depends on: Jadwal)
```

**Catatan:** Entitas independen bisa diimport dalam urutan apa saja

---

## 🎓 Template Specifications

### Tahun Ajaran Template

**Sheet 1: Instruksi**
```
INSTRUKSI PENGISIAN

• Isi data sesuai dengan kolom yang tersedia
• Tahun: Tahun ajaran (wajib diisi, contoh: 2024/2025)
• Flag: Status aktif (1 untuk aktif, 0 untuk tidak aktif)
• Jangan mengubah nama kolom header
• Hapus baris contoh (baris 2) sebelum mengupload
```

**Sheet 2: Data**
```
| Tahun     | Flag |
|-----------|------|
| 2024/2025 | 1    | <- Contoh (hapus sebelum upload)
```

---

### Jadwal Template

**Sheet 1: Instruksi**
```
INSTRUKSI PENGISIAN

• Isi data sesuai dengan kolom yang tersedia
• Kode Guru: Kode guru yang sudah terdaftar (wajib diisi)
• Kode Mapel: Kode mata pelajaran yang sudah terdaftar (wajib diisi)
• Tahun Ajaran: Tahun ajaran yang sudah terdaftar (wajib diisi, contoh: 2024/2025)
• Nama Kelas: Nama kelas yang sudah terdaftar (wajib diisi)
• Hari: Pilih dari dropdown (Senin, Selasa, Rabu, Kamis, Jumat, Sabtu)
• Jam Ke: Jam pelajaran (wajib diisi, contoh: 1, 2, 3)
• Pastikan data guru, mapel, tahun ajaran, dan kelas sudah ada di database
• Jangan mengubah nama kolom header
• Hapus baris contoh (baris 2) sebelum mengupload
```

**Sheet 2: Data** (dengan dropdown untuk Hari)
```
| Kode Guru | Kode Mapel | Tahun Ajaran | Nama Kelas | Hari  | Jam Ke |
|-----------|------------|--------------|------------|-------|--------|
| G001      | MTK        | 2024/2025    | XII RPL 1  | Senin | 1      | <- Contoh
```

---

### Guru Mengajar Template

**Sheet 1: Instruksi**
```
INSTRUKSI PENGISIAN

• Isi data sesuai dengan kolom yang tersedia
• Kode Guru: Kode guru yang sudah terdaftar (wajib diisi)
• Nama Kelas: Nama kelas yang sudah terdaftar (wajib diisi)
• Kode Mapel: Kode mata pelajaran yang sudah terdaftar (wajib diisi)
• Hari: Pilih dari dropdown (Senin, Selasa, Rabu, Kamis, Jumat, Sabtu)
• Jam Ke: Jam pelajaran (wajib diisi, contoh: 1, 2, 3)
• Status: Pilih dari dropdown (masuk atau tidak_masuk)
• Keterangan: Keterangan tambahan (opsional)
• Pastikan jadwal dengan kombinasi guru, kelas, mapel, hari, dan jam sudah ada di database
• Jangan mengubah nama kolom header
• Hapus baris contoh (baris 2) sebelum mengupload
```

**Sheet 2: Data** (dengan 2 dropdown: Hari & Status)
```
| Kode Guru | Nama Kelas | Kode Mapel | Hari  | Jam Ke | Status | Keterangan |
|-----------|------------|------------|-------|--------|--------|------------|
| G001      | XII RPL 1  | MTK        | Senin | 1      | masuk  | -          | <- Contoh
```

---

## 🔍 Validation Rules Detail

### Tahun Ajaran
```php
[
    0 => ['name' => 'Tahun', 'required' => true, 'max_length' => 20, 'unique' => true],
    1 => ['name' => 'Flag', 'required' => true, 'in' => ['0', '1']]
]
```

### Jadwal
```php
[
    0 => ['name' => 'Kode Guru', 'required' => true, 'exists' => Guru::kode_guru],
    1 => ['name' => 'Kode Mapel', 'required' => true, 'exists' => Mapel::kode_mapel],
    2 => ['name' => 'Tahun Ajaran', 'required' => true, 'exists' => TahunAjaran::tahun],
    3 => ['name' => 'Nama Kelas', 'required' => true, 'exists' => Kelas::nama_kelas],
    4 => ['name' => 'Hari', 'required' => true, 'in' => ['Senin',...,'Sabtu']],
    5 => ['name' => 'Jam Ke', 'required' => true]
]
```

### Guru Mengajar
```php
[
    0 => ['name' => 'Kode Guru', 'required' => true, 'exists' => Guru::kode_guru],
    1 => ['name' => 'Nama Kelas', 'required' => true, 'exists' => Kelas::nama_kelas],
    2 => ['name' => 'Kode Mapel', 'required' => true, 'exists' => Mapel::kode_mapel],
    3 => ['name' => 'Hari', 'required' => true, 'in' => ['Senin',...,'Sabtu']],
    4 => ['name' => 'Jam Ke', 'required' => true],
    5 => ['name' => 'Status', 'required' => true, 'in' => ['masuk', 'tidak_masuk']]
]
```

---

## ⚠️ Error Handling

### Tahun Ajaran Errors

**Error 1: Duplikat**
```
Baris 3: Tahun '2024/2025' sudah ada di database
```
**Solusi:** Gunakan tahun ajaran yang berbeda

**Error 2: Flag Invalid**
```
Baris 4: Flag harus salah satu dari: 0, 1
```
**Solusi:** Gunakan 0 atau 1

---

### Jadwal Errors

**Error 1: Guru Tidak Ditemukan**
```
Baris 3: Kode Guru 'G999' tidak ditemukan di database
```
**Solusi:** Pastikan guru dengan kode G999 sudah ada, atau import guru terlebih dahulu

**Error 2: Mapel Tidak Ditemukan**
```
Baris 5: Kode Mapel 'BIO' tidak ditemukan di database
```
**Solusi:** Import mapel BIO terlebih dahulu

**Error 3: Kelas Tidak Ditemukan**
```
Baris 7: Nama Kelas 'XII TKJ 1' tidak ditemukan di database
```
**Solusi:** Import kelas terlebih dahulu

**Error 4: Hari Invalid**
```
Baris 8: Hari harus salah satu dari: Senin, Selasa, Rabu, Kamis, Jumat, Sabtu
```
**Solusi:** Gunakan dropdown, jangan ketik manual

---

### Guru Mengajar Errors

**Error 1: Jadwal Tidak Ditemukan**
```
Baris 3: Jadwal tidak ditemukan untuk kombinasi Guru 'G001', Kelas 'XII RPL 1', Mapel 'MTK', Hari 'Senin', Jam '1'
```
**Solusi:** 
1. Cek apakah jadwal dengan kombinasi tersebut sudah ada
2. Import jadwal terlebih dahulu jika belum ada
3. Pastikan kode guru, nama kelas, dan kode mapel tepat sama

**Error 2: Status Invalid**
```
Baris 5: Status harus salah satu dari: masuk, tidak_masuk
```
**Solusi:** Gunakan dropdown, pilih 'masuk' atau 'tidak_masuk'

---

## 💡 Best Practices

### 1. Prepare Data Master First
```
Sebelum import Jadwal:
✓ Import semua Guru
✓ Import semua Mapel  
✓ Import semua Kelas
✓ Import semua Tahun Ajaran

Sebelum import Guru Mengajar:
✓ Pastikan semua Jadwal sudah ada
```

### 2. Verify Before Import
```
1. Download data master current (Export Excel)
2. Cocokkan kode/nama yang akan diimport
3. Pastikan exact match (case sensitive)
```

### 3. Test with Small Data
```
1. Import 2-3 row terlebih dahulu
2. Verify hasilnya di database
3. Baru import data lengkap
```

### 4. Handle Partial Import
```
Guru Mengajar mendukung partial import:
- Data valid akan masuk
- Data error akan di-skip
- Error message detail untuk debugging
```

---

## 📊 Import Statistics

### Expected Performance

**Tahun Ajaran:**
- 10 rows: < 2 seconds
- 50 rows: < 5 seconds

**Jadwal:**
- 10 rows: < 3 seconds (karena ada lookup FK)
- 100 rows: < 10 seconds

**Guru Mengajar:**
- 10 rows: < 4 seconds (karena complex lookup)
- 100 rows: < 15 seconds

---

## 🧪 Testing Scenarios

### Test 1: Tahun Ajaran Valid Import
```excel
Tahun     | Flag
2024/2025 | 1
2025/2026 | 0
2023/2024 | 1
```
✅ Expected: 3 data imported successfully

### Test 2: Jadwal Valid Import (after preparing data)
```excel
Kode Guru | Kode Mapel | Tahun Ajaran | Nama Kelas | Hari   | Jam Ke
G001      | MTK        | 2024/2025    | XII RPL 1  | Senin  | 1
G002      | IPA        | 2024/2025    | XII RPL 1  | Senin  | 2
G001      | MTK        | 2024/2025    | XII RPL 2  | Senin  | 1
```
✅ Expected: 3 jadwal imported successfully

### Test 3: Guru Mengajar with Missing Jadwal
```excel
Kode Guru | Nama Kelas | Kode Mapel | Hari  | Jam Ke | Status | Keterangan
G001      | XII RPL 1  | MTK        | Senin | 1      | masuk  | OK
G999      | XII TKJ 1  | BIO        | Rabu  | 3      | masuk  | Belum ada jadwal
```
✅ Expected: 
- 1 data imported
- 1 error message for row 3
- Warning notification showing partial import

---

## 🔧 Troubleshooting Extended

### Problem: "Jadwal tidak ditemukan"
**Diagnosis:**
1. Check apakah jadwal dengan kombinasi tersebut ada
2. Verify exact match untuk kode guru, nama kelas, kode mapel
3. Pastikan hari dan jam exact match

**Solution:**
```sql
-- Query untuk cek jadwal
SELECT j.*, g.kode_guru, k.nama_kelas, m.kode_mapel
FROM jadwals j
JOIN gurus g ON j.guru_id = g.id
JOIN kelas k ON j.kelas_id = k.id
JOIN mapels m ON j.mapel_id = m.id
WHERE g.kode_guru = 'G001'
  AND k.nama_kelas = 'XII RPL 1'
  AND m.kode_mapel = 'MTK'
  AND j.hari = 'Senin'
  AND j.jam_ke = '1';
```

### Problem: Import Lambat untuk Guru Mengajar
**Cause:** Complex lookup dengan 5 field

**Solution:**
1. Split import menjadi batch kecil (50 rows)
2. Ensure index on foreign keys
3. Run during low-traffic time

---

## 📞 Developer Notes

### Foreign Key Lookup Pattern
```php
// Pattern untuk import dengan FK
$guru = Guru::where('kode_guru', $row[0])->first();
$mapel = Mapel::where('kode_mapel', $row[1])->first();
// ... get other FKs

if (!$guru || !$mapel || ...) {
    // Handle missing FK
}

Model::create([
    'guru_id' => $guru->id,
    'mapel_id' => $mapel->id,
    // ...
]);
```

### Partial Import Pattern
```php
$imported = 0;
$errors = [];

foreach ($data as $index => $row) {
    try {
        // Import logic
        $imported++;
    } catch (Exception $e) {
        $errors[] = "Baris " . ($index + 2) . ": " . $e->getMessage();
    }
}

if (!empty($errors)) {
    return redirect()->back()->with('warning', "Partial import: {$imported} sukses, " . count($errors) . " error");
}
```

---

## ✅ Feature Completion Checklist

- [x] Tahun Ajaran import
- [x] Jadwal import (with FK validation)
- [x] Guru Mengajar import (with complex lookup)
- [x] Template generation untuk semua
- [x] Dropdown validation
- [x] Foreign key existence checking
- [x] Partial import support (Guru Mengajar)
- [x] Error messages dengan row numbers
- [x] UI integration dengan warning banners
- [x] Routes registration
- [x] Documentation

---

**Status:** ✅ **COMPLETE**  
**Date:** October 29, 2025  
**Version:** 2.0.0 (Extended Import Features)
