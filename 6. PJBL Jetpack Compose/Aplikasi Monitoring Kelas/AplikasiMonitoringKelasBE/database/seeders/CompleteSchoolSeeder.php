<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use App\Models\User;
use App\Models\Guru;
use App\Models\Mapel;
use App\Models\Kelas;
use App\Models\TahunAjaran;
use App\Models\Jadwal;
use App\Models\GuruMengajar;
use App\Models\GuruPengganti;
use Carbon\Carbon;

class CompleteSchoolSeeder extends Seeder
{
    /**
     * Run the database seeders.
     * 
     * Struktur Lengkap:
     * - 3 User Admin (admin, kurikulum, kepala_sekolah)
     * - Kelas: X, XI, XII untuk jurusan AK(3), MP(3), BD(2), DKV(3), LPB(1), RPL(1)
     * - 1 Siswa pengurus per kelas
     * - Mapel lengkap untuk semua jurusan
     * - Guru untuk semua mapel
     * - 1 Tahun Ajaran aktif
     * - Jadwal terstruktur per kelas, hari, jam
     */
    public function run(): void
    {
        $this->command->info('');
        $this->command->info('🏫 ================================================');
        $this->command->info('   COMPLETE SCHOOL DATABASE SEEDER');
        $this->command->info('   Sistem Monitoring Kehadiran Guru SMK');
        $this->command->info('================================================');
        $this->command->info('');
        
        // Clear existing data
        $this->command->info('🗑️  Membersihkan database lama...');
        DB::statement('SET FOREIGN_KEY_CHECKS=0;');
        GuruPengganti::truncate();
        GuruMengajar::truncate();
        Jadwal::truncate();
        TahunAjaran::truncate();
        Mapel::truncate();
        Guru::truncate();
        Kelas::truncate();
        User::whereIn('role', ['admin', 'kurikulum', 'kepala_sekolah', 'siswa'])->delete();
        DB::statement('SET FOREIGN_KEY_CHECKS=1;');
        $this->command->info('   ✓ Database berhasil dibersihkan');
        $this->command->info('');

        // 1. Create Admin Users
        $this->createAdminUsers();
        
        // 2. Create Kelas
        $kelasData = $this->createKelas();
        
        // 3. Create Siswa (Pengurus Kelas)
        $this->createSiswa($kelasData);
        
        // 4. Create Mapel
        $mapelData = $this->createMapel();
        
        // 5. Create Guru
        $guruData = $this->createGuru($mapelData);
        
        // 6. Create Tahun Ajaran
        $tahunAjaran = $this->createTahunAjaran();
        
        // 7. Create Jadwal
        $jadwalData = $this->createJadwal($kelasData, $mapelData, $guruData, $tahunAjaran);

        $this->command->info('');
        $this->command->info('🎉 ================================================');
        $this->command->info('   SEEDER BERHASIL DIJALANKAN!');
        $this->command->info('================================================');
        $this->command->info('');
    }

    /**
     * Create 3 Admin Users: admin, kurikulum, kepala_sekolah
     */
    private function createAdminUsers()
    {
        $admins = [
            ['name' => 'Administrator', 'email' => 'admin@sekolah.sch.id', 'role' => 'admin'],
            ['name' => 'Kurikulum', 'email' => 'kurikulum@sekolah.sch.id', 'role' => 'kurikulum'],
            ['name' => 'Kepala Sekolah', 'email' => 'kepsek@sekolah.sch.id', 'role' => 'kepala_sekolah'],
        ];

        foreach ($admins as $admin) {
            User::create([
                'name' => $admin['name'],
                'email' => $admin['email'],
                'password' => Hash::make('password'),
                'role' => $admin['role'],
            ]);
        }

        $this->command->info('✅ Created 3 Admin Users');
    }

    /**
     * Create Kelas: X, XI, XII untuk AK(3), MP(3), BD(2), DKV(3), LPB(1), RPL(1)
     * Total: 39 kelas
     */
    private function createKelas()
    {
        $tingkat = ['X', 'XI', 'XII'];
        $jurusan = [
            'AK' => 3,   // Akuntansi - 3 kelas per tingkat
            'MP' => 3,   // Manajemen Perkantoran - 3 kelas
            'BD' => 2,   // Bisnis Daring - 2 kelas
            'DKV' => 3,  // Desain Komunikasi Visual - 3 kelas
            'LPB' => 1,  // Layanan Perbankan - 1 kelas
            'RPL' => 1,  // Rekayasa Perangkat Lunak - 1 kelas
        ];

        $kelasData = [];

        foreach ($tingkat as $t) {
            foreach ($jurusan as $j => $jumlah) {
                for ($i = 1; $i <= $jumlah; $i++) {
                    $namaKelas = "$t $j $i";
                    $kelas = Kelas::create(['nama_kelas' => $namaKelas]);
                    $kelasData[] = $kelas;
                }
            }
        }

        $this->command->info('✅ Created ' . count($kelasData) . ' Kelas');
        return $kelasData;
    }

