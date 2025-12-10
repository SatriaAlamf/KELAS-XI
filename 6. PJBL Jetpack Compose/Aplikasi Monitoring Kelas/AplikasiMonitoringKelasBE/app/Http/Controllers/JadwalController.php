<?php

namespace App\Http\Controllers;

use App\Models\Jadwal;
use App\Http\Requests\StoreJadwalRequest;
use App\Http\Requests\UpdateJadwalRequest;

class JadwalController extends Controller
{
    public function index()
    {
        $query = Jadwal::with(['guru', 'mapel', 'tahunAjaran', 'kelas']);
        
        // Filter berdasarkan parameter query
        if (request('kelas_id')) {
            $query->where('kelas_id', request('kelas_id'));
        }
        
        if (request('hari')) {
            $query->where('hari', request('hari'));
        }
        
        if (request('guru_id')) {
            $query->where('guru_id', request('guru_id'));
        }
        
        if (request('mapel_id')) {
            $query->where('mapel_id', request('mapel_id'));
        }
        
        if (request('jam_ke')) {
            $query->where('jam_ke', request('jam_ke'));
        }
        
        if (request('tahun_ajaran_id')) {
            $query->where('tahun_ajaran_id', request('tahun_ajaran_id'));
        }
        
        // Filter berdasarkan jurusan (dari nama kelas)
        if (request('jurusan')) {
            $jurusan = request('jurusan');
            $query->whereHas('kelas', function($q) use ($jurusan) {
                $q->where('nama_kelas', 'LIKE', '%' . $jurusan . '%');
            });
        }
        
        // Filter berdasarkan tingkat kelas (10, 11, 12)
        if (request('tingkat')) {
            $tingkat = request('tingkat');
            $query->whereHas('kelas', function($q) use ($tingkat) {
                $q->where('nama_kelas', 'LIKE', $tingkat . '%');
            });
        }
        
        // Sorting
        $sortBy = request('sort_by', 'hari');
        $sortOrder = request('sort_order', 'asc');
        $query->orderBy($sortBy, $sortOrder);
        
        $jadwals = $query->get();
        
        return response()->json([
            'success' => true,
            'data' => $jadwals,
            'filters_applied' => [
                'kelas_id' => request('kelas_id'),
                'hari' => request('hari'),
                'guru_id' => request('guru_id'),
                'mapel_id' => request('mapel_id'),
                'jam_ke' => request('jam_ke'),
                'tahun_ajaran_id' => request('tahun_ajaran_id'),
                'jurusan' => request('jurusan'),
                'tingkat' => request('tingkat')
            ]
        ], 200);
    }

    public function store(StoreJadwalRequest $request)
    {
        $jadwal = Jadwal::create($request->validated());
        $jadwal->load(['guru', 'mapel', 'tahunAjaran', 'kelas']);
        return response()->json([
            'success' => true,
            'message' => 'Jadwal berhasil ditambahkan',
            'data' => $jadwal
        ], 201);
    }

    public function show(Jadwal $jadwal)
    {
        return response()->json([
            'success' => true,
            'data' => $jadwal->load(['guru', 'mapel', 'tahunAjaran', 'kelas', 'guruMengajars'])
        ], 200);
    }

    public function update(UpdateJadwalRequest $request, Jadwal $jadwal)
    {
        $jadwal->update($request->validated());
        $jadwal->load(['guru', 'mapel', 'tahunAjaran', 'kelas']);
        return response()->json([
            'success' => true,
            'message' => 'Jadwal berhasil diupdate',
            'data' => $jadwal
        ], 200);
    }

    public function destroy(Jadwal $jadwal)
    {
        $jadwal->delete();
        return response()->json([
            'success' => true,
            'message' => 'Jadwal berhasil dihapus'
        ], 200);
    }

