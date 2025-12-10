<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Mapel;

class MapelSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $this->command->info('📚 Membuat data Mata Pelajaran SMK...');
        
        $mapels = [
            // ===== MATA PELAJARAN UMUM (SEMUA JURUSAN) =====
            ['kode_mapel' => 'MTK', 'nama_mapel' => 'Matematika'],
            ['kode_mapel' => 'BIN', 'nama_mapel' => 'Bahasa Indonesia'],
            ['kode_mapel' => 'BING', 'nama_mapel' => 'Bahasa Inggris'],
            ['kode_mapel' => 'PAI', 'nama_mapel' => 'Pendidikan Agama Islam'],
            ['kode_mapel' => 'PJOK', 'nama_mapel' => 'Pendidikan Jasmani Olahraga & Kesehatan'],
            ['kode_mapel' => 'PKN', 'nama_mapel' => 'Pendidikan Kewarganegaraan'],
            ['kode_mapel' => 'SEJARAH', 'nama_mapel' => 'Sejarah Indonesia'],
            ['kode_mapel' => 'SBK', 'nama_mapel' => 'Seni Budaya'],
            ['kode_mapel' => 'KIMIA', 'nama_mapel' => 'Kimia'],
            ['kode_mapel' => 'FISIKA', 'nama_mapel' => 'Fisika'],
            
            // ===== MATA PELAJARAN REKAYASA PERANGKAT LUNAK (RPL) =====
            ['kode_mapel' => 'PWB', 'nama_mapel' => 'Pemrograman Web'],
            ['kode_mapel' => 'PMB', 'nama_mapel' => 'Pemrograman Mobile'],
            ['kode_mapel' => 'BDT', 'nama_mapel' => 'Basis Data'],
            ['kode_mapel' => 'RPL', 'nama_mapel' => 'Rekayasa Perangkat Lunak'],
            ['kode_mapel' => 'JKM', 'nama_mapel' => 'Jaringan Komputer'],
            ['kode_mapel' => 'SOP', 'nama_mapel' => 'Sistem Operasi'],
            ['kode_mapel' => 'PPL', 'nama_mapel' => 'Pemrograman Berorientasi Objek'],
            ['kode_mapel' => 'ALGO', 'nama_mapel' => 'Algoritma dan Struktur Data'],
            
            // ===== MATA PELAJARAN AKUNTANSI (AK) =====
            ['kode_mapel' => 'AKD', 'nama_mapel' => 'Akuntansi Dasar'],
            ['kode_mapel' => 'AKK', 'nama_mapel' => 'Akuntansi Keuangan'],
            ['kode_mapel' => 'AKP', 'nama_mapel' => 'Akuntansi Perusahaan'],
            ['kode_mapel' => 'PERPAJAKAN', 'nama_mapel' => 'Perpajakan'],
            ['kode_mapel' => 'AUDIT', 'nama_mapel' => 'Auditing'],
            ['kode_mapel' => 'SIA', 'nama_mapel' => 'Sistem Informasi Akuntansi'],
            ['kode_mapel' => 'MYOB', 'nama_mapel' => 'Aplikasi Pengolah Angka/Spreadsheet'],
            ['kode_mapel' => 'EKON', 'nama_mapel' => 'Ekonomi Bisnis'],
            ['kode_mapel' => 'ADMINISTRASI', 'nama_mapel' => 'Administrasi Umum'],
            
            // ===== MATA PELAJARAN DESAIN KOMUNIKASI VISUAL (DKV) =====
            ['kode_mapel' => 'DGR', 'nama_mapel' => 'Desain Grafis'],
            ['kode_mapel' => 'DKV_DASAR', 'nama_mapel' => 'Dasar-Dasar DKV'],
            ['kode_mapel' => 'TIPOGRAFI', 'nama_mapel' => 'Tipografi'],
            ['kode_mapel' => 'ILUSTRASI', 'nama_mapel' => 'Ilustrasi'],
            ['kode_mapel' => 'FOTOGRAFI', 'nama_mapel' => 'Fotografi'],
            ['kode_mapel' => 'VIDEO_EDITING', 'nama_mapel' => 'Video Editing'],
            ['kode_mapel' => 'ANIMASI_2D', 'nama_mapel' => 'Animasi 2D'],
            ['kode_mapel' => 'ANIMASI_3D', 'nama_mapel' => 'Animasi 3D'],
            ['kode_mapel' => 'UI_UX', 'nama_mapel' => 'UI/UX Design'],
            ['kode_mapel' => 'BRANDING', 'nama_mapel' => 'Branding dan Identitas Visual'],
            
            // ===== MATA PELAJARAN MANAJEMEN PERKANTORAN (MP) =====
            ['kode_mapel' => 'KORESPONDENSI', 'nama_mapel' => 'Korespondensi'],
            ['kode_mapel' => 'KEARSIPAN', 'nama_mapel' => 'Kearsipan'],
            ['kode_mapel' => 'OTOMATISASI', 'nama_mapel' => 'Otomatisasi Tata Kelola Perkantoran'],
            ['kode_mapel' => 'TEKNOLOGI_PERKANTORAN', 'nama_mapel' => 'Teknologi Perkantoran'],
            ['kode_mapel' => 'PSIKOLOGI_KOMUNIKASI', 'nama_mapel' => 'Psikologi Komunikasi'],
            ['kode_mapel' => 'PRESENTASI', 'nama_mapel' => 'Teknologi Presentasi'],
            ['kode_mapel' => 'HUMAS', 'nama_mapel' => 'Hubungan Masyarakat'],
            ['kode_mapel' => 'MANAJEMEN_SDM', 'nama_mapel' => 'Manajemen Sumber Daya Manusia'],
            ['kode_mapel' => 'ETIKA_PROFESI', 'nama_mapel' => 'Etika Profesi'],
            
            // ===== MATA PELAJARAN LAYANAN PERBANKAN (LPB) =====
            ['kode_mapel' => 'BANK_DASAR', 'nama_mapel' => 'Dasar-Dasar Perbankan'],
            ['kode_mapel' => 'AKUNTANSI_BANK', 'nama_mapel' => 'Akuntansi Perbankan'],
            ['kode_mapel' => 'PELAYANAN_NASABAH', 'nama_mapel' => 'Pelayanan Prima'],
            ['kode_mapel' => 'PRODUK_BANK', 'nama_mapel' => 'Produk dan Jasa Bank'],
            ['kode_mapel' => 'KREDIT_BANK', 'nama_mapel' => 'Perkreditan'],
            ['kode_mapel' => 'MANAJEMEN_RISIKO', 'nama_mapel' => 'Manajemen Risiko'],
            ['kode_mapel' => 'PASAR_MODAL', 'nama_mapel' => 'Pasar Modal'],
            ['kode_mapel' => 'ASURANSI', 'nama_mapel' => 'Asuransi'],
            ['kode_mapel' => 'FINTECH', 'nama_mapel' => 'Financial Technology'],
            
            // ===== MATA PELAJARAN BISNIS DIGITAL (BD) =====
            ['kode_mapel' => 'E_COMMERCE', 'nama_mapel' => 'E-Commerce'],
            ['kode_mapel' => 'DIGITAL_MARKETING', 'nama_mapel' => 'Digital Marketing'],
            ['kode_mapel' => 'MARKETPLACE', 'nama_mapel' => 'Marketplace Management'],
            ['kode_mapel' => 'SOSMED_MARKETING', 'nama_mapel' => 'Social Media Marketing'],
            ['kode_mapel' => 'SEO_SEM', 'nama_mapel' => 'SEO dan SEM'],
            ['kode_mapel' => 'CONTENT_MARKETING', 'nama_mapel' => 'Content Marketing'],
            ['kode_mapel' => 'COPYWRITING', 'nama_mapel' => 'Copywriting'],
            ['kode_mapel' => 'AFFILIATE_MARKETING', 'nama_mapel' => 'Affiliate Marketing'],
            ['kode_mapel' => 'ANALYTICS', 'nama_mapel' => 'Web Analytics'],
            ['kode_mapel' => 'DROPSHIPPING', 'nama_mapel' => 'Dropshipping'],
            
            // ===== MATA PELAJARAN UMUM KEJURUAN =====
            ['kode_mapel' => 'PKK', 'nama_mapel' => 'Produk Kreatif dan Kewirausahaan'],
            ['kode_mapel' => 'PKL', 'nama_mapel' => 'Praktik Kerja Lapangan'],
            ['kode_mapel' => 'SIMULASI_DIGITAL', 'nama_mapel' => 'Simulasi dan Komunikasi Digital'],
        ];

        foreach ($mapels as $mapel) {
            Mapel::create($mapel);
            $this->command->info("   ✓ {$mapel['kode_mapel']} - {$mapel['nama_mapel']}");
        }
        
        $this->command->info("   Total: " . count($mapels) . " mata pelajaran berhasil dibuat");
    }
}
