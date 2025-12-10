@extends('layouts.app')
@section('title', 'Guru Mengajar')
@section('page-title', 'Manajemen Guru Mengajar')

@section('content')
<div class="mb-6">
    <!-- Header & Actions -->
    <div class="flex justify-between items-center mb-4">
        <div>
            <h3 class="text-lg font-semibold text-gray-800">Daftar Guru Mengajar</h3>
            <p class="text-sm text-gray-600">Total: {{ $guruMengajars->total() }} data</p>
        </div>
        <div class="flex gap-2">
            <a href="{{ route('admin.guru-mengajars.export') }}" class="bg-green-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-green-700 transition shadow-lg">
                <i class="fas fa-file-excel mr-2"></i>Export Excel
            </a>
            <button onclick="document.getElementById('importModal').classList.remove('hidden')" class="bg-orange-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-orange-700 transition shadow-lg">
                <i class="fas fa-file-import mr-2"></i>Import Excel
            </button>
            <a href="{{ route('admin.guru-mengajars.create') }}" class="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 transition shadow-lg">
                <i class="fas fa-plus mr-2"></i>Tambah Data
            </a>
        </div>
    </div>

    <!-- Statistics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-lg p-6 text-white">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm opacity-90">Total Mengajar</p>
                    <h3 class="text-3xl font-bold mt-1">{{ $guruMengajars->total() }}</h3>
                </div>
                <div class="text-4xl opacity-80">
                    <i class="fas fa-chalkboard-teacher"></i>
                </div>
            </div>
        </div>

        <div class="bg-gradient-to-br from-green-500 to-green-600 rounded-xl shadow-lg p-6 text-white">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm opacity-90">Guru Masuk</p>
                    <h3 class="text-3xl font-bold mt-1">{{ $guruMengajars->where('status', 'masuk')->count() }}</h3>
                </div>
                <div class="text-4xl opacity-80">
                    <i class="fas fa-check-circle"></i>
                </div>
            </div>
        </div>

        <div class="bg-gradient-to-br from-red-500 to-red-600 rounded-xl shadow-lg p-6 text-white">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm opacity-90">Tidak Masuk</p>
                    <h3 class="text-3xl font-bold mt-1">{{ $guruMengajars->where('status', 'tidak_masuk')->count() }}</h3>
                </div>
                <div class="text-4xl opacity-80">
                    <i class="fas fa-times-circle"></i>
                </div>
            </div>
        </div>

        <div class="bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl shadow-lg p-6 text-white">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm opacity-90">Ada Pengganti</p>
                    <h3 class="text-3xl font-bold mt-1">
                        {{ $guruMengajars->filter(function($gm) { return $gm->guruPengganti && $gm->guruPengganti->where('status', 'aktif')->count() > 0; })->count() }}
                    </h3>
                </div>
                <div class="text-4xl opacity-80">
                    <i class="fas fa-user-friends"></i>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Data Table -->
