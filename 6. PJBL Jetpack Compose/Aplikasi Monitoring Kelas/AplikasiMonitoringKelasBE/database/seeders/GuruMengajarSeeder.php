<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\GuruMengajar;
use App\Models\Jadwal;
use Carbon\Carbon;

class GuruMengajarSeeder extends Seeder
{
    /**
     * Run the database seeds.
     * Membuat data kehadiran guru untuk berbagai jadwal
     */
    public function run(): void
    {
        $this->command->info('🎓 Membuat data Guru Mengajar (Kehadiran) SMK RPL...');
        $this->command->info('');
        
        // Hapus data lama
        GuruMengajar::truncate();
        
        // Get all jadwals
        $jadwals = Jadwal::with(['guru', 'kelas', 'mapel'])->get();
        
        if ($jadwals->isEmpty()) {
            $this->command->error('❌ Tidak ada data jadwal. Jalankan JadwalSeeder terlebih dahulu.');
            return;
        }
        
        $totalMasuk = 0;
        $totalTidakMasuk = 0;
        $totalKosong = 0;
        
        // Buat data kehadiran untuk semua jadwal dengan skenario yang bervariasi
        foreach ($jadwals as $jadwal) {
            // Random status dengan probabilitas: 70% masuk, 20% tidak_masuk, 10% kelas_kosong
            $random = rand(1, 10);
            
            if ($random <= 7) {
                // 70% Guru Masuk
                $status = 'masuk';
                $keterangan = $this->getKeteranganMasuk($jadwal->mapel->nama_mapel);
                $totalMasuk++;
            } elseif ($random <= 9) {
                // 20% Guru Tidak Masuk
                $status = 'tidak_masuk';
                $keterangan = $this->getKeteranganTidakMasuk();
                $totalTidakMasuk++;
            } else {
                // 10% Kelas Kosong
                $status = 'kelas_kosong';
                $keterangan = $this->getKeteranganKelasKosong();
                $totalKosong++;
            }
            
            GuruMengajar::create([
                'jadwal_id' => $jadwal->id,
                'keterangan' => $keterangan,
                'status' => $status,
                'created_at' => Carbon::now()->subDays(rand(0, 7)),
                'updated_at' => Carbon::now(),
            ]);
            
            // Display per kelas
            $statusIcon = $status == 'masuk' ? '✓' : ($status == 'tidak_masuk' ? '✗' : '○');
            $statusColor = $status == 'masuk' ? 'green' : ($status == 'tidak_masuk' ? 'red' : 'yellow');
            
            $this->command->info(
                "   {$statusIcon} {$jadwal->kelas->nama_kelas} - {$jadwal->mapel->nama_mapel} " .
                "({$jadwal->hari}, {$jadwal->jam_mulai}-{$jadwal->jam_selesai}) - " .
                strtoupper($status)
            );
        }
        
        $this->command->info('');
        $this->command->info('📊 === STATISTIK KEHADIRAN ===');
        $this->command->info("   ✓ Guru Masuk      : {$totalMasuk} jadwal");
        $this->command->info("   ✗ Guru Tidak Masuk: {$totalTidakMasuk} jadwal");
        $this->command->info("   ○ Kelas Kosong    : {$totalKosong} jadwal");
        $this->command->info("   📚 Total Jadwal   : " . ($totalMasuk + $totalTidakMasuk + $totalKosong));
        $this->command->info('');
        $this->command->info('✅ Seeder GuruMengajar selesai!');
        $this->command->info('💡 Tip: Data "tidak_masuk" siap diisi dengan guru pengganti');
    }
    
    /**
     * Generate keterangan untuk guru masuk
     */
    private function getKeteranganMasuk($namaMapel): string
    {
        $keteranganList = [
            // Keterangan umum
            'Pembelajaran normal',
            'Melanjutkan materi sebelumnya',
            'Review materi',
            
            // Keterangan spesifik programming
            'Materi OOP - Class dan Object',
            'Praktik Laravel - CRUD Operations',
            'Materi Database - SQL Queries',
            'Praktik Git & GitHub',
            'Materi Framework - MVC Pattern',
            'Ulangan Harian',
            'Diskusi Project Akhir',
            
            // Keterangan umum mata pelajaran
            'Materi baru: ' . $namaMapel,
            'Praktikum ' . $namaMapel,
            'Latihan soal ' . $namaMapel,
            'Presentasi kelompok',
            'Quiz ' . $namaMapel,
        ];
        
        return $keteranganList[array_rand($keteranganList)];
    }
    
    /**
     * Generate keterangan untuk guru tidak masuk
     */
    private function getKeteranganTidakMasuk(): string
    {
        $keteranganList = [
            'Guru sakit',
            'Guru izin keperluan keluarga',
            'Guru menghadiri workshop',
            'Guru dinas luar',
            'Guru menghadiri seminar',
            'Guru cuti',
            'Guru izin mendadak',
            'Guru rapat dinas',
            'Guru mengikuti pelatihan',
            'Guru tidak ada kabar',
        ];
        
        return $keteranganList[array_rand($keteranganList)];
    }
    
    /**
     * Generate keterangan untuk kelas kosong
     */
    private function getKeteranganKelasKosong(): string
    {
        $keteranganList = [
            'Siswa mengikuti kegiatan ekstrakurikuler',
            'Siswa mengikuti upacara',
            'Kelas dibatalkan - ada acara sekolah',
            'Libur nasional',
            'Siswa study tour',
            'Kelas digabung dengan kelas lain',
            'Siswa mengikuti lomba',
            'Kelas dibatalkan sementara',
        ];
        
        return $keteranganList[array_rand($keteranganList)];
    }
}