    /**
     * Get jadwal by kelas_id and hari
     * Menampilkan data jadwal lengkap dengan relasi
     * 
     * @param int $kelasId
     * @param string $hari
     * @return \Illuminate\Http\JsonResponse
     */
    public function getByKelasAndHari($kelasId, $hari)
    {
        $jadwals = Jadwal::with(['guru', 'mapel', 'kelas', 'tahunAjaran'])
            ->where('kelas_id', $kelasId)
            ->where('hari', $hari)
            ->get();

        if ($jadwals->isEmpty()) {
            return response()->json([
                'success' => false,
                'message' => 'Tidak ada jadwal ditemukan untuk kelas dan hari tersebut'
            ], 404);
        }

        return response()->json([
            'success' => true,
            'data' => $jadwals
        ], 200);
    }

    /**
     * Get jadwal details by hari and kelas_id
     * Menampilkan: Nama Guru, Mapel, Tahun Ajaran, Jam Ke
     * 
     * @param string $hari
     * @param int $kelasId
     * @return \Illuminate\Http\JsonResponse
     */
    public function getDetailByHariAndKelas($hari, $kelasId)
    {
        $jadwals = Jadwal::with(['guru', 'mapel', 'tahunAjaran', 'kelas'])
            ->where('hari', $hari)
            ->where('kelas_id', $kelasId)
            ->get()
            ->map(function ($jadwal) {
                return [
                    'id' => $jadwal->id,
                    'nama_guru' => $jadwal->guru->nama_guru,
                    'mapel' => $jadwal->mapel->nama_mapel,
                    'tahun_ajaran' => $jadwal->tahunAjaran->tahun,
                    'jam_ke' => $jadwal->jam_ke,
                    'kelas' => $jadwal->kelas->nama_kelas,
                    'hari' => $jadwal->hari
                ];
            });

        if ($jadwals->isEmpty()) {
            return response()->json([
                'success' => false,
                'message' => 'Tidak ada jadwal ditemukan untuk hari dan kelas tersebut'
            ], 404);
        }

        return response()->json([
            'success' => true,
            'data' => $jadwals
        ], 200);
    }

    /**
     * CASCADE FILTER ENDPOINTS FOR GANTI GURU FEATURE
     * These endpoints support dynamic filtering based on previous selections
     */

    /**
     * Get distinct kelas by hari
     * Returns unique kelas for selected hari
     * 
     * @param string $hari
     * @return \Illuminate\Http\JsonResponse
     */
    public function getKelasByHari($hari)
    {
        $kelas = Jadwal::with('kelas')
            ->where('hari', $hari)
            ->get()
            ->pluck('kelas')
            ->unique('id')
            ->values()
            ->map(function ($kelas) {
                return [
                    'id' => $kelas->id,
                    'nama_kelas' => $kelas->nama_kelas
                ];
            });

        return response()->json([
            'success' => true,
            'message' => 'Kelas berhasil diambil',
            'data' => $kelas
        ], 200);
    }

    /**
     * Get distinct guru by hari and kelas_id
     * Returns unique guru for selected hari and kelas
     * 
     * @param string $hari
     * @param int $kelasId
     * @return \Illuminate\Http\JsonResponse
     */
    public function getGuruByHariAndKelas($hari, $kelasId)
    {
        $gurus = Jadwal::with('guru')
            ->where('hari', $hari)
            ->where('kelas_id', $kelasId)
            ->get()
            ->pluck('guru')
            ->unique('id')
            ->values()
            ->map(function ($guru) {
                return [
                    'id' => $guru->id,
                    'kode_guru' => $guru->kode_guru,
                    'nama_guru' => $guru->nama_guru
                ];
            });

        return response()->json([
            'success' => true,
            'message' => 'Guru berhasil diambil',
            'data' => $gurus
        ], 200);
    }

