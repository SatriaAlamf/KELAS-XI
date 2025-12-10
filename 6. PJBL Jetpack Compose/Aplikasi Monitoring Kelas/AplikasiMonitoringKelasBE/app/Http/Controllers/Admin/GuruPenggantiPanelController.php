<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\GuruPengganti;
use App\Models\GuruMengajar;
use App\Models\Guru;
use App\Models\Kelas;
use Illuminate\Http\Request;
use App\Exports\GuruPenggantiExport;
use App\Imports\GuruPenggantiImport;

class GuruPenggantiPanelController extends Controller
{
    /**
     * Display a listing of the resource with advanced filters
     */
    public function index(Request $request)
    {
        $query = GuruPengganti::with([
            'guruAsli',
            'guruPengganti',
            'guruMengajar.jadwal.kelas',
            'guruMengajar.jadwal.mapel',
            'guruMengajar.jadwal'
        ]);

        // Filter by status
        if ($request->filled('status')) {
            $query->where('status', $request->status);
        }

        // Filter by guru asli
        if ($request->filled('guru_asli_id')) {
            $query->where('guru_asli_id', $request->guru_asli_id);
        }

        // Filter by guru pengganti
        if ($request->filled('guru_pengganti_id')) {
            $query->where('guru_pengganti_id', $request->guru_pengganti_id);
        }

        // Filter by kelas
        if ($request->filled('kelas_id')) {
            $query->whereHas('guruMengajar.jadwal', function($q) use ($request) {
                $q->where('kelas_id', $request->kelas_id);
            });
        }

        // Filter by date range
        if ($request->filled('tanggal_mulai')) {
            $query->where('tanggal_mulai', '>=', $request->tanggal_mulai);
        }
        if ($request->filled('tanggal_selesai')) {
            $query->where('tanggal_selesai', '<=', $request->tanggal_selesai);
        }

        // Search
        if ($request->filled('search')) {
            $search = $request->search;
            $query->where(function($q) use ($search) {
                $q->whereHas('guruAsli', function($subQ) use ($search) {
                    $subQ->where('nama_guru', 'like', "%{$search}%");
                })
                ->orWhereHas('guruPengganti', function($subQ) use ($search) {
                    $subQ->where('nama_guru', 'like', "%{$search}%");
                })
                ->orWhere('alasan', 'like', "%{$search}%");
            });
        }

        $guruPenggantis = $query->latest()->paginate(15)->withQueryString();

        // Data untuk filter dropdowns
        $gurus = Guru::orderBy('nama_guru')->get();
        $kelas = Kelas::orderBy('nama_kelas')->get();

        return view('admin.guru-penggantis.index', compact('guruPenggantis', 'gurus', 'kelas'));
    }

    /**
     * Show the form for creating a new resource
     */
    public function create()
    {
        // Get guru mengajar yang tidak masuk
        $guruMengajars = GuruMengajar::with(['jadwal.guru', 'jadwal.mapel', 'jadwal.kelas'])
            ->where('status', 'tidak_masuk')
            ->whereDoesntHave('guruPengganti', function($q) {
                $q->where('status', 'aktif');
            })
            ->get();

        $gurus = Guru::orderBy('nama_guru')->get();

        return view('admin.guru-penggantis.create', compact('guruMengajars', 'gurus'));
    }

    /**
     * Store a newly created resource in storage
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'guru_mengajar_id' => 'required|exists:guru_mengajars,id',
            'guru_pengganti_id' => 'required|exists:gurus,id',
            'tanggal_mulai' => 'required|date',
            'tanggal_selesai' => 'required|date|after_or_equal:tanggal_mulai',
            'alasan' => 'required|string|max:255',
            'keterangan' => 'nullable|string',
            'status' => 'required|in:aktif,selesai'
        ]);

        // Get guru asli from guru_mengajar
        $guruMengajar = GuruMengajar::with('jadwal')->findOrFail($request->guru_mengajar_id);

        // Validate guru pengganti tidak sama dengan guru asli
        if ($guruMengajar->jadwal->guru_id == $request->guru_pengganti_id) {
            return redirect()->back()
                ->withInput()
                ->with('error', 'Guru pengganti tidak boleh sama dengan guru asli!');
        }

        $validated['guru_asli_id'] = $guruMengajar->jadwal->guru_id;

        GuruPengganti::create($validated);

        return redirect()->route('admin.guru-penggantis')
            ->with('success', 'Data guru pengganti berhasil ditambahkan!');
    }

    /**
     * Display the specified resource
     */
    public function show(GuruPengganti $guruPengganti)
    {
        $guruPengganti->load([
            'guruAsli',
            'guruPengganti',
            'guruMengajar.jadwal.guru',
            'guruMengajar.jadwal.mapel',
            'guruMengajar.jadwal.kelas'
        ]);

        return view('admin.guru-penggantis.show', compact('guruPengganti'));
    }

