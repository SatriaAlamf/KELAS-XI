<?php

namespace App\Http\Controllers;

use App\Models\GuruMengajar;
use App\Models\Jadwal;
use App\Models\GuruPengganti;
use App\Http\Requests\StoreGuruMengajarRequest;
use App\Http\Requests\UpdateGuruMengajarRequest;
use Illuminate\Http\Request;

class GuruMengajarController extends Controller
{
    public function index()
    {
        $guruMengajars = GuruMengajar::with('jadwal.guru', 'jadwal.mapel', 'jadwal.kelas')->get();
        return response()->json([
            'success' => true,
            'data' => $guruMengajars
        ], 200);
    }

    public function store(StoreGuruMengajarRequest $request)
    {
        $guruMengajar = GuruMengajar::create($request->validated());
        $guruMengajar->load('jadwal.guru', 'jadwal.mapel', 'jadwal.kelas');
        return response()->json([
            'success' => true,
            'message' => 'Data Guru Mengajar berhasil ditambahkan',
            'data' => $guruMengajar
        ], 201);
    }

    public function show(GuruMengajar $guruMengajar)
    {
        return response()->json([
            'success' => true,
            'data' => $guruMengajar->load('jadwal.guru', 'jadwal.mapel', 'jadwal.kelas')
        ], 200);
    }

    public function update(UpdateGuruMengajarRequest $request, GuruMengajar $guruMengajar)
    {
        $guruMengajar->update($request->validated());
        $guruMengajar->load('jadwal.guru', 'jadwal.mapel', 'jadwal.kelas');
        return response()->json([
            'success' => true,
            'message' => 'Data Guru Mengajar berhasil diupdate',
            'data' => $guruMengajar
        ], 200);
    }

    public function destroy(GuruMengajar $guruMengajar)
    {
        $guruMengajar->delete();
        return response()->json([
            'success' => true,
            'message' => 'Data Guru Mengajar berhasil dihapus'
        ], 200);
    }

    /**
     * Store guru mengajar by finding jadwal first
     * Cari jadwal_id berdasarkan hari, kelas_id, guru_id, mapel_id, jam_ke
     * Lalu simpan status dan keterangan
     */
    public function storeByJadwalParams(Request $request)
    {
        $request->validate([
            'hari' => 'required|string|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
            'kelas_id' => 'required|exists:kelas,id',
            'guru_id' => 'required|exists:gurus,id',
            'mapel_id' => 'required|exists:mapels,id',
            'jam_ke' => 'required|string',
            'status' => 'required|in:masuk,tidak_masuk',
            'keterangan' => 'nullable|string'
        ]);

        // Cari jadwal berdasarkan parameter
        $jadwal = Jadwal::where('hari', $request->hari)
            ->where('kelas_id', $request->kelas_id)
            ->where('guru_id', $request->guru_id)
            ->where('mapel_id', $request->mapel_id)
            ->where('jam_ke', $request->jam_ke)
            ->first();

        if (!$jadwal) {
            return response()->json([
                'success' => false,
                'message' => 'Jadwal tidak ditemukan dengan parameter tersebut'
            ], 404);
        }

        // Buat data guru mengajar
        $guruMengajar = GuruMengajar::create([
            'jadwal_id' => $jadwal->id,
            'status' => $request->status,
            'keterangan' => $request->keterangan
        ]);

        $guruMengajar->load('jadwal.guru', 'jadwal.mapel', 'jadwal.kelas');

        return response()->json([
            'success' => true,
            'message' => 'Data Guru Mengajar berhasil ditambahkan',
            'data' => $guruMengajar
        ], 201);
    }