    /**
     * Get distinct mapel by hari, kelas_id, and guru_id
     * Returns unique mapel for selected hari, kelas, and guru
     * 
     * @param string $hari
     * @param int $kelasId
     * @param int $guruId
     * @return \Illuminate\Http\JsonResponse
     */
    public function getMapelByHariKelasGuru($hari, $kelasId, $guruId)
    {
        $mapels = Jadwal::with('mapel')
            ->where('hari', $hari)
            ->where('kelas_id', $kelasId)
            ->where('guru_id', $guruId)
            ->get()
            ->pluck('mapel')
            ->unique('id')
            ->values()
            ->map(function ($mapel) {
                return [
                    'id' => $mapel->id,
                    'kode_mapel' => $mapel->kode_mapel,
                    'nama_mapel' => $mapel->nama_mapel
                ];
            });

        return response()->json([
            'success' => true,
            'message' => 'Mapel berhasil diambil',
            'data' => $mapels
        ], 200);
    }

    /**
     * Get jadwal details by hari, kelas_id, guru_id, and mapel_id
     * Returns complete jadwal info including jam_ke
     * This is used to auto-fill jam_ke field
     * 
     * @param string $hari
     * @param int $kelasId
     * @param int $guruId
     * @param int $mapelId
     * @return \Illuminate\Http\JsonResponse
     */
    public function getJadwalDetails($hari, $kelasId, $guruId, $mapelId)
    {
        $jadwal = Jadwal::with(['guru', 'mapel', 'kelas', 'tahunAjaran'])
            ->where('hari', $hari)
            ->where('kelas_id', $kelasId)
            ->where('guru_id', $guruId)
            ->where('mapel_id', $mapelId)
            ->first();

        if (!$jadwal) {
            return response()->json([
                'success' => false,
                'message' => 'Jadwal tidak ditemukan'
            ], 404);
        }

        return response()->json([
            'success' => true,
            'message' => 'Jadwal berhasil diambil',
            'data' => [
                'id' => $jadwal->id,
                'jadwal_id' => $jadwal->id,
                'hari' => $jadwal->hari,
                'jam_ke' => $jadwal->jam_ke,
                'kelas_id' => $jadwal->kelas_id,
                'kelas' => $jadwal->kelas->nama_kelas,
                'guru_id' => $jadwal->guru_id,
                'guru' => $jadwal->guru->nama_guru,
                'mapel_id' => $jadwal->mapel_id,
                'mapel' => $jadwal->mapel->nama_mapel,
                'tahun_ajaran_id' => $jadwal->tahun_ajaran_id,
                'tahun_ajaran' => $jadwal->tahunAjaran->tahun
            ]
        ], 200);
    }

