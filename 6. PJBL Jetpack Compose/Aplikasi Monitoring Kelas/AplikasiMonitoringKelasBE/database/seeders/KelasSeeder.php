 <?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Kelas;

class KelasSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $this->command->info('🏫 Membuat data Kelas SMK...');
        
        $kelasList = [
            // Kelas RPL (existing)
            ['nama_kelas' => '10 RPL'],
            ['nama_kelas' => '11 RPL'],
            ['nama_kelas' => '12 RPL'],
            
            // Kelas Akuntansi (AK) - 3 kelas per angkatan
            ['nama_kelas' => '10 AK 1'],
            ['nama_kelas' => '10 AK 2'],
            ['nama_kelas' => '10 AK 3'],
            ['nama_kelas' => '11 AK 1'],
            ['nama_kelas' => '11 AK 2'],
            ['nama_kelas' => '11 AK 3'],
            ['nama_kelas' => '12 AK 1'],
            ['nama_kelas' => '12 AK 2'],
            ['nama_kelas' => '12 AK 3'],
            
            // Kelas Desain Komunikasi Visual (DKV) - 3 kelas per angkatan
            ['nama_kelas' => '10 DKV 1'],
            ['nama_kelas' => '10 DKV 2'],
            ['nama_kelas' => '10 DKV 3'],
            ['nama_kelas' => '11 DKV 1'],
            ['nama_kelas' => '11 DKV 2'],
            ['nama_kelas' => '11 DKV 3'],
            ['nama_kelas' => '12 DKV 1'],
            ['nama_kelas' => '12 DKV 2'],
            ['nama_kelas' => '12 DKV 3'],
            
            // Kelas Manajemen Perkantoran (MP) - 3 kelas per angkatan
            ['nama_kelas' => '10 MP 1'],
            ['nama_kelas' => '10 MP 2'],
            ['nama_kelas' => '10 MP 3'],
            ['nama_kelas' => '11 MP 1'],
            ['nama_kelas' => '11 MP 2'],
            ['nama_kelas' => '11 MP 3'],
            ['nama_kelas' => '12 MP 1'],
            ['nama_kelas' => '12 MP 2'],
            ['nama_kelas' => '12 MP 3'],
            
            // Kelas Layanan Perbankan (LPB) - 1 kelas per angkatan
            ['nama_kelas' => '10 LPB'],
            ['nama_kelas' => '11 LPB'],
            ['nama_kelas' => '12 LPB'],
            
            // Kelas Bisnis Digital (BD) - 2 kelas per angkatan
            ['nama_kelas' => '10 BD 1'],
            ['nama_kelas' => '10 BD 2'],
            ['nama_kelas' => '11 BD 1'],
            ['nama_kelas' => '11 BD 2'],
            ['nama_kelas' => '12 BD 1'],
            ['nama_kelas' => '12 BD 2'],
        ];

        foreach ($kelasList as $kelas) {
            Kelas::create($kelas);
            $this->command->info("   ✓ Kelas {$kelas['nama_kelas']}");
        }
        
        $this->command->info("   Total: " . count($kelasList) . " kelas berhasil dibuat");
    }
}