    /**
     * Update guru mengajar by finding jadwal first
     * Cari jadwal_id berdasarkan hari, kelas_id, guru_id, mapel_id, jam_ke
     * Lalu update status dan keterangan
     */
    public function updateByJadwalParams(Request $request)
    {
        $request->validate([
            'hari' => 'required|string|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
            'kelas_id' => 'required|exists:kelas,id',
            'guru_id' => 'required|exists:gurus,id',
            'mapel_id' => 'required|exists:mapels,id',
            'jam_ke' => 'required|string',
            'status' => 'required|in:masuk,tidak_masuk',
            'keterangan' => 'nullable|string'
        ]);

        // Cari jadwal berdasarkan parameter
        $jadwal = Jadwal::where('hari', $request->hari)
            ->where('kelas_id', $request->kelas_id)
            ->where('guru_id', $request->guru_id)
            ->where('mapel_id', $request->mapel_id)
            ->where('jam_ke', $request->jam_ke)
            ->first();

        if (!$jadwal) {
            return response()->json([
                'success' => false,
                'message' => 'Jadwal tidak ditemukan dengan parameter tersebut'
            ], 404);
        }

        // Cari atau buat data guru mengajar
        $guruMengajar = GuruMengajar::where('jadwal_id', $jadwal->id)->first();

        if ($guruMengajar) {
            // Update jika sudah ada
            $guruMengajar->update([
                'status' => $request->status,
                'keterangan' => $request->keterangan
            ]);
            $message = 'Data Guru Mengajar berhasil diupdate';
        } else {
            // Buat baru jika belum ada
            $guruMengajar = GuruMengajar::create([
                'jadwal_id' => $jadwal->id,
                'status' => $request->status,
                'keterangan' => $request->keterangan
            ]);
            $message = 'Data Guru Mengajar berhasil ditambahkan';
        }

        $guruMengajar->load('jadwal.guru', 'jadwal.mapel', 'jadwal.kelas');

        return response()->json([
            'success' => true,
            'message' => $message,
            'data' => $guruMengajar
        ], 200);
    }

    /**
     * Get guru mengajar by hari and kelas_id
     * Menampilkan: id, nama_guru, mapel, status, keterangan
     */
    public function getByHariAndKelas($hari, $kelasId)
    {
        $guruMengajars = GuruMengajar::whereHas('jadwal', function ($query) use ($hari, $kelasId) {
            $query->where('hari', $hari)
                  ->where('kelas_id', $kelasId);
        })
        ->with(['jadwal.guru', 'jadwal.mapel'])
        ->get()
        ->map(function ($item) {
            return [
                'id' => $item->id,
                'nama_guru' => $item->jadwal->guru->nama_guru,
                'mapel' => $item->jadwal->mapel->nama_mapel,
                'jam_ke' => $item->jadwal->jam_ke,
                'status' => $item->status,
                'keterangan' => $item->keterangan
            ];
        });

        if ($guruMengajars->isEmpty()) {
            return response()->json([
                'success' => false,
                'message' => 'Tidak ada data guru mengajar untuk hari dan kelas tersebut'
            ], 404);
        }

        return response()->json([
            'success' => true,
            'data' => $guruMengajars
        ], 200);
    }

    /**
     * Get guru yang tidak masuk by hari, kelas_id, and status = tidak_masuk
     * Menampilkan: nama_guru, mapel, jam_ke, status, keterangan
     */
    public function getGuruTidakMasuk($hari, $kelasId)
    {
        $guruTidakMasuk = GuruMengajar::whereHas('jadwal', function ($query) use ($hari, $kelasId) {
            $query->where('hari', $hari)
                  ->where('kelas_id', $kelasId);
        })
        ->where('status', 'tidak_masuk')
        ->with(['jadwal.guru', 'jadwal.mapel', 'jadwal.kelas'])
        ->get()
        ->map(function ($item) {
            return [
                'id' => $item->id,
                'nama_guru' => $item->jadwal->guru->nama_guru,
                'mapel' => $item->jadwal->mapel->nama_mapel,
                'jam_ke' => $item->jadwal->jam_ke,
                'status' => $item->status,
                'keterangan' => $item->keterangan
            ];
        });

        if ($guruTidakMasuk->isEmpty()) {
            return response()->json([
                'success' => false,
                'message' => 'Tidak ada guru yang tidak masuk untuk hari dan kelas tersebut'
            ], 404);
        }

        return response()->json([
            'success' => true,
            'data' => $guruTidakMasuk
        ], 200);
    }