    /**
     * Get jadwal by jurusan
     * Returns jadwal filtered by jurusan (AK, DKV, MP, LPB, BD, RPL)
     * 
     * @param string $jurusan
     * @return \Illuminate\Http\JsonResponse
     */
    public function getByJurusan($jurusan)
    {
        $jadwals = Jadwal::with(['guru', 'mapel', 'tahunAjaran', 'kelas'])
            ->whereHas('kelas', function($q) use ($jurusan) {
                $q->where('nama_kelas', 'LIKE', '%' . $jurusan . '%');
            })
            ->orderBy('hari')
            ->orderBy('jam_ke')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $jadwals,
            'jurusan' => $jurusan,
            'total' => $jadwals->count()
        ], 200);
    }

    /**
     * Get jadwal by tingkat
     * Returns jadwal filtered by tingkat (10, 11, 12)
     * 
     * @param string $tingkat
     * @return \Illuminate\Http\JsonResponse
     */
    public function getByTingkat($tingkat)
    {
        $jadwals = Jadwal::with(['guru', 'mapel', 'tahunAjaran', 'kelas'])
            ->whereHas('kelas', function($q) use ($tingkat) {
                $q->where('nama_kelas', 'LIKE', $tingkat . '%');
            })
            ->orderBy('hari')
            ->orderBy('jam_ke')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $jadwals,
            'tingkat' => $tingkat,
            'total' => $jadwals->count()
        ], 200);
    }

    /**
     * Get jadwal with multiple filters
     * Advanced filtering for mobile and web applications
     * 
     * @return \Illuminate\Http\JsonResponse
     */
    public function filter()
    {
        $query = Jadwal::with(['guru', 'mapel', 'tahunAjaran', 'kelas']);
        
        // Filter berdasarkan kelas_id
        if (request('kelas_id')) {
            $query->where('kelas_id', request('kelas_id'));
        }
        
        // Filter berdasarkan hari
        if (request('hari')) {
            $query->where('hari', request('hari'));
        }
        
        // Filter berdasarkan guru_id
        if (request('guru_id')) {
            $query->where('guru_id', request('guru_id'));
        }
        
        // Filter berdasarkan mapel_id
        if (request('mapel_id')) {
            $query->where('mapel_id', request('mapel_id'));
        }
        
        // Filter berdasarkan jam_ke
        if (request('jam_ke')) {
            $query->where('jam_ke', request('jam_ke'));
        }
        
        // Filter berdasarkan tahun_ajaran_id
        if (request('tahun_ajaran_id')) {
            $query->where('tahun_ajaran_id', request('tahun_ajaran_id'));
        }
        
        // Filter berdasarkan jurusan
        if (request('jurusan')) {
            $jurusan = request('jurusan');
            $query->whereHas('kelas', function($q) use ($jurusan) {
                $q->where('nama_kelas', 'LIKE', '%' . $jurusan . '%');
            });
        }
        
        // Filter berdasarkan tingkat
        if (request('tingkat')) {
            $tingkat = request('tingkat');
            $query->whereHas('kelas', function($q) use ($tingkat) {
                $q->where('nama_kelas', 'LIKE', $tingkat . '%');
            });
        }
        
        // Sorting
        $sortBy = request('sort_by', 'hari');
        $sortOrder = request('sort_order', 'asc');
        
        if (in_array($sortBy, ['hari', 'jam_ke', 'created_at'])) {
            $query->orderBy($sortBy, $sortOrder);
        } else {
            $query->orderBy('hari', 'asc')->orderBy('jam_ke', 'asc');
        }
        
        $jadwals = $query->get();
        
        return response()->json([
            'success' => true,
            'data' => $jadwals,
            'total' => $jadwals->count(),
            'filters_applied' => [
                'kelas_id' => request('kelas_id'),
                'hari' => request('hari'),
                'guru_id' => request('guru_id'),
                'mapel_id' => request('mapel_id'),
                'jam_ke' => request('jam_ke'),
                'tahun_ajaran_id' => request('tahun_ajaran_id'),
                'jurusan' => request('jurusan'),
                'tingkat' => request('tingkat'),
                'sort_by' => $sortBy,
                'sort_order' => $sortOrder
            ]
        ], 200);
    }

    /**
     * Get available filter options
     * Returns all available options for filters (jurusan, tingkat, hari, etc.)
     * 
     * @return \Illuminate\Http\JsonResponse
     */
    public function getFilterOptions()
    {
        $kelas = \App\Models\Kelas::select('id', 'nama_kelas')->get();
        $guru = \App\Models\Guru::select('id', 'nama_guru')->get();
        $mapel = \App\Models\Mapel::select('id', 'nama_mapel')->get();
        $tahunAjaran = \App\Models\TahunAjaran::select('id', 'tahun')->get();
        
        // Extract unique jurusan dari nama kelas
        $jurusan = $kelas->map(function($k) {
            $parts = explode(' ', $k->nama_kelas);
            return isset($parts[1]) ? $parts[1] : null;
        })->filter()->unique()->values();
        
        // Extract unique tingkat dari nama kelas
        $tingkat = $kelas->map(function($k) {
            $parts = explode(' ', $k->nama_kelas);
            return isset($parts[0]) ? $parts[0] : null;
        })->filter()->unique()->values();
        
        $hari = ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu'];
        $jamKe = ['1-2', '3-4', '5-6', '7-8'];
        
        return response()->json([
            'success' => true,
            'data' => [
                'kelas' => $kelas,
                'guru' => $guru,
                'mapel' => $mapel,
                'tahun_ajaran' => $tahunAjaran,
                'jurusan' => $jurusan,
                'tingkat' => $tingkat,
                'hari' => $hari,
                'jam_ke' => $jamKe
            ]
        ], 200);
    }
}
