@extends('layouts.app')
@section('title', 'Guru Pengganti')
@section('page-title', 'Manajemen Guru Pengganti')

@section('content')
<div class="mb-6">
    <!-- Header & Actions -->
    <div class="flex justify-between items-center mb-4">
        <div>
            <h3 class="text-lg font-semibold text-gray-800">Daftar Guru Pengganti</h3>
            <p class="text-sm text-gray-600">Total: {{ $guruPenggantis->total() }} data</p>
        </div>
        <div class="flex gap-2">
            <a href="{{ route('admin.guru-penggantis.export', request()->query()) }}" class="bg-green-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-green-700 transition shadow-lg">
                <i class="fas fa-file-excel mr-2"></i>Export Excel
            </a>
            <button onclick="document.getElementById('importModal').classList.remove('hidden')" class="bg-orange-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-orange-700 transition shadow-lg">
                <i class="fas fa-file-import mr-2"></i>Import Excel
            </button>
            <a href="{{ route('admin.guru-penggantis.create') }}" class="bg-blue-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-blue-700 transition shadow-lg">
                <i class="fas fa-plus mr-2"></i>Tambah Data
            </a>
        </div>
    </div>

    <!-- Advanced Filters -->
    <div class="bg-white rounded-xl shadow-lg p-6 mb-6">
        <div class="flex items-center justify-between mb-4">
            <h4 class="text-md font-semibold text-gray-700">
                <i class="fas fa-filter mr-2 text-blue-600"></i>Filter Pencarian
            </h4>
            <button onclick="toggleFilters()" class="text-sm text-blue-600 hover:text-blue-800">
                <i class="fas fa-chevron-down" id="filterIcon"></i>
            </button>
        </div>
        
        <form method="GET" action="{{ route('admin.guru-penggantis') }}" id="filterForm" class="hidden">
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">
                <!-- Search -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i class="fas fa-search mr-1"></i>Pencarian
                    </label>
                    <input type="text" name="search" value="{{ request('search') }}" placeholder="Cari nama guru, alasan..."
                        class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                </div>

                <!-- Status Filter -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i class="fas fa-info-circle mr-1"></i>Status
                    </label>
                    <select name="status" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                        <option value="">Semua Status</option>
                        <option value="aktif" {{ request('status') == 'aktif' ? 'selected' : '' }}>Aktif</option>
                        <option value="selesai" {{ request('status') == 'selesai' ? 'selected' : '' }}>Selesai</option>
                    </select>
                </div>

                <!-- Guru Asli Filter -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i class="fas fa-user-tie mr-1"></i>Guru Asli
                    </label>
                    <select name="guru_asli_id" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                        <option value="">Semua Guru Asli</option>
                        @foreach($gurus as $guru)
                            <option value="{{ $guru->id }}" {{ request('guru_asli_id') == $guru->id ? 'selected' : '' }}>
                                {{ $guru->nama_guru }}
                            </option>
                        @endforeach
                    </select>
                </div>

                <!-- Guru Pengganti Filter -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i class="fas fa-user-friends mr-1"></i>Guru Pengganti
                    </label>
                    <select name="guru_pengganti_id" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                        <option value="">Semua Guru Pengganti</option>
                        @foreach($gurus as $guru)
                            <option value="{{ $guru->id }}" {{ request('guru_pengganti_id') == $guru->id ? 'selected' : '' }}>
                                {{ $guru->nama_guru }}
                            </option>
                        @endforeach
                    </select>
                </div>

                <!-- Kelas Filter -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i class="fas fa-door-open mr-1"></i>Kelas
                    </label>
                    <select name="kelas_id" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                        <option value="">Semua Kelas</option>
                        @foreach($kelas as $k)
                            <option value="{{ $k->id }}" {{ request('kelas_id') == $k->id ? 'selected' : '' }}>
                                {{ $k->nama_kelas }}
                            </option>
                        @endforeach
                    </select>
                </div>

                <!-- Tanggal Mulai -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i class="fas fa-calendar-alt mr-1"></i>Tanggal Mulai (Dari)
                    </label>
                    <input type="date" name="tanggal_mulai" value="{{ request('tanggal_mulai') }}"
                        class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                </div>

                <!-- Tanggal Selesai -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i class="fas fa-calendar-check mr-1"></i>Tanggal Selesai (Sampai)
                    </label>
                    <input type="date" name="tanggal_selesai" value="{{ request('tanggal_selesai') }}"
                        class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                </div>
            </div>

            <div class="flex gap-3">
                <button type="submit" class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition">
                    <i class="fas fa-search mr-2"></i>Cari
                </button>
                <a href="{{ route('admin.guru-penggantis') }}" class="px-6 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition">
                    <i class="fas fa-redo mr-2"></i>Reset
                </a>
            </div>
        </form>
    </div>

    <!-- Statistics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-lg p-6 text-white">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm opacity-90">Total Pengganti</p>
                    <h3 class="text-3xl font-bold mt-1">{{ $guruPenggantis->total() }}</h3>
                </div>
                <div class="text-4xl opacity-80">
                    <i class="fas fa-users"></i>
                </div>
            </div>
        </div>

        <div class="bg-gradient-to-br from-green-500 to-green-600 rounded-xl shadow-lg p-6 text-white">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm opacity-90">Status Aktif</p>
                    <h3 class="text-3xl font-bold mt-1">{{ $guruPenggantis->where('status', 'aktif')->count() }}</h3>
                </div>
                <div class="text-4xl opacity-80">
                    <i class="fas fa-check-circle"></i>
                </div>
            </div>
        </div>

        <div class="bg-gradient-to-br from-gray-500 to-gray-600 rounded-xl shadow-lg p-6 text-white">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm opacity-90">Status Selesai</p>
                    <h3 class="text-3xl font-bold mt-1">{{ $guruPenggantis->where('status', 'selesai')->count() }}</h3>
                </div>
                <div class="text-4xl opacity-80">
                    <i class="fas fa-flag-checkered"></i>
                </div>
            </div>
        </div>

        <div class="bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl shadow-lg p-6 text-white">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm opacity-90">Hari Ini</p>
                    <h3 class="text-3xl font-bold mt-1">
                        {{ $guruPenggantis->filter(function($item) {
                            return $item->tanggal_mulai <= now() && $item->tanggal_selesai >= now() && $item->status == 'aktif';
                        })->count() }}
                    </h3>
                </div>
                <div class="text-4xl opacity-80">
                    <i class="fas fa-calendar-day"></i>
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
                        <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Guru Asli</th>
                        <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Guru Pengganti</th>
                        <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Detail Jadwal</th>
                        <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Durasi</th>
                        <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Alasan</th>
                        <th class="px-6 py-4 text-left text-xs font-bold text-gray-700 uppercase tracking-wider">Status</th>
                        <th class="px-6 py-4 text-center text-xs font-bold text-gray-700 uppercase tracking-wider">Aksi</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                    @forelse($guruPenggantis as $index => $gp)
                        @php
                            $durasi = ($gp->tanggal_mulai && $gp->tanggal_selesai) 
                                ? $gp->tanggal_mulai->diffInDays($gp->tanggal_selesai) + 1 
                                : 0;
                            $isActive = $gp->tanggal_mulai && $gp->tanggal_selesai && 
                                       $gp->tanggal_mulai <= now() && $gp->tanggal_selesai >= now() && $gp->status == 'aktif';
                        @endphp
                        <tr class="hover:bg-gray-50 transition {{ $isActive ? 'bg-green-50' : '' }}">
                            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                {{ $guruPenggantis->firstItem() + $index }}
                            </td>
                            <td class="px-6 py-4">
                                <div class="flex items-center">
                                    <div class="flex-shrink-0 h-10 w-10 bg-gradient-to-br from-red-400 to-red-600 rounded-full flex items-center justify-center text-white font-bold">
                                        {{ substr($gp->guruAsli->nama_guru, 0, 2) }}
                                    </div>
                                    <div class="ml-3">
                                        <p class="text-sm font-semibold text-gray-900">{{ $gp->guruAsli->nama_guru }}</p>
                                        <p class="text-xs text-gray-500">{{ $gp->guruAsli->kode_guru }}</p>
                                    </div>
                                </div>
                            </td>
                            <td class="px-6 py-4">
                                <div class="flex items-center">
                                    <div class="flex-shrink-0 h-10 w-10 bg-gradient-to-br from-blue-400 to-blue-600 rounded-full flex items-center justify-center text-white font-bold">
                                        {{ substr($gp->guruPengganti->nama_guru, 0, 2) }}
                                    </div>
                                    <div class="ml-3">
                                        <p class="text-sm font-semibold text-gray-900">{{ $gp->guruPengganti->nama_guru }}</p>
                                        <p class="text-xs text-gray-500">{{ $gp->guruPengganti->kode_guru }}</p>
                                    </div>
                                </div>
                            </td>
                            <td class="px-6 py-4">
                                <div class="text-sm">
                                    <p class="font-semibold text-gray-900">
                                        <i class="fas fa-book text-purple-600 mr-1"></i>{{ $gp->guruMengajar->jadwal->mapel->nama_mapel }}
                                    </p>
                                    <p class="text-gray-600">
                                        <i class="fas fa-door-open text-blue-600 mr-1"></i>{{ $gp->guruMengajar->jadwal->kelas->nama_kelas }}
                                    </p>
                                    <p class="text-gray-500 text-xs">
                                        <i class="fas fa-calendar text-green-600 mr-1"></i>{{ $gp->guruMengajar->jadwal->hari }} - 
                                        <i class="fas fa-clock text-orange-600 mr-1"></i>Jam {{ $gp->guruMengajar->jadwal->jam_ke }}
                                    </p>
                                </div>
                            </td>
                            <td class="px-6 py-4">
                                <div class="text-sm">
                                    <p class="font-medium text-gray-900">
                                        <i class="fas fa-calendar-alt text-blue-600 mr-1"></i>{{ $durasi }} Hari
                                    </p>
                                    @if($gp->tanggal_mulai && $gp->tanggal_selesai)
                                        <p class="text-xs text-gray-500">{{ $gp->tanggal_mulai->format('d M Y') }}</p>
                                        <p class="text-xs text-gray-500">s/d {{ $gp->tanggal_selesai->format('d M Y') }}</p>
                                    @else
                                        <p class="text-xs text-gray-500">Tanpa durasi tetap</p>
                                    @endif
                                </div>
                            </td>
                            <td class="px-6 py-4">
                                <div class="text-sm">
                                    <p class="font-medium text-gray-900">{{ $gp->alasan }}</p>
                                    @if($gp->keterangan)
                                        <p class="text-xs text-gray-500 mt-1">{{ Str::limit($gp->keterangan, 50) }}</p>
                                    @endif
                                </div>
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap">
                                <span class="px-3 py-1 inline-flex text-xs leading-5 font-semibold rounded-full {{ $gp->status == 'aktif' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800' }}">
                                    <i class="fas {{ $gp->status == 'aktif' ? 'fa-check-circle' : 'fa-flag-checkered' }} mr-1"></i>
                                    {{ ucfirst($gp->status) }}
                                </span>
                                @if($isActive)
                                    <span class="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800 ml-1">
                                        <i class="fas fa-clock mr-1"></i>Berlangsung
                                    </span>
                                @endif
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap text-center text-sm font-medium">
                                <div class="flex items-center justify-center gap-2">
                                    <button onclick="openDurasiModal({{ $gp->id }}, '{{ $gp->tanggal_mulai?->format('Y-m-d') ?? '' }}', '{{ $gp->tanggal_selesai?->format('Y-m-d') ?? '' }}')" 
                                       class="text-purple-600 hover:text-purple-900 px-3 py-2 inline-block" 
                                       title="Ubah Durasi">
                                        <i class="fas fa-calendar-alt"></i>
                                    </button>
                                    <a href="{{ route('admin.guru-penggantis.edit', $gp) }}" 
                                       class="text-blue-600 hover:text-blue-900 px-3 py-2 inline-block" 
                                       title="Edit">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                    <form action="{{ route('admin.guru-penggantis.destroy', $gp) }}" 
                                          method="POST" 
                                          class="inline" 
                                          onsubmit="return confirm('Yakin ingin menghapus data guru pengganti ini?')">
                                        @csrf
                                        @method('DELETE')
                                        <button type="submit" 
                                                class="text-red-600 hover:text-red-900 px-3 py-2" 
                                                title="Hapus">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    @empty
                        <tr>
                            <td colspan="8" class="px-6 py-12 text-center">
                                <div class="flex flex-col items-center justify-center">
                                    <i class="fas fa-inbox text-6xl text-gray-300 mb-4"></i>
                                    <p class="text-gray-500 text-lg">Belum ada data guru pengganti</p>
                                    <a href="{{ route('admin.guru-penggantis.create') }}" class="mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
                                        <i class="fas fa-plus mr-2"></i>Tambah Data Baru
                                    </a>
                                </div>
                            </td>
                        </tr>
                    @endforelse
                </tbody>
            </table>
        </div>

        @if($guruPenggantis->hasPages())
            <div class="bg-gray-50 px-6 py-4 border-t border-gray-200">
                {{ $guruPenggantis->links() }}
            </div>
        @endif
    </div>
