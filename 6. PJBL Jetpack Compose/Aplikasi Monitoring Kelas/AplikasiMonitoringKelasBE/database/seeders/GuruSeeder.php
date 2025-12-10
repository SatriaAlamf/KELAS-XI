<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Guru;

class GuruSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $this->command->info('👨‍🏫 Membuat data Guru SMK RPL...');
        
        $gurus = [
            // Guru Matematika
            ['kode_guru' => 'GR001', 'nama_guru' => 'Drs. Ahmad Fauzi, M.Pd', 'telepon' => '081234567890'],
            
            // Guru Bahasa Indonesia
            ['kode_guru' => 'GR002', 'nama_guru' => 'Dra. Siti Rahayu, M.Pd', 'telepon' => '081234567891'],
            
            // Guru Bahasa Inggris
            ['kode_guru' => 'GR003', 'nama_guru' => 'Sarah Anderson, S.Pd', 'telepon' => '081234567892'],
            
            // Guru Pendidikan Agama
            ['kode_guru' => 'GR004', 'nama_guru' => 'Drs. H. Muhammad Ali, M.Ag', 'telepon' => '081234567893'],
            
            // Guru Pendidikan Jasmani
            ['kode_guru' => 'GR005', 'nama_guru' => 'Drs. Eko Prasetyo, M.Or', 'telepon' => '081234567894'],
            
            // Guru PKN
            ['kode_guru' => 'GR006', 'nama_guru' => 'Dra. Dewi Lestari, M.Pd', 'telepon' => '081234567895'],
            
            // Guru Pemrograman Web
            ['kode_guru' => 'GR007', 'nama_guru' => 'Andi Nugroho, S.Kom, M.T', 'telepon' => '081234567896'],
            
            // Guru Pemrograman Mobile
            ['kode_guru' => 'GR008', 'nama_guru' => 'Dedi Kurniawan, S.T, M.Kom', 'telepon' => '081234567897'],
            
            // Guru Basis Data
            ['kode_guru' => 'GR009', 'nama_guru' => 'Budi Santoso, S.Kom, M.T', 'telepon' => '081234567898'],
            
            // Guru RPL
            ['kode_guru' => 'GR010', 'nama_guru' => 'Hendra Wijaya, S.T, M.T', 'telepon' => '081234567899'],
            
            // Guru Jaringan Komputer
            ['kode_guru' => 'GR011', 'nama_guru' => 'Fajar Ramadhan, S.Kom, M.Kom', 'telepon' => '081234567900'],
            
            // Guru Sistem Operasi
            ['kode_guru' => 'GR012', 'nama_guru' => 'Galih Pratama, S.Kom', 'telepon' => '081234567901'],
            
            // Guru Desain Grafis
            ['kode_guru' => 'GR013', 'nama_guru' => 'Eka Putri, S.Ds, M.Ds', 'telepon' => '081234567902'],
            
            // Guru PKK
            ['kode_guru' => 'GR014', 'nama_guru' => 'Dr. Irfan Hidayat, S.E, M.M', 'telepon' => '081234567903'],
            
            // Guru Pemrograman Berorientasi Objek
            ['kode_guru' => 'GR015', 'nama_guru' => 'Joko Widodo, S.Kom, M.Kom', 'telepon' => '081234567904'],
        ];

        foreach ($gurus as $guru) {
            Guru::create($guru);
            $this->command->info("   ✓ {$guru['kode_guru']} - {$guru['nama_guru']}");
        }
        
        $this->command->info("   Total: " . count($gurus) . " guru berhasil dibuat");
    }
}
