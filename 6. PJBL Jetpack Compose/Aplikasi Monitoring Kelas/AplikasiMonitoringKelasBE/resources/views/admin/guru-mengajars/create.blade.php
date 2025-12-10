@extends('layouts.app')
@section('title', 'Tambah Guru Mengajar')
@section('page-title', 'Tambah Data Guru Mengajar')

@section('content')
<div class="max-w-2xl">
    <a href="{{ route('admin.guru-mengajars') }}" class="inline-flex items-center text-gray-600 hover:text-gray-900 mb-6"><i class="fas fa-arrow-left mr-2"></i>Kembali</a>
    <div class="bg-white rounded-xl shadow-lg p-8">
        <form action="{{ route('admin.guru-mengajars.store') }}" method="POST" class="space-y-6">
            @csrf
            <div>
                <label class="block text-sm font-semibold mb-2">Jadwal</label>
                <select name="jadwal_id" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    <option value="">Pilih Jadwal</option>
                    @foreach($jadwals as $jadwal)
                        <option value="{{ $jadwal->id }}" {{ old('jadwal_id') == $jadwal->id ? 'selected' : '' }}>
                            {{ $jadwal->hari }} - {{ $jadwal->jam_ke }} | {{ $jadwal->kelas->nama_kelas }} - {{ $jadwal->mapel->nama_mapel }} ({{ $jadwal->guru->nama_guru }})
                        </option>
                    @endforeach
                </select>
                @error('jadwal_id')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Status</label>
                <select name="status" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    <option value="">Pilih Status</option>
                    <option value="masuk" {{ old('status') == 'masuk' ? 'selected' : '' }}>Masuk</option>
                    <option value="tidak_masuk" {{ old('status') == 'tidak_masuk' ? 'selected' : '' }}>Tidak Masuk</option>
                </select>
                @error('status')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Keterangan</label>
                <textarea name="keterangan" rows="4" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" placeholder="Masukkan keterangan jika diperlukan">{{ old('keterangan') }}</textarea>
                @error('keterangan')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div class="flex space-x-4 pt-4">
                <button type="submit" class="flex-1 bg-blue-600 text-white font-bold py-3 px-6 rounded-lg hover:bg-blue-700"><i class="fas fa-save mr-2"></i>Simpan</button>
                <a href="{{ route('admin.guru-mengajars') }}" class="flex-1 bg-gray-200 text-gray-700 font-bold py-3 px-6 rounded-lg hover:bg-gray-300 text-center"><i class="fas fa-times mr-2"></i>Batal</a>
            </div>
        </form>
    </div>
</div>
@endsection
