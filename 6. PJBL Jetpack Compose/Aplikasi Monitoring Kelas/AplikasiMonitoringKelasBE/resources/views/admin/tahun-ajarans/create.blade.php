@extends('layouts.app')
@section('title', 'Tambah Tahun Ajaran')
@section('page-title', 'Tambah Tahun Ajaran')

@section('content')
<div class="max-w-2xl">
    <a href="{{ route('admin.tahun-ajarans') }}" class="inline-flex items-center text-gray-600 hover:text-gray-900 mb-6"><i class="fas fa-arrow-left mr-2"></i>Kembali</a>
    <div class="bg-white rounded-xl shadow-lg p-8">
        <form action="{{ route('admin.tahun-ajarans.store') }}" method="POST" class="space-y-6">
            @csrf
            <div>
                <label class="block text-sm font-semibold mb-2">Tahun Ajaran</label>
                <input type="text" name="tahun" value="{{ old('tahun') }}" placeholder="Contoh: 2024/2025" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                @error('tahun')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div>
                <label class="flex items-center">
                    <input type="checkbox" name="flag" value="1" class="w-5 h-5 text-blue-600 rounded focus:ring-2 focus:ring-blue-500" {{ old('flag') ? 'checked' : '' }}>
                    <span class="ml-2 text-sm font-semibold">Aktif</span>
                </label>
            </div>
            <div class="flex space-x-4 pt-4">
                <button type="submit" class="flex-1 bg-blue-600 text-white font-bold py-3 px-6 rounded-lg hover:bg-blue-700"><i class="fas fa-save mr-2"></i>Simpan</button>
                <a href="{{ route('admin.tahun-ajarans') }}" class="flex-1 bg-gray-200 text-gray-700 font-bold py-3 px-6 rounded-lg hover:bg-gray-300 text-center"><i class="fas fa-times mr-2"></i>Batal</a>
            </div>
        </form>
    </div>
</div>
@endsection
