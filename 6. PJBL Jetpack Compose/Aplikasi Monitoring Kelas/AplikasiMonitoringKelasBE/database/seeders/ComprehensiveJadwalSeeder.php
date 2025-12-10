<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Jadwal;
use App\Models\Kelas;
use App\Models\Mapel;
use App\Models\Guru;

class ComprehensiveJadwalSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $this->command->info('📋 Membuat data Jadwal Pelajaran untuk Semua Jurusan...');
        
        // Mapping mata pelajaran per jurusan
        $mapelPerJurusan = [
            'RPL' => [
                'umum' => ['MTK', 'BIN', 'BING', 'PAI', 'PJOK', 'PKN', 'SEJARAH'],
                'kejuruan' => ['PWB', 'PMB', 'BDT', 'RPL', 'JKM', 'SOP', 'PPL', 'ALGO', 'PKK']
            ],
            'AK' => [
                'umum' => ['MTK', 'BIN', 'BING', 'PAI', 'PJOK', 'PKN', 'SEJARAH'],
                'kejuruan' => ['AKD', 'AKK', 'AKP', 'PERPAJAKAN', 'AUDIT', 'SIA', 'MYOB', 'EKON', 'PKK']
            ],
            'DKV' => [
                'umum' => ['MTK', 'BIN', 'BING', 'PAI', 'PJOK', 'PKN', 'SBK'],
                'kejuruan' => ['DGR', 'DKV_DASAR', 'TIPOGRAFI', 'ILUSTRASI', 'FOTOGRAFI', 'VIDEO_EDITING', 'ANIMASI_2D', 'UI_UX', 'PKK']
            ],
            'MP' => [
                'umum' => ['MTK', 'BIN', 'BING', 'PAI', 'PJOK', 'PKN', 'SEJARAH'],
                'kejuruan' => ['KORESPONDENSI', 'KEARSIPAN', 'OTOMATISASI', 'TEKNOLOGI_PERKANTORAN', 'PSIKOLOGI_KOMUNIKASI', 'PRESENTASI', 'HUMAS', 'PKK']
            ],
            'LPB' => [
                'umum' => ['MTK', 'BIN', 'BING', 'PAI', 'PJOK', 'PKN', 'EKON'],
                'kejuruan' => ['BANK_DASAR', 'AKUNTANSI_BANK', 'PELAYANAN_NASABAH', 'PRODUK_BANK', 'KREDIT_BANK', 'MANAJEMEN_RISIKO', 'PASAR_MODAL', 'FINTECH', 'PKK']
            ],
            'BD' => [
                'umum' => ['MTK', 'BIN', 'BING', 'PAI', 'PJOK', 'PKN', 'EKON'],
                'kejuruan' => ['E_COMMERCE', 'DIGITAL_MARKETING', 'MARKETPLACE', 'SOSMED_MARKETING', 'SEO_SEM', 'CONTENT_MARKETING', 'COPYWRITING', 'ANALYTICS', 'PKK']
            ]
        ];

        $hariList = ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'];
        $jamList = ['1-2', '3-4', '5-6', '7-8'];
        
        $jadwals = [];
        $jadwalCount = 0;

        // Ambil semua kelas, mapel, dan guru
        $allKelas = Kelas::all()->sortBy('nama_kelas'); // Sort kelas alfabetis
        $allMapel = Mapel::all();
        $allGuru = Guru::all();

        // Mapping kode mapel ke ID
        $mapelMap = [];
        foreach ($allMapel as $mapel) {
            $mapelMap[$mapel->kode_mapel] = $mapel->id;
        }

        foreach ($allKelas as $kelas) {
            // Tentukan jurusan berdasarkan nama kelas
            $jurusan = null;
            if (strpos($kelas->nama_kelas, 'RPL') !== false) {
                $jurusan = 'RPL';
            } elseif (strpos($kelas->nama_kelas, 'AK') !== false) {
                $jurusan = 'AK';
            } elseif (strpos($kelas->nama_kelas, 'DKV') !== false) {
                $jurusan = 'DKV';
            } elseif (strpos($kelas->nama_kelas, 'MP') !== false) {
                $jurusan = 'MP';
            } elseif (strpos($kelas->nama_kelas, 'LPB') !== false) {
                $jurusan = 'LPB';
            } elseif (strpos($kelas->nama_kelas, 'BD') !== false) {
                $jurusan = 'BD';
            }

            if (!$jurusan || !isset($mapelPerJurusan[$jurusan])) {
                continue;
            }

            $this->command->info("   📝 Membuat jadwal untuk kelas: {$kelas->nama_kelas}");

            // Gabungkan mapel umum dan kejuruan
            $mapelUntukKelas = array_merge(
                $mapelPerJurusan[$jurusan]['umum'],
                $mapelPerJurusan[$jurusan]['kejuruan']
            );

            $mapelIndex = 0;
            
            // Buat jadwal untuk setiap hari secara berurutan
            foreach ($hariList as $hariIndex => $hari) {
                $jamPerHari = ($hari == 'Jumat') ? 3 : 4; // Jumat hanya 3 jam pelajaran
                
                $this->command->info("     📅 {$hari}:");
                
                // Buat jadwal untuk setiap jam secara berurutan
                for ($jamKe = 0; $jamKe < $jamPerHari; $jamKe++) {
                    if ($mapelIndex >= count($mapelUntukKelas)) {
                        $mapelIndex = 0; // Reset jika sudah habis mapel
                    }

                    $kodeMapel = $mapelUntukKelas[$mapelIndex];
                    
                    // Cek apakah mapel ada di database
                    if (!isset($mapelMap[$kodeMapel])) {
                        $mapelIndex++;
                        if ($mapelIndex >= count($mapelUntukKelas)) {
                            $mapelIndex = 0;
                        }
                        $kodeMapel = $mapelUntukKelas[$mapelIndex];
                    }

                    $mapelId = $mapelMap[$kodeMapel];
                    
                    // Pilih guru secara konsisten (berdasarkan kelas dan hari untuk konsistensi)
                    $guruIndex = ($kelas->id + $hariIndex + $jamKe) % count($allGuru);
                    $guruId = $allGuru[$guruIndex]->id;

                    $jadwals[] = [
                        'guru_id' => $guruId,
                        'mapel_id' => $mapelId,
                        'tahun_ajaran_id' => 2, // Assuming current tahun ajaran
                        'kelas_id' => $kelas->id,
                        'jam_ke' => $jamList[$jamKe],
                        'hari' => $hari
                    ];

                    // Get mapel name untuk logging
                    $mapelName = $allMapel->where('id', $mapelId)->first()->nama_mapel;
                    $guruName = $allGuru->where('id', $guruId)->first()->nama_guru;
                    
                    $this->command->info("       🕐 Jam {$jamList[$jamKe]}: {$mapelName} - {$guruName}");

                    $jadwalCount++;
                    $mapelIndex++;
                }
            }
        }

        // Urutkan jadwal berdasarkan kelas_id, hari, dan jam_ke sebelum disimpan
        $this->command->info("   📋 Mengurutkan jadwal berdasarkan kelas, hari, dan jam...");
        
        // Define hari order untuk sorting
        $hariOrder = [
            'Senin' => 1,
            'Selasa' => 2,
            'Rabu' => 3,
            'Kamis' => 4,
            'Jumat' => 5,
            'Sabtu' => 6
        ];
        
        // Define jam order untuk sorting
        $jamOrder = [
            '1-2' => 1,
            '3-4' => 2,
            '5-6' => 3,
            '7-8' => 4
        ];
        
        // Sort jadwals
        usort($jadwals, function($a, $b) use ($hariOrder, $jamOrder) {
            // Sort by kelas_id first
            if ($a['kelas_id'] != $b['kelas_id']) {
                return $a['kelas_id'] <=> $b['kelas_id'];
            }
            
            // Then by hari
            $hariA = $hariOrder[$a['hari']] ?? 999;
            $hariB = $hariOrder[$b['hari']] ?? 999;
            if ($hariA != $hariB) {
                return $hariA <=> $hariB;
            }
            
            // Finally by jam_ke
            $jamA = $jamOrder[$a['jam_ke']] ?? 999;
            $jamB = $jamOrder[$b['jam_ke']] ?? 999;
            return $jamA <=> $jamB;
        });

        // Insert semua jadwal yang sudah terurut
        $this->command->info("   💾 Menyimpan {$jadwalCount} jadwal ke database...");
        
        $chunkSize = 100;
        $chunks = array_chunk($jadwals, $chunkSize);
        $savedCount = 0;
        
        foreach ($chunks as $chunkIndex => $chunk) {
            foreach ($chunk as $jadwal) {
                Jadwal::create($jadwal);
            }
            $savedCount += count($chunk);
            $this->command->info("   ✓ {$savedCount}/{$jadwalCount} jadwal tersimpan...");
        }
        
        $this->command->info("   🎉 Total: {$jadwalCount} jadwal berhasil dibuat untuk semua jurusan");
        $this->command->info("   📊 Jadwal tersimpan secara terurut: Kelas → Hari (Senin-Sabtu) → Jam (1-2 s/d 7-8)");
    }
}