    /**
     * POST method untuk get guru mengajar by hari and kelas_id
     * Alternative POST endpoint untuk mobile app compatibility
     * Request body: { "hari": "Senin", "kelas_id": 1 }
     * Response: id, nama_guru, mapel, status, keterangan, guru_pengganti info
     */
    public function getByHariKelasPost(Request $request)
    {
        $request->validate([
            'hari' => 'required|string|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu,Minggu',
            'kelas_id' => 'required|exists:kelas,id'
        ]);

        $guruMengajars = GuruMengajar::whereHas('jadwal', function ($query) use ($request) {
            $query->where('hari', $request->hari)
                  ->where('kelas_id', $request->kelas_id);
        })
        ->with([
            'jadwal.guru', 
            'jadwal.mapel',
            'guruPengganti' => function ($query) {
                $query->where('status', 'aktif')
                      ->with(['guruAsli', 'guruPengganti']);
            }
        ])
        ->get()
        ->map(function ($item) {
            $guruPenggantiData = null;
            
            // Check if there's an active guru pengganti
            if ($item->guruPengganti && $item->guruPengganti->count() > 0) {
                $pengganti = $item->guruPengganti->first();
                $guruPenggantiData = [
                    'id' => $pengganti->id,
                    'guru_mengajar_id' => $pengganti->guru_mengajar_id,
                    'guru_asli_id' => $pengganti->guru_asli_id,
                    'guru_pengganti_id' => $pengganti->guru_pengganti_id,
                    'tanggal_mulai' => $pengganti->tanggal_mulai?->format('Y-m-d'),
                    'tanggal_selesai' => $pengganti->tanggal_selesai?->format('Y-m-d'),
                    'alasan' => $pengganti->alasan,
                    'keterangan' => $pengganti->keterangan,
                    'status' => $pengganti->status,
                    'guru_asli' => [
                        'id' => $pengganti->guruAsli->id,
                        'nama_guru' => $pengganti->guruAsli->nama_guru
                    ],
                    'guru_pengganti' => [
                        'id' => $pengganti->guruPengganti->id,
                        'nama_guru' => $pengganti->guruPengganti->nama_guru
                    ]
                ];
            }
            
            return [
                'id' => $item->id,
                'jadwal_id' => $item->jadwal_id,
                'nama_guru' => $item->jadwal->guru->nama_guru,
                'mapel' => $item->jadwal->mapel->nama_mapel,
                'jam_ke' => $item->jadwal->jam_ke,
                'status' => $item->status,
                'keterangan' => $item->keterangan,
                'guru_pengganti' => $guruPenggantiData ? [$guruPenggantiData] : []
            ];
        });

        return response()->json([
            'success' => true,
            'message' => 'Data guru mengajar berhasil diambil',
            'data' => $guruMengajars
        ], 200);
    }

    /**
     * POST method untuk get guru tidak masuk by hari and kelas_id
     * Alternative POST endpoint untuk mobile app compatibility
     * Request body: { "hari": "Senin", "kelas_id": 1 }
     * Response: id, nama_guru, mapel, jam_ke, status, keterangan
     */
    public function getGuruTidakMasukPost(Request $request)
    {
        $request->validate([
            'hari' => 'required|string|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu,Minggu',
            'kelas_id' => 'required|exists:kelas,id'
        ]);

        return $this->getGuruTidakMasuk($request->hari, $request->kelas_id);
    }

    // =====================================================
    // GURU PENGGANTI METHODS
    // =====================================================

