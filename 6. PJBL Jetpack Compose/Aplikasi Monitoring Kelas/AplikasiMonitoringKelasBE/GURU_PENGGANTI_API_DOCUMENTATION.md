# 📚 API Documentation: Guru Pengganti (Substitute Teacher)

## Overview
API untuk mengelola data guru pengganti yang menggantikan guru asli yang tidak masuk mengajar. Fitur ini mencakup:
- ✅ CRUD lengkap (Create, Read, Update, Delete)
- ✅ Tracking durasi penggantian
- ✅ Status guru pengganti (aktif/selesai)
- ✅ Relasi dengan GuruMengajar dan Jadwal
- ✅ Informasi guru asli dan guru pengganti

---

## Base URL
```
http://localhost:8000/api
```

---

## 📋 Endpoints

### 1. **Get All Guru Pengganti (With Filters)**
Mengambil semua data guru pengganti dengan filter optional.

**Endpoint:**
```
GET /guru-pengganti
```

**Query Parameters (Optional):**
| Parameter | Type | Description |
|-----------|------|-------------|
| `status` | string | Filter by status: `aktif` atau `selesai` |
| `guru_mengajar_id` | integer | Filter by specific guru mengajar ID |

**Example Request:**
```bash
# Get all
GET http://localhost:8000/api/guru-pengganti

# Get only aktif
GET http://localhost:8000/api/guru-pengganti?status=aktif

# Get by guru_mengajar_id
GET http://localhost:8000/api/guru-pengganti?guru_mengajar_id=5
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Data guru pengganti berhasil diambil",
  "data": [
    {
      "id": 1,
      "guru_mengajar_id": 15,
      "guru_asli": {
        "id": 3,
        "kode_guru": "G003",
        "nama_guru": "Guru Informatika 3"
      },
      "guru_pengganti": {
        "id": 7,
        "kode_guru": "G007",
        "nama_guru": "Guru Bahasa Inggris 1"
      },
      "kelas": "X AK 1",
      "mapel": "Informatika",
      "hari": "Senin",
      "jam_ke": "3",
      "tanggal_mulai": "2025-11-22",
      "tanggal_selesai": "2025-11-28",
      "durasi_hari": 7,
      "alasan": "Sakit",
      "keterangan": "Demam tinggi, perlu istirahat",
      "status": "aktif",
      "created_at": "2025-11-23T09:25:32.000000Z"
    }
  ]
}
```

---

### 2. **Get Guru Pengganti Aktif Only**
Mengambil semua guru pengganti dengan status aktif.

**Endpoint:**
```
GET /guru-pengganti/aktif/list
```

**Example Request:**
```bash
GET http://localhost:8000/api/guru-pengganti/aktif/list
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Data guru pengganti aktif berhasil diambil",
  "data": [
    {
      "id": 1,
      "guru_asli": {
        "id": 3,
        "nama_guru": "Guru Informatika 3"
      },
      "guru_pengganti": {
        "id": 7,
        "nama_guru": "Guru Bahasa Inggris 1"
      },
      "kelas": "X AK 1",
      "mapel": "Informatika",
      "hari": "Senin",
      "jam_ke": "3",
      "tanggal_mulai": "2025-11-22",
      "tanggal_selesai": "2025-11-28",
      "durasi_hari": 7,
      "alasan": "Sakit",
      "keterangan": "Demam tinggi, perlu istirahat",
      "status": "aktif"
    }
  ]
}
```

---

### 3. **Get Guru Pengganti by Guru Mengajar ID**
Mengambil guru pengganti berdasarkan guru_mengajar_id tertentu.

**Endpoint:**
```
GET /guru-pengganti/guru-mengajar/{guruMengajarId}
```

**Example Request:**
```bash
GET http://localhost:8000/api/guru-pengganti/guru-mengajar/15
```

