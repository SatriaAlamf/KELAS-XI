<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Jadwal;

class JadwalSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $this->command->info('📋 Membuat data Jadwal Pelajaran SMK RPL...');
        
        $jadwals = [];
        
        // ========== KELAS 10 RPL (kelas_id = 1) ==========
        // Senin
        $jadwals[] = ['guru_id' => 1, 'mapel_id' => 1, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '1-2', 'hari' => 'Senin'];
        $jadwals[] = ['guru_id' => 2, 'mapel_id' => 2, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '3-4', 'hari' => 'Senin'];
        $jadwals[] = ['guru_id' => 3, 'mapel_id' => 3, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '5-6', 'hari' => 'Senin'];
        $jadwals[] = ['guru_id' => 7, 'mapel_id' => 7, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '7-8', 'hari' => 'Senin'];
        
        // Selasa
        $jadwals[] = ['guru_id' => 4, 'mapel_id' => 4, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '1-2', 'hari' => 'Selasa'];
        $jadwals[] = ['guru_id' => 5, 'mapel_id' => 5, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '3-4', 'hari' => 'Selasa'];
        $jadwals[] = ['guru_id' => 9, 'mapel_id' => 9, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '5-6', 'hari' => 'Selasa'];
        $jadwals[] = ['guru_id' => 13, 'mapel_id' => 13, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '7-8', 'hari' => 'Selasa'];
        
        // Rabu
        $jadwals[] = ['guru_id' => 1, 'mapel_id' => 1, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '1-2', 'hari' => 'Rabu'];
        $jadwals[] = ['guru_id' => 8, 'mapel_id' => 8, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '3-4', 'hari' => 'Rabu'];
        $jadwals[] = ['guru_id' => 15, 'mapel_id' => 15, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '5-6', 'hari' => 'Rabu'];
        $jadwals[] = ['guru_id' => 6, 'mapel_id' => 6, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '7-8', 'hari' => 'Rabu'];
        
        // Kamis
        $jadwals[] = ['guru_id' => 7, 'mapel_id' => 7, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '1-2', 'hari' => 'Kamis'];
        $jadwals[] = ['guru_id' => 11, 'mapel_id' => 11, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '3-4', 'hari' => 'Kamis'];
        $jadwals[] = ['guru_id' => 12, 'mapel_id' => 12, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '5-6', 'hari' => 'Kamis'];
        $jadwals[] = ['guru_id' => 3, 'mapel_id' => 3, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '7-8', 'hari' => 'Kamis'];
        
        // Jumat
        $jadwals[] = ['guru_id' => 2, 'mapel_id' => 2, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '1-2', 'hari' => 'Jumat'];
        $jadwals[] = ['guru_id' => 9, 'mapel_id' => 9, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '3-4', 'hari' => 'Jumat'];
        $jadwals[] = ['guru_id' => 13, 'mapel_id' => 13, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '5-6', 'hari' => 'Jumat'];
        
        // Sabtu
        $jadwals[] = ['guru_id' => 10, 'mapel_id' => 10, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '1-2', 'hari' => 'Sabtu'];
        $jadwals[] = ['guru_id' => 14, 'mapel_id' => 14, 'tahun_ajaran_id' => 2, 'kelas_id' => 1, 'jam_ke' => '3-4', 'hari' => 'Sabtu'];
        
        
        // ========== KELAS 11 RPL (kelas_id = 2) ==========
        // Senin
        $jadwals[] = ['guru_id' => 1, 'mapel_id' => 1, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '1-2', 'hari' => 'Senin'];
        $jadwals[] = ['guru_id' => 8, 'mapel_id' => 8, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '3-4', 'hari' => 'Senin'];
        $jadwals[] = ['guru_id' => 10, 'mapel_id' => 10, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '5-6', 'hari' => 'Senin'];
        $jadwals[] = ['guru_id' => 11, 'mapel_id' => 11, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '7-8', 'hari' => 'Senin'];
        
        // Selasa
        $jadwals[] = ['guru_id' => 2, 'mapel_id' => 2, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '1-2', 'hari' => 'Selasa'];
        $jadwals[] = ['guru_id' => 3, 'mapel_id' => 3, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '3-4', 'hari' => 'Selasa'];
        $jadwals[] = ['guru_id' => 12, 'mapel_id' => 12, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '5-6', 'hari' => 'Selasa'];
        $jadwals[] = ['guru_id' => 13, 'mapel_id' => 13, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '7-8', 'hari' => 'Selasa'];
        
        // Rabu
        $jadwals[] = ['guru_id' => 7, 'mapel_id' => 7, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '1-2', 'hari' => 'Rabu'];
        $jadwals[] = ['guru_id' => 9, 'mapel_id' => 9, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '3-4', 'hari' => 'Rabu'];
        $jadwals[] = ['guru_id' => 15, 'mapel_id' => 15, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '5-6', 'hari' => 'Rabu'];
        $jadwals[] = ['guru_id' => 4, 'mapel_id' => 4, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '7-8', 'hari' => 'Rabu'];
        
        // Kamis
        $jadwals[] = ['guru_id' => 1, 'mapel_id' => 1, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '1-2', 'hari' => 'Kamis'];
        $jadwals[] = ['guru_id' => 8, 'mapel_id' => 8, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '3-4', 'hari' => 'Kamis'];
        $jadwals[] = ['guru_id' => 10, 'mapel_id' => 10, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '5-6', 'hari' => 'Kamis'];
        $jadwals[] = ['guru_id' => 14, 'mapel_id' => 14, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '7-8', 'hari' => 'Kamis'];
        
        // Jumat
        $jadwals[] = ['guru_id' => 5, 'mapel_id' => 5, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '1-2', 'hari' => 'Jumat'];
        $jadwals[] = ['guru_id' => 6, 'mapel_id' => 6, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '3-4', 'hari' => 'Jumat'];
        $jadwals[] = ['guru_id' => 11, 'mapel_id' => 11, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '5-6', 'hari' => 'Jumat'];
        
        // Sabtu
        $jadwals[] = ['guru_id' => 9, 'mapel_id' => 9, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '1-2', 'hari' => 'Sabtu'];
        $jadwals[] = ['guru_id' => 12, 'mapel_id' => 12, 'tahun_ajaran_id' => 2, 'kelas_id' => 2, 'jam_ke' => '3-4', 'hari' => 'Sabtu'];
        
        
        // ========== KELAS 12 RPL (kelas_id = 3) ==========
        // Senin
        $jadwals[] = ['guru_id' => 1, 'mapel_id' => 1, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '1-2', 'hari' => 'Senin'];
        $jadwals[] = ['guru_id' => 7, 'mapel_id' => 7, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '3-4', 'hari' => 'Senin'];
        $jadwals[] = ['guru_id' => 10, 'mapel_id' => 10, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '5-6', 'hari' => 'Senin'];
        $jadwals[] = ['guru_id' => 14, 'mapel_id' => 14, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '7-8', 'hari' => 'Senin'];
        
        // Selasa
        $jadwals[] = ['guru_id' => 8, 'mapel_id' => 8, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '1-2', 'hari' => 'Selasa'];
        $jadwals[] = ['guru_id' => 9, 'mapel_id' => 9, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '3-4', 'hari' => 'Selasa'];
        $jadwals[] = ['guru_id' => 11, 'mapel_id' => 11, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '5-6', 'hari' => 'Selasa'];
        $jadwals[] = ['guru_id' => 15, 'mapel_id' => 15, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '7-8', 'hari' => 'Selasa'];
        
        // Rabu
        $jadwals[] = ['guru_id' => 2, 'mapel_id' => 2, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '1-2', 'hari' => 'Rabu'];
        $jadwals[] = ['guru_id' => 3, 'mapel_id' => 3, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '3-4', 'hari' => 'Rabu'];
        $jadwals[] = ['guru_id' => 12, 'mapel_id' => 12, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '5-6', 'hari' => 'Rabu'];
        $jadwals[] = ['guru_id' => 13, 'mapel_id' => 13, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '7-8', 'hari' => 'Rabu'];
        
        // Kamis
        $jadwals[] = ['guru_id' => 1, 'mapel_id' => 1, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '1-2', 'hari' => 'Kamis'];
        $jadwals[] = ['guru_id' => 7, 'mapel_id' => 7, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '3-4', 'hari' => 'Kamis'];
        $jadwals[] = ['guru_id' => 10, 'mapel_id' => 10, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '5-6', 'hari' => 'Kamis'];
        $jadwals[] = ['guru_id' => 14, 'mapel_id' => 14, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '7-8', 'hari' => 'Kamis'];
        
        // Jumat
        $jadwals[] = ['guru_id' => 4, 'mapel_id' => 4, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '1-2', 'hari' => 'Jumat'];
        $jadwals[] = ['guru_id' => 5, 'mapel_id' => 5, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '3-4', 'hari' => 'Jumat'];
        $jadwals[] = ['guru_id' => 6, 'mapel_id' => 6, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '5-6', 'hari' => 'Jumat'];
        
        // Sabtu
        $jadwals[] = ['guru_id' => 8, 'mapel_id' => 8, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '1-2', 'hari' => 'Sabtu'];
        $jadwals[] = ['guru_id' => 9, 'mapel_id' => 9, 'tahun_ajaran_id' => 2, 'kelas_id' => 3, 'jam_ke' => '3-4', 'hari' => 'Sabtu'];
        
        $count = 0;
        foreach ($jadwals as $jadwal) {
            Jadwal::create($jadwal);
            $count++;
            if ($count % 10 == 0) {
                $this->command->info("   ✓ {$count} jadwal telah dibuat...");
            }
        }
        
        $this->command->info("   Total: " . count($jadwals) . " jadwal berhasil dibuat untuk kelas 10, 11, dan 12 RPL");
    }
}