    /**
     * Get all guru pengganti dengan filter
     * Query params: status (aktif/selesai), guru_mengajar_id
     */
    public function indexGuruPengganti(Request $request)
    {
        $query = GuruPengganti::with([
            'guruMengajar.jadwal.kelas',
            'guruMengajar.jadwal.mapel',
            'guruAsli',
            'guruPengganti'
        ]);

        // Filter by status
        if ($request->has('status')) {
            $query->where('status', $request->status);
        }

        // Filter by guru_mengajar_id
        if ($request->has('guru_mengajar_id')) {
            $query->where('guru_mengajar_id', $request->guru_mengajar_id);
        }

        $guruPenggantis = $query->get()->map(function ($item) {
            return [
                'id' => $item->id,
                'guru_mengajar_id' => $item->guru_mengajar_id,
                'guru_asli' => [
                    'id' => $item->guruAsli->id,
                    'kode_guru' => $item->guruAsli->kode_guru,
                    'nama_guru' => $item->guruAsli->nama_guru,
                ],
                'guru_pengganti' => [
                    'id' => $item->guruPengganti->id,
                    'kode_guru' => $item->guruPengganti->kode_guru,
                    'nama_guru' => $item->guruPengganti->nama_guru,
                ],
                'kelas' => $item->guruMengajar->jadwal->kelas->nama_kelas,
                'mapel' => $item->guruMengajar->jadwal->mapel->nama_mapel,
                'hari' => $item->guruMengajar->jadwal->hari,
                'jam_ke' => $item->guruMengajar->jadwal->jam_ke,
                'tanggal_mulai' => $item->tanggal_mulai?->format('Y-m-d'),
                'tanggal_selesai' => $item->tanggal_selesai?->format('Y-m-d'),
                'durasi_hari' => $item->tanggal_mulai && $item->tanggal_selesai 
                    ? $item->tanggal_mulai->diffInDays($item->tanggal_selesai) + 1 
                    : null,
                'alasan' => $item->alasan,
                'keterangan' => $item->keterangan,
                'status' => $item->status,
                'created_at' => $item->created_at,
            ];
        });

        return response()->json([
            'success' => true,
            'message' => 'Data guru pengganti berhasil diambil',
            'data' => $guruPenggantis
        ], 200);
    }

    /**
     * Store guru pengganti baru
     * Request body: {
     *   "guru_mengajar_id": 1,
     *   "guru_pengganti_id": 2,
     *   "tanggal_mulai": "2025-01-10",
     *   "tanggal_selesai": "2025-01-15",
     *   "alasan": "Sakit",
     *   "keterangan": "Demam tinggi",
     *   "status": "aktif"
     * }
     */
    public function storeGuruPengganti(Request $request)
    {
        $request->validate([
            'guru_mengajar_id' => 'required|exists:guru_mengajars,id',
            'guru_pengganti_id' => 'required|exists:gurus,id',
            'tanggal_mulai' => 'nullable|date',
            'tanggal_selesai' => 'nullable|date|after_or_equal:tanggal_mulai',
            'alasan' => 'required|string|max:255',
            'keterangan' => 'nullable|string',
            'status' => 'required|in:aktif,selesai'
        ]);

        // Ambil guru_mengajar untuk mendapatkan guru_asli_id
        $guruMengajar = GuruMengajar::with('jadwal.guru')->findOrFail($request->guru_mengajar_id);

        // Validasi: guru pengganti tidak boleh sama dengan guru asli
        if ($guruMengajar->jadwal->guru_id == $request->guru_pengganti_id) {
            return response()->json([
                'success' => false,
                'message' => 'Guru pengganti tidak boleh sama dengan guru asli'
            ], 422);
        }

        $guruPengganti = GuruPengganti::create([
            'guru_mengajar_id' => $request->guru_mengajar_id,
            'guru_asli_id' => $guruMengajar->jadwal->guru_id,
            'guru_pengganti_id' => $request->guru_pengganti_id,
            'tanggal_mulai' => $request->tanggal_mulai,
            'tanggal_selesai' => $request->tanggal_selesai,
            'alasan' => $request->alasan,
            'keterangan' => $request->keterangan,
            'status' => $request->status
        ]);

        $guruPengganti->load(['guruAsli', 'guruPengganti', 'guruMengajar.jadwal.kelas', 'guruMengajar.jadwal.mapel']);

        return response()->json([
            'success' => true,
            'message' => 'Guru pengganti berhasil ditambahkan',
            'data' => [
                'id' => $guruPengganti->id,
                'guru_asli' => [
                    'id' => $guruPengganti->guruAsli->id,
                    'nama_guru' => $guruPengganti->guruAsli->nama_guru,
                ],
                'guru_pengganti' => [
                    'id' => $guruPengganti->guruPengganti->id,
                    'nama_guru' => $guruPengganti->guruPengganti->nama_guru,
                ],
                'kelas' => $guruPengganti->guruMengajar->jadwal->kelas->nama_kelas,
                'mapel' => $guruPengganti->guruMengajar->jadwal->mapel->nama_mapel,
                'tanggal_mulai' => $guruPengganti->tanggal_mulai?->format('Y-m-d'),
                'tanggal_selesai' => $guruPengganti->tanggal_selesai?->format('Y-m-d'),
                'durasi_hari' => $guruPengganti->tanggal_mulai && $guruPengganti->tanggal_selesai 
                    ? $guruPengganti->tanggal_mulai->diffInDays($guruPengganti->tanggal_selesai) + 1 
                    : null,
                'alasan' => $guruPengganti->alasan,
                'keterangan' => $guruPengganti->keterangan,
                'status' => $guruPengganti->status,
            ]
        ], 201);
    }

