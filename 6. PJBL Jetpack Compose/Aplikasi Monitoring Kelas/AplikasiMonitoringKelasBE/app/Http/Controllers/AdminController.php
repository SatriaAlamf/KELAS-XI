<?php

namespace App\Http\Controllers;

use App\Models\User;
use App\Models\Guru;
use App\Models\Mapel;
use App\Models\Kelas;
use App\Models\TahunAjaran;
use App\Models\Jadwal;
use App\Models\GuruMengajar;
use App\Models\GuruPengganti;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use App\Helpers\ExcelExporter;
use App\Helpers\ExcelImporter;
use Carbon\Carbon;

class AdminController extends Controller
{
    // Dashboard
    public function dashboard()
    {
        $stats = [
            'total_users' => User::count(),
            'total_guru' => Guru::count(),
            'total_siswa' => User::where('role', 'siswa')->count(),
            'total_kelas' => Kelas::count(),
            'total_mapel' => Mapel::count(),
            'total_jadwal' => Jadwal::count(),
        ];
        
        $recent_users = User::latest()->take(5)->get();
        $recent_jadwal = Jadwal::with(['kelas', 'mapel', 'guru'])->latest()->take(5)->get();
        
        return view('admin.dashboard', compact('stats', 'recent_users', 'recent_jadwal'));
    }
    
    // ========== USER MANAGEMENT ==========
    public function users()
    {
        $users = User::latest()->paginate(15);
        return view('admin.users.index', compact('users'));
    }
    
    public function createUser()
    {
        return view('admin.users.create');
    }
    