</div>

<!-- Import Modal -->
<div id="importModal" class="hidden fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
    <div class="relative top-20 mx-auto p-8 border w-full max-w-2xl shadow-2xl rounded-2xl bg-white">
        <div class="flex justify-between items-center mb-6">
            <h3 class="text-2xl font-bold text-gray-800">
                <i class="fas fa-file-import text-orange-600 mr-2"></i>Import Data Guru Pengganti
            </h3>
            <button onclick="document.getElementById('importModal').classList.add('hidden')" 
                    class="text-gray-400 hover:text-gray-600 text-3xl font-bold">
                &times;
            </button>
        </div>

        <div class="mb-4 p-4 bg-yellow-50 border-l-4 border-yellow-500 rounded">
            <p class="text-sm text-yellow-800">
                <i class="fas fa-exclamation-triangle mr-2"></i>
                Pastikan data Guru Mengajar sudah ada sebelum import!
            </p>
        </div>

        <form action="{{ route('admin.guru-penggantis.import') }}" method="POST" enctype="multipart/form-data" class="space-y-6">
            @csrf
            <div>
                <a href="{{ route('admin.guru-penggantis.template') }}" 
                   class="inline-flex items-center px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition shadow-lg">
                    <i class="fas fa-file-excel mr-2"></i>Download Template Excel
                </a>
            </div>

            <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Upload File Excel</label>
                <input type="file" 
                       name="file" 
                       accept=".xlsx,.xls,.csv" 
                       required 
                       class="block w-full text-sm text-gray-900 border border-gray-300 rounded-lg cursor-pointer focus:outline-none focus:ring-2 focus:ring-blue-500 p-3">
                <p class="mt-1 text-sm text-gray-500">Format: .xlsx, .xls, .csv (Max 5MB)</p>
            </div>

            <div class="flex justify-end gap-3">
                <button type="button" 
                        onclick="document.getElementById('importModal').classList.add('hidden')" 
                        class="px-6 py-3 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition">
                    Batal
                </button>
                <button type="submit" 
                        class="px-6 py-3 bg-orange-600 text-white rounded-lg hover:bg-orange-700 transition">
                    <i class="fas fa-upload mr-2"></i>Import Sekarang
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Durasi Modal -->
<div id="durasiModal" class="hidden fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
    <div class="relative top-20 mx-auto p-8 border w-full max-w-2xl shadow-2xl rounded-2xl bg-white">
        <div class="flex justify-between items-center mb-6">
            <h3 class="text-2xl font-bold text-gray-800">
                <i class="fas fa-calendar-alt text-purple-600 mr-2"></i>Ubah Durasi Guru Pengganti
            </h3>
            <button onclick="closeDurasiModal()" 
                    class="text-gray-400 hover:text-gray-600 text-3xl font-bold">
                &times;
            </button>
        </div>

        <form id="durasiForm" method="POST" class="space-y-6">
            @csrf
            @method('PUT')
            
            <div class="mb-4 p-4 bg-blue-50 border-l-4 border-blue-500 rounded">
                <p class="text-sm text-blue-800">
                    <i class="fas fa-info-circle mr-2"></i>
                    Ubah tanggal mulai dan selesai untuk menyesuaikan durasi penggantian guru
                </p>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2">
                        <i class="fas fa-calendar-alt text-green-600 mr-1"></i>
                        Tanggal Mulai <span class="text-red-500">*</span>
                    </label>
                    <input type="date" 
                           id="durasi_tanggal_mulai"
                           name="tanggal_mulai" 
                           required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                </div>

                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2">
                        <i class="fas fa-calendar-check text-orange-600 mr-1"></i>
                        Tanggal Selesai <span class="text-red-500">*</span>
                    </label>
                    <input type="date" 
                           id="durasi_tanggal_selesai"
                           name="tanggal_selesai" 
                           required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                </div>
            </div>

            <div id="durasiInfo" class="hidden p-4 bg-purple-50 border-l-4 border-purple-500 rounded">
                <p class="text-sm text-purple-800">
                    <i class="fas fa-calendar-day mr-2"></i>
                    Durasi: <strong id="durasiDays">0 hari</strong>
                </p>
            </div>

            <div class="flex justify-end gap-3">
                <button type="button" 
                        onclick="closeDurasiModal()" 
                        class="px-6 py-3 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition">
                    Batal
                </button>
                <button type="submit" 
                        class="px-6 py-3 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition">
                    <i class="fas fa-save mr-2"></i>Simpan Perubahan
                </button>
            </div>
        </form>
    </div>