    /**
     * Show detail guru pengganti
     */
    public function showGuruPengganti($id)
    {
        $guruPengganti = GuruPengganti::with([
            'guruAsli',
            'guruPengganti',
            'guruMengajar.jadwal.kelas',
            'guruMengajar.jadwal.mapel'
        ])->find($id);

        if (!$guruPengganti) {
            return response()->json([
                'success' => false,
                'message' => 'Guru pengganti tidak ditemukan'
            ], 404);
        }

        return response()->json([
            'success' => true,
            'data' => [
                'id' => $guruPengganti->id,
                'guru_asli' => [
                    'id' => $guruPengganti->guruAsli->id,
                    'kode_guru' => $guruPengganti->guruAsli->kode_guru,
                    'nama_guru' => $guruPengganti->guruAsli->nama_guru,
                ],
                'guru_pengganti' => [
                    'id' => $guruPengganti->guruPengganti->id,
                    'kode_guru' => $guruPengganti->guruPengganti->kode_guru,
                    'nama_guru' => $guruPengganti->guruPengganti->nama_guru,
                ],
                'kelas' => $guruPengganti->guruMengajar->jadwal->kelas->nama_kelas,
                'mapel' => $guruPengganti->guruMengajar->jadwal->mapel->nama_mapel,
                'hari' => $guruPengganti->guruMengajar->jadwal->hari,
                'jam_ke' => $guruPengganti->guruMengajar->jadwal->jam_ke,
                'tanggal_mulai' => $guruPengganti->tanggal_mulai?->format('Y-m-d'),
                'tanggal_selesai' => $guruPengganti->tanggal_selesai?->format('Y-m-d'),
                'durasi_hari' => $guruPengganti->tanggal_mulai && $guruPengganti->tanggal_selesai 
                    ? $guruPengganti->tanggal_mulai->diffInDays($guruPengganti->tanggal_selesai) + 1 
                    : null,
                'alasan' => $guruPengganti->alasan,
                'keterangan' => $guruPengganti->keterangan,
                'status' => $guruPengganti->status,
                'created_at' => $guruPengganti->created_at,
                'updated_at' => $guruPengganti->updated_at,
            ]
        ], 200);
    }