    /**
     * Create 1 Siswa (Pengurus Kelas) per kelas
     */
    private function createSiswa($kelasData)
    {
        foreach ($kelasData as $index => $kelas) {
            $cleanName = str_replace(' ', '', $kelas->nama_kelas); // XIAk1 -> XIAK1
            
            User::create([
                'name' => 'Pengurus ' . $kelas->nama_kelas,
                'email' => strtolower($cleanName) . '@student.sch.id',
                'password' => Hash::make('password'),
                'role' => 'siswa',
                'kelas_id' => $kelas->id,
            ]);
        }

        $this->command->info('✅ Created ' . count($kelasData) . ' Siswa (Pengurus Kelas)');
    }

    /**
     * Create 6 Mapel Pokok
     */
    private function createMapel()
    {
        $mapels = [
            ['kode' => 'BIND', 'nama' => 'Bahasa Indonesia'],
            ['kode' => 'MTK', 'nama' => 'Matematika'],
            ['kode' => 'INF', 'nama' => 'Informatika'],
            ['kode' => 'BING', 'nama' => 'Bahasa Inggris'],
            ['kode' => 'BDAE', 'nama' => 'Bahasa Daerah'],
            ['kode' => 'PPKN', 'nama' => 'Pendidikan Pancasila dan Kewarganegaraan'],
        ];

        $mapelData = [];

        foreach ($mapels as $m) {
            $mapel = Mapel::create([
                'kode_mapel' => $m['kode'],
                'nama_mapel' => $m['nama'],
            ]);
            $mapelData[] = $mapel;
        }

        $this->command->info('✅ Created ' . count($mapelData) . ' Mapel');
        return $mapelData;
    }

    /**
     * Create 3 Guru per Mapel (18 guru total)
     */
    private function createGuru($mapelData)
    {
        $guruData = [];
        $counter = 1;

        foreach ($mapelData as $mapel) {
            for ($i = 1; $i <= 3; $i++) {
                $kodeGuru = sprintf('G%03d', $counter);
                
                $guru = Guru::create([
                    'kode_guru' => $kodeGuru,
                    'nama_guru' => "Guru {$mapel->nama_mapel} {$i}",
                    'telepon' => '08' . str_pad($counter, 10, '0', STR_PAD_LEFT),
                ]);
                
                $guruData[] = [
                    'guru' => $guru,
                    'mapel' => $mapel,
                ];
                
                $counter++;
            }
        }

        $this->command->info('✅ Created 18 Guru (3 per Mapel)');
        return $guruData;
    }

    /**
     * Create Tahun Ajaran
     */
    private function createTahunAjaran()
    {
        $tahunAjaran = TahunAjaran::create([
            'tahun' => '2024/2025',
            'flag' => 1, // Aktif
        ]);

        $this->command->info('✅ Created Tahun Ajaran 2024/2025');
        return $tahunAjaran;
    }

    /**
     * Create Jadwal terstruktur
     * - Setiap kelas mendapat 6 mapel
     * - Distribusi merata per hari (Senin-Sabtu)
     * - Jam ke 1-8
     */
    private function createJadwal($kelasData, $mapelData, $guruData, $tahunAjaran)
    {
        $hari = ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'];
        $jadwalData = [];
        $jadwalCounter = 0;

        foreach ($kelasData as $kelas) {
            $hariIndex = 0;
            $jamKe = 1;

            // Setiap kelas dapat semua mapel
            foreach ($mapelData as $mapel) {
                // Cari guru yang mengajar mapel ini (ambil salah satu dari 3 guru)
                $guruForMapel = array_filter($guruData, function($item) use ($mapel) {
                    return $item['mapel']->id === $mapel->id;
                });
                
                // Ambil guru secara round-robin
                $guruIndex = $jadwalCounter % 3;
                $selectedGuru = array_values($guruForMapel)[$guruIndex]['guru'];

                $jadwal = Jadwal::create([
                    'guru_id' => $selectedGuru->id,
                    'mapel_id' => $mapel->id,
                    'tahun_ajaran_id' => $tahunAjaran->id,
                    'kelas_id' => $kelas->id,
                    'hari' => $hari[$hariIndex],
                    'jam_ke' => (string)$jamKe,
                ]);

                $jadwalData[] = $jadwal;
                $jadwalCounter++;

                // Increment jam dan hari
                $jamKe++;
                if ($jamKe > 8) {
                    $jamKe = 1;
                    $hariIndex++;
                    if ($hariIndex >= count($hari)) {
                        $hariIndex = 0;
                    }
                }
            }
        }

        $this->command->info('✅ Created ' . count($jadwalData) . ' Jadwal');
        return $jadwalData;
    }
}

