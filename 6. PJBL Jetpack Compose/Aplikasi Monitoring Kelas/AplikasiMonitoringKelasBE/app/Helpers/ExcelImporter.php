<?php

namespace App\Helpers;

use PhpOffice\PhpSpreadsheet\IOFactory;
use PhpOffice\PhpSpreadsheet\Spreadsheet;
use PhpOffice\PhpSpreadsheet\Writer\Xlsx;
use PhpOffice\PhpSpreadsheet\Style\Fill;
use PhpOffice\PhpSpreadsheet\Style\Alignment;
use PhpOffice\PhpSpreadsheet\Style\Border;
use PhpOffice\PhpSpreadsheet\Cell\DataValidation;

class ExcelImporter
{
    /**
     * Read Excel/CSV file and return data as array
     */
    public static function read($filePath)
    {
        try {
            $spreadsheet = IOFactory::load($filePath);
            $sheet = $spreadsheet->getActiveSheet();
            $data = $sheet->toArray();
            
            // Remove header row
            array_shift($data);
            
            // Filter empty rows
            $data = array_filter($data, function($row) {
                return !empty(array_filter($row));
            });
            
            return [
                'success' => true,
                'data' => array_values($data)
            ];
        } catch (\Exception $e) {
            return [
                'success' => false,
                'message' => 'Error reading file: ' . $e->getMessage()
            ];
        }
    }
    
    /**
     * Generate template Excel file with headers and instructions
     */
    public static function generateTemplate($headers, $filename, $instructions = [], $dropdowns = [], $headerColor = '4472C4')
    {
        $spreadsheet = new Spreadsheet();
        $sheet = $spreadsheet->getActiveSheet();
        
        // Set sheet name
        $sheet->setTitle('Data');
        
        // Add instructions sheet if provided
        if (!empty($instructions)) {
            $instructionSheet = $spreadsheet->createSheet(0);
            $instructionSheet->setTitle('Instruksi');
            $instructionSheet->setCellValue('A1', 'INSTRUKSI PENGISIAN');
            $instructionSheet->getStyle('A1')->applyFromArray([
                'font' => ['bold' => true, 'size' => 14, 'color' => ['rgb' => 'FFFFFF']],
                'fill' => ['fillType' => Fill::FILL_SOLID, 'startColor' => ['rgb' => $headerColor]],
                'alignment' => ['horizontal' => Alignment::HORIZONTAL_CENTER]
            ]);
            $instructionSheet->mergeCells('A1:B1');
            
            $row = 3;
            foreach ($instructions as $instruction) {
                $instructionSheet->setCellValue('A' . $row, '• ' . $instruction);
                $instructionSheet->getStyle('A' . $row)->getAlignment()->setWrapText(true);
                $row++;
            }
            
            $instructionSheet->getColumnDimension('A')->setWidth(80);
            $instructionSheet->getColumnDimension('B')->setWidth(20);
            
            // Set active sheet back to Data
            $spreadsheet->setActiveSheetIndex(1);
            $sheet = $spreadsheet->getActiveSheet();
        }
        
        // Set headers
        $lastColumn = chr(64 + count($headers));
        $sheet->fromArray($headers, null, 'A1');
        
        // Style header
        $sheet->getStyle('A1:' . $lastColumn . '1')->applyFromArray([
            'font' => ['bold' => true, 'size' => 12, 'color' => ['rgb' => 'FFFFFF']],
            'fill' => ['fillType' => Fill::FILL_SOLID, 'startColor' => ['rgb' => $headerColor]],
            'alignment' => ['horizontal' => Alignment::HORIZONTAL_CENTER, 'vertical' => Alignment::VERTICAL_CENTER],
            'borders' => [
                'allBorders' => ['borderStyle' => Border::BORDER_THIN, 'color' => ['rgb' => '000000']]
            ]
        ]);
        
        // Set row height for header
        $sheet->getRowDimension(1)->setRowHeight(25);
        
        // Auto width for all columns
        foreach (range('A', $lastColumn) as $col) {
            $sheet->getColumnDimension($col)->setWidth(20);
        }
        
        // Add dropdown validations if provided
        foreach ($dropdowns as $column => $options) {
            $columnLetter = chr(65 + $column); // A=65 in ASCII
            $validation = $sheet->getCell($columnLetter . '2')->getDataValidation();
            $validation->setType(DataValidation::TYPE_LIST);
            $validation->setErrorStyle(DataValidation::STYLE_INFORMATION);
            $validation->setAllowBlank(false);
            $validation->setShowInputMessage(true);
            $validation->setShowErrorMessage(true);
            $validation->setShowDropDown(true);
            $validation->setErrorTitle('Input Error');
            $validation->setError('Value is not in list.');
            $validation->setPromptTitle('Pilih dari daftar');
            $validation->setPrompt('Silakan pilih dari dropdown');
            $validation->setFormula1('"' . implode(',', $options) . '"');
            
            // Copy validation to 1000 rows
            for ($i = 2; $i <= 1000; $i++) {
                $sheet->getCell($columnLetter . $i)->setDataValidation(clone $validation);
            }
        }
        
        // Add example data row (row 2) with light gray background
        $sheet->getStyle('A2:' . $lastColumn . '2')->applyFromArray([
            'fill' => ['fillType' => Fill::FILL_SOLID, 'startColor' => ['rgb' => 'F0F0F0']],
            'font' => ['italic' => true, 'color' => ['rgb' => '808080']],
            'borders' => [
                'allBorders' => ['borderStyle' => Border::BORDER_THIN, 'color' => ['rgb' => 'CCCCCC']]
            ]
        ]);
        
        // Freeze header row
        $sheet->freezePane('A2');
        
        // Create writer and save
        $writer = new Xlsx($spreadsheet);
        $filepath = storage_path('app/public/templates/' . $filename);
        
        // Ensure directory exists
        if (!file_exists(storage_path('app/public/templates'))) {
            mkdir(storage_path('app/public/templates'), 0755, true);
        }
        
        $writer->save($filepath);
        
        return $filepath;
    }
    