    /**
     * Update guru pengganti
     */
    public function updateGuruPengganti(Request $request, $id)
    {
        $guruPengganti = GuruPengganti::find($id);

        if (!$guruPengganti) {
            return response()->json([
                'success' => false,
                'message' => 'Guru pengganti tidak ditemukan'
            ], 404);
        }

        $request->validate([
            'guru_pengganti_id' => 'sometimes|exists:gurus,id',
            'tanggal_mulai' => 'sometimes|date',
            'tanggal_selesai' => 'sometimes|date|after_or_equal:tanggal_mulai',
            'alasan' => 'sometimes|string|max:255',
            'keterangan' => 'nullable|string',
            'status' => 'sometimes|in:aktif,selesai'
        ]);

        // Validasi: guru pengganti tidak boleh sama dengan guru asli
        if ($request->has('guru_pengganti_id') && $guruPengganti->guru_asli_id == $request->guru_pengganti_id) {
            return response()->json([
                'success' => false,
                'message' => 'Guru pengganti tidak boleh sama dengan guru asli'
            ], 422);
        }

        $guruPengganti->update($request->all());
        $guruPengganti->load(['guruAsli', 'guruPengganti', 'guruMengajar.jadwal.kelas', 'guruMengajar.jadwal.mapel']);

        return response()->json([
            'success' => true,
            'message' => 'Guru pengganti berhasil diupdate',
            'data' => [
                'id' => $guruPengganti->id,
                'guru_asli' => [
                    'id' => $guruPengganti->guruAsli->id,
                    'nama_guru' => $guruPengganti->guruAsli->nama_guru,
                ],
                'guru_pengganti' => [
                    'id' => $guruPengganti->guruPengganti->id,
                    'nama_guru' => $guruPengganti->guruPengganti->nama_guru,
                ],
                'kelas' => $guruPengganti->guruMengajar->jadwal->kelas->nama_kelas,
                'mapel' => $guruPengganti->guruMengajar->jadwal->mapel->nama_mapel,
                'tanggal_mulai' => $guruPengganti->tanggal_mulai?->format('Y-m-d'),
                'tanggal_selesai' => $guruPengganti->tanggal_selesai?->format('Y-m-d'),
                'durasi_hari' => $guruPengganti->tanggal_mulai && $guruPengganti->tanggal_selesai 
                    ? $guruPengganti->tanggal_mulai->diffInDays($guruPengganti->tanggal_selesai) + 1 
                    : null,
                'alasan' => $guruPengganti->alasan,
                'keterangan' => $guruPengganti->keterangan,
                'status' => $guruPengganti->status,
            ]
        ], 200);
    }

    /**
     * Delete guru pengganti
     */
    public function destroyGuruPengganti($id)
    {
        $guruPengganti = GuruPengganti::find($id);

        if (!$guruPengganti) {
            return response()->json([
                'success' => false,
                'message' => 'Guru pengganti tidak ditemukan'
            ], 404);
        }

        $guruPengganti->delete();

        return response()->json([
            'success' => true,
            'message' => 'Guru pengganti berhasil dihapus'
        ], 200);
    }

    /**
     * Get guru pengganti by guru mengajar id
     */
    public function getGuruPenggantiByGuruMengajar($guruMengajarId)
    {
        $guruPenggantis = GuruPengganti::with([
            'guruAsli',
            'guruPengganti',
            'guruMengajar.jadwal.kelas',
            'guruMengajar.jadwal.mapel'
        ])
        ->where('guru_mengajar_id', $guruMengajarId)
        ->get()
        ->map(function ($item) {
            return [
                'id' => $item->id,
                'guru_asli' => [
                    'id' => $item->guruAsli->id,
                    'nama_guru' => $item->guruAsli->nama_guru,
                ],
                'guru_pengganti' => [
                    'id' => $item->guruPengganti->id,
                    'nama_guru' => $item->guruPengganti->nama_guru,
                ],
                'tanggal_mulai' => $item->tanggal_mulai?->format('Y-m-d'),
                'tanggal_selesai' => $item->tanggal_selesai?->format('Y-m-d'),
                'durasi_hari' => $item->tanggal_mulai && $item->tanggal_selesai 
                    ? $item->tanggal_mulai->diffInDays($item->tanggal_selesai) + 1 
                    : null,
                'alasan' => $item->alasan,
                'keterangan' => $item->keterangan,
                'status' => $item->status,
            ];
        });

        return response()->json([
            'success' => true,
            'data' => $guruPenggantis
        ], 200);
    }

    /**
     * Get guru pengganti aktif (status = aktif)
     */
    public function getGuruPenggantiAktif()
    {
        $guruPenggantis = GuruPengganti::with([
            'guruAsli',
            'guruPengganti',
            'guruMengajar.jadwal.kelas',
            'guruMengajar.jadwal.mapel'
        ])
        ->where('status', 'aktif')
        ->get()
        ->map(function ($item) {
            return [
                'id' => $item->id,
                'guru_asli' => [
                    'id' => $item->guruAsli->id,
                    'nama_guru' => $item->guruAsli->nama_guru,
                ],
                'guru_pengganti' => [
                    'id' => $item->guruPengganti->id,
                    'nama_guru' => $item->guruPengganti->nama_guru,
                ],
                'kelas' => $item->guruMengajar->jadwal->kelas->nama_kelas,
                'mapel' => $item->guruMengajar->jadwal->mapel->nama_mapel,
                'hari' => $item->guruMengajar->jadwal->hari,
                'jam_ke' => $item->guruMengajar->jadwal->jam_ke,
                'tanggal_mulai' => $item->tanggal_mulai?->format('Y-m-d'),
                'tanggal_selesai' => $item->tanggal_selesai?->format('Y-m-d'),
                'durasi_hari' => $item->tanggal_mulai && $item->tanggal_selesai 
                    ? $item->tanggal_mulai->diffInDays($item->tanggal_selesai) + 1 
                    : null,
                'alasan' => $item->alasan,
                'keterangan' => $item->keterangan,
                'status' => $item->status,
            ];
        });

        return response()->json([
            'success' => true,
            'message' => 'Data guru pengganti aktif berhasil diambil',
            'data' => $guruPenggantis
        ], 200);
    }