**Response Success (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "guru_asli": {
        "id": 3,
        "nama_guru": "Guru Informatika 3"
      },
      "guru_pengganti": {
        "id": 7,
        "nama_guru": "Guru Bahasa Inggris 1"
      },
      "tanggal_mulai": "2025-11-22",
      "tanggal_selesai": "2025-11-28",
      "durasi_hari": 7,
      "alasan": "Sakit",
      "keterangan": "Demam tinggi, perlu istirahat",
      "status": "aktif"
    }
  ]
}
```

---

### 4. **Create Guru Pengganti**
Membuat data guru pengganti baru.

**Endpoint:**
```
POST /guru-pengganti
```

**Request Body:**
```json
{
  "guru_mengajar_id": 15,
  "guru_pengganti_id": 7,
  "tanggal_mulai": "2025-11-25",
  "tanggal_selesai": "2025-11-30",
  "alasan": "Sakit",
  "keterangan": "Demam tinggi, perlu istirahat total",
  "status": "aktif"
}
```

**Field Validations:**
| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `guru_mengajar_id` | integer | Yes | Must exist in guru_mengajars table |
| `guru_pengganti_id` | integer | Yes | Must exist in gurus table, tidak boleh sama dengan guru asli |
| `tanggal_mulai` | date | Yes | Format: YYYY-MM-DD |
| `tanggal_selesai` | date | Yes | Format: YYYY-MM-DD, must be >= tanggal_mulai |
| `alasan` | string | Yes | Max 255 characters |
| `keterangan` | string | No | Any text |
| `status` | string | Yes | Must be: `aktif` or `selesai` |

**Response Success (201):**
```json
{
  "success": true,
  "message": "Guru pengganti berhasil ditambahkan",
  "data": {
    "id": 55,
    "guru_asli": {
      "id": 3,
      "nama_guru": "Guru Informatika 3"
    },
    "guru_pengganti": {
      "id": 7,
      "nama_guru": "Guru Bahasa Inggris 1"
    },
    "kelas": "X AK 1",
    "mapel": "Informatika",
    "tanggal_mulai": "2025-11-25",
    "tanggal_selesai": "2025-11-30",
    "durasi_hari": 6,
    "alasan": "Sakit",
    "keterangan": "Demam tinggi, perlu istirahat total",
    "status": "aktif"
  }
}
```

**Response Error (422) - Guru Sama:**
```json
{
  "success": false,
  "message": "Guru pengganti tidak boleh sama dengan guru asli"
}
```

---

### 5. **Get Detail Guru Pengganti**
Mengambil detail guru pengganti berdasarkan ID.

**Endpoint:**
```
GET /guru-pengganti/{id}
```

**Example Request:**
```bash
GET http://localhost:8000/api/guru-pengganti/1
```

**Response Success (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "guru_asli": {
      "id": 3,
      "kode_guru": "G003",
      "nama_guru": "Guru Informatika 3"
    },
    "guru_pengganti": {
      "id": 7,
      "kode_guru": "G007",
      "nama_guru": "Guru Bahasa Inggris 1"
    },
    "kelas": "X AK 1",
    "mapel": "Informatika",
    "hari": "Senin",
    "jam_ke": "3",
    "tanggal_mulai": "2025-11-22",
    "tanggal_selesai": "2025-11-28",
    "durasi_hari": 7,
    "alasan": "Sakit",
    "keterangan": "Demam tinggi, perlu istirahat",
    "status": "aktif",
    "created_at": "2025-11-23T09:25:32.000000Z",
    "updated_at": "2025-11-23T09:25:32.000000Z"
  }
}
```

**Response Error (404):**
```json
{
  "success": false,
  "message": "Guru pengganti tidak ditemukan"
}
```

---

### 6. **Update Guru Pengganti**
Mengupdate data guru pengganti.

**Endpoint:**
```
PUT /guru-pengganti/{id}
```

**Request Body (All fields optional):**
```json
{
  "guru_pengganti_id": 8,
  "tanggal_mulai": "2025-11-26",
  "tanggal_selesai": "2025-12-01",
  "alasan": "Cuti",
  "keterangan": "Cuti tahunan",
  "status": "selesai"
}
```

**Example Request:**
```bash
PUT http://localhost:8000/api/guru-pengganti/1
Content-Type: application/json

{
  "status": "selesai"
}
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Guru pengganti berhasil diupdate",
  "data": {
    "id": 1,
    "guru_asli": {
      "id": 3,
      "nama_guru": "Guru Informatika 3"
    },
    "guru_pengganti": {
      "id": 8,
      "nama_guru": "Guru Bahasa Inggris 2"
    },
    "kelas": "X AK 1",
    "mapel": "Informatika",
    "tanggal_mulai": "2025-11-26",
    "tanggal_selesai": "2025-12-01",
    "durasi_hari": 6,
    "alasan": "Cuti",
    "keterangan": "Cuti tahunan",
    "status": "selesai"
  }
}
```

**Response Error (404):**
```json
{
  "success": false,
  "message": "Guru pengganti tidak ditemukan"
}
```

---

### 7. **Delete Guru Pengganti**
Menghapus data guru pengganti.

**Endpoint:**
```
DELETE /guru-pengganti/{id}
```

**Example Request:**
```bash
DELETE http://localhost:8000/api/guru-pengganti/1
```

**Response Success (200):**
```json
{
  "success": true,
  "message": "Guru pengganti berhasil dihapus"
}
```

**Response Error (404):**
```json
{
  "success": false,
  "message": "Guru pengganti tidak ditemukan"
}
```

---

## 🔗 Integration dengan GuruMengajar

