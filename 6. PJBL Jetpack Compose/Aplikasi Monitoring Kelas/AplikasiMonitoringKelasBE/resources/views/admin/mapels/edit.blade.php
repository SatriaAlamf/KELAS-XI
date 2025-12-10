@extends('layouts.app')
@section('title', 'Edit Mapel')
@section('page-title', 'Edit Mata Pelajaran')

@section('content')
<div class="max-w-2xl">
    <a href="{{ route('admin.mapels') }}" class="inline-flex items-center text-gray-600 hover:text-gray-900 mb-6"><i class="fas fa-arrow-left mr-2"></i>Kembali</a>
    <div class="bg-white rounded-xl shadow-lg p-8">
        <form action="{{ route('admin.mapels.update', $mapel) }}" method="POST" class="space-y-6">
            @csrf @method('PUT')
            <div>
                <label class="block text-sm font-semibold mb-2">Kode Mapel</label>
                <input type="text" name="kode_mapel" value="{{ old('kode_mapel', $mapel->kode_mapel) }}" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                @error('kode_mapel')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Nama Mata Pelajaran</label>
                <input type="text" name="nama_mapel" value="{{ old('nama_mapel', $mapel->nama_mapel) }}" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                @error('nama_mapel')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div class="flex space-x-4 pt-4">
                <button type="submit" class="flex-1 bg-blue-600 text-white font-bold py-3 px-6 rounded-lg hover:bg-blue-700"><i class="fas fa-save mr-2"></i>Update</button>
                <a href="{{ route('admin.mapels') }}" class="flex-1 bg-gray-200 text-gray-700 font-bold py-3 px-6 rounded-lg hover:bg-gray-300 text-center"><i class="fas fa-times mr-2"></i>Batal</a>
            </div>
        </form>
    </div>
</div>
@endsection