    /**
     * Validate imported data
     */
    public static function validate($data, $rules)
    {
        $errors = [];
        
        foreach ($data as $index => $row) {
            $rowNumber = $index + 2; // +2 because of header and 0-based index
            
            foreach ($rules as $columnIndex => $rule) {
                $value = $row[$columnIndex] ?? null;
                
                // Required check
                if (isset($rule['required']) && $rule['required'] && empty($value)) {
                    $errors[] = "Baris {$rowNumber}: {$rule['name']} wajib diisi";
                }
                
                // Unique check (requires callback)
                if (isset($rule['unique']) && !empty($value)) {
                    $exists = call_user_func($rule['unique'], $value);
                    if ($exists) {
                        $errors[] = "Baris {$rowNumber}: {$rule['name']} '{$value}' sudah ada di database";
                    }
                }
                
                // Max length check
                if (isset($rule['max_length']) && strlen($value) > $rule['max_length']) {
                    $errors[] = "Baris {$rowNumber}: {$rule['name']} maksimal {$rule['max_length']} karakter";
                }
                
                // Email check
                if (isset($rule['email']) && $rule['email'] && !empty($value)) {
                    if (!filter_var($value, FILTER_VALIDATE_EMAIL)) {
                        $errors[] = "Baris {$rowNumber}: {$rule['name']} '{$value}' bukan format email yang valid";
                    }
                }
                
                // In array check
                if (isset($rule['in']) && !empty($value)) {
                    if (!in_array($value, $rule['in'])) {
                        $errors[] = "Baris {$rowNumber}: {$rule['name']} harus salah satu dari: " . implode(', ', $rule['in']);
                    }
                }
                
                // Exists check (requires callback)
                if (isset($rule['exists']) && !empty($value)) {
                    $exists = call_user_func($rule['exists'], $value);
                    if (!$exists) {
                        $errors[] = "Baris {$rowNumber}: {$rule['name']} '{$value}' tidak ditemukan di database";
                    }
                }
            }
        }
        
        return [
            'valid' => empty($errors),
            'errors' => $errors
        ];
    }
}