    public function storeUser(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email',
            'password' => 'required|min:6',
            'role' => 'required|in:admin,guru,siswa,kepsek,kurikulum',
        ]);
        
        $validated['password'] = Hash::make($validated['password']);
        User::create($validated);
        
        return redirect()->route('admin.users')->with('success', 'User berhasil ditambahkan!');
    }
    
    public function editUser(User $user)
    {
        return view('admin.users.edit', compact('user'));
    }
    
    public function updateUser(Request $request, User $user)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email,' . $user->id,
            'role' => 'required|in:admin,guru,siswa,kepsek,kurikulum',
        ]);
        
        if ($request->filled('password')) {
            $validated['password'] = Hash::make($request->password);
        }
        
        $user->update($validated);
        
        return redirect()->route('admin.users')->with('success', 'User berhasil diupdate!');
    }
    
    public function destroyUser(User $user)
    {
        $user->delete();
        return redirect()->route('admin.users')->with('success', 'User berhasil dihapus!');
    }
    
    public function exportUsers()
    {
        $users = User::orderBy('created_at', 'desc')->get();
        
        $headers = ['No', 'Nama', 'Email', 'Role', 'Dibuat Pada', 'Update Terakhir'];
        $data = [];
        
        foreach ($users as $index => $user) {
            $data[] = [
                $index + 1,
                $user->name,
                $user->email,
                ucfirst($user->role),
                $user->created_at->format('d-m-Y H:i'),
                $user->updated_at->format('d-m-Y H:i'),
            ];
        }
        
        $columnWidths = ['A' => 8, 'B' => 30, 'C' => 35, 'D' => 15, 'E' => 20, 'F' => 20];
        $filename = 'Data_Users_' . now()->format('Y-m-d_His') . '.xlsx';
        
        $filepath = ExcelExporter::export($data, $headers, $filename, '4472C4', $columnWidths);
        
        return response()->download($filepath, $filename)->deleteFileAfterSend(true);
    }
    
    public function downloadTemplateUsers()
    {
        $headers = ['Nama', 'Email', 'Password', 'Role'];
        $instructions = [
            'Isi data sesuai dengan kolom yang tersedia',
            'Nama: Nama lengkap user (wajib diisi)',
            'Email: Email valid dan unik (wajib diisi)',
            'Password: Password minimal 8 karakter (wajib diisi)',
            'Role: Pilih salah satu dari dropdown (siswa, kurikulum, kepala_sekolah, admin)',
            'Jangan mengubah nama kolom header',
            'Hapus baris contoh (baris 2) sebelum mengupload'
        ];
        $dropdowns = [
            3 => ['siswa', 'kurikulum', 'kepala_sekolah', 'admin'] // Role column
        ];
        
        $filename = 'Template_Import_Users.xlsx';
        $filepath = ExcelImporter::generateTemplate($headers, $filename, $instructions, $dropdowns, '4472C4');
        
        return response()->download($filepath, $filename);
    }
    
    public function importUsers(Request $request)
    {
        $request->validate([
            'file' => 'required|file|mimes:xlsx,xls,csv|max:10240'
        ]);
        
        $file = $request->file('file');
        $result = ExcelImporter::read($file->getPathname());
        
        if (!$result['success']) {
            return redirect()->back()->with('error', $result['message']);
        }
        
        $data = $result['data'];
        
        // Validate data
        $validation = ExcelImporter::validate($data, [
            0 => ['name' => 'Nama', 'required' => true, 'max_length' => 255],
            1 => ['name' => 'Email', 'required' => true, 'email' => true, 'unique' => function($value) {
                return User::where('email', $value)->exists();
            }],
            2 => ['name' => 'Password', 'required' => true, 'max_length' => 255],
            3 => ['name' => 'Role', 'required' => true, 'in' => ['siswa', 'kurikulum', 'kepala_sekolah', 'admin']]
        ]);
        
        if (!$validation['valid']) {
            return redirect()->back()->with('error', 'Validasi gagal:<br>' . implode('<br>', $validation['errors']));
        }
        
        // Import data
        $imported = 0;
        foreach ($data as $row) {
            User::create([
                'name' => $row[0],
                'email' => $row[1],
                'password' => Hash::make($row[2]),
                'role' => $row[3],
            ]);
            $imported++;
        }
        
        return redirect()->route('admin.users')->with('success', "Berhasil import {$imported} data user!");
    }
    
    // ========== GURU MANAGEMENT ==========
    public function gurus()
    {
        $gurus = Guru::latest()->paginate(15);
        return view('admin.gurus.index', compact('gurus'));
    }
    
    public function createGuru()
    {
        return view('admin.gurus.create');
    }
    
    public function storeGuru(Request $request)
    {
        $validated = $request->validate([
            'kode_guru' => 'required|string|max:50|unique:gurus,kode_guru',
            'nama_guru' => 'required|string|max:255',
            'telepon' => 'nullable|string|max:20',
        ]);
        
        Guru::create($validated);
        
        return redirect()->route('admin.gurus')->with('success', 'Guru berhasil ditambahkan!');
    }
    
    public function editGuru(Guru $guru)
    {
        return view('admin.gurus.edit', compact('guru'));
    }
    
    public function updateGuru(Request $request, Guru $guru)
    {
        $validated = $request->validate([
            'kode_guru' => 'required|string|max:50|unique:gurus,kode_guru,' . $guru->id,
            'nama_guru' => 'required|string|max:255',
            'telepon' => 'nullable|string|max:20',
        ]);
        
        $guru->update($validated);
        
        return redirect()->route('admin.gurus')->with('success', 'Guru berhasil diupdate!');
    }
    
    public function destroyGuru(Guru $guru)
    {
        $guru->delete();
        return redirect()->route('admin.gurus')->with('success', 'Guru berhasil dihapus!');
    }
    
    public function exportGurus()
    {
        $gurus = Guru::orderBy('created_at', 'desc')->get();
        
        $headers = ['No', 'Kode Guru', 'Nama Guru', 'Telepon', 'Dibuat Pada'];
        $data = [];
        
        foreach ($gurus as $index => $guru) {
            $data[] = [
                $index + 1,
                $guru->kode_guru,
                $guru->nama_guru,
                $guru->telepon,
                $guru->created_at->format('d-m-Y H:i'),
            ];
        }
        
        $columnWidths = ['A' => 8, 'B' => 15, 'C' => 30, 'D' => 20, 'E' => 20];
        $filename = 'Data_Guru_' . now()->format('Y-m-d_His') . '.xlsx';
        
        $filepath = ExcelExporter::export($data, $headers, $filename, '70AD47', $columnWidths);
        
        return response()->download($filepath, $filename)->deleteFileAfterSend(true);
    }
    
    public function downloadTemplateGurus()
    {
        $headers = ['Kode Guru', 'Nama Guru', 'Telepon'];
        $instructions = [
            'Isi data sesuai dengan kolom yang tersedia',
            'Kode Guru: Kode unik guru (wajib diisi, contoh: G001)',
            'Nama Guru: Nama lengkap guru (wajib diisi)',
            'Telepon: Nomor telepon guru (opsional)',
            'Jangan mengubah nama kolom header',
            'Hapus baris contoh (baris 2) sebelum mengupload'
        ];
        
        $filename = 'Template_Import_Guru.xlsx';
        $filepath = ExcelImporter::generateTemplate($headers, $filename, $instructions, [], '70AD47');
        
        return response()->download($filepath, $filename);
    }
    
    public function importGurus(Request $request)
    {
        $request->validate([
            'file' => 'required|file|mimes:xlsx,xls,csv|max:10240'
        ]);
        
        $file = $request->file('file');
        $result = ExcelImporter::read($file->getPathname());
        
        if (!$result['success']) {
            return redirect()->back()->with('error', $result['message']);
        }
        
        $data = $result['data'];
        
        // Validate data
        $validation = ExcelImporter::validate($data, [
            0 => ['name' => 'Kode Guru', 'required' => true, 'max_length' => 50, 'unique' => function($value) {
                return Guru::where('kode_guru', $value)->exists();
            }],
            1 => ['name' => 'Nama Guru', 'required' => true, 'max_length' => 255],
            2 => ['name' => 'Telepon', 'max_length' => 20]
        ]);
        
        if (!$validation['valid']) {
            return redirect()->back()->with('error', 'Validasi gagal:<br>' . implode('<br>', $validation['errors']));
        }
        
        // Import data
        $imported = 0;
        foreach ($data as $row) {
            Guru::create([
                'kode_guru' => $row[0],
                'nama_guru' => $row[1],
                'telepon' => $row[2] ?? null,
            ]);
            $imported++;
        }
        
        return redirect()->route('admin.gurus')->with('success', "Berhasil import {$imported} data guru!");
    }
    
    // ========== MAPEL MANAGEMENT ==========
    public function mapels()
    {
        $mapels = Mapel::latest()->paginate(15);
        return view('admin.mapels.index', compact('mapels'));
    }
    
    public function createMapel()
    {
        return view('admin.mapels.create');
    }
    
    public function storeMapel(Request $request)
    {
        $validated = $request->validate([
            'kode_mapel' => 'required|string|max:20|unique:mapels,kode_mapel',
            'nama_mapel' => 'required|string|max:255',
            'deskripsi' => 'nullable|string',
        ]);
        
        Mapel::create($validated);
        
        return redirect()->route('admin.mapels')->with('success', 'Mata Pelajaran berhasil ditambahkan!');
    }
    
    public function editMapel(Mapel $mapel)
    {
        return view('admin.mapels.edit', compact('mapel'));
    }
    
    public function updateMapel(Request $request, Mapel $mapel)
    {
        $validated = $request->validate([
            'kode_mapel' => 'required|string|max:20|unique:mapels,kode_mapel,' . $mapel->id,
            'nama_mapel' => 'required|string|max:255',
            'deskripsi' => 'nullable|string',
        ]);
        
        $mapel->update($validated);
        
        return redirect()->route('admin.mapels')->with('success', 'Mata Pelajaran berhasil diupdate!');
    }
    
    public function destroyMapel(Mapel $mapel)
    {
        $mapel->delete();
        return redirect()->route('admin.mapels')->with('success', 'Mata Pelajaran berhasil dihapus!');
    }
    
    public function exportMapels()
    {
        $mapels = Mapel::orderBy('kode_mapel')->get();
        
        $headers = ['No', 'Kode Mapel', 'Nama Mapel'];
        $data = [];
        
        foreach ($mapels as $index => $mapel) {
            $data[] = [
                $index + 1,
                $mapel->kode_mapel,
                $mapel->nama_mapel,
            ];
        }
        
        $columnWidths = ['A' => 8, 'B' => 15, 'C' => 40];
        $filename = 'Data_Mapel_' . now()->format('Y-m-d_His') . '.xlsx';
        
        $filepath = ExcelExporter::export($data, $headers, $filename, 'FFC000', $columnWidths);
        
        return response()->download($filepath, $filename)->deleteFileAfterSend(true);
    }
    
    public function downloadTemplateMapels()
    {
        $headers = ['Kode Mapel', 'Nama Mapel', 'Deskripsi'];
        $instructions = [
            'Isi data sesuai dengan kolom yang tersedia',
            'Kode Mapel: Kode unik mata pelajaran (wajib diisi, contoh: MTK)',
            'Nama Mapel: Nama mata pelajaran (wajib diisi, contoh: Matematika)',
            'Deskripsi: Keterangan mata pelajaran (opsional)',
            'Jangan mengubah nama kolom header',
            'Hapus baris contoh (baris 2) sebelum mengupload'
        ];
        
        $filename = 'Template_Import_Mapel.xlsx';
        $filepath = ExcelImporter::generateTemplate($headers, $filename, $instructions, [], 'FFC000');
        
        return response()->download($filepath, $filename);
    }
    
    public function importMapels(Request $request)
    {
        $request->validate([
            'file' => 'required|file|mimes:xlsx,xls,csv|max:10240'
        ]);
        
        $file = $request->file('file');
        $result = ExcelImporter::read($file->getPathname());
        
        if (!$result['success']) {
            return redirect()->back()->with('error', $result['message']);
        }
        
        $data = $result['data'];
        
        // Validate data
        $validation = ExcelImporter::validate($data, [
            0 => ['name' => 'Kode Mapel', 'required' => true, 'max_length' => 20, 'unique' => function($value) {
                return Mapel::where('kode_mapel', $value)->exists();
            }],
            1 => ['name' => 'Nama Mapel', 'required' => true, 'max_length' => 255]
        ]);
        
        if (!$validation['valid']) {
            return redirect()->back()->with('error', 'Validasi gagal:<br>' . implode('<br>', $validation['errors']));
        }
        
        // Import data
        $imported = 0;
        foreach ($data as $row) {
            Mapel::create([
                'kode_mapel' => $row[0],
                'nama_mapel' => $row[1],
                'deskripsi' => $row[2] ?? null,
            ]);
            $imported++;
        }
        
        return redirect()->route('admin.mapels')->with('success', "Berhasil import {$imported} data mata pelajaran!");
    }
    
    // ========== KELAS MANAGEMENT ==========
    public function kelas()
    {
        $kelas = Kelas::latest()->paginate(15);
        return view('admin.kelas.index', compact('kelas'));
    }
    
    public function createKelas()
    {
        return view('admin.kelas.create');
    }
    
    public function storeKelas(Request $request)
    {
        $validated = $request->validate([
            'nama_kelas' => 'required|string|max:50',
        ]);
        
        Kelas::create($validated);
        
        return redirect()->route('admin.kelas')->with('success', 'Kelas berhasil ditambahkan!');
    }
    
    public function editKelas(Kelas $kela)
    {
        return view('admin.kelas.edit', compact('kela'));
    }
    
    public function updateKelas(Request $request, Kelas $kela)
    {
        $validated = $request->validate([
            'nama_kelas' => 'required|string|max:50',
        ]);
        
        $kela->update($validated);
        
        return redirect()->route('admin.kelas')->with('success', 'Kelas berhasil diupdate!');
    }
    
    public function destroyKelas(Kelas $kela)
    {
        $kela->delete();
        return redirect()->route('admin.kelas')->with('success', 'Kelas berhasil dihapus!');
    }
    
    public function exportKelas()
    {
        $kelas = Kelas::orderBy('nama_kelas')->get();
        
        $headers = ['No', 'Nama Kelas'];
        $data = [];
        
        foreach ($kelas as $index => $k) {
            $data[] = [
                $index + 1,
                $k->nama_kelas,
            ];
        }
        
        $columnWidths = ['A' => 8, 'B' => 30];
        $filename = 'Data_Kelas_' . now()->format('Y-m-d_His') . '.xlsx';
        
        $filepath = ExcelExporter::export($data, $headers, $filename, '5B9BD5', $columnWidths);
        
        return response()->download($filepath, $filename)->deleteFileAfterSend(true);
    }
    
    public function downloadTemplateKelas()
    {
        $headers = ['Nama Kelas'];
        $instructions = [
            'Isi data sesuai dengan kolom yang tersedia',
            'Nama Kelas: Nama kelas (wajib diisi, contoh: XII RPL 1)',
            'Jangan mengubah nama kolom header',
            'Hapus baris contoh (baris 2) sebelum mengupload'
        ];
        
        $filename = 'Template_Import_Kelas.xlsx';
        $filepath = ExcelImporter::generateTemplate($headers, $filename, $instructions, [], '5B9BD5');
        
        return response()->download($filepath, $filename);
    }
    
    public function importKelas(Request $request)
    {
        $request->validate([
            'file' => 'required|file|mimes:xlsx,xls,csv|max:10240'
        ]);
        
        $file = $request->file('file');
        $result = ExcelImporter::read($file->getPathname());
        
        if (!$result['success']) {
            return redirect()->back()->with('error', $result['message']);
        }
        
        $data = $result['data'];
        
        // Validate data
        $validation = ExcelImporter::validate($data, [
            0 => ['name' => 'Nama Kelas', 'required' => true, 'max_length' => 255, 'unique' => function($value) {
                return Kelas::where('nama_kelas', $value)->exists();
            }]
        ]);
        
        if (!$validation['valid']) {
            return redirect()->back()->with('error', 'Validasi gagal:<br>' . implode('<br>', $validation['errors']));
        }
        
        // Import data
        $imported = 0;
        foreach ($data as $row) {
            Kelas::create([
                'nama_kelas' => $row[0]
            ]);
            $imported++;
        }
        
        return redirect()->route('admin.kelas')->with('success', "Berhasil import {$imported} data kelas!");
    }
    
    // ========== TAHUN AJARAN MANAGEMENT ==========
    public function tahunAjarans()
    {
        $tahunAjarans = TahunAjaran::latest()->paginate(15);
        return view('admin.tahun-ajarans.index', compact('tahunAjarans'));
    }
    
    public function createTahunAjaran()
    {
        return view('admin.tahun-ajarans.create');
    }
    
    public function storeTahunAjaran(Request $request)
    {
        $validated = $request->validate([
            'tahun' => 'required|string|max:20|unique:tahun_ajarans,tahun',
            'flag' => 'nullable|boolean',
        ]);
        
        $validated['flag'] = $request->has('flag') ? 1 : 0;
        
        TahunAjaran::create($validated);
        
        return redirect()->route('admin.tahun-ajarans')->with('success', 'Tahun Ajaran berhasil ditambahkan!');
    }
    
    public function editTahunAjaran(TahunAjaran $tahunAjaran)
    {
        return view('admin.tahun-ajarans.edit', compact('tahunAjaran'));
    }
    
    public function updateTahunAjaran(Request $request, TahunAjaran $tahunAjaran)
    {
        $validated = $request->validate([
            'tahun' => 'required|string|max:20|unique:tahun_ajarans,tahun,' . $tahunAjaran->id,
            'flag' => 'nullable|boolean',
        ]);
        
        $validated['flag'] = $request->has('flag') ? 1 : 0;
        
        $tahunAjaran->update($validated);
        
        return redirect()->route('admin.tahun-ajarans')->with('success', 'Tahun Ajaran berhasil diupdate!');
    }
    
    public function destroyTahunAjaran(TahunAjaran $tahunAjaran)
    {
        $tahunAjaran->delete();
        return redirect()->route('admin.tahun-ajarans')->with('success', 'Tahun Ajaran berhasil dihapus!');
    }
    
    public function exportTahunAjarans()
    {
        $tahunAjarans = TahunAjaran::orderBy('tahun', 'desc')->get();
        
        $headers = ['No', 'Tahun', 'Semester', 'Aktif'];
        $data = [];
        
        foreach ($tahunAjarans as $index => $ta) {
            $data[] = [
                $index + 1,
                $ta->tahun,
                $ta->semester,
                $ta->is_active ? 'Ya' : 'Tidak',
            ];
        }
        
        $columnWidths = ['A' => 8, 'B' => 15, 'C' => 15, 'D' => 10];
        $filename = 'Data_Tahun_Ajaran_' . now()->format('Y-m-d_His') . '.xlsx';
        
        $filepath = ExcelExporter::export($data, $headers, $filename, 'ED7D31', $columnWidths);
        
        return response()->download($filepath, $filename)->deleteFileAfterSend(true);
    }
    
    public function downloadTemplateTahunAjarans()
    {
        $headers = ['Tahun', 'Flag'];
        $instructions = [
            'Isi data sesuai dengan kolom yang tersedia',
            'Tahun: Tahun ajaran (wajib diisi, contoh: 2024/2025)',
            'Flag: Status aktif (1 untuk aktif, 0 untuk tidak aktif)',
            'Jangan mengubah nama kolom header',
            'Hapus baris contoh (baris 2) sebelum mengupload'
        ];
        $dropdowns = [
            1 => ['0', '1'] // Flag column
        ];
        
        $filename = 'Template_Import_TahunAjaran.xlsx';
        $filepath = ExcelImporter::generateTemplate($headers, $filename, $instructions, $dropdowns, 'ED7D31');
        
        return response()->download($filepath, $filename);
    }
    
    public function importTahunAjarans(Request $request)
    {
        $request->validate([
            'file' => 'required|file|mimes:xlsx,xls,csv|max:10240'
        ]);
        
        $file = $request->file('file');
        $result = ExcelImporter::read($file->getPathname());
        
        if (!$result['success']) {
            return redirect()->back()->with('error', $result['message']);
        }
        
        $data = $result['data'];
        
        // Validate data
        $validation = ExcelImporter::validate($data, [
            0 => ['name' => 'Tahun', 'required' => true, 'max_length' => 20, 'unique' => function($value) {
                return TahunAjaran::where('tahun', $value)->exists();
            }],
            1 => ['name' => 'Flag', 'required' => true, 'in' => ['0', '1']]
        ]);
        
        if (!$validation['valid']) {
            return redirect()->back()->with('error', 'Validasi gagal:<br>' . implode('<br>', $validation['errors']));
        }
        
        // Import data
        $imported = 0;
        foreach ($data as $row) {
            TahunAjaran::create([
                'tahun' => $row[0],
                'flag' => (int)$row[1],
            ]);
            $imported++;
        }
        
        return redirect()->route('admin.tahun-ajarans')->with('success', "Berhasil import {$imported} data tahun ajaran!");
    }
    
    // ========== JADWAL MANAGEMENT ==========
    public function jadwals()
    {
        $jadwals = Jadwal::with(['kelas', 'mapel', 'guru', 'tahunAjaran'])->latest()->paginate(15);
        return view('admin.jadwals.index', compact('jadwals'));
    }
    
    public function createJadwal()
    {
        $kelas = Kelas::all();
        $mapels = Mapel::all();
        $gurus = Guru::all();
        $tahunAjarans = TahunAjaran::all();
        return view('admin.jadwals.create', compact('kelas', 'mapels', 'gurus', 'tahunAjarans'));
    }
    
    public function storeJadwal(Request $request)
    {
        $validated = $request->validate([
            'kelas_id' => 'required|exists:kelas,id',
            'mapel_id' => 'required|exists:mapels,id',
            'guru_id' => 'required|exists:gurus,id',
            'tahun_ajaran_id' => 'required|exists:tahun_ajarans,id',
            'hari' => 'required|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
            'jam_ke' => 'required|string',
        ]);
        
        Jadwal::create($validated);
        
        return redirect()->route('admin.jadwals')->with('success', 'Jadwal berhasil ditambahkan!');
    }
    
    public function editJadwal(Jadwal $jadwal)
    {
        $kelas = Kelas::all();
        $mapels = Mapel::all();
        $gurus = Guru::all();
        $tahunAjarans = TahunAjaran::all();
        return view('admin.jadwals.edit', compact('jadwal', 'kelas', 'mapels', 'gurus', 'tahunAjarans'));
    }
    
    public function updateJadwal(Request $request, Jadwal $jadwal)
    {
        $validated = $request->validate([
            'kelas_id' => 'required|exists:kelas,id',
            'mapel_id' => 'required|exists:mapels,id',
            'guru_id' => 'required|exists:gurus,id',
            'tahun_ajaran_id' => 'required|exists:tahun_ajarans,id',
            'hari' => 'required|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
            'jam_ke' => 'required|string',
        ]);
        
        $jadwal->update($validated);
        
        return redirect()->route('admin.jadwals')->with('success', 'Jadwal berhasil diupdate!');
    }
    
    public function destroyJadwal(Jadwal $jadwal)
    {
        $jadwal->delete();
        return redirect()->route('admin.jadwals')->with('success', 'Jadwal berhasil dihapus!');
    }
    
    public function exportJadwals()
    {
        $jadwals = Jadwal::with(['kelas', 'mapel'])->orderBy('hari')->orderBy('jam_ke')->get();
        
        $headers = ['No', 'Hari', 'Jam Ke', 'Kelas', 'Mata Pelajaran'];
        $data = [];
        
        foreach ($jadwals as $index => $jadwal) {
            $data[] = [
                $index + 1,
                $jadwal->hari,
                $jadwal->jam_ke,
                $jadwal->kelas->nama_kelas ?? '-',
                $jadwal->mapel->nama_mapel ?? '-',
            ];
        }
        
        $columnWidths = ['A' => 8, 'B' => 12, 'C' => 12, 'D' => 12, 'E' => 20, 'F' => 30];
        $filename = 'Data_Jadwal_' . now()->format('Y-m-d_His') . '.xlsx';
        
        $filepath = ExcelExporter::export($data, $headers, $filename, '7030A0', $columnWidths);
        
        return response()->download($filepath, $filename)->deleteFileAfterSend(true);
    }
    
    public function downloadTemplateJadwals()
    {
        $headers = ['Kode Guru', 'Kode Mapel', 'Tahun Ajaran', 'Nama Kelas', 'Hari', 'Jam Ke'];
        $instructions = [
            'Isi data sesuai dengan kolom yang tersedia',
            'Kode Guru: Kode guru yang sudah terdaftar (wajib diisi)',
            'Kode Mapel: Kode mata pelajaran yang sudah terdaftar (wajib diisi)',
            'Tahun Ajaran: Tahun ajaran yang sudah terdaftar (wajib diisi, contoh: 2024/2025)',
            'Nama Kelas: Nama kelas yang sudah terdaftar (wajib diisi)',
            'Hari: Pilih dari dropdown (Senin, Selasa, Rabu, Kamis, Jumat, Sabtu)',
            'Jam Ke: Jam pelajaran (wajib diisi, contoh: 1, 2, 3)',
            'Pastikan data guru, mapel, tahun ajaran, dan kelas sudah ada di database',
            'Jangan mengubah nama kolom header',
            'Hapus baris contoh (baris 2) sebelum mengupload'
        ];
        $dropdowns = [
            4 => ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'] // Hari column
        ];
        
        $filename = 'Template_Import_Jadwal.xlsx';
        $filepath = ExcelImporter::generateTemplate($headers, $filename, $instructions, $dropdowns, '7030A0');
        
        return response()->download($filepath, $filename);
    }
    
    public function importJadwals(Request $request)
    {
        $request->validate([
            'file' => 'required|file|mimes:xlsx,xls,csv|max:10240'
        ]);
        
        $file = $request->file('file');
        $result = ExcelImporter::read($file->getPathname());
        
        if (!$result['success']) {
            return redirect()->back()->with('error', $result['message']);
        }
        
        $data = $result['data'];
        
        // Validate data
        $validation = ExcelImporter::validate($data, [
            0 => ['name' => 'Kode Guru', 'required' => true, 'exists' => function($value) {
                return Guru::where('kode_guru', $value)->exists();
            }],
            1 => ['name' => 'Kode Mapel', 'required' => true, 'exists' => function($value) {
                return Mapel::where('kode_mapel', $value)->exists();
            }],
            2 => ['name' => 'Tahun Ajaran', 'required' => true, 'exists' => function($value) {
                return TahunAjaran::where('tahun', $value)->exists();
            }],
            3 => ['name' => 'Nama Kelas', 'required' => true, 'exists' => function($value) {
                return Kelas::where('nama_kelas', $value)->exists();
            }],
            4 => ['name' => 'Hari', 'required' => true, 'in' => ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu']],
            5 => ['name' => 'Jam Ke', 'required' => true]
        ]);
        
        if (!$validation['valid']) {
            return redirect()->back()->with('error', 'Validasi gagal:<br>' . implode('<br>', $validation['errors']));
        }
        
        // Import data
        $imported = 0;
        foreach ($data as $row) {
            // Get IDs from codes/names
            $guru = Guru::where('kode_guru', $row[0])->first();
            $mapel = Mapel::where('kode_mapel', $row[1])->first();
            $tahunAjaran = TahunAjaran::where('tahun', $row[2])->first();
            $kelas = Kelas::where('nama_kelas', $row[3])->first();
            
            Jadwal::create([
                'guru_id' => $guru->id,
                'mapel_id' => $mapel->id,
                'tahun_ajaran_id' => $tahunAjaran->id,
                'kelas_id' => $kelas->id,
                'hari' => $row[4],
                'jam_ke' => $row[5],
            ]);
            $imported++;
        }
        
        return redirect()->route('admin.jadwals')->with('success', "Berhasil import {$imported} data jadwal!");
    }
    
    // ========== GURU MENGAJAR MANAGEMENT ==========
    public function guruMengajars()
    {
        $guruMengajars = GuruMengajar::with([
            'jadwal.kelas', 
            'jadwal.mapel', 
            'jadwal.guru',
            'guruPengganti.guruPengganti',
            'guruPengganti.guruAsli'
        ])
        ->latest()->paginate(15);
        return view('admin.guru-mengajars.index', compact('guruMengajars'));
    }
    
    public function createGuruMengajar()
    {
        $jadwals = Jadwal::with(['kelas', 'mapel', 'guru'])->get();
        return view('admin.guru-mengajars.create', compact('jadwals'));
    }
    
    public function storeGuruMengajar(Request $request)
    {
        $validated = $request->validate([
            'jadwal_id' => 'required|exists:jadwals,id',
            'status' => 'required|in:masuk,tidak_masuk',
            'keterangan' => 'nullable|string',
        ]);
        
        GuruMengajar::create($validated);
        
        return redirect()->route('admin.guru-mengajars')->with('success', 'Data Guru Mengajar berhasil ditambahkan!');
    }
    
    public function editGuruMengajar(GuruMengajar $guruMengajar)
    {
        $jadwals = Jadwal::with(['kelas', 'mapel', 'guru'])->get();
        return view('admin.guru-mengajars.edit', compact('guruMengajar', 'jadwals'));
    }
    
    public function updateGuruMengajar(Request $request, GuruMengajar $guruMengajar)
    {
        $validated = $request->validate([
            'jadwal_id' => 'required|exists:jadwals,id',
            'status' => 'required|in:masuk,tidak_masuk',
            'keterangan' => 'nullable|string',
        ]);
        
        $guruMengajar->update($validated);
        
        return redirect()->route('admin.guru-mengajars')->with('success', 'Data Guru Mengajar berhasil diupdate!');
    }
    
    public function destroyGuruMengajar(GuruMengajar $guruMengajar)
    {
        $guruMengajar->delete();
        return redirect()->route('admin.guru-mengajars')->with('success', 'Data Guru Mengajar berhasil dihapus!');
    }
    
    public function exportGuruMengajars()
    {
        $guruMengajars = GuruMengajar::with(['jadwal.kelas', 'jadwal.mapel', 'guru', 'tahunAjaran'])->get();
        
        $headers = ['No', 'Guru', 'Hari', 'Jam', 'Kelas', 'Mata Pelajaran', 'Tahun Ajaran'];
        $data = [];
        
        foreach ($guruMengajars as $index => $gm) {
            $data[] = [
                $index + 1,
                $gm->guru->nama_guru ?? '-',
                $gm->jadwal->hari ?? '-',
                ($gm->jadwal->jam_mulai ?? '-') . ' - ' . ($gm->jadwal->jam_selesai ?? '-'),
                $gm->jadwal->kelas->nama_kelas ?? '-',
                $gm->jadwal->mapel->nama_mapel ?? '-',
                ($gm->tahunAjaran->tahun ?? '-') . ' - ' . ($gm->tahunAjaran->semester ?? '-'),
            ];
        }
        
        $columnWidths = ['A' => 8, 'B' => 25, 'C' => 12, 'D' => 15, 'E' => 20, 'F' => 30, 'G' => 20];
        $filename = 'Data_Guru_Mengajar_' . now()->format('Y-m-d_His') . '.xlsx';
        
        $filepath = ExcelExporter::export($data, $headers, $filename, 'C00000', $columnWidths);
        
        return response()->download($filepath, $filename)->deleteFileAfterSend(true);
    }
    
    public function downloadTemplateGuruMengajars()
    {
        $headers = ['Kode Guru', 'Nama Kelas', 'Kode Mapel', 'Hari', 'Jam Ke', 'Status', 'Keterangan'];
        $instructions = [
            'Isi data sesuai dengan kolom yang tersedia',
            'Kode Guru: Kode guru yang sudah terdaftar (wajib diisi)',
            'Nama Kelas: Nama kelas yang sudah terdaftar (wajib diisi)',
            'Kode Mapel: Kode mata pelajaran yang sudah terdaftar (wajib diisi)',
            'Hari: Pilih dari dropdown (Senin, Selasa, Rabu, Kamis, Jumat, Sabtu)',
            'Jam Ke: Jam pelajaran (wajib diisi, contoh: 1, 2, 3)',
            'Status: Pilih dari dropdown (masuk atau tidak_masuk)',
            'Keterangan: Keterangan tambahan (opsional)',
            'Pastikan jadwal dengan kombinasi guru, kelas, mapel, hari, dan jam sudah ada di database',
            'Jangan mengubah nama kolom header',
            'Hapus baris contoh (baris 2) sebelum mengupload'
        ];
        $dropdowns = [
            3 => ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'], // Hari column
            5 => ['masuk', 'tidak_masuk'] // Status column
        ];
        
        $filename = 'Template_Import_GuruMengajar.xlsx';
        $filepath = ExcelImporter::generateTemplate($headers, $filename, $instructions, $dropdowns, 'C00000');
        
        return response()->download($filepath, $filename);
    }
    
    public function importGuruMengajars(Request $request)
    {
        $request->validate([
            'file' => 'required|file|mimes:xlsx,xls,csv|max:10240'
        ]);
        
        $file = $request->file('file');
        $result = ExcelImporter::read($file->getPathname());
        
        if (!$result['success']) {
            return redirect()->back()->with('error', $result['message']);
        }
        
        $data = $result['data'];
        
        // Validate data
        $validation = ExcelImporter::validate($data, [
            0 => ['name' => 'Kode Guru', 'required' => true, 'exists' => function($value) {
                return Guru::where('kode_guru', $value)->exists();
            }],
            1 => ['name' => 'Nama Kelas', 'required' => true, 'exists' => function($value) {
                return Kelas::where('nama_kelas', $value)->exists();
            }],
            2 => ['name' => 'Kode Mapel', 'required' => true, 'exists' => function($value) {
                return Mapel::where('kode_mapel', $value)->exists();
            }],
            3 => ['name' => 'Hari', 'required' => true, 'in' => ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu']],
            4 => ['name' => 'Jam Ke', 'required' => true],
            5 => ['name' => 'Status', 'required' => true, 'in' => ['masuk', 'tidak_masuk']]
        ]);
        
        if (!$validation['valid']) {
            return redirect()->back()->with('error', 'Validasi gagal:<br>' . implode('<br>', $validation['errors']));
        }
        
        // Import data
        $imported = 0;
        $errors = [];
        
        foreach ($data as $index => $row) {
            $rowNumber = $index + 2;
            
            // Get IDs from codes/names
            $guru = Guru::where('kode_guru', $row[0])->first();
            $kelas = Kelas::where('nama_kelas', $row[1])->first();
            $mapel = Mapel::where('kode_mapel', $row[2])->first();
            
            // Find jadwal with matching criteria
            $jadwal = Jadwal::where('guru_id', $guru->id)
                ->where('kelas_id', $kelas->id)
                ->where('mapel_id', $mapel->id)
                ->where('hari', $row[3])
                ->where('jam_ke', $row[4])
                ->first();
            
            if (!$jadwal) {
                $errors[] = "Baris {$rowNumber}: Jadwal tidak ditemukan untuk kombinasi Guru '{$row[0]}', Kelas '{$row[1]}', Mapel '{$row[2]}', Hari '{$row[3]}', Jam '{$row[4]}'";
                continue;
            }
            
            GuruMengajar::create([
                'jadwal_id' => $jadwal->id,
                'status' => $row[5],
                'keterangan' => $row[6] ?? null,
            ]);
            $imported++;
        }
        
        if (!empty($errors)) {
            $errorMsg = "Import selesai dengan {$imported} data berhasil. Error:<br>" . implode('<br>', $errors);
            return redirect()->route('admin.guru-mengajars')->with('warning', $errorMsg);
        }
        
        return redirect()->route('admin.guru-mengajars')->with('success', "Berhasil import {$imported} data guru mengajar!");
    }

    // ========== GURU PENGGANTI MANAGEMENT ==========
    public function guruPenggantis()
    {
        $query = GuruPengganti::with(['guruMengajar.jadwal.kelas', 'guruMengajar.jadwal.mapel', 'guruMengajar.jadwal.guru', 'guruAsli', 'guruPenggantiGuru']);
        
        // Filter by status
        if (request('status')) {
            $query->where('status', request('status'));
        }
        
        // Filter by tanggal_mulai
        if (request('tanggal_mulai')) {
            $query->where('tanggal_mulai', '>=', request('tanggal_mulai'));
        }
        
        // Filter by tanggal_selesai
        if (request('tanggal_selesai')) {
            $query->where('tanggal_selesai', '<=', request('tanggal_selesai'));
        }
        
        $guruPenggantis = $query->latest()->paginate(20);
        
        return view('admin.guru-penggantis.index', compact('guruPenggantis'));
    }
    
    public function createGuruPengganti()
    {
        // Ambil hanya guru mengajar dengan status tidak_masuk
        $guruMengajars = GuruMengajar::with(['jadwal.guru', 'jadwal.kelas', 'jadwal.mapel'])
            ->where('status', 'tidak_masuk')
            ->get();
        
        $gurus = Guru::all();
        
        return view('admin.guru-penggantis.create', compact('guruMengajars', 'gurus'));
    }
    
    public function storeGuruPengganti(Request $request)
    {
        $request->validate([
            'guru_mengajar_id' => 'required|exists:guru_mengajars,id',
            'guru_asli_id' => 'required|exists:gurus,id',
            'guru_pengganti_id' => 'required|exists:gurus,id|different:guru_asli_id',
            'tanggal_mulai' => 'required|date',
            'tanggal_selesai' => 'required|date|after_or_equal:tanggal_mulai',
            'alasan' => 'nullable|string',
            'keterangan' => 'nullable|string',
            'status' => 'required|in:aktif,selesai,dibatalkan',
        ], [
            'guru_pengganti_id.different' => 'Guru pengganti harus berbeda dengan guru asli',
            'tanggal_selesai.after_or_equal' => 'Tanggal selesai harus sama atau setelah tanggal mulai',
        ]);
        
        GuruPengganti::create($request->all());
        
        return redirect()->route('admin.guru-penggantis')->with('success', 'Guru pengganti berhasil ditambahkan!');
    }
    
    public function editGuruPengganti(GuruPengganti $guruPengganti)
    {
        $guruMengajars = GuruMengajar::with(['jadwal.guru', 'jadwal.kelas', 'jadwal.mapel'])
            ->where('status', 'tidak_masuk')
            ->get();
        
        $gurus = Guru::all();
        
        return view('admin.guru-penggantis.edit', compact('guruPengganti', 'guruMengajars', 'gurus'));
    }
    
    public function updateGuruPengganti(Request $request, GuruPengganti $guruPengganti)
    {
        $request->validate([
            'guru_mengajar_id' => 'required|exists:guru_mengajars,id',
            'guru_asli_id' => 'required|exists:gurus,id',
            'guru_pengganti_id' => 'required|exists:gurus,id|different:guru_asli_id',
            'tanggal_mulai' => 'required|date',
            'tanggal_selesai' => 'required|date|after_or_equal:tanggal_mulai',
            'alasan' => 'nullable|string',
            'keterangan' => 'nullable|string',
            'status' => 'required|in:aktif,selesai,dibatalkan',
        ], [
            'guru_pengganti_id.different' => 'Guru pengganti harus berbeda dengan guru asli',
            'tanggal_selesai.after_or_equal' => 'Tanggal selesai harus sama atau setelah tanggal mulai',
        ]);
        
        $guruPengganti->update($request->all());
        
        return redirect()->route('admin.guru-penggantis')->with('success', 'Guru pengganti berhasil diupdate!');
    }
    
    public function destroyGuruPengganti(GuruPengganti $guruPengganti)
    {
        $guruPengganti->delete();
        
        return redirect()->route('admin.guru-penggantis')->with('success', 'Guru pengganti berhasil dihapus!');
    }
    
    public function exportGuruPenggantis()
    {
        $query = GuruPengganti::with(['guruMengajar.jadwal.kelas', 'guruMengajar.jadwal.mapel', 'guruMengajar.jadwal.guru', 'guruAsli', 'guruPenggantiGuru']);
        
        // Apply same filters as index
        if (request('status')) {
            $query->where('status', request('status'));
        }
        if (request('tanggal_mulai')) {
            $query->where('tanggal_mulai', '>=', request('tanggal_mulai'));
        }
        if (request('tanggal_selesai')) {
            $query->where('tanggal_selesai', '<=', request('tanggal_selesai'));
        }
        
        $guruPenggantis = $query->get();
        
        $headers = ['No', 'Guru Asli', 'Guru Pengganti', 'Kelas', 'Mapel', 'Hari', 'Tanggal Mulai', 'Tanggal Selesai', 'Durasi (Hari)', 'Alasan', 'Keterangan', 'Status'];
        
        $data = [];
        $no = 1;
        foreach ($guruPenggantis as $gp) {
            $durasi = Carbon::parse($gp->tanggal_mulai)->diffInDays(Carbon::parse($gp->tanggal_selesai)) + 1;
            $data[] = [
                $no++,
                $gp->guruAsli->nama_guru,
                $gp->guruPenggantiGuru->nama_guru,
                $gp->guruMengajar->jadwal->kelas->nama_kelas,
                $gp->guruMengajar->jadwal->mapel->nama_mapel,
                $gp->guruMengajar->jadwal->hari,
                Carbon::parse($gp->tanggal_mulai)->format('d-m-Y'),
                Carbon::parse($gp->tanggal_selesai)->format('d-m-Y'),
                $durasi,
                $gp->alasan,
                $gp->keterangan,
                ucfirst($gp->status),
            ];
        }
        
        $filename = 'Data_Guru_Pengganti_' . date('Y-m-d_His');
        
        return ExcelExporter::export($data, $headers, $filename);
    }
}
