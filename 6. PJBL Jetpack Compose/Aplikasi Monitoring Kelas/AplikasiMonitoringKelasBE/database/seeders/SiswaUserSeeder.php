<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\Kelas;
use Illuminate\Support\Facades\Hash;

class SiswaUserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $this->command->info('👥 Membuat user siswa untuk setiap kelas...');
        
        // Get all kelas
        $kelasList = Kelas::all();
        
        if ($kelasList->isEmpty()) {
            $this->command->error('❌ Tidak ada kelas yang ditemukan. Pastikan KelasSeeder sudah dijalankan terlebih dahulu.');
            return;
        }
        
        $password = Hash::make('siswa123');
        $totalUsers = 0;
        
        foreach ($kelasList as $kelas) {
            // Generate 10 siswa untuk setiap kelas
            for ($i = 1; $i <= 10; $i++) {
                $nama = "Siswa {$kelas->nama_kelas} {$i}";
                $email = strtolower(str_replace(' ', '', "siswa{$kelas->nama_kelas}{$i}@smk.edu"));
                
                // Remove special characters from email
                $email = preg_replace('/[^a-z0-9@.]/', '', $email);
                
                // Check if user already exists
                $existingUser = User::where('email', $email)->first();
                if (!$existingUser) {
                    User::create([
                        'name' => $nama,
                        'email' => $email,
                        'password' => $password,
                        'role' => 'siswa',
                        'kelas_id' => $kelas->id,
                    ]);
                    
                    $totalUsers++;
                    $this->command->info("   ✓ {$nama} ({$email})");
                } else {
                    $this->command->info("   - {$nama} ({$email}) sudah ada");
                }
            }
        }
        
        $this->command->info("   Total: {$totalUsers} siswa berhasil dibuat");
        $this->command->info("   Password semua siswa: siswa123");
        
        // Create some admin/staff users without kelas_id
        $this->command->info('👤 Membuat user admin dan staff...');
        
        $adminUsers = [
            [
                'name' => 'Admin Sistem',
                'email' => 'admin@smk.edu',
                'role' => 'admin',
            ],
            [
                'name' => 'Kurikulum',
                'email' => 'kurikulum@smk.edu',
                'role' => 'kurikulum',
            ],
            [
                'name' => 'Kepala Sekolah',
                'email' => 'kepsek@smk.edu',
                'role' => 'kepala_sekolah',
            ],
        ];
        
        foreach ($adminUsers as $userData) {
            $existingAdmin = User::where('email', $userData['email'])->first();
            if (!$existingAdmin) {
                User::create([
                    'name' => $userData['name'],
                    'email' => $userData['email'],
                    'password' => Hash::make('admin123'),
                    'role' => $userData['role'],
                    'kelas_id' => null, // No kelas for admin/staff
                ]);
                
                $this->command->info("   ✓ {$userData['name']} ({$userData['email']})");
            } else {
                $this->command->info("   - {$userData['name']} ({$userData['email']}) sudah ada");
            }
        }
        
        $this->command->info("   Password admin/staff: admin123");
    }
}
