@extends('layouts.app')
@section('title', 'Edit Guru Mengajar')
@section('page-title', 'Edit Data Guru Mengajar')

@section('content')
<div class="max-w-2xl">
    <a href="{{ route('admin.guru-mengajars') }}" class="inline-flex items-center text-gray-600 hover:text-gray-900 mb-6"><i class="fas fa-arrow-left mr-2"></i>Kembali</a>
    <div class="bg-white rounded-xl shadow-lg p-8">
        <form action="{{ route('admin.guru-mengajars.update', $guruMengajar) }}" method="POST" class="space-y-6">
            @csrf @method('PUT')
            <div>
                <label class="block text-sm font-semibold mb-2">Jadwal</label>
                <select name="jadwal_id" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    @foreach($jadwals as $jadwal)
                        <option value="{{ $jadwal->id }}" {{ old('jadwal_id', $guruMengajar->jadwal_id) == $jadwal->id ? 'selected' : '' }}>
                            {{ $jadwal->hari }} - {{ $jadwal->jam_ke }} | {{ $jadwal->kelas->nama_kelas }} - {{ $jadwal->mapel->nama_mapel }} ({{ $jadwal->guru->nama_guru }})
                        </option>
                    @endforeach
                </select>
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Status</label>
                <select name="status" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    <option value="masuk" {{ old('status', $guruMengajar->status) == 'masuk' ? 'selected' : '' }}>Masuk</option>
                    <option value="tidak_masuk" {{ old('status', $guruMengajar->status) == 'tidak_masuk' ? 'selected' : '' }}>Tidak Masuk</option>
                </select>
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Keterangan</label>
                <textarea name="keterangan" rows="4" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500">{{ old('keterangan', $guruMengajar->keterangan) }}</textarea>
            </div>
            <div class="flex space-x-4 pt-4">
                <button type="submit" class="flex-1 bg-blue-600 text-white font-bold py-3 px-6 rounded-lg hover:bg-blue-700"><i class="fas fa-save mr-2"></i>Update</button>
                <a href="{{ route('admin.guru-mengajars') }}" class="flex-1 bg-gray-200 text-gray-700 font-bold py-3 px-6 rounded-lg hover:bg-gray-300 text-center"><i class="fas fa-times mr-2"></i>Batal</a>
            </div>
        </form>
    </div>
</div>
@endsection