<div class="bg-white rounded-xl shadow-lg overflow-hidden">
    <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gradient-to-r from-gray-50 to-gray-100">
                <tr>
                    <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">#</th>
                    <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Jadwal</th>
                    <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Guru Asli</th>
                    <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Status</th>
                    <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Guru Pengganti</th>
                    <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Keterangan</th>
                    <th class="px-6 py-4 text-center text-xs font-bold text-gray-700 uppercase tracking-wider">Aksi</th>
                </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
                @forelse($guruMengajars as $index => $gm)
                    @php
                        $guruPenggantiAktif = $gm->guruPengganti ? $gm->guruPengganti->where('status', 'aktif')->first() : null;
                        $hasActivePengganti = $guruPenggantiAktif && 
                                             $guruPenggantiAktif->tanggal_mulai && 
                                             $guruPenggantiAktif->tanggal_selesai &&
                                             $guruPenggantiAktif->tanggal_mulai <= now() && 
                                             $guruPenggantiAktif->tanggal_selesai >= now();
                    @endphp
                    <tr class="hover:bg-gray-50 transition {{ $hasActivePengganti ? 'bg-purple-50' : '' }}">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                            {{ $guruMengajars->firstItem() + $index }}
                        </td>
                        <td class="px-6 py-4">
                            <div class="text-sm">
                                <p class="font-semibold text-gray-900">
                                    <i class="fas fa-book text-purple-600 mr-1"></i>{{ $gm->jadwal->mapel->nama_mapel }}
                                </p>
                                <p class="text-gray-600">
                                    <i class="fas fa-door-open text-blue-600 mr-1"></i>{{ $gm->jadwal->kelas->nama_kelas }}
                                </p>
                                <p class="text-xs text-gray-500">
                                    <i class="fas fa-calendar text-green-600 mr-1"></i>{{ $gm->jadwal->hari }} - 
                                    <i class="fas fa-clock text-orange-600 mr-1"></i>Jam {{ $gm->jadwal->jam_ke }}
                                </p>
                            </div>
                        </td>
                        <td class="px-6 py-4">
                            <div class="flex items-center">
                                <div class="flex-shrink-0 h-10 w-10 bg-gradient-to-br from-blue-400 to-blue-600 rounded-full flex items-center justify-center text-white font-bold">
                                    {{ substr($gm->jadwal->guru->nama_guru, 0, 2) }}
                                </div>
                                <div class="ml-3">
                                    <p class="text-sm font-semibold text-gray-900">{{ $gm->jadwal->guru->nama_guru }}</p>
                                    <p class="text-xs text-gray-500">{{ $gm->jadwal->guru->kode_guru }}</p>
                                </div>
                            </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <span class="px-3 py-1 inline-flex text-xs leading-5 font-semibold rounded-full {{ $gm->status == 'masuk' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800' }}">
                                <i class="fas {{ $gm->status == 'masuk' ? 'fa-check-circle' : 'fa-times-circle' }} mr-1"></i>
                                {{ ucfirst($gm->status) }}
                            </span>
                        </td>
                        <td class="px-6 py-4">
                            @if($guruPenggantiAktif)
                                <div class="flex items-center">
                                    <div class="flex-shrink-0 h-8 w-8 bg-gradient-to-br from-purple-400 to-purple-600 rounded-full flex items-center justify-center text-white text-xs font-bold">
                                        {{ substr($guruPenggantiAktif->guruPengganti->nama_guru, 0, 2) }}
                                    </div>
                                    <div class="ml-2">
                                        <p class="text-sm font-semibold text-gray-900">{{ $guruPenggantiAktif->guruPengganti->nama_guru }}</p>
                                        <p class="text-xs text-gray-500">
                                            @if($guruPenggantiAktif->tanggal_mulai && $guruPenggantiAktif->tanggal_selesai)
                                                {{ $guruPenggantiAktif->tanggal_mulai->format('d/m') }} - {{ $guruPenggantiAktif->tanggal_selesai->format('d/m') }}
                                            @else
                                                Tanpa durasi tetap
                                            @endif
                                        </p>
                                    </div>
                                </div>
                                @if($hasActivePengganti)
                                    <span class="mt-1 px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-800">
                                        <i class="fas fa-clock mr-1"></i>Sedang Mengganti
                                    </span>
                                @endif
                            @else
                                @if($gm->status == 'tidak_masuk')
                                    <a href="{{ route('admin.guru-penggantis.create') }}?guru_mengajar_id={{ $gm->id }}" 
                                       class="text-xs text-blue-600 hover:text-blue-800 font-semibold">
                                        <i class="fas fa-plus-circle mr-1"></i>Assign Pengganti
                                    </a>
                                @else
                                    <span class="text-xs text-gray-400">-</span>
                                @endif
                            @endif
                        </td>
                        <td class="px-6 py-4 text-sm text-gray-600">
                            {{ $gm->keterangan ?? '-' }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-center text-sm font-medium">
                            <a href="{{ route('admin.guru-mengajars.edit', $gm) }}" 
                               class="text-blue-600 hover:text-blue-900 px-3 py-2 inline-block" 
                               title="Edit">
                                <i class="fas fa-edit"></i>
                            </a>
                            <form action="{{ route('admin.guru-mengajars.destroy', $gm) }}" 
                                  method="POST" 
                                  class="inline" 
                                  onsubmit="return confirm('Yakin ingin menghapus data ini?')">
                                @csrf
                                @method('DELETE')
                                <button type="submit" 
                                        class="text-red-600 hover:text-red-900 px-3 py-2" 
                                        title="Hapus">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </form>
                            @if($gm->status == 'tidak_masuk')
                                <a href="{{ route('admin.guru-penggantis.create') }}?guru_mengajar_id={{ $gm->id }}" 
                                   class="text-purple-600 hover:text-purple-900 px-3 py-2 inline-block" 
                                   title="Tambah Pengganti">
                                    <i class="fas fa-user-plus"></i>
                                </a>
                            @endif
                        </td>
                    </tr>
                @empty
                    <tr>
                        <td colspan="7" class="px-6 py-12 text-center">
                            <div class="flex flex-col items-center justify-center">
                                <i class="fas fa-inbox text-6xl text-gray-300 mb-4"></i>
                                <p class="text-gray-500 text-lg">Belum ada data guru mengajar</p>
                                <a href="{{ route('admin.guru-mengajars.create') }}" class="mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
                                    <i class="fas fa-plus mr-2"></i>Tambah Data Baru
                                </a>
                            </div>
                        </td>
                    </tr>
                @endforelse
            </tbody>
        </table>
    </div>
    @if($guruMengajars->hasPages())
        <div class="bg-gray-50 px-6 py-4 border-t border-gray-200">
            {{ $guruMengajars->links() }}
        </div>
    @endif
</div>

<!-- Import Modal -->
<div id="importModal" class="hidden fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
    <div class="relative top-20 mx-auto p-8 border w-full max-w-2xl shadow-2xl rounded-2xl bg-white">
        <div class="flex justify-between items-center mb-6">
            <h3 class="text-2xl font-bold text-gray-800"><i class="fas fa-file-import text-orange-600 mr-2"></i>Import Data Guru Mengajar</h3>
            <button onclick="document.getElementById('importModal').classList.add('hidden')" class="text-gray-400 hover:text-gray-600 text-3xl font-bold">&times;</button>
        </div>
        <div class="mb-4 p-4 bg-yellow-50 border-l-4 border-yellow-500 rounded">
            <p class="text-sm text-yellow-800"><i class="fas fa-exclamation-triangle mr-2"></i>Pastikan data Jadwal (Guru, Kelas, Mapel, Hari, Jam) sudah ada sebelum import!</p>
        </div>
        <form action="{{ route('admin.guru-mengajars.import') }}" method="POST" enctype="multipart/form-data" class="space-y-6">
            @csrf
            <div><a href="{{ route('admin.guru-mengajars.template') }}" class="inline-flex items-center px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition shadow-lg"><i class="fas fa-file-excel mr-2"></i>Download Template</a></div>
            <div><input type="file" name="file" accept=".xlsx,.xls,.csv" required class="block w-full text-sm border rounded-lg p-3"></div>
            <div class="flex justify-end gap-3">
                <button type="button" onclick="document.getElementById('importModal').classList.add('hidden')" class="px-6 py-3 bg-gray-300 rounded-lg hover:bg-gray-400">Batal</button>
                <button type="submit" class="px-6 py-3 bg-orange-600 text-white rounded-lg hover:bg-orange-700">Import</button>
            </div>
        </form>
    </div>
</div>
@endsection
