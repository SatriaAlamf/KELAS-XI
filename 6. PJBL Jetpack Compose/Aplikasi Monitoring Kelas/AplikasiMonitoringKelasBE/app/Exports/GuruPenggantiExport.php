<?php

namespace App\Exports;

use App\Models\GuruPengganti;
use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
use PhpOffice\PhpSpreadsheet\Style\Fill;
use PhpOffice\PhpSpreadsheet\Style\Alignment;
use Carbon\Carbon;

class GuruPenggantiExport
{
    public static function generate(array $filters = [])
    {
        $query = GuruPengganti::with([
            'guruAsli',
            'guruPengganti',
            'guruMengajar.jadwal.kelas',
            'guruMengajar.jadwal.mapel'
        ]);

        // Apply filters
        if (!empty($filters['status'])) {
            $query->where('status', $filters['status']);
        }

        if (!empty($filters['guru_asli_id'])) {
            $query->where('guru_asli_id', $filters['guru_asli_id']);
        }

        if (!empty($filters['guru_pengganti_id'])) {
            $query->where('guru_pengganti_id', $filters['guru_pengganti_id']);
        }

        $data = $query->latest()->get();

        // Create spreadsheet
        $spreadsheet = new Spreadsheet();
        $sheet = $spreadsheet->getActiveSheet();

        // Set headers
        $headers = [
            'No', 'Guru Asli', 'Guru Pengganti', 'Kelas', 'Mapel', 'Hari', 'Jam Ke',
            'Tanggal Mulai', 'Tanggal Selesai', 'Durasi (Hari)', 'Alasan', 'Keterangan', 'Status', 'Dibuat Pada'
        ];
        $sheet->fromArray($headers, null, 'A1');

        // Style header
        $sheet->getStyle('A1:N1')->applyFromArray([
            'font' => ['bold' => true, 'size' => 12, 'color' => ['rgb' => 'FFFFFF']],
            'fill' => ['fillType' => Fill::FILL_SOLID, 'startColor' => ['rgb' => '4F46E5']],
            'alignment' => ['horizontal' => Alignment::HORIZONTAL_CENTER, 'vertical' => Alignment::VERTICAL_CENTER]
        ]);

        // Add data
        $row = 2;
        foreach ($data as $index => $item) {
            try {
                $tanggalMulai = $item->tanggal_mulai instanceof Carbon 
                    ? $item->tanggal_mulai 
                    : Carbon::parse($item->tanggal_mulai);
                
                $tanggalSelesai = $item->tanggal_selesai instanceof Carbon 
                    ? $item->tanggal_selesai 
                    : Carbon::parse($item->tanggal_selesai);
                
                $durasi = $tanggalMulai->diffInDays($tanggalSelesai) + 1;

                $sheet->fromArray([
                    $index + 1,
                    $item->guruAsli->nama_guru ?? '-',
                    $item->guruPengganti->nama_guru ?? '-',
                    $item->guruMengajar->jadwal->kelas->nama_kelas ?? '-',
                    $item->guruMengajar->jadwal->mapel->nama_mapel ?? '-',
                    $item->guruMengajar->jadwal->hari ?? '-',
                    $item->guruMengajar->jadwal->jam_ke ?? '-',
                    $tanggalMulai->format('d/m/Y'),
                    $tanggalSelesai->format('d/m/Y'),
                    $durasi,
                    $item->alasan ?? '-',
                    $item->keterangan ?? '-',
                    ucfirst($item->status ?? 'aktif'),
                    $item->created_at ? $item->created_at->format('d/m/Y H:i') : '-'
                ], null, 'A' . $row);
            } catch (\Exception $e) {
                // Skip error rows
            }
            $row++;
        }

        // Auto size columns
        foreach (range('A', 'N') as $col) {
            $sheet->getColumnDimension($col)->setAutoSize(true);
        }

        // Generate file
        $filename = 'guru-pengganti-' . date('Y-m-d-His') . '.xlsx';
        $filepath = storage_path('app/public/' . $filename);

        // Ensure directory exists
        if (!file_exists(storage_path('app/public'))) {
            mkdir(storage_path('app/public'), 0755, true);
        }

        $writer = new Xlsx($spreadsheet);
        $writer->save($filepath);

        return $filepath;
    }
}
