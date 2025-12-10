@extends('layouts.app')
@section('title', 'Edit Guru Pengganti')
@section('page-title', 'Edit Data Guru Pengganti')

@section('content')
<div class="max-w-4xl mx-auto">
    <div class="bg-white rounded-xl shadow-lg overflow-hidden">
        <!-- Header -->
        <div class="bg-gradient-to-r from-orange-600 to-red-600 px-8 py-6">
            <h3 class="text-2xl font-bold text-white">
                <i class="fas fa-edit mr-2"></i>Edit Guru Pengganti
            </h3>
            <p class="text-orange-100 text-sm mt-1">Update informasi guru pengganti</p>
        </div>

        <!-- Current Info Box -->
        <div class="p-6 bg-blue-50 border-b border-blue-200">
            <h4 class="font-semibold text-gray-800 mb-3">
                <i class="fas fa-info-circle text-blue-600 mr-2"></i>Informasi Saat Ini
            </h4>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                <div>
                    <p class="text-gray-600">Guru Asli:</p>
                    <p class="font-semibold text-gray-800">{{ $guruPengganti->guruAsli->nama_guru }}</p>
                </div>
                <div>
                    <p class="text-gray-600">Jadwal:</p>
                    <p class="font-semibold text-gray-800">
                        {{ $guruPengganti->guruMengajar->jadwal->mapel->nama_mapel }} - 
                        {{ $guruPengganti->guruMengajar->jadwal->kelas->nama_kelas }}
                    </p>
                </div>
                <div>
                    <p class="text-gray-600">Hari & Jam:</p>
                    <p class="font-semibold text-gray-800">
                        {{ $guruPengganti->guruMengajar->jadwal->hari }} - 
                        Jam {{ $guruPengganti->guruMengajar->jadwal->jam_ke }}
                    </p>
                </div>
                <div>
                    <p class="text-gray-600">Status Guru Mengajar:</p>
                    <span class="inline-block px-3 py-1 rounded-full text-xs font-semibold {{ $guruPengganti->guruMengajar->status == 'tidak_masuk' ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800' }}">
                        {{ ucfirst($guruPengganti->guruMengajar->status) }}
                    </span>
                </div>
            </div>
        </div>

        <!-- Form -->
        <form action="{{ route('admin.guru-penggantis.update', $guruPengganti) }}" method="POST" class="p-8">
            @csrf
            @method('PUT')

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
                        <option value="{{ $guru->id }}" 
                                {{ old('guru_pengganti_id', $guruPengganti->guru_pengganti_id) == $guru->id ? 'selected' : '' }}
                                {{ $guru->id == $guruPengganti->guru_asli_id ? 'disabled' : '' }}>
                            {{ $guru->kode_guru }} - {{ $guru->nama_guru }}
                            {{ $guru->id == $guruPengganti->guru_asli_id ? '(Guru Asli - Tidak Bisa Dipilih)' : '' }}
                        </option>
                    @endforeach
                </select>
                @error('guru_pengganti_id')
                    <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                @enderror
                <p class="mt-2 text-xs text-gray-500">
                    <i class="fas fa-exclamation-triangle text-yellow-500 mr-1"></i>
                    Guru pengganti tidak boleh sama dengan guru asli ({{ $guruPengganti->guruAsli->nama_guru }})
                </p>
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
                           value="{{ old('tanggal_mulai', $guruPengganti->tanggal_mulai?->format('Y-m-d') ?? '') }}" 
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
                           value="{{ old('tanggal_selesai', $guruPengganti->tanggal_selesai?->format('Y-m-d') ?? '') }}" 
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent @error('tanggal_selesai') border-red-500 @enderror">
                    @error('tanggal_selesai')
                        <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                    @enderror
                </div>
            </div>

            <!-- Duration Info -->
            @if($guruPengganti->tanggal_mulai && $guruPengganti->tanggal_selesai)
            <div class="mb-6 p-4 bg-purple-50 border-l-4 border-purple-500 rounded">
                @php
                    $currentDurasi = $guruPengganti->tanggal_mulai->diffInDays($guruPengganti->tanggal_selesai) + 1;
                @endphp
                <p class="text-sm text-purple-800">
                    <i class="fas fa-calendar-day mr-2"></i>
                    Durasi saat ini: <strong>{{ $currentDurasi }} hari</strong>
                    ({{ $guruPengganti->tanggal_mulai->format('d M Y') }} - {{ $guruPengganti->tanggal_selesai->format('d M Y') }})
                </p>
            </div>
            @endif

            <!-- Alasan -->
            <div class="mb-6">
                <label class="block text-sm font-bold text-gray-700 mb-2">
                    <i class="fas fa-exclamation-circle text-yellow-600 mr-1"></i>
                    Alasan <span class="text-red-500">*</span>
                </label>
                <input type="text" 
                       name="alasan" 
                       value="{{ old('alasan', $guruPengganti->alasan) }}" 
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
                          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent @error('keterangan') border-red-500 @enderror">{{ old('keterangan', $guruPengganti->keterangan) }}</textarea>
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
                    <option value="aktif" {{ old('status', $guruPengganti->status) == 'aktif' ? 'selected' : '' }}>Aktif</option>
                    <option value="selesai" {{ old('status', $guruPengganti->status) == 'selesai' ? 'selected' : '' }}>Selesai</option>
                </select>
                @error('status')
                    <p class="mt-1 text-sm text-red-600">{{ $message }}</p>
                @enderror
                <p class="mt-2 text-xs text-gray-500">
                    <i class="fas fa-lightbulb text-yellow-500 mr-1"></i>
                    Status "Selesai" menandakan masa penggantian sudah berakhir
                </p>
            </div>

            <!-- Buttons -->
            <div class="flex justify-end gap-3 pt-6 border-t border-gray-200">
                <a href="{{ route('admin.guru-penggantis') }}" 
                   class="px-6 py-3 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition font-semibold">
                    <i class="fas fa-times mr-2"></i>Batal
                </a>
                <button type="submit" 
                        class="px-6 py-3 bg-orange-600 text-white rounded-lg hover:bg-orange-700 transition font-semibold shadow-lg">
                    <i class="fas fa-save mr-2"></i>Update Data
                </button>
            </div>
        </form>
    </div>

    <!-- Audit Info -->
    <div class="mt-4 bg-gray-100 rounded-lg p-4">
        <p class="text-xs text-gray-600">
            <i class="fas fa-clock mr-1"></i>
            Dibuat: {{ $guruPengganti->created_at->format('d M Y H:i') }} | 
            Terakhir diupdate: {{ $guruPengganti->updated_at->format('d M Y H:i') }}
        </p>
    </div>
</div>
@endsection
