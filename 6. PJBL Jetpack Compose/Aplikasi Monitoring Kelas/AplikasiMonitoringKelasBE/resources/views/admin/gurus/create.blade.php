@extends('layouts.app')

@section('title', 'Tambah Guru')
@section('page-title', 'Tambah Guru')

@section('content')
<div class="max-w-2xl">
    <a href="{{ route('admin.gurus') }}" class="inline-flex items-center text-gray-600 hover:text-gray-900 mb-6">
        <i class="fas fa-arrow-left mr-2"></i>Kembali
    </a>

    <div class="bg-white rounded-xl shadow-lg p-8">
        <form action="{{ route('admin.gurus.store') }}" method="POST" class="space-y-6">
            @csrf
            <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">Kode Guru</label>
                <input type="text" name="kode_guru" value="{{ old('kode_guru') }}" 
                    class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                @error('kode_guru')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">Nama Guru</label>
                <input type="text" name="nama_guru" value="{{ old('nama_guru') }}" 
                    class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                @error('nama_guru')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">Telepon</label>
                <input type="text" name="telepon" value="{{ old('telepon') }}" 
                    class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500">
                @error('telepon')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div class="flex space-x-4 pt-4">
                <button type="submit" class="flex-1 bg-blue-600 text-white font-bold py-3 px-6 rounded-lg hover:bg-blue-700">
                    <i class="fas fa-save mr-2"></i>Simpan
                </button>
                <a href="{{ route('admin.gurus') }}" class="flex-1 bg-gray-200 text-gray-700 font-bold py-3 px-6 rounded-lg hover:bg-gray-300 text-center">
                    <i class="fas fa-times mr-2"></i>Batal
                </a>
            </div>
        </form>
    </div>
</div>
@endsection
