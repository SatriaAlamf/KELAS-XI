<?php

namespace App\Helpers;

use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
use PhpOffice\PhpSpreadsheet\Style\Fill;
use PhpOffice\PhpSpreadsheet\Style\Alignment;

class ExcelExporter
{
    public static function export($data, $headers, $filename, $headerColor = '4472C4', $columnWidths = [])
    {
        $spreadsheet = new Spreadsheet();
        $sheet = $spreadsheet->getActiveSheet();
        
        // Set headers
        $sheet->fromArray($headers, null, 'A1');
        
        // Style header
        $lastColumn = chr(64 + count($headers));
        $sheet->getStyle('A1:' . $lastColumn . '1')->applyFromArray([
            'font' => ['bold' => true, 'size' => 12, 'color' => ['rgb' => 'FFFFFF']],
            'fill' => ['fillType' => Fill::FILL_SOLID, 'startColor' => ['rgb' => $headerColor]],
            'alignment' => ['horizontal' => Alignment::HORIZONTAL_CENTER]
        ]);
        
        // Set data
        $row = 2;
        foreach ($data as $rowData) {
            $sheet->fromArray($rowData, null, 'A' . $row);
            $row++;
        }
        
        // Set column widths
        if (!empty($columnWidths)) {
            foreach ($columnWidths as $column => $width) {
                $sheet->getColumnDimension($column)->setWidth($width);
            }
        } else {
            // Auto width for all columns
            foreach (range('A', $lastColumn) as $col) {
                $sheet->getColumnDimension($col)->setAutoSize(true);
            }
        }
        
        // Create writer and save
        $writer = new Xlsx($spreadsheet);
        $filepath = storage_path('app/public/' . $filename);
        
        // Ensure directory exists
        if (!file_exists(storage_path('app/public'))) {
            mkdir(storage_path('app/public'), 0755, true);
        }
        
        $writer->save($filepath);
        
        return $filepath;
    }
}
