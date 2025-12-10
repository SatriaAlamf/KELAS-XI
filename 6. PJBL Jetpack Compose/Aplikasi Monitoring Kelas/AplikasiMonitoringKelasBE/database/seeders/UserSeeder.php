<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

class UserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $this->command->info('👤 Membuat data Users...');
        
        $users = [
            // Admin
            [
                'name' => 'Administrator Sistem',
                'email' => 'admin@sekolah.com',
                'password' => Hash::make('admin123'),
                'role' => 'admin',
            ],
            
            // Kepala Sekolah
            [
                'name' => 'Dr. Budi Santoso, M.Pd',
                'email' => 'kepsek@sekolah.com',
                'password' => Hash::make('kepsek123'),
                'role' => 'kepala_sekolah',
            ],
            
            // Staff Kurikulum
            [
                'name' => 'Dra. Siti Nurhaliza, M.Pd',
                'email' => 'kurikulum@sekolah.com',
                'password' => Hash::make('kurikulum123'),
                'role' => 'kurikulum',
            ],
            
            // Siswa - Piket Kelas 10 IPA 1
            [
                'name' => 'Ahmad Fauzi',
                'email' => 'ahmad.fauzi@siswa.com',
                'password' => Hash::make('siswa123'),
                'role' => 'siswa',
            ],
            
            // Siswa - Piket Kelas 10 IPA 2
            [
                'name' => 'Siti Aminah',
                'email' => 'siti.aminah@siswa.com',
                'password' => Hash::make('siswa123'),
                'role' => 'siswa',
            ],
            
            // Siswa - Piket Kelas 11 IPA 1
            [
                'name' => 'Budi Setiawan',
                'email' => 'budi.setiawan@siswa.com',
                'password' => Hash::make('siswa123'),
                'role' => 'siswa',
            ],
            
            // Siswa - Piket Kelas 12 IPA 1
            [
                'name' => 'Dewi Lestari',
                'email' => 'dewi.lestari@siswa.com',
                'password' => Hash::make('siswa123'),
                'role' => 'siswa',
            ],
        ];

        foreach ($users as $userData) {
            User::create($userData);
            $this->command->info("   ✓ {$userData['name']} ({$userData['role']})");
        }
        
        $this->command->info("   Total: " . count($users) . " users berhasil dibuat");
    }
}
