<?php

namespace App\Imports;

use App\Models\GuruPengganti;
use App\Models\GuruMengajar;
use PhpOffice\PhpSpreadsheet\IOFactory;
use Carbon\Carbon;
use Illuminate\Support\Facades\Validator;

class GuruPenggantiImport
{
    public static function process($filePath)
    {
        $spreadsheet = IOFactory::load($filePath);
        $sheet = $spreadsheet->getActiveSheet();
        $data = $sheet->toArray();

        // Remove header row
        $headers = array_shift($data);

        $imported = 0;
        $errors = [];

        foreach ($data as $index => $row) {
            $rowNumber = $index + 2; // +2 for header and 1-based index

            // Skip empty rows
            if (empty($row[0])) {
                continue;
            }

            try {
                // Map row data
                $rowData = [
                    'guru_mengajar_id' => $row[0] ?? null,
                    'guru_pengganti_id' => $row[1] ?? null,
                    'tanggal_mulai' => $row[2] ?? null,
                    'tanggal_selesai' => $row[3] ?? null,
                    'alasan' => $row[4] ?? null,
                    'keterangan' => $row[5] ?? null,
                    'status' => $row[6] ?? 'aktif',
                ];

                // Validate
                $validator = Validator::make($rowData, [
                    'guru_mengajar_id' => 'required|exists:guru_mengajars,id',
                    'guru_pengganti_id' => 'required|exists:gurus,id',
                    'tanggal_mulai' => 'required|date',
                    'tanggal_selesai' => 'required|date|after_or_equal:tanggal_mulai',
                    'alasan' => 'required|string|max:255',
                    'keterangan' => 'nullable|string',
                    'status' => 'nullable|in:aktif,selesai',
                ]);

                if ($validator->fails()) {
                    $errors[] = "Baris {$rowNumber}: " . implode(', ', $validator->errors()->all());
                    continue;
                }

                // Get guru_mengajar to extract guru_asli_id
                $guruMengajar = GuruMengajar::with('jadwal')->find($rowData['guru_mengajar_id']);

                if (!$guruMengajar || !$guruMengajar->jadwal) {
                    $errors[] = "Baris {$rowNumber}: Data guru mengajar atau jadwal tidak ditemukan";
                    continue;
                }

                // Validate guru pengganti tidak sama dengan guru asli
                if ($guruMengajar->jadwal->guru_id == $rowData['guru_pengganti_id']) {
                    $errors[] = "Baris {$rowNumber}: Guru pengganti tidak boleh sama dengan guru asli";
                    continue;
                }

                // Create record
                GuruPengganti::create([
                    'guru_mengajar_id' => $rowData['guru_mengajar_id'],
                    'guru_asli_id' => $guruMengajar->jadwal->guru_id,
                    'guru_pengganti_id' => $rowData['guru_pengganti_id'],
                    'tanggal_mulai' => Carbon::parse($rowData['tanggal_mulai']),
                    'tanggal_selesai' => Carbon::parse($rowData['tanggal_selesai']),
                    'alasan' => $rowData['alasan'],
                    'keterangan' => $rowData['keterangan'],
                    'status' => $rowData['status'],
                ]);

                $imported++;
            } catch (\Exception $e) {
                $errors[] = "Baris {$rowNumber}: " . $e->getMessage();
            }
        }

        return [
            'success' => $imported > 0,
            'imported' => $imported,
            'errors' => $errors
        ];
    }
}