</div>

<script>
function openDurasiModal(id, tanggalMulai, tanggalSelesai) {
    const modal = document.getElementById('durasiModal');
    const form = document.getElementById('durasiForm');
    const inputMulai = document.getElementById('durasi_tanggal_mulai');
    const inputSelesai = document.getElementById('durasi_tanggal_selesai');
    
    // Set form action
    form.action = `/admin/guru-penggantis/${id}/update-durasi`;
    
    // Set current values
    inputMulai.value = tanggalMulai;
    inputSelesai.value = tanggalSelesai;
    
    // Calculate initial duration if dates exist
    if (tanggalMulai && tanggalSelesai) {
        calculateDuration();
    }
    
    modal.classList.remove('hidden');
}

function closeDurasiModal() {
    document.getElementById('durasiModal').classList.add('hidden');
    document.getElementById('durasiForm').reset();
    document.getElementById('durasiInfo').classList.add('hidden');
}

function calculateDuration() {
    const mulai = document.getElementById('durasi_tanggal_mulai').value;
    const selesai = document.getElementById('durasi_tanggal_selesai').value;
    
    if (mulai && selesai) {
        const date1 = new Date(mulai);
        const date2 = new Date(selesai);
        const diffTime = Math.abs(date2 - date1);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
        
        document.getElementById('durasiDays').textContent = diffDays + ' hari';
        document.getElementById('durasiInfo').classList.remove('hidden');
    }
}

// Add event listeners for date inputs
document.addEventListener('DOMContentLoaded', function() {
    const inputMulai = document.getElementById('durasi_tanggal_mulai');
    const inputSelesai = document.getElementById('durasi_tanggal_selesai');
    
    if (inputMulai && inputSelesai) {
        inputMulai.addEventListener('change', calculateDuration);
        inputSelesai.addEventListener('change', calculateDuration);
    }
});

function toggleFilters() {
    const form = document.getElementById('filterForm');
    const icon = document.getElementById('filterIcon');
    
    if (form.classList.contains('hidden')) {
        form.classList.remove('hidden');
        icon.classList.remove('fa-chevron-down');
        icon.classList.add('fa-chevron-up');
    } else {
        form.classList.add('hidden');
        icon.classList.remove('fa-chevron-up');
        icon.classList.add('fa-chevron-down');
    }
}

// Auto open filters if there are active filters
@if(request()->hasAny(['search', 'status', 'guru_asli_id', 'guru_pengganti_id', 'kelas_id', 'tanggal_mulai', 'tanggal_selesai']))
    document.addEventListener('DOMContentLoaded', function() {
        toggleFilters();
    });
@endif
</script>
@endsection