### Workflow Lengkap
1. **Guru tidak masuk** → Buat record di `guru_mengajars` dengan `status = tidak_masuk`
2. **Assign guru pengganti** → Buat record di `guru_pengganti` dengan `guru_mengajar_id`
3. **Tracking durasi** → Set `tanggal_mulai` dan `tanggal_selesai`
4. **Selesai penggantian** → Update `status = selesai`

### Contoh Flow:
```bash
# 1. Get guru yang tidak masuk hari ini
POST http://localhost:8000/api/guru-mengajar/tidak-masuk
{
  "hari": "Senin",
  "kelas_id": 1
}

# Response akan return guru_mengajar_id yang tidak masuk

# 2. Assign guru pengganti
POST http://localhost:8000/api/guru-pengganti
{
  "guru_mengajar_id": 15,  # dari response step 1
  "guru_pengganti_id": 7,
  "tanggal_mulai": "2025-11-25",
  "tanggal_selesai": "2025-11-30",
  "alasan": "Sakit",
  "keterangan": "Demam tinggi",
  "status": "aktif"
}

# 3. Setelah selesai, update status
PUT http://localhost:8000/api/guru-pengganti/55
{
  "status": "selesai"
}
```

---

## 📊 Database Schema

### Table: `guru_pengganti`
```sql
CREATE TABLE `guru_pengganti` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `guru_mengajar_id` bigint unsigned NOT NULL,
  `guru_asli_id` bigint unsigned NOT NULL,
  `guru_pengganti_id` bigint unsigned NOT NULL,
  `tanggal_mulai` date NOT NULL,
  `tanggal_selesai` date NOT NULL,
  `alasan` varchar(255) NOT NULL,
  `keterangan` text,
  `status` enum('aktif','selesai') DEFAULT 'aktif',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`guru_mengajar_id`) REFERENCES `guru_mengajars` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`guru_asli_id`) REFERENCES `gurus` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`guru_pengganti_id`) REFERENCES `gurus` (`id`) ON DELETE CASCADE
);
```

### Relationships:
- `guru_pengganti` **belongs to** `guru_mengajars`
- `guru_pengganti` **belongs to** `gurus` (as guru_asli)
- `guru_pengganti` **belongs to** `gurus` (as guru_pengganti)

---

## ✅ Testing Guide

### Prerequisites:
```bash
# Run migration and seeder
php artisan migrate:fresh --seed
```

### Test Scenarios:

#### 1. Get All Guru Pengganti
```bash
curl -X GET http://localhost:8000/api/guru-pengganti
```

#### 2. Get Only Aktif
```bash
curl -X GET "http://localhost:8000/api/guru-pengganti?status=aktif"
```

#### 3. Create New Guru Pengganti
```bash
curl -X POST http://localhost:8000/api/guru-pengganti \
  -H "Content-Type: application/json" \
  -d '{
    "guru_mengajar_id": 15,
    "guru_pengganti_id": 7,
    "tanggal_mulai": "2025-11-25",
    "tanggal_selesai": "2025-11-30",
    "alasan": "Sakit",
    "keterangan": "Test create",
    "status": "aktif"
  }'
```

#### 4. Update Status to Selesai
```bash
curl -X PUT http://localhost:8000/api/guru-pengganti/1 \
  -H "Content-Type: application/json" \
  -d '{
    "status": "selesai"
  }'
```

#### 5. Delete Guru Pengganti
```bash
curl -X DELETE http://localhost:8000/api/guru-pengganti/1
```

---

## 📱 Mobile App Integration

### RecyclerView Adapter Example (Kotlin)
```kotlin
data class GuruPenggantiItem(
    val id: Int,
    val guruAsli: String,
    val guruPengganti: String,
    val kelas: String,
    val mapel: String,
    val tanggalMulai: String,
    val tanggalSelesai: String,
    val durasiHari: Int,
    val alasan: String,
    val status: String
)

// Usage
val response = api.getGuruPenggantiAktif()
recyclerView.adapter = GuruPenggantiAdapter(response.data)
```

---

## 🎯 Key Features

1. **Durasi Otomatis**: Field `durasi_hari` dihitung otomatis dari selisih tanggal
2. **Validasi Guru**: Guru pengganti tidak boleh sama dengan guru asli
3. **Status Tracking**: Aktif (sedang menggantikan) atau Selesai (sudah selesai)
4. **Full Relations**: Mengambil data lengkap guru asli, pengganti, kelas, mapel
5. **Flexible Filtering**: Filter by status atau guru_mengajar_id

---

## 🔒 Notes

- Endpoint ini **PUBLIC** (tidak butuh authentication)
- Untuk production, tambahkan middleware `auth:sanctum`
- Data seeder menghasilkan:
  - 54 guru pengganti
  - 41 status aktif
  - 13 status selesai
  - 24 guru tidak masuk belum ada pengganti

---

## 📞 Support

Jika ada pertanyaan atau bug, silakan hubungi tim development.

**Happy Coding! 🚀**
