@extends('layouts.app')
@section('title', 'Tambah Guru Pengganti')
@section('page-title', 'Tambah Data Guru Pengganti')

@section('content')
<div class="max-w-4xl mx-auto">
    <div class="bg-white rounded-xl shadow-lg overflow-hidden">
        <!-- Header -->
        <div class="bg-gradient-to-r from-blue-600 to-purple-600 px-8 py-6">
            <h3 class="text-2xl font-bold text-white">
                <i class="fas fa-user-plus mr-2"></i>Tambah Guru Pengganti Baru
            </h3>
            <p class="text-blue-100 text-sm mt-1">Isi form di bawah untuk menambahkan data guru pengganti</p>
        </div>

        <!-- Form -->
        <form action="{{ route('admin.guru-penggantis.store') }}" method="POST" class="p-8">
            @csrf

            <!-- Info Box -->
            <div class="mb-6 p-4 bg-blue-50 border-l-4 border-blue-500 rounded">
                <p class="text-sm text-blue-800">
                    <i class="fas fa-info-circle mr-2"></i>
                    Pilih dari daftar guru yang tidak masuk untuk diassign guru pengganti
                </p>
            </div>

            <!-- Guru Mengajar Selection -->
            <div class="mb-6">
                <label class="block text-sm font-bold text-gray-700 mb-2">
                    <i class="fas fa-chalkboard-teacher text-red-600 mr-1"></i>
                    Guru Yang Tidak Masuk <span class="text-red-500">*</span>
                </label>
                <select name="guru_mengajar_id" required 
                        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent @error('guru_mengajar_id') border-red-500 @enderror">
                    <option value="">-- Pilih Guru Yang Tidak Masuk --</option>
                    @forelse($guruMengajars as $gm)
                        <option value="{{ $gm->id }}" {{ (old('guru_mengajar_id', request('guru_mengajar_id')) == $gm->id) ? 'selected' : '' }}>
                            {{ $gm->jadwal->guru->nama_guru }} - 
                            {{ $gm->jadwal->mapel->nama_mapel }} - 
                            {{ $gm->jadwal->kelas->nama_kelas }} - 
                            {{ $gm->jadwal->hari }} (Jam {{ $gm->jadwal->jam_ke }})
                            - {{ ucfirst($gm->keterangan ?? $gm->status) }}
                        </option>
                    @empty
                        <option value="">Tidak ada guru yang tidak masuk tanpa pengganti aktif</option>
                    @endforelse
                </select>
                @error('guru_mengajar_id')
                    <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                @enderror
                <p class="mt-2 text-xs text-gray-500">
                    <i class="fas fa-lightbulb text-yellow-500 mr-1"></i>
                    Hanya menampilkan guru yang tidak masuk dan belum memiliki pengganti aktif
                </p>
            </div>

            <!-- Guru Pengganti Selection -->
            <div class="mb-6">
                <label class="block text-sm font-bold text-gray-700 mb-2">
                    <i class="fas fa-user-friends text-blue-600 mr-1"></i>
                    Guru Pengganti <span class="text-red-500">*</span>
                </label>
                <select name="guru_pengganti_id" required 
                        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent @error('guru_pengganti_id') border-red-500 @enderror">
                    <option value="">-- Pilih Guru Pengganti --</option>
                    @foreach($gurus as $guru)
                        <option value="{{ $guru->id }}" {{ old('guru_pengganti_id') == $guru->id ? 'selected' : '' }}>
                            {{ $guru->kode_guru }} - {{ $guru->nama_guru }}
                        </option>
                    @endforeach
                </select>
                @error('guru_pengganti_id')
                    <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                @enderror
            </div>

            <!-- Date Range -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2">
                        <i class="fas fa-calendar-alt text-green-600 mr-1"></i>
                        Tanggal Mulai <span class="text-red-500">*</span>
                    </label>
                    <input type="date" 
                           name="tanggal_mulai" 
                           value="{{ old('tanggal_mulai') }}" 
                           required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent @error('tanggal_mulai') border-red-500 @enderror">
                    @error('tanggal_mulai')
                        <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                    @enderror
                </div>

                <div>
                    <label class="block text-sm font-bold text-gray-700 mb-2">
                        <i class="fas fa-calendar-check text-orange-600 mr-1"></i>
                        Tanggal Selesai <span class="text-red-500">*</span>
                    </label>
                    <input type="date" 
                           name="tanggal_selesai" 
                           value="{{ old('tanggal_selesai') }}" 
                           required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent @error('tanggal_selesai') border-red-500 @enderror">
                    @error('tanggal_selesai')
                        <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                    @enderror
                </div>
            </div>

            <!-- Alasan -->
            <div class="mb-6">
                <label class="block text-sm font-bold text-gray-700 mb-2">
                    <i class="fas fa-exclamation-circle text-yellow-600 mr-1"></i>
                    Alasan <span class="text-red-500">*</span>
                </label>
                <input type="text" 
                       name="alasan" 
                       value="{{ old('alasan') }}" 
                       placeholder="Contoh: Sakit, Cuti, Dinas Luar, dll"
                       required
                       maxlength="255"
                       class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent @error('alasan') border-red-500 @enderror">
                @error('alasan')
                    <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                @enderror
            </div>

            <!-- Keterangan -->
            <div class="mb-6">
                <label class="block text-sm font-bold text-gray-700 mb-2">
                    <i class="fas fa-comment-alt text-purple-600 mr-1"></i>
                    Keterangan (Opsional)
                </label>
                <textarea name="keterangan" 
                          rows="4" 
                          placeholder="Detail keterangan tambahan..."
                          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent @error('keterangan') border-red-500 @enderror">{{ old('keterangan') }}</textarea>
                @error('keterangan')
                    <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                @enderror
            </div>

            <!-- Status -->
            <div class="mb-6">
                <label class="block text-sm font-bold text-gray-700 mb-2">
                    <i class="fas fa-info-circle text-blue-600 mr-1"></i>
                    Status <span class="text-red-500">*</span>
                </label>
                <select name="status" required 
                        class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent @error('status') border-red-500 @enderror">
                    <option value="aktif" {{ old('status', 'aktif') == 'aktif' ? 'selected' : '' }}>Aktif</option>
                    <option value="selesai" {{ old('status') == 'selesai' ? 'selected' : '' }}>Selesai</option>
                </select>
                @error('status')
                    <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                @enderror
            </div>

            <!-- Buttons -->
            <div class="flex justify-end gap-3 pt-6 border-t border-gray-200">
                <a href="{{ route('admin.guru-penggantis') }}" 
                   class="px-6 py-3 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition font-semibold">
                    <i class="fas fa-times mr-2"></i>Batal
                </a>
                <button type="submit" 
                        class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition font-semibold shadow-lg">
                    <i class="fas fa-save mr-2"></i>Simpan Data
                </button>
            </div>
        </form>
    </div>
</div>
@endsection
