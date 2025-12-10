@extends('layouts.app')
@section('title', 'Tambah Jadwal')
@section('page-title', 'Tambah Jadwal')

@section('content')
<div class="max-w-2xl">
    <a href="{{ route('admin.jadwals') }}" class="inline-flex items-center text-gray-600 hover:text-gray-900 mb-6"><i class="fas fa-arrow-left mr-2"></i>Kembali</a>
    <div class="bg-white rounded-xl shadow-lg p-8">
        <form action="{{ route('admin.jadwals.store') }}" method="POST" class="space-y-6">
            @csrf
            <div>
                <label class="block text-sm font-semibold mb-2">Kelas</label>
                <select name="kelas_id" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    <option value="">Pilih Kelas</option>
                    @foreach($kelas as $k)
                        <option value="{{ $k->id }}" {{ old('kelas_id') == $k->id ? 'selected' : '' }}>{{ $k->nama_kelas }}</option>
                    @endforeach
                </select>
                @error('kelas_id')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Mata Pelajaran</label>
                <select name="mapel_id" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    <option value="">Pilih Mapel</option>
                    @foreach($mapels as $mapel)
                        <option value="{{ $mapel->id }}" {{ old('mapel_id') == $mapel->id ? 'selected' : '' }}>{{ $mapel->nama_mapel }}</option>
                    @endforeach
                </select>
                @error('mapel_id')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Guru</label>
                <select name="guru_id" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    <option value="">Pilih Guru</option>
                    @foreach($gurus as $guru)
                        <option value="{{ $guru->id }}" {{ old('guru_id') == $guru->id ? 'selected' : '' }}>{{ $guru->nama_guru }}</option>
                    @endforeach
                </select>
                @error('guru_id')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Tahun Ajaran</label>
                <select name="tahun_ajaran_id" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    <option value="">Pilih Tahun Ajaran</option>
                    @foreach($tahunAjarans as $ta)
                        <option value="{{ $ta->id }}" {{ old('tahun_ajaran_id') == $ta->id ? 'selected' : '' }}>{{ $ta->tahun }}</option>
                    @endforeach
                </select>
                @error('tahun_ajaran_id')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Hari</label>
                <select name="hari" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                    <option value="">Pilih Hari</option>
                    <option value="Senin" {{ old('hari') == 'Senin' ? 'selected' : '' }}>Senin</option>
                    <option value="Selasa" {{ old('hari') == 'Selasa' ? 'selected' : '' }}>Selasa</option>
                    <option value="Rabu" {{ old('hari') == 'Rabu' ? 'selected' : '' }}>Rabu</option>
                    <option value="Kamis" {{ old('hari') == 'Kamis' ? 'selected' : '' }}>Kamis</option>
                    <option value="Jumat" {{ old('hari') == 'Jumat' ? 'selected' : '' }}>Jumat</option>
                    <option value="Sabtu" {{ old('hari') == 'Sabtu' ? 'selected' : '' }}>Sabtu</option>
                </select>
                @error('hari')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div>
                <label class="block text-sm font-semibold mb-2">Jam Ke</label>
                <input type="text" name="jam_ke" value="{{ old('jam_ke') }}" placeholder="Contoh: 1, 2, 3-4" class="w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500" required>
                @error('jam_ke')<p class="mt-2 text-sm text-red-600">{{ $message }}</p>@enderror
            </div>
            <div class="flex space-x-4 pt-4">
                <button type="submit" class="flex-1 bg-blue-600 text-white font-bold py-3 px-6 rounded-lg hover:bg-blue-700"><i class="fas fa-save mr-2"></i>Simpan</button>
                <a href="{{ route('admin.jadwals') }}" class="flex-1 bg-gray-200 text-gray-700 font-bold py-3 px-6 rounded-lg hover:bg-gray-300 text-center"><i class="fas fa-times mr-2"></i>Batal</a>
            </div>
        </form>
    </div>
</div>
@endsection
