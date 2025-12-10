package com.komputerkit.aplikasimonitoringkelas.api.models

import com.google.gson.annotations.SerializedName

// Request untuk POST /api/guru-mengajar/by-hari-kelas
data class GuruMengajarByHariKelasRequest(
    @SerializedName("hari")
    val hari: String,
    
    @SerializedName("kelas_id")
    val kelasId: Int
)

// Request untuk POST /api/guru-mengajar/tidak-masuk
data class GuruMengajarRequest(
    @SerializedName("hari")
    val hari: String,
    
    @SerializedName("kelas_id")
    val kelas_id: Int
)

// Data guru mengajar untuk response
data class GuruMengajarData(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("jadwal_id")
    val jadwalId: Int,
    
    @SerializedName("nama_guru")
    val namaGuru: String,
    
    @SerializedName("mapel")
    val mapel: String,
    
    @SerializedName("jam_ke")
    val jamKe: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("keterangan")
    val keterangan: String?,
    
    @SerializedName("guru_pengganti")
    val guruPengganti: List<GuruPenggantiData>? = null
)

// Data guru pengganti
data class GuruPenggantiData(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("guru_mengajar_id")
    val guruMengajarId: Int,
    
    @SerializedName("guru_asli_id")
    val guruAsliId: Int,
    
    @SerializedName("guru_pengganti_id")
    val guruPenggantiId: Int,
    
    @SerializedName("tanggal_mulai")
    val tanggalMulai: String?,
    
    @SerializedName("tanggal_selesai")
    val tanggalSelesai: String?,
    
    @SerializedName("durasi_hari")
    val durasiHari: Int?,
    
    @SerializedName("alasan")
    val alasan: String,
    
    @SerializedName("keterangan")
    val keterangan: String?,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("guru_asli")
    val guruAsli: GuruAsliData? = null,
    
    @SerializedName("guru_pengganti")
    val guruPenggantiData: GuruPenggantiDataDetail? = null
)

// Detail guru asli
data class GuruAsliData(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("nama_guru")
    val namaGuru: String
)

// Detail guru pengganti
data class GuruPenggantiDataDetail(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("nama_guru")
    val namaGuru: String
)

// Response wrapper untuk list guru mengajar
data class GuruMengajarListResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: List<GuruMengajarData>
)

// Request untuk UPDATE guru mengajar (PUT /guru-mengajars/{id})
data class UpdateGuruMengajarRequest(
    @SerializedName("status")
    val status: String,
    
    @SerializedName("keterangan")
    val keterangan: String?
)

// Response untuk single guru mengajar operation
data class GuruMengajarResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: GuruMengajarData?
)

// Response untuk delete operation
data class DeleteResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String
)

// Request untuk POST /api/guru-pengganti (Tambah guru pengganti)
data class GuruPenggantiRequest(
    @SerializedName("guru_mengajar_id")
    val guru_mengajar_id: Int,
    
    @SerializedName("guru_pengganti_id")
    val guru_pengganti_id: Int,
    
    @SerializedName("alasan")
    val alasan: String,
    
    @SerializedName("tanggal_mulai")
    val tanggal_mulai: String? = null,
    
    @SerializedName("tanggal_selesai")
    val tanggal_selesai: String? = null,
    
    @SerializedName("keterangan")
    val keterangan: String? = null,
    
    @SerializedName("status")
    val status: String = "aktif"
)

// Response untuk guru pengganti operation
data class GuruPenggantiResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: GuruPenggantiData?
)

// =====================================================
// KEPSEK COMPREHENSIVE LIST MODELS
// =====================================================

// Request untuk GET/POST /api/guru-mengajar/kepsek/comprehensive
data class KepsekComprehensiveRequest(
    @SerializedName("hari")
    val hari: String? = null,
    
    @SerializedName("kelas_id")
    val kelasId: Int? = null,
    
    @SerializedName("status_mengajar")
    val statusMengajar: String? = null, // masuk, tidak_masuk
    
    @SerializedName("status_pengganti")
    val statusPengganti: String? = null, // aktif, selesai, semua, tanpa_pengganti
    
    @SerializedName("durasi_min")
    val durasiMin: Int? = null,
    
    @SerializedName("durasi_max")
    val durasiMax: Int? = null,
    
    @SerializedName("tanggal_from")
    val tanggalFrom: String? = null, // YYYY-MM-DD
    
    @SerializedName("tanggal_to")
    val tanggalTo: String? = null, // YYYY-MM-DD
    
    @SerializedName("has_pengganti")
    val hasPengganti: String? = null // true, false, all
)

// Enhanced Guru Mengajar Data untuk Kepsek
data class GuruMengajarKepsekData(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("jadwal_id")
    val jadwalId: Int,
    
    @SerializedName("nama_guru")
    val namaGuru: String,
    
    @SerializedName("kode_guru")
    val kodeGuru: String,
    
    @SerializedName("mapel")
    val mapel: String,
    
    @SerializedName("kelas")
    val kelas: String,
    
    @SerializedName("jurusan")
    val jurusan: String?,
    
    @SerializedName("tingkat")
    val tingkat: Int?,
    
    @SerializedName("hari")
    val hari: String,
    
    @SerializedName("jam_ke")
    val jamKe: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("keterangan")
    val keterangan: String?,
    
    @SerializedName("has_active_pengganti")
    val hasActivePengganti: Boolean,
    
    @SerializedName("jumlah_pengganti")
    val jumlahPengganti: Int,
    
    @SerializedName("guru_pengganti")
    val guruPengganti: List<GuruPenggantiData>
)

// Summary data untuk statistik
data class KepsekSummary(
    @SerializedName("total_guru_mengajar")
    val totalGuruMengajar: Int,
    
    @SerializedName("total_masuk")
    val totalMasuk: Int,
    
    @SerializedName("total_tidak_masuk")
    val totalTidakMasuk: Int,
    
    @SerializedName("total_dengan_pengganti")
    val totalDenganPengganti: Int,
    
    @SerializedName("total_tanpa_pengganti")
    val totalTanpaPengganti: Int,
    
    @SerializedName("total_pengganti_aktif")
    val totalPenggantiAktif: Int
)

// Response wrapper untuk kepsek comprehensive list
data class KepsekComprehensiveResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("summary")
    val summary: KepsekSummary,
    
    @SerializedName("data")
    val data: List<GuruMengajarKepsekData>
)