    /**
     * Show the form for editing the specified resource
     */
    public function edit(GuruPengganti $guruPengganti)
    {
        $guruPengganti->load('guruMengajar.jadwal');
        $gurus = Guru::orderBy('nama_guru')->get();

        return view('admin.guru-penggantis.edit', compact('guruPengganti', 'gurus'));
    }

    /**
     * Update the specified resource in storage
     */
    public function update(Request $request, GuruPengganti $guruPengganti)
    {
        $validated = $request->validate([
            'guru_pengganti_id' => 'required|exists:gurus,id',
            'tanggal_mulai' => 'required|date',
            'tanggal_selesai' => 'required|date|after_or_equal:tanggal_mulai',
            'alasan' => 'required|string|max:255',
            'keterangan' => 'nullable|string',
            'status' => 'required|in:aktif,selesai'
        ]);

        // Validate guru pengganti tidak sama dengan guru asli
        if ($guruPengganti->guru_asli_id == $request->guru_pengganti_id) {
            return redirect()->back()
                ->withInput()
                ->with('error', 'Guru pengganti tidak boleh sama dengan guru asli!');
        }

        $guruPengganti->update($validated);

        return redirect()->route('admin.guru-penggantis')
            ->with('success', 'Data guru pengganti berhasil diupdate!');
    }

    /**
     * Update durasi (tanggal mulai dan selesai) guru pengganti
     */
    public function updateDurasi(Request $request, GuruPengganti $guruPengganti)
    {
        $request->validate([
            'tanggal_mulai' => 'required|date',
            'tanggal_selesai' => 'required|date|after_or_equal:tanggal_mulai',
        ], [
            'tanggal_mulai.required' => 'Tanggal mulai harus diisi',
            'tanggal_selesai.required' => 'Tanggal selesai harus diisi',
            'tanggal_selesai.after_or_equal' => 'Tanggal selesai harus setelah atau sama dengan tanggal mulai',
        ]);

        $guruPengganti->update([
            'tanggal_mulai' => $request->tanggal_mulai,
            'tanggal_selesai' => $request->tanggal_selesai,
        ]);

        return redirect()->route('admin.guru-penggantis')
            ->with('success', 'Durasi guru pengganti berhasil diperbarui!');
    }

    /**
     * Remove the specified resource from storage
     */
    public function destroy(GuruPengganti $guruPengganti)
    {
        $guruPengganti->delete();

        return redirect()->route('admin.guru-penggantis')
            ->with('success', 'Data guru pengganti berhasil dihapus!');
    }

    /**
     * Export to Excel
     */
    public function export(Request $request)
    {
        try {
            $filepath = GuruPenggantiExport::generate($request->all());
            $filename = basename($filepath);
            
            return response()->download($filepath, $filename)->deleteFileAfterSend(true);
        } catch (\Exception $e) {
            return redirect()->back()
                ->with('error', 'Gagal export data: ' . $e->getMessage());
        }
    }

    /**
     * Import from Excel
     */
    public function import(Request $request)
    {
        $request->validate([
            'file' => 'required|mimes:xlsx,xls,csv|max:5120'
        ]);

        try {
            $result = GuruPenggantiImport::process($request->file('file')->getPathname());

            if ($result['success']) {
                $message = "Berhasil import {$result['imported']} data guru pengganti!";
                
                if (!empty($result['errors'])) {
                    $message .= " Dengan " . count($result['errors']) . " error.";
                }
                
                return redirect()->route('admin.guru-penggantis')
                    ->with('success', $message)
                    ->with('import_errors', $result['errors']);
            } else {
                return redirect()->back()
                    ->with('error', 'Gagal import data. Periksa format file Anda.')
                    ->with('import_errors', $result['errors']);
            }
        } catch (\Exception $e) {
            return redirect()->back()
                ->with('error', 'Gagal import data: ' . $e->getMessage());
        }
    }

    /**
     * Download template Excel
     */
    public function template()
    {
        $headers = [
            'Content-Type' => 'application/vnd.ms-excel',
            'Content-Disposition' => 'attachment; filename="template-guru-pengganti.xlsx"'
        ];

        $templateData = [
            ['Guru Mengajar ID', 'Guru Pengganti ID', 'Tanggal Mulai', 'Tanggal Selesai', 'Alasan', 'Keterangan', 'Status'],
            ['1', '5', '2025-11-25', '2025-11-30', 'Sakit', 'Demam tinggi', 'aktif'],
            ['2', '6', '2025-11-26', '2025-12-01', 'Cuti', 'Cuti tahunan', 'aktif'],
        ];

        $callback = function() use ($templateData) {
            $file = fopen('php://output', 'w');
            foreach ($templateData as $row) {
                fputcsv($file, $row);
            }
            fclose($file);
        };

        return response()->stream($callback, 200, $headers);
    }
}