    /**
     * Get comprehensive list for KEPSEK with advanced filters
     * Request params:
     * - hari (optional): filter by hari
     * - kelas_id (optional): filter by kelas
     * - status_mengajar (optional): masuk/tidak_masuk
     * - status_pengganti (optional): aktif/selesai/semua/tanpa_pengganti
     * - durasi_min (optional): minimum durasi in days
     * - durasi_max (optional): maximum durasi in days
     * - tanggal_from (optional): filter pengganti start date from
     * - tanggal_to (optional): filter pengganti start date to
     * - has_pengganti (optional): true/false/all
     */
    public function getKepsekComprehensiveList(Request $request)
    {
        $request->validate([
            'hari' => 'nullable|string|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu,Minggu',
            'kelas_id' => 'nullable|exists:kelas,id',
            'status_mengajar' => 'nullable|in:masuk,tidak_masuk',
            'status_pengganti' => 'nullable|in:aktif,selesai,semua,tanpa_pengganti',
            'durasi_min' => 'nullable|integer|min:0',
            'durasi_max' => 'nullable|integer|min:0',
            'tanggal_from' => 'nullable|date',
            'tanggal_to' => 'nullable|date|after_or_equal:tanggal_from',
            'has_pengganti' => 'nullable|in:true,false,all'
        ]);

        $query = GuruMengajar::with([
            'jadwal.guru', 
            'jadwal.mapel',
            'jadwal.kelas',
            'guruPengganti' => function ($query) use ($request) {
                // Filter guru pengganti by status
                if ($request->filled('status_pengganti') && $request->status_pengganti !== 'semua' && $request->status_pengganti !== 'tanpa_pengganti') {
                    $query->where('status', $request->status_pengganti);
                }
                $query->with(['guruAsli', 'guruPengganti']);
            }
        ]);

        // Filter by hari
        if ($request->filled('hari')) {
            $query->whereHas('jadwal', function ($q) use ($request) {
                $q->where('hari', $request->hari);
            });
        }

        // Filter by kelas_id
        if ($request->filled('kelas_id')) {
            $query->whereHas('jadwal', function ($q) use ($request) {
                $q->where('kelas_id', $request->kelas_id);
            });
        }

        // Filter by status mengajar
        if ($request->filled('status_mengajar')) {
            $query->where('status', $request->status_mengajar);
        }

        // Filter by has_pengganti
        if ($request->filled('has_pengganti')) {
            if ($request->has_pengganti === 'true') {
                $query->has('guruPengganti');
            } elseif ($request->has_pengganti === 'false') {
                $query->doesntHave('guruPengganti');
            }
        }

        // Filter for tanpa_pengganti status
        if ($request->filled('status_pengganti') && $request->status_pengganti === 'tanpa_pengganti') {
            $query->doesntHave('guruPengganti');
        }

        $guruMengajars = $query->get()->map(function ($item) use ($request) {
            $guruPenggantiList = [];
            $hasActiveGuruPengganti = false;

            // Process guru pengganti
            if ($item->guruPengganti && $item->guruPengganti->count() > 0) {
                foreach ($item->guruPengganti as $pengganti) {
                    $durasi = $pengganti->tanggal_mulai && $pengganti->tanggal_selesai 
                        ? $pengganti->tanggal_mulai->diffInDays($pengganti->tanggal_selesai) + 1 
                        : 0;

                    // Apply durasi filter
                    $passedDurasiFilter = true;
                    if ($request->filled('durasi_min') && $durasi < $request->durasi_min) {
                        $passedDurasiFilter = false;
                    }
                    if ($request->filled('durasi_max') && $durasi > $request->durasi_max) {
                        $passedDurasiFilter = false;
                    }

                    // Apply tanggal filter
                    $passedTanggalFilter = true;
                    if ($request->filled('tanggal_from') && $pengganti->tanggal_mulai) {
                        if ($pengganti->tanggal_mulai < $request->tanggal_from) {
                            $passedTanggalFilter = false;
                        }
                    }
                    if ($request->filled('tanggal_to') && $pengganti->tanggal_mulai) {
                        if ($pengganti->tanggal_mulai > $request->tanggal_to) {
                            $passedTanggalFilter = false;
                        }
                    }

                    // Only add if passed all filters
                    if ($passedDurasiFilter && $passedTanggalFilter) {
                        $guruPenggantiList[] = [
                            'id' => $pengganti->id,
                            'guru_mengajar_id' => $pengganti->guru_mengajar_id,
                            'guru_asli_id' => $pengganti->guru_asli_id,
                            'guru_pengganti_id' => $pengganti->guru_pengganti_id,
                            'tanggal_mulai' => $pengganti->tanggal_mulai?->format('Y-m-d'),
                            'tanggal_selesai' => $pengganti->tanggal_selesai?->format('Y-m-d'),
                            'durasi_hari' => $durasi,
                            'alasan' => $pengganti->alasan,
                            'keterangan' => $pengganti->keterangan,
                            'status' => $pengganti->status,
                            'guru_asli' => [
                                'id' => $pengganti->guruAsli->id,
                                'nama_guru' => $pengganti->guruAsli->nama_guru
                            ],
                            'guru_pengganti' => [
                                'id' => $pengganti->guruPengganti->id,
                                'nama_guru' => $pengganti->guruPengganti->nama_guru
                            ]
                        ];

                        if ($pengganti->status === 'aktif') {
                            $hasActiveGuruPengganti = true;
                        }
                    }
                }
            }

            // Return null if filters eliminated all pengganti data and it's required
            if ($request->filled('durasi_min') || $request->filled('durasi_max') || $request->filled('tanggal_from') || $request->filled('tanggal_to')) {
                if ($item->guruPengganti->count() > 0 && count($guruPenggantiList) === 0) {
                    return null;
                }
            }

            return [
                'id' => $item->id,
                'jadwal_id' => $item->jadwal_id,
                'nama_guru' => $item->jadwal->guru->nama_guru,
                'kode_guru' => $item->jadwal->guru->kode_guru,
                'mapel' => $item->jadwal->mapel->nama_mapel,
                'kelas' => $item->jadwal->kelas->nama_kelas,
                'jurusan' => $item->jadwal->kelas->jurusan,
                'tingkat' => $item->jadwal->kelas->tingkat,
                'hari' => $item->jadwal->hari,
                'jam_ke' => $item->jadwal->jam_ke,
                'status' => $item->status,
                'keterangan' => $item->keterangan,
                'has_active_pengganti' => $hasActiveGuruPengganti,
                'jumlah_pengganti' => count($guruPenggantiList),
                'guru_pengganti' => $guruPenggantiList
            ];
        })->filter()->values(); // Remove nulls and reindex

        $summary = [
            'total_guru_mengajar' => $guruMengajars->count(),
            'total_masuk' => $guruMengajars->where('status', 'masuk')->count(),
            'total_tidak_masuk' => $guruMengajars->where('status', 'tidak_masuk')->count(),
            'total_dengan_pengganti' => $guruMengajars->where('jumlah_pengganti', '>', 0)->count(),
            'total_tanpa_pengganti' => $guruMengajars->where('jumlah_pengganti', 0)->count(),
            'total_pengganti_aktif' => $guruMengajars->where('has_active_pengganti', true)->count(),
        ];

        return response()->json([
            'success' => true,
            'message' => 'Data comprehensive guru mengajar berhasil diambil',
            'summary' => $summary,
            'data' => $guruMengajars
        ], 200);
    }
}